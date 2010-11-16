/**
 * SAIL - biological samples availability index
 * 
 * Copyright (C) 2008,2009 Microarray Informatics Team, EMBL-European Bioinformatics Institute
 *
 *   This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *
 *  @author Mikhail Gostev <gostev@ebi.ac.uk>
 *
 */

package uk.ac.ebi.sail.client.ui.module;

import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.GroupRequestItem;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.GridCellListener;

public class PredefQueryTable extends GridPanel
{
 private ObjectAction<GroupRequestItem> listener;

 public PredefQueryTable()
 {
//  setTitle("Predefined queries");
  hideColumnHeader();
  
  ColumnConfig nmCol = new ColumnConfig("Expression", "expr");
  nmCol.setId("expr");
 
  nmCol.setRenderer( new Renderer()
  {
   @Override
   public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store)
   {

    String text = record.getAsString("desc");
    if(text != null)
    {
     String html_attr = "ext:qtitle='" + Format.htmlEncode(record.getAsString("expr")) + "'";

     html_attr += " ext:qtip='" + Format.htmlEncode(text) + "'";
     cellMetadata.setHtmlAttribute(html_attr);
    }

    return value != null ? value.toString() : null;
   }
  });
  
  ColumnConfig icCol = new ColumnConfig("Icon", "icon", 30);
  icCol.setSortable(false);
  icCol.setId("icon");
//  icCol.setCss("predefinedQueryIcon");

  icCol.setRenderer(new Renderer()
  {
   public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store)
   {
    return "<div class='predefinedQueryIcon' style='width: 16px; height: 16px'>&nbsp;</div>";
    //return "<img class=\"predefIcon\" src=\""+ConfigManager.SAIL_RESOURCE_PATH+"images/icons/fam/asterisk_orange.png\" />";
   }

  });   
  
  setColumnModel(new ColumnModel(new ColumnConfig[] { icCol, nmCol } ) );
  setAutoExpandColumn("expr");
  
  RecordDef recDef = new RecordDef( new FieldDef[] { new StringFieldDef("expr"), new StringFieldDef("desc"),  new ObjectFieldDef("obj") } );
  
  Store store = new Store(recDef);
  setStore(store);
  
  for( GroupRequestItem gri  : DataManager.getInstance().getExpressions() )
   store.add( recDef.createRecord( new Object[]{gri.getName(),gri.getDescription(),gri} ) );
 
  Toolbar tb = new Toolbar();

  tb.addItem(new ToolbarTextItem("Predefined queries"));
  
  tb.addFill();
  
  BtListener btlsn = new BtListener();
  
  ToolbarButton tbBt = new ToolbarButton();
  tbBt.setIconCls("addPredefQueryIcon");
  tbBt.setStateId("add");
  tbBt.addListener(btlsn);
  tb.addButton( tbBt );
 
  tbBt = new ToolbarButton();
  tbBt.setIconCls("addPredefQuery2GrpIcon");
  tbBt.setStateId("addGrp");
  tbBt.addListener(btlsn);
  tb.addButton( tbBt );

  setTopToolbar(tb);
  
  
  addGridCellListener( new ClickListener() );
 
  setEnableDragDrop(true);
  setDdGroup("sailDDGroup");

 }

 public void setListener( ObjectAction<GroupRequestItem> l )
 {
  listener = l;
 }
 
 private void addToRequest( String typ )
 {
  if( listener == null )
   return;
  
  Record[] sel = getSelectionModel().getSelections();
  
  if( sel == null || sel.length == 0 || sel.length > 1 )
   return;
  
  listener.doAction(typ, (GroupRequestItem)sel[0].getAsObject("obj"));
 }
 
 private class ClickListener implements GridCellListener
 {

  @Override
  public void onCellClick(GridPanel grid, int rowIndex, int colIndex, EventObject e)
  {
  }

  @Override
  public void onCellContextMenu(GridPanel grid, int rowIndex, int cellIndex, EventObject e)
  {
  }

  @Override
  public void onCellDblClick(GridPanel grid, int rowIndex, int colIndex, EventObject e)
  {
   addToRequest("add");
  }
 }

 private class BtListener extends ButtonListenerAdapter
 {
  public void onClick(Button button, EventObject e)
  {
   String state = button.getStateId();
   
   addToRequest(state);
  }
 }
}
