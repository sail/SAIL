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

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.sail.client.ConfigManager;
import uk.ac.ebi.sail.client.common.EnumFilteredRequestItem;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.RequestItem;
import uk.ac.ebi.sail.client.ui.CollectionProvider;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.core.TextAlign;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Container;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.ContainerListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.Radio;
import com.gwtext.client.widgets.form.event.CheckboxListenerAdapter;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.layout.AnchorLayout;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

@Deprecated
public class RequestTable extends Panel
{

 private List<MakeReportListener> listeners = new ArrayList<MakeReportListener>(3);
 private Store                    store;
 GridPanel                        grid;
 private Checkbox useRepSplit;
 private Radio splitByAll;
 private Radio splitBySel;

 private Checkbox useRels;

 
 private CollectionProvider repProvider;

 private static RecordDef   recordDef = new RecordDef( new FieldDef[] { 
   new StringFieldDef("parameter"),
   new StringFieldDef("enum"), 
   new ObjectFieldDef("obj") });

 public RequestTable()
 {
  super();

  setBorder(false);
  setFrame(true);
  // setMargins(10);
  setTitle("Report request");
  setLayout(new AnchorLayout());

  grid = new GridPanel();
  grid.setLayout(new FitLayout());
  grid.setSelectionModel(new RowSelectionModel(true));

  ColumnConfig[] columns = new ColumnConfig[3];

  int k = 0;

  ColumnConfig cc = new ColumnConfig("Name", "parameter");
  cc.setId("parameter");
  cc.setWidth(200);
  cc.setAlign(TextAlign.LEFT);
  cc.setSortable(false);

  columns[k++] = cc;

  cc = new ColumnConfig("Enumeration", "enum");
  cc.setId("enum");
  cc.setAlign(TextAlign.LEFT);
  cc.setSortable(false);

  columns[k++] = cc;

  cc = new ColumnConfig("F", "enum");
  cc.setWidth(25);
  cc.setRenderer(new FiltRenderer());
  cc.setSortable(false);

  columns[k++] = cc;

  ColumnModel columnModel = new ColumnModel(columns);
  grid.setColumnModel(columnModel);

  grid.setFrame(false);
  grid.setStripeRows(true);
  grid.setAutoExpandColumn("enum");

  grid.setHeight(350);

  grid.setAutoWidth(true);
  // grid.setMonitorWindowResize

  Panel controlPanel = new Panel();
  
  controlPanel.setPaddings(3);
  
  Button bt;
  ButtonListenerAdapter edtLsnr = new ReportEditor();
  
  bt=new Button("Remove");
  bt.setStateId("remove");
  bt.addListener(edtLsnr);
  controlPanel.addButton(bt);
  
  bt=new Button("Clear");
  bt.setStateId("clear");
  bt.addListener(edtLsnr);
  controlPanel.addButton(bt);
  
  bt=new Button("Up");
  bt.setStateId("up");
  bt.addListener(edtLsnr);
  controlPanel.addButton(bt);
  
  bt=new Button("Down");
  bt.setStateId("down");
  bt.addListener(edtLsnr);
  controlPanel.addButton(bt);

  FormPanel leftPart = new FormPanel();
  FormPanel rightPart = new FormPanel();
  
  
  useRepSplit = new Checkbox("Use split by collection");
  splitByAll = new Radio("Split by all","split");
  splitBySel = new Radio("Split by selected","split");
  
  useRepSplit.setChecked(true);
  splitByAll.setChecked(true);
  
  useRepSplit.addListener( new SplitListener() );
  
  final Button relSelButton = new Button("Select", new RelSelector() );
  relSelButton.setDisabled(true);
  
  useRels = new Checkbox("Use relations");
  useRels.addListener( new CheckboxListenerAdapter()
  {
   public void onCheck(Checkbox field, boolean checked)
   {
    relSelButton.setDisabled(!checked);
   }
  }
  );
  
  
  controlPanel.setLayout( new ColumnLayout() );
  
  controlPanel.add(leftPart, new ColumnLayoutData(0.5));
  controlPanel.add(rightPart, new ColumnLayoutData(0.5));

  rightPart.setHideLabels(true);
  leftPart.setHideLabels(true);
  
  rightPart.add(useRels);
  rightPart.add(relSelButton);
  
  leftPart.add( useRepSplit );
  leftPart.add( splitByAll );
  leftPart.add( splitBySel );

  rightPart.setHeight(85);
  leftPart.setHeight(85);
  
  leftPart.setFrame(true);
  rightPart.setFrame(true);
  
  controlPanel.setHeight(130);
  
  add(grid, new AnchorLayoutData("100% -130"));

  add(controlPanel, new AnchorLayoutData("100%"));
  
  setButtonAlign(Position.CENTER);

  Button b = new Button("Make report");
  addButton(b);

  b.addListener(new ReportBtListener());

//  b = new Button("Remove");
//  addButton(b);
//  b.addListener(new RemoveBtListener());
//
//  b = new Button("Clear");
//  addButton(b);
//  b.addListener(new ClearBtListener());

  store = new Store(recordDef);
  grid.setStore(store);

  grid.addListener(new ContainerListenerAdapter()
  {
   public void onAfterLayout(Container self)
   {
    grid.getView().refresh();
   }
  });

 }

 
 public void setCollectionProvider(CollectionProvider rp)
 {
  repProvider=rp;
 }
 
 /*
 
 public void addParameter(Parameter p, Variable v)
 {
  ReportRequestItem rq = new ReportRequestItem(p.getName(), v.getName(), v);

  Record r = recordDef.createRecord(new Object[] { p.getName(), v.getName(), rq });
  store.add(r);
 }

 public void addParameter(Parameter p)
 {
  ReportRequestItem rq = new ReportRequestItem(p.getName(), "", p);

  Record r = recordDef.createRecord(new Object[] { p.getName(), "", rq });
  store.add(r);
 }

 public void addParameter(Parameter p, Qualifier q)
 {
  ReportRequestItem rq = new ReportRequestItem(p.getName(), q.getName(), q);

  Record r = recordDef.createRecord(new Object[] { p.getName(), (q != null ? q.getName() : ""), rq });
  store.add(r);
  // grid.getView().refresh();
 }
 
 public void addParameter(FilteredEnumeration fe)
 {
  Parameter p=null;
  String enName=null;
  
  p=fe.getEnumeration().getParameter();
  enName = fe.getEnumeration().getName();
  
  ReportRequestItem rq = null;
  
   rq = new ReportRequestItem(p.getName(), enName, fe);

  Record r = recordDef.createRecord(new Object[] { p.getName(), enName, rq });
  store.add(r);
  // grid.getView().refresh();
 }

 private static int ids = 1;

 public void addGroup(List<Parameter> lp)
 {
  if(lp == null)
   return;

  int id = ids++;

  Parameter p = new Parameter();
  p.setName("Parameter group " + id);
  p.setId(-id);

  StringBuilder sb = new StringBuilder();
  sb.append("[ ");
  for(Parameter pfl : lp)
   sb.append(pfl.getName()).append(", ");

  sb.setLength(sb.length() - 2);
  sb.append(" ]");

  ReportRequestItem rq = new ReportRequestItem("Parameter group " + id, sb.toString(), lp);

  Record r = recordDef.createRecord(new Object[] { p.getName(), rq.getSubjectName(), rq });
  store.add(r);
 }

 public void addReportListener(MakeReportListener l)
 {
  listeners.add(l);
 }
*/
 private class ReportBtListener extends ButtonListenerAdapter
 {
  @Override
  public void onClick(Button button, EventObject e)
  {
   Record[] rcd = store.getRecords();

   if(rcd == null || rcd.length == 0)
    return;

   ReportRequest rreq = new ReportRequest();
   
   if( useRels.getValue() )
    rreq.setRelations( SelectRelationsDialog.getInstance().getRelations() );
   
//   List<ReportRequestItem> rril = new ArrayList<ReportRequestItem>(rcd.length);

   if( useRepSplit.getValue() )
   {
    int[] ids;
    
    if( splitByAll.getValue() )
     ids = new int[0];
    else
     ids = repProvider.getCollections();
    
    if( ids == null )
    {
     ErrorBox.showError("At least one collection must be selected with such options");
     return;
    }
    
   }

   for(Record r : rcd)
    rreq.add((RequestItem) r.getAsObject("obj"));

   for(MakeReportListener l : listeners)
    l.makeReport(rreq);
  }
 }

 private class RemoveBtListener extends ButtonListenerAdapter
 {
  @Override
  public void onClick(Button button, EventObject e)
  {
   Record sel = grid.getSelectionModel().getSelected();

   if(sel != null)

    store.remove(sel);
  }
 }

 private class RelSelector extends ButtonListenerAdapter
 {
  @Override
  public void onClick(Button button, EventObject e)
  {
   SelectRelationsDialog.getInstance().show();
  }
 }
 
// private class ClearBtListener extends ButtonListenerAdapter
// {
//  @Override
//  public void onClick(Button button, EventObject e)
//  {
//   store.removeAll();
//  }
// }
 
 private class ReportEditor extends ButtonListenerAdapter
 {
  @Override
  public void onClick(Button button, EventObject e)
  {
   String state = button.getStateId();
   
   if( "clear".equals(state) )
    store.removeAll();
   else if( "remove".equals(state) )
   {
    Record sel = grid.getSelectionModel().getSelected();

    if(sel != null)
     store.remove(sel);
   }
   else if( "down".equals(state) || "up".equals(state) )
   {
    Record r = grid.getSelectionModel().getSelected();

    if(r == null)
     return;

    RequestItem rri = (RequestItem) r.getAsObject("obj");

    int i=0;
    for(Record rd : store.getRecords())
    {
     i++;

     if( rri == (RequestItem) rd.getAsObject("obj") )
      break;
     
    }
    
    
    if( "down".equals(state) )
    {
     if( i == store.getCount() )
      return;
    }
    else
    {
     if( i == 1 )
      return;
     
     i-=2;
    }
    
    
    store.remove(r);
    store.insert(i, r);

    grid.getSelectionModel().selectRow(i);
   }
  }
 }

 private static class FiltRenderer implements Renderer
 {
  public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store)
  {
   RequestItem rri = (RequestItem)record.getAsObject("obj");
   
   
   if(  rri instanceof EnumFilteredRequestItem  )
   {
    if( ((EnumFilteredRequestItem)rri).getVariants() != null )
     return "<img class=\"infotip\" src=\""+ConfigManager.SAIL_RESOURCE_PATH+"images/icons/funnel_plus.gif\" />";
   }
   return "";
  }
 }
 
 private class SplitListener extends CheckboxListenerAdapter
 {
  public void onCheck(Checkbox field, boolean checked)
  {
   splitByAll.setDisabled(!checked);
   splitBySel.setDisabled(!checked);
  }
 }


}
