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

import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.common.ClassifiableManager;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.common.Classifier.Target;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.layout.FitLayout;

public class SelectRelationsDialog // extends Window
{
 private static CheckboxSelectionModel sMod = new CheckboxSelectionModel();

 private static RecordDef         recordDef = new RecordDef(new FieldDef[] {
   new StringFieldDef("class"),
   new StringFieldDef("tag"),
   new ObjectFieldDef("obj") });

 private static ColumnModel columnModel = new ColumnModel(
   new BaseColumnConfig[] { 
     new CheckboxColumnConfig(sMod),
     new ColumnConfig("Classifier", "class", 200, true, null, "class"),
     new ColumnConfig("Relation", "tag", 200, true, null, "tag")
   }
   );
 
 
 private static SelectRelationsDialog instance;
 
 private List<Integer> relations;
 private Window window;
 private GridPanel grid;
 
 public static SelectRelationsDialog getInstance()
 {
  if( instance == null )
   instance = new SelectRelationsDialog();
  
  return instance;
 }

 private void init()
 {
  if( window == null || ! window.isRendered() )
  {
   window = new Window("Select relations");
   window.setSize(480,400);
   window.setModal( true );
   
   OkCancelListener lsnr = new OkCancelListener();
   
   Button bt = new Button("OK");
   bt.setStateId("ok");
   bt.addListener(lsnr);
   
   window.addButton( bt );
   
   bt = new Button("Cancel");
   bt.setStateId("cancel");
   bt.addListener(lsnr);
   
   window.addButton( bt );
   
   window.setLayout( new FitLayout() );
   
   grid = new GridPanel();
   grid.setAutoScroll(true);
   grid.setStripeRows(true);

   Store store = new Store(recordDef);
   grid.setStore( store );
   
   grid.setColumnModel( columnModel );
   
   grid.setSelectionModel(sMod);
   
   ClassifiableManager<Classifier> mngr = DataManager.getInstance().getClassifierManager(Target.RELATION);
   
   for(Classifier cl : mngr.getClassifiable() )
   {
    for( Tag t : cl.getTags() )
    {
     store.add(recordDef.createRecord(new Object[]{cl.getName(),t.getName(),t}));
    }
   }
   
   window.add(grid);
   
   grid.addListener(new PanelListenerAdapter(){


    public void onRender(Component component)
    {
     if( relations == null || relations.size() == 0 )
      return;
     
     grid.getSelectionModel().clearSelections();
     
     int i=0;
     for( Record r : grid.getStore().getRecords() )
     {
      if( relations.contains(((Tag)r.getAsObject("obj")).getId()) )
       grid.getSelectionModel().selectRow(i, true);
//      else
//       grid.getSelectionModel().deselectRow(i);
      
      i++;
     }
     
    }
   });
  }
  else
   System.out.println("Rendered: "+(window.isRendered()?"yes":"no"));
  
  
 }

 public void show()
 {
  instance.init();
  window.show();
 }

 private class OkCancelListener extends ButtonListenerAdapter
 {
  @Override
  public void onClick(Button button, EventObject e)
  {
   if( "ok".equals(button.getStateId()))
   {
    Record[] recs = grid.getSelectionModel().getSelections();
    
    relations = new ArrayList<Integer>(recs.length);
    
    for( Record r: recs )
    {
     System.out.println("Selected : "+r.getAsString("tag"));
     relations.add(((Tag)r.getAsObject("obj")).getId());
    }
   }
   
   
   window.close();
   window=null;
  }
 }

 public List<Integer> getRelations()
 {
  return relations;
 }
 
}
