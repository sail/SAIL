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
import uk.ac.ebi.sail.client.ui.Action;

import com.gwtext.client.data.BooleanFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;

public class ClassifierList extends ClassifiableList<Classifier> implements DataChangeListener
{
 private static RecordDef recordDef = new RecordDef(new FieldDef[] { 
   new StringFieldDef("class"),
   new StringFieldDef("description"),
   new StringFieldDef("type"),
   new BooleanFieldDef("mandat"),
   new IntegerFieldDef("tags"),
   new ObjectFieldDef("obj")
   });

 private static ColumnModel columnModel = new ColumnModel( new ColumnConfig[]
                                                                             {
   new ColumnConfig("Classifier", "class", 130,true, null, "class"),
   new ColumnConfig("Description", "description", 130,true, null, "description"),
   new ColumnConfig("Type", "type", 130,true, null, "type"),
   new ColumnConfig("Mandatoty", "mandat", 65,true, null, "mandat"),
   new ColumnConfig("Tags", "tags", 50, true, null, "tags")
                                                                             }
   );

 private ClassifiableManager<Classifier> manager;
 
 public ClassifierList(Selection single, Action[] buttons, ClassifiableManager<Classifier> m )
 {
  super(single, buttons, recordDef, columnModel,"description", DataManager.getInstance().getClassifierManager(Classifier.Target.CLASSIFIER));
  
  setTitle("Classifiers list");
  getGrid().setAutoExpandColumn("description");
  manager=m;
  
  manager.addClassifiableChangeListener(this);
  
  loadData();
 }

 public void loadData()
 {
  getStore().removeAll();
  
  for(Classifier p : manager.getClassifiable())
  {
   Object[] data = new Object[6];

   data[0] = p.getName();
   data[1] = p.getDescription();
   data[2] = p.getTarget().getText();
   data[3] = p.isMandatory();
   data[4] = p.getTags()==null?0:p.getTags().size();
   data[5] = p;

   getStore().add(recordDef.createRecord(data));
  }
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
