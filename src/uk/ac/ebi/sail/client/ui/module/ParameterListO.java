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
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.ParametersRepository;
import uk.ac.ebi.sail.client.common.ClassifiableManager;
import uk.ac.ebi.sail.client.common.Parameter;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.core.TextAlign;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Container;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.ContainerListener;
import com.gwtext.client.widgets.event.ContainerListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtextux.client.widgets.grid.plugins.GridSearchPlugin;

public class ParameterListO extends Panel
{
 private static RecordDef recordDef = new RecordDef(new FieldDef[] { 
   new StringFieldDef("code"),
   new StringFieldDef("name"),
   new StringFieldDef("desc"),
   new IntegerFieldDef("recs"),
   new IntegerFieldDef("v"),
   new IntegerFieldDef("e"),
   new ObjectFieldDef("obj")
   });

 private static ColumnModel columnModel;
 
 static
 {
  ColumnConfig[] columns = new ColumnConfig[6];

  int k = 0;
  ColumnConfig cc = new ColumnConfig("Code", "code", 60);
  cc.setId("code");
  cc.setAlign(TextAlign.LEFT);
  cc.setSortable(true);

  columns[k++] = cc;

  cc = new ColumnConfig("Name", "name");
  cc.setId("parameter");
  cc.setWidth(200);
  cc.setAlign(TextAlign.LEFT);
  cc.setSortable(true);

  columns[k++] = cc;

  cc = new ColumnConfig("Description", "desc");
  cc.setId("description");
  cc.setAlign(TextAlign.LEFT);
  cc.setSortable(false);

  columns[k++] = cc;

  cc = new ColumnConfig("Records", "recs", 50);
  cc.setAlign(TextAlign.RIGHT);
  cc.setSortable(false);

  columns[k++] = cc;

  cc = new ColumnConfig("V", "v", 30);
  cc.setAlign(TextAlign.RIGHT);
  cc.setSortable(false);

  columns[k++] = cc;

  cc = new ColumnConfig("E", "e", 30);
  cc.setAlign(TextAlign.RIGHT);
  cc.setSortable(false);

  columns[k++] = cc;

  columnModel = new ColumnModel(columns);
 }
 
 private List<BtLsnr> listeners = new ArrayList<BtLsnr>(3);
 private Store        store;
 private GridPanel            grid;
 
 public ParameterListO(boolean single, String[][] buttons, ClassifiableManager<Parameter> m)
 {
  super();

  setBorder(false);
  setFrame(true);
//  setPaddings(1);
  setTitle("Parameters");
  setLayout( new FitLayout() );
  
  grid = new GridPanel();
  grid.setAutoScroll(true);
  grid.setSelectionModel(new RowSelectionModel(single));

  grid.setColumnModel(columnModel);

  grid.setFrame(false);
  grid.setStripeRows(true);
  grid.setAutoExpandColumn("description");
  
  grid.setAutoWidth(true);
  grid.setMonitorWindowResize(true);

  Toolbar topToolbar = new Toolbar();
  topToolbar.addFill();
  grid.setTopToolbar(topToolbar);

  GridSearchPlugin gridSearch = new GridSearchPlugin(GridSearchPlugin.TOP);
  gridSearch.setMode(GridSearchPlugin.LOCAL);
  grid.addPlugin(gridSearch);

  grid.setStore(store=new Store( recordDef ));
  
  setData(m.getClassifiable());
  
  add(grid);

  setButtonAlign(Position.CENTER);

  if(buttons != null)
  {
   for(String[] bt : buttons)
   {
    Button b = new Button(bt[1]);
    b.setStateId(bt[0]);
    addButton(b);
    b.addListener(new ButtonListener());
   }
  }

  
  grid.addListener(new ContainerListenerAdapter(){
   public void onAfterLayout(Container self)
   { 
    grid.getView().refresh();
   }
  });

 }


 private void setData(Collection<Parameter> list)
 {
  for(Parameter p : list)
  {
   Object[] data = new Object[8];

   data[0] = p.getId();
   data[1] = p.getCode();
   data[2] = p.getName();
   data[3] = p.getDescription();
   data[4] = p.getRecordsCount();
   data[5] = new Integer(p.countVariables());
   data[6] = new Integer(p.countEnumerations());
   data[7] = p;

   store.add( recordDef.createRecord(data) );
  }
 }

// public void setSingleSelection(boolean single)
// {
//  grid.setSelectionModel(new RowSelectionModel(single));
// }

 public void addParameterAction(String btn, ObjectAction lsnr)
 {
  listeners.add(new BtLsnr(btn, lsnr, true));
 }

 public void addParameterAction(String btn, ObjectAction lsnr, boolean selReq)
 {
  listeners.add(new BtLsnr(btn, lsnr, selReq));
 }

 class ButtonListener extends ButtonListenerAdapter
 {
  public void onClick(Button button, EventObject e)
  {
   Record[] selr = grid.getSelectionModel().getSelections();

//   System.out.println("Sel: " + selr.length+"");

   if(selr != null)
   {
    if(selr.length > 1)
    {
     List<Parameter> lp = new ArrayList<Parameter>(selr.length);

     for(Record r : selr)
     {
      Parameter p = (Parameter) r.getAsObject("parameter");
      lp.add(p);
     }

     String title = button.getTitle();

     for(BtLsnr l : listeners)
     {
      if(l.button.equals(title))
       l.act.doMultyAction(title, lp);
     }

     return;
    }
    else
    {
     Parameter p = (Parameter) selr[0].getAsObject("parameter");
     String title = button.getTitle();

     for(BtLsnr l : listeners)
     {
      if(l.button.equals(title))
       l.act.doAction(title, p);
     }

     return;
    }
   }

  }

 }

 static class BtLsnr
 {
  String          button;
  ObjectAction act;
  boolean         selReq;

  public BtLsnr(String button, ObjectAction act, boolean selReq)
  {
   super();
   this.act = act;
   this.button = button;
   this.selReq = selReq;
  }
 }

}
