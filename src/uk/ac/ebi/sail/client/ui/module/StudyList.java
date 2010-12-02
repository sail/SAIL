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
import uk.ac.ebi.sail.client.StudyManager;
import uk.ac.ebi.sail.client.common.Study;
import uk.ac.ebi.sail.client.ui.Action;

import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;


public class StudyList extends ObjectList<Study> implements DataChangeListener
{
 private static RecordDef recordDef = new RecordDef(new FieldDef[] { 
   new StringFieldDef("name"),
   new IntegerFieldDef("colrecs"),
   new IntegerFieldDef("elirecs"),
   new IntegerFieldDef("selrecs"),
   new ObjectFieldDef("obj")
   });

 private static ColumnModel columnModel = new ColumnModel( new ColumnConfig[]
                                                                             {
   new ColumnConfig("Study", "name", 130,true, null, "name"),
   new ColumnConfig("Total Samples", "colrecs", 160,true, null, "colrecs"),
   new ColumnConfig("Eligible Samples", "elirecs", 160,true, null, "elirecs"),
   new ColumnConfig("Selected Samples", "selrecs", 160,true, null, "selrecs"),
                                                                             }
   );

 private StudyManager manager;

 public StudyList(Selection single, Action[] buttons, StudyManager mngr)
 {
  super(single, buttons, recordDef, columnModel, "name" );
  
  setTitle("Study list");
  getGrid().setAutoExpandColumn("name");
  manager=mngr;
  
  manager.addDataChangeListener(this);
  
  loadData();
 }

 public void loadData()
 {
  getStore().removeAll();
  
  if( manager.getStudies() == null )
   return;
  
  for(Study p : manager.getStudies())
  {
   Object[] data = new Object[5];

   data[0] = p.getName();
   data[1] = p.getCollectionsSamples();
   data[2] = p.getEligibleSamples();
   data[3] = p.getSelectedSamples();
   data[4] = p;

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
 
// public int[] getCollections()
// {
//  Collection<SampleCollection> rs =  getSelection();
//  
//  if( rs == null )
//   return null;
//  
//  int[] rIds = new int[ rs.size() ];
//  
//  int i=0;
//  for( SampleCollection r  :rs )
//   rIds[i++]=r.getId();
//  
//  
//  return rIds;
// }
}
