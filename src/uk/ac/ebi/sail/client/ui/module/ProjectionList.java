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
import uk.ac.ebi.sail.client.ProjectionManager;
import uk.ac.ebi.sail.client.common.Projection;
import uk.ac.ebi.sail.client.ui.Action;

import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;

public class ProjectionList extends ObjectList<Projection> implements DataChangeListener
{
 private static RecordDef recordDef = new RecordDef(new FieldDef[] { 
   new StringFieldDef("name"),
   new StringFieldDef("classifiers"),
   new ObjectFieldDef("obj")
   });

 private static ColumnModel columnModel = new ColumnModel( new ColumnConfig[]
                                                                             {
   new ColumnConfig("Projection", "name", 130,true, null, "name"),
   new ColumnConfig("Classifiers", "classifiers", 130,true, null, "classifiers")
                                                                             }
   );

 private ProjectionManager manager;
 
 public ProjectionList(Selection single, Action[] buttons, ProjectionManager m )
 {
  super(single, buttons, recordDef, columnModel,"classifiers" );
  
  setTitle("Classifiers list");
  getGrid().setAutoExpandColumn("classifiers");
  manager=m;
  
  manager.addDataChangeListener(this);
  
  loadData();
 }

 public void loadData()
 {
  getStore().removeAll();
  
  for(Projection p : manager.getProjections())
  {
   Object[] data = new Object[3];

   data[0] = p.getName();
   data[1] = p.getClassifiersString();
   data[2] = p;

   getStore().add(recordDef.createRecord(data));
  }
 }

 public void dataChanged()
 {
  loadData();
 }

 public void destroy()
 {
  manager.removeDataChangeListener( this );

  super.destroy();
 }
 
}
