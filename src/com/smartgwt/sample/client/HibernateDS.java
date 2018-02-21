package com.smartgwt.sample.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.smartgwt.client.core.KeyIdentifier;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.*;
import com.smartgwt.client.util.PageKeyHandler;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.viewer.DetailViewer;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class HibernateDS implements EntryPoint {
    private ListGrid boundList;
    private DynamicForm boundForm;
    private IButton saveBtn;
    private DetailViewer boundViewer;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        
		KeyIdentifier debugKey = new KeyIdentifier();
		debugKey.setCtrlKey(true);
		debugKey.setKeyName("D");

		Page.registerKey(debugKey, new PageKeyHandler() {
			public void execute(String keyName) {
				SC.showConsole();
			}
		});


        
		RPCManager.setActionURL(GWT.getModuleBaseURL() + "supplyItemHibernateOperations.rpc");
		
        // instantiate DataSource on the client only (this example explicitly bypasses
        // ISC server-side DataSource management)

        DataSource dataSource = new DataSource();
        dataSource.setID("supplyItem");

        DataSourceSequenceField itemID = new DataSourceSequenceField("itemID");
        itemID.setPrimaryKey(true);
        itemID.setHidden(true);

        DataSourceTextField itemName = new DataSourceTextField("itemName", "Item", 128, true);

        DataSourceTextField SKU = new DataSourceTextField("SKU", "SKU", 10);

        DataSourceTextField description = new DataSourceTextField("description", "Description");        
        DataSourceTextField category = new DataSourceTextField("category", "Category", 128);
        DataSourceTextField units = new DataSourceTextField("units", "Units", 5);

        DataSourceFloatField unitCost = new DataSourceFloatField("unitCost", "Unit Cost");
        DataSourceBooleanField inStock = new DataSourceBooleanField("inStock", "In Stock");

        DataSourceDateField nextShipment = new DataSourceDateField("nextShipment", "Next Shipment");

        dataSource.setFields(itemID, itemName, SKU, description, category, units, unitCost, inStock, nextShipment);


        VStack vStack = new VStack();
        vStack.setLeft(175);
        vStack.setTop(75);
        vStack.setWidth("70%");
        vStack.setMembersMargin(20);

        Label label = new Label();
        label.setContents("<ul>" +
                "<li>click a record in the grid to view and edit that record in the form</li>" +
                "<li>click <b>New</b> to start editing a new record in the form</li>" +
                "<li>click <b>Save</b> to save changes to a new or edited record in the form</li>" +
                "<li>click <b>Clear</b> to clear all fields in the form</li>" +
                "<li>click <b>Filter</b> to filter (substring match) the grid based on form values</li>" +
                "<li>click <b>Fetch</b> to fetch records (exact match) for the grid based on form values</li>" +
                "<li>double-click a record in the grid to edit inline (press Return, or arrow/tab to another record, to save)</li>" +
                "</ul>");
        vStack.addMember(label);

        boundList = new ListGrid();
        boundList.setHeight(200);
        boundList.setCanEdit(true);
        boundList.setDataSource(dataSource);

        boundList.addRecordClickHandler(new RecordClickHandler() {
            public void onRecordClick(RecordClickEvent event) {
                Record record = event.getRecord();
                boundForm.editRecord(record);
                saveBtn.enable();
                boundViewer.viewSelectedData(boundList);
            }
        });
        vStack.addMember(boundList);

        boundForm = new DynamicForm();
        boundForm.setDataSource(dataSource);
        boundForm.setNumCols(6);
        boundForm.setAutoFocus(false);
        vStack.addMember(boundForm);

        ToolStrip toolbar = new ToolStrip();
        toolbar.setMembersMargin(10);
        toolbar.setHeight(22);

        saveBtn = new IButton("Save");
        saveBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                boundForm.saveData();
                if (!boundForm.hasErrors()) {
                    boundForm.clearValues();
                    saveBtn.disable();
                }
            }
        });
        toolbar.addMember(saveBtn);

        IButton clearBtn = new IButton("Clear");
        clearBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                boundForm.clearValues();
                saveBtn.disable();
            }
        });
        toolbar.addMember(clearBtn);

        IButton filterBtn = new IButton("Filter");
        filterBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                boundList.filterData(boundForm.getValuesAsCriteria());
                saveBtn.disable();
            }
        });
        toolbar.addMember(filterBtn);

        IButton fetchBtn = new IButton("Fetch");
        fetchBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                boundList.fetchData(boundForm.getValuesAsCriteria());
                saveBtn.disable();
            }
        });
        toolbar.addMember(fetchBtn);

        IButton deleteBtn = new IButton("Delete");
        deleteBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                boundList.removeSelectedData();
                boundList.deselectAllRecords();
            }
        });
        toolbar.addMember(deleteBtn);

        vStack.addMember(toolbar);

        boundViewer = new DetailViewer();
        boundViewer.setDataSource(dataSource);
        vStack.addMember(boundViewer);

        vStack.draw();

        boundList.filterData(null);
    }



}
