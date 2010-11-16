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

import uk.ac.ebi.sail.client.DataChangeListener;
import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.common.ClassifiableManager;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.Timer;
import uk.ac.ebi.sail.client.ui.Action;

import com.gwtext.client.core.TextAlign;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;

public class ParameterList extends ClassifiableList<Parameter> implements DataChangeListener
{
 private static FieldDef[] fieldDef = new FieldDef[] { 
  new IntegerFieldDef("id"),
  new StringFieldDef("code"),
  new StringFieldDef("name"),
  new StringFieldDef("description"),
  new IntegerFieldDef("recs"),
  new IntegerFieldDef("v"),
  new IntegerFieldDef("e"),
  new ObjectFieldDef("obj")
  };
 
 private static RecordDef recordDef = new RecordDef( fieldDef );

 private static ColumnModel columnModel;
 
 static
 {
  ColumnConfig[] columns = new ColumnConfig[6];

  int k = 0;
  ColumnConfig cc = new ColumnConfig("Code", "code", 100);
  cc.setId("code");
  cc.setAlign(TextAlign.LEFT);
  cc.setSortable(true);

  columns[k++] = cc;

  cc = new ColumnConfig("Name", "name");
  cc.setId("parameter");
  cc.setWidth(150);
  cc.setAlign(TextAlign.LEFT);
  cc.setSortable(true);

  columns[k++] = cc;

  cc = new ColumnConfig("Description", "description");
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

 private ClassifiableManager<Parameter> manager;

 public ParameterList(Selection single, Action[] buttons, ClassifiableManager<Parameter> m )
 {
  super(single, buttons, recordDef, columnModel,"desc",
    DataManager.getInstance().getClassifierManager( Classifier.Target.PARAMETER));
  
  setTitle("Parameters list");
  getGrid().setAutoExpandColumn("description");
  manager=m;
  
  manager.addClassifiableChangeListener( this );
  
  loadData();
 }

 public void loadData()
 {
  getStore().removeAll();

  Timer.reportEvent("Adding parameters into grid. start. ");

  Record[] recs = new Record[ manager.getClassifiable().size() ];
  
  int i=0;
  for(Parameter p : manager.getClassifiable())
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

   recs[i++]=recordDef.createRecord(data);
  }
  
  setData(recs);

  Timer.reportEvent("Adding parameters into grid. stop. ");

//  FieldDef[] fl = new FieldDef[] { 
//    new IntegerFieldDef("id"),
//    new StringFieldDef("code"),
//    new StringFieldDef("name"),
//    new StringFieldDef("desc"),
//    new IntegerFieldDef("recs"),
//    new IntegerFieldDef("v"),
//    new IntegerFieldDef("e"),
//    new ObjectFieldDef("obj")
//    };
  
//  Timer.reportEvent("Filling store. start. ");
//
//  Object[][] array = new Object[manager.getClassifiable().size()][];
//  
//  int i=0;
//  for(Parameter p : manager.getClassifiable())
//  {
//   Object[] data = new Object[8];
//
//   data[0] = p.getId();
//   data[1] = p.getCode();
//   data[2] = p.getName();
//   data[3] = p.getDescription();
//   data[4] = p.getRecordsCount();
//   data[5] = new Integer(p.countVariables());
//   data[6] = new Integer(p.countEnumerations());
//   data[7] = p;
//
//   array[i++]=data;
//  }
//  Timer.reportEvent("Filling store. cp1. ");
//
//  SimpleStore store = new SimpleStore(0,fieldDef,array);
//  Timer.reportEvent("Filling store. cp2. ");
// 
//  setStore(store);
//  Timer.reportEvent("Filling store. stop. ");
 }

 public void dataChanged()
 {
  loadData();
 }
 
 public void destroy()
 {
  manager.removeClassifiableChangeListener( this );

  super.destroy();
 }




}
