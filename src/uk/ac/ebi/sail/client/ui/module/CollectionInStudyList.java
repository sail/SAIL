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

import java.util.List;

import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.Study;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.layout.FitLayout;

public class CollectionInStudyList extends GridPanel
{
 private static RecordDef recordDef = new RecordDef(new FieldDef[] { 
   new StringFieldDef("name"),
   new ObjectFieldDef("recs"),
   new ObjectFieldDef("obj")
   });

 private static ColumnModel columnModel = new ColumnModel( new ColumnConfig[]
                                                                             {
   new ColumnConfig("Collection", "name", 130,true, null, "name"),
   new ColumnConfig("Records", "recs", 130,true, null, "recs"),
                                                                             }
   );
 
 private Study study;
 private Store store;

 public CollectionInStudyList()
 {
  setTitle("Collection List");
  setLayout(new FitLayout());
  setSelectionModel(new RowSelectionModel(false));

  setColumnModel(columnModel);

  setFrame(true);
  setCollapsible(true);
  setStripeRows(true);
  setAutoExpandColumn("name");
  setAutoHeight(true);

  BtListener btLs = new BtListener();
  
  Button bt = new Button("Add", btLs);
  bt.setStateId("add");
  
  addButton(bt);

  bt = new Button("Remove", btLs);
  bt.setStateId("remove");
  addButton(bt);

  store = new Store(recordDef);
  setStore(store);
 }
 
 public void setStudy( Study st )
 {
  study = st;
  
  if( st.getCollections() == null )
   return;
  
  for( SampleCollection ct : st.getCollections() )
  {   
   Object[] data = new Object[3];

   data[0] = ct.getName();
   data[1] = ct.getSampleCount();
   data[2] = ct;

   getStore().add(recordDef.createRecord(data));     
  }
 }
 
 public class BtListener extends ButtonListenerAdapter
 {
  public void onClick(Button bt, EventObject eo )
  {
   String state=bt.getStateId();
   
   if( "add".equals(state) )
   {
    AddCollectionDialog dlg = new AddCollectionDialog();
    
    dlg.setActionListener(new ObjectAction<SampleCollection>()
    {

     @Override
     public void doAction(String actName, SampleCollection ct)
     {
      for( Record r : getStore().getRecords() )
      {
       if( ct == r.getAsObject("obj") )
        return;
      }
      
      Object[] data = new Object[3];

      data[0] = ct.getName();
      data[1] = ct.getSampleCount();
      data[2] = ct;

      getStore().add(recordDef.createRecord(data));
      study.addCollection(ct);
     }

     @Override
     public void doMultyAction(String actName, List<SampleCollection> lp)
     {
      cyc: for( SampleCollection ct : lp )
      {
       for( Record r : getStore().getRecords() )
       {
        if( ct == r.getAsObject("obj") )
         continue cyc;
       }
       
       Object[] data = new Object[3];

       data[0] = ct.getName();
       data[1] = ct.getSampleCount();
       data[2] = ct;

       getStore().add(recordDef.createRecord(data));     
       study.addCollection(ct);
      }
     }
    });
    
    dlg.show();
   }
   else if( "remove".equals(state) )
   {
    Record[] sel = getSelectionModel().getSelections();
    
    for( Record r : sel )
    {
     study.removeCollection((SampleCollection)r.getAsObject("obj"));
     getStore().remove(r);
    }
   }
   
  }
 }
}
