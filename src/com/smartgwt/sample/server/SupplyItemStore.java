package com.smartgwt.sample.server;

import com.isomorphic.util.DataTools;
import com.isomorphic.datasource.DataSourceManager;
import com.isomorphic.datasource.DataSource;
import com.isomorphic.log.*;

import java.util.*;

public class SupplyItemStore {

    static Logger log = new Logger(SupplyItemStore.class.getName());

    // itemID->supplyItem storage map - prepopulated from sql below
    static Map itemsByID = new HashMap();

    static {
        try {
            // NOTE: The sample supplyItem dataset contains about 4,000 records.
            // grab at most 300 so we don't use too much memory
            DataSource ds = DataSourceManager.get("supplyItem");
            List supplyItems = ds.fetch(new HashMap());
            int maxRows = supplyItems.size();
            if (maxRows > 300) maxRows = 300;
            for (int i = 0; i < maxRows; i++) {
                Map properties = (Map)supplyItems.get(i);
                SupplyItem item = new SupplyItem();
                // DataTools.setProperties() this is a SmartClient utility method that applies a
                // map of properties on an object using the Bean reflection mechanism
                DataTools.setProperties(properties, item);
                storeItem(item);
            }
        } catch (Exception e) {
            log.error("Failed to initialize SupplyItemStore", e);
        }
    }

    // add a supply item bean to the stored beans, fetching an itemID for it if necessary
    // if the bean already has an itemID, it will replace any existing bean with that itemID
    public static SupplyItem storeItem(SupplyItem item) {
        Long itemID = item.getItemID();
        if (itemID == null) itemID = getNextItemID();
        item.setItemID(itemID);
        itemsByID.put(itemID, item);
        return item;
    }

    // delete the bean with the given itemID
    public static SupplyItem removeItem(Long itemID)  {
        return (SupplyItem)itemsByID.remove(itemID);
    }

    // get the next available itemID
    public static Long getNextItemID() {
        int highest = 0;
        for (Iterator i = itemsByID.keySet().iterator(); i.hasNext(); ) {
            Long id = (Long) i.next();
            highest = Math.max(highest, id.intValue());
        }
        return new Long(highest + 1);
    }

    // get the supply item bean, given its itemID, or null if it can't be found
    public static SupplyItem getItemByID(Long itemID)  {
        return (SupplyItem) itemsByID.get(itemID);
    }

    public static List findMatchingItems(Long itemID, String itemName)  {
        Collection items = itemsByID.values();
        List matchingItems = new ArrayList();
        for (Iterator i = items.iterator(); i.hasNext(); ) {
            SupplyItem item = (SupplyItem)i.next();

            if (itemID != null && !itemID.equals(item.getItemID())) continue;

            if (itemName != null &&
                item.getItemName().toLowerCase().indexOf(itemName.toLowerCase()) == -1)
                continue;

            matchingItems.add(item);
        }
        return matchingItems;
    }
}