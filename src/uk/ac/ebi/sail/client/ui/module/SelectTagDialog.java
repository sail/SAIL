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

import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.Tag;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListener;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.layout.FitLayout;

public class SelectTagDialog extends Window
{
 private static RecordDef recordDef = new RecordDef(new FieldDef[] { 
   new StringFieldDef("tag"),
   new StringFieldDef("desc"),
   new IntegerFieldDef("recs"),
   new ObjectFieldDef("obj")
   });

 private static ColumnModel columnModel = new ColumnModel( new ColumnConfig[]
                                                                             {
   new ColumnConfig("Tag", "tag", 130,true, null, "tag"),
   new ColumnConfig("Description", "desc", 130,true, null, "desc"),
   new ColumnConfig("Records", "recs", 50, true, null, "recs")
                                                                             }
   );
 
 private static SelectTagDialog instance; 
 
 private Store store;
 private Tag selected;
 
 private ObjectAction<Tag> listener; 
 
 public SelectTagDialog()
 {
  setLayout( new FitLayout() );
  setTitle("Select Tag");
  setFrame(true);
  
  setSize(400,300);
  setCloseAction(Window.HIDE);
  setModal(true);
  setClosable(true);
  setPlain(true);
  
  final GridPanel grid = new GridPanel();
  store = new Store(recordDef);
  grid.setStore(store);
  grid.setColumnModel(columnModel);
  grid.setAutoExpandColumn("desc");
  grid.setAutoScroll(true);

  add(grid);
  
  final Button selBt = new Button("Select");

  ButtonListener btLsnr = new ButtonListenerAdapter()
  {


   public void onClick(Button button, EventObject e)
   {
    if( listener != null )
    {
     if( button != selBt )
     {
      listener.doAction("cancel", null);
      return;
     }
     
     Record chck = grid.getSelectionModel().getSelected();
     
     if( chck == null )
      return;
     
     listener.doAction( "ok", (Tag) chck.getAsObject("obj") );
    }
   }

  };
  
  selBt.addListener(btLsnr);
  
  addButton( selBt );
  addButton( new Button("Cancel",btLsnr) );

 }

 public Tag getSelected()
 {
  return selected;
 }
 
 public void setClassifier( Classifier cl )
 {
  store.removeAll();
  
  if( cl.getTags() == null )
   return;
  
  for( Tag t : cl.getTags() )
  {
   Record r = recordDef.createRecord( new Object[]{ t.getName(), t.getDescription(), t.getCount(), t } );
   store.add(r);
  }
 }
 
 public void setObjectActionListener( ObjectAction<Tag> ls )
 {
  listener=ls;
 }
 
 public static SelectTagDialog getDialog()
 {
  if( instance == null )
   instance = new SelectTagDialog();
  
  return instance;
 }
}
