package com.smartgwt.sample.server;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.datasource.DataSource;
import com.isomorphic.rpc.ClientMustResubmitException;
import com.isomorphic.rpc.RPCManager;
import com.isomorphic.rpc.RPCRequest;
import com.isomorphic.util.DataTools;
import com.isomorphic.util.ErrorReport;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SupplyItemHibernateOperationsController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RPCManager rpc;
        try {
            rpc = new RPCManager(request, response);
        } catch (ClientMustResubmitException e) {
            return null;
        }

        for (Iterator i = rpc.getRequests().iterator(); i.hasNext();) {

            // To be completely safe, check what kind of request we received.  As the developer
            // you have complete control over which requests go where, but if you have a single
            // request dispatcher for RPCRequests and DSRequests, you'll need this check.
            //
            Object req = i.next();
            if (req instanceof RPCRequest)
                throw new Exception("This example expects only DSRequests");
            DSRequest dsRequest = (DSRequest) req;


            // inspect the name of the datasource for this request.  In this example, we only
            // care about performing custom logic on the supplyItem datasource.  All other
            // datasource should follow the default logic.
            //
            String dsName = dsRequest.getDataSourceName();
            if (!"supplyItem".equals(dsName)) {
                // a datasource request that we don't care to override, just invoke
                // default processing logic
                rpc.send(dsRequest, dsRequest.execute());
                continue;
            }
            DataSource supplyItem = dsRequest.getDataSource();

            // instantiate the response object we'll send back: we'll populate it with data below.
            //
            DSResponse dsResponse = new DSResponse();

            // assume the operation will be successful.  If there's a failure, we'll override this with
            // an error code and provide the problem report as the data so the client can log it.
            dsResponse.setSuccess();

            // Configure and initialize hibernate - reads /WEB-INF/classes/hibernate.cfg.xml by default
            Configuration cfg = new Configuration().configure();

            // add SupplyItem.hbm.xml to config
            cfg.addClass(SupplyItem.class);
            SessionFactory hibernateSessionFactory = cfg.buildSessionFactory();
            Session hibernateSession = hibernateSessionFactory.openSession();
            Transaction transaction = hibernateSession.beginTransaction();

            // inspect the operation type.  We have different logic for each.
            //
            String operation = dsRequest.getOperationType();
            if (operation.equals(DataSource.OP_FETCH)) {
                // DataSource protocol: get filter criteria
                String itemName = (String) dsRequest.getFieldValue("itemName");

                // DataSource protocol: get requested row range
                long startRow = (int) dsRequest.getStartRow();
                long endRow = (int) dsRequest.getEndRow();

                Criteria criteria = hibernateSession.createCriteria(SupplyItem.class);
                Criterion itemNameRestriction = null;
                if (itemName != null) {
                    itemNameRestriction = Restrictions.like("itemName", itemName, MatchMode.ANYWHERE);
                    criteria.add(itemNameRestriction);
                }

                // determine total available rows - we return this to the client so the ListGrid can
                // correctly size the scrollbar
                criteria.setProjection(Projections.rowCount());
                Object rowCount = criteria.uniqueResult();
                long totalRows = 0;
                // Later versions of Hibernate return a Long rather than an Integer here, for all
                // those occasions when a fetch returns more than 2.1 billion rows...
                if (rowCount instanceof Integer) {
                    totalRows = ((Integer)rowCount).intValue();
                } else if (rowCount instanceof Long) {
                    totalRows = ((Long)rowCount).longValue();
                }

                // clamp endRow to available rows and slice out requested range
                endRow = Math.min(endRow, totalRows);

                // rebuilt the criteria minus the rowCount projection
                criteria = hibernateSession.createCriteria(SupplyItem.class);
                if (itemName != null) criteria.add(itemNameRestriction);

                // limit number of rows returned to just what the ListGrid asked for
                criteria.setFirstResult((int) startRow);
                criteria.setMaxResults((int)(endRow - startRow));
                List matchingItems = criteria.list();

                // DataSource protocol: return matching item beans
                dsResponse.setData(matchingItems);
                // tell client what rows are being returned, and what's available
                dsResponse.setStartRow(startRow);
                dsResponse.setEndRow(endRow);
                dsResponse.setTotalRows(totalRows);
            } else if (operation.equals(DataSource.OP_ADD)) {
                // DataSource protocol: get new values to be saved
                Map newValues = dsRequest.getValues();


                ErrorReport errorReport = supplyItem.validate(newValues, false);
                if (errorReport != null) {
                    dsResponse.setStatus(DSResponse.STATUS_VALIDATION_ERROR);
                    dsResponse.setErrorReport(errorReport);
                    System.out.println("Errors: " + DataTools.prettyPrint(errorReport));
                } else {
                    // bean storage specific: create a new item bean
                    SupplyItem item = new SupplyItem();
                    // apply values to item bean
                    DataTools.setProperties(dsRequest.getValues(), item);

                    // store new item bean
                    hibernateSession.save(item);

                    // DataSource protocol: return the committed item bean to the client for cache update
                    dsResponse.setData(item);
                }
            } else if (operation.equals(DataSource.OP_UPDATE)) {
                // DataSource protocol: get primary keys for update (itemID field in this case)
                Long itemID = (Long) dsRequest.getFieldValue("itemID");
                // DataSource protocol: get new values to be saved
                Map newValues = dsRequest.getValues();

                ErrorReport errorReport = supplyItem.validate(newValues, false);
                if (errorReport != null) {
                    dsResponse.setStatus(DSResponse.STATUS_VALIDATION_ERROR);
                    dsResponse.setErrorReport(errorReport);
                    System.out.println("Errors: " + DataTools.prettyPrint(errorReport));
                } else {
                    // bean storage specific:  get the supply item bean to be updated
                    SupplyItem item = (SupplyItem) hibernateSession.get(SupplyItem.class, itemID);
                    // apply update values to item bean
                    DataTools.setProperties(newValues, item);
                    hibernateSession.update(item);

                    // DataSource protocol: return the committed bean to the client for cache update
                    dsResponse.setData(item);
                }
            } else if (operation.equals(DataSource.OP_REMOVE)) {
                // DataSource protocol: get primary key of removed record
                Long itemID = (Long) dsRequest.getFieldValue("itemID");

                // hibernate delete bean
                SupplyItem item = (SupplyItem) hibernateSession.get(SupplyItem.class, itemID);
                hibernateSession.delete(item);

                // DataSource protocol: return the primary key of the deleted bean to the client for
                // cache update
                dsResponse.setData(dsRequest.getCriteria());
            } else {
                // encountered an unknown operation type.  Report this problem to the client.
                dsResponse.setFailure();
                dsResponse.setData("Unknown operationType: " + operation);
            }

            transaction.commit();
            hibernateSession.close();

            // match the response to the request
            rpc.send(dsRequest, dsResponse);

        } // for(requests)
        return null;
    }
}
