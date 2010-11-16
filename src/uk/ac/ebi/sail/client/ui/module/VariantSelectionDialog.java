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

import java.util.Collection;

import uk.ac.ebi.sail.client.EnumerationSelectListener;
import uk.ac.ebi.sail.client.FilteredEnumeration;
import uk.ac.ebi.sail.client.common.Qualifier;
import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.client.common.Variant;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

public class VariantSelectionDialog extends Window
{
 private FilteredEnumeration en;
 private GridPanel grid;
 private Store store;
 private EnumerationSelectListener lsnr = null;
 
 private static RecordDef         recordDef = new RecordDef(new FieldDef[] {
   new IntegerFieldDef("id"),
   new StringFieldDef("variant"),
   new IntegerFieldDef("records"),
   new ObjectFieldDef("enumObj") });

 
 public VariantSelectionDialog()
 {
  super("Select variants");
  
  setLayout(new BorderLayout()); 
  setModal( true );
  setWidth(600);  
  setHeight(225);
//  setPlain(true);  
  
  BorderLayoutData centerData = new BorderLayoutData(RegionPosition.CENTER);  
  centerData.setMargins(3, 0, 3, 3); 
  
  CheckboxSelectionModel selModel=new CheckboxSelectionModel();
  grid = new GridPanel();
  grid.setSelectionModel(selModel);


  BaseColumnConfig[] columns = new BaseColumnConfig[] {
    new CheckboxColumnConfig(selModel),
    new ColumnConfig("ID", "id", 40),
    new ColumnConfig("Variant", "variant", 160, true, null, "variant"),
    new ColumnConfig("Records", "records", 55)
  };

  ColumnModel columnModel = new ColumnModel(columns);
  grid.setColumnModel(columnModel);

  grid.setFrame(false);
  grid.setStripeRows(true);
  grid.setAutoExpandColumn("variant");

  grid.setHeight("100%");
  grid.setWidth("100%");

  store = new Store(recordDef);
  grid.setStore(store);

  add( grid, centerData );
  
  SelectLsnr lsnr = new SelectLsnr();
  Button bt = new Button("OK");
  bt.setStateId("ok");
  bt.addListener( lsnr );

  addButton( bt );
  

  bt = new Button("Cancel");
  bt.setStateId("cancel");
  bt.addListener( lsnr );
  addButton( bt );
 }

 public void setEnumerationSelectListener( EnumerationSelectListener lsnr )
 {
  this.lsnr=lsnr;
 }
 
 public void setEnumeration(FilteredEnumeration fc)
 {
  Collection<Variant> varis = null;
  
  if( fc.getEnumeration() instanceof Variable )
  {
   varis =((Variable)fc.getEnumeration()).getVariants();
  }
  else
   varis =((Qualifier)fc.getEnumeration()).getVariants();
  
  if( varis == null )
   return;
  
  for( Variant vr : varis )
  {
   Record rec = recordDef.createRecord( new Object[]{vr.getId(),vr.getName(),vr.getCount(),vr} );
   store.add(rec);
  }
   
  en=fc;
 }
 
 private class SelectLsnr extends ButtonListenerAdapter
 {
  public void onClick(Button button, EventObject e)
  {
   String state = button.getStateId();
   close();
   
   if( "cancel".equals(state) )
   {
    return;
   }
   
   Record[] sels = grid.getSelectionModel().getSelections();
   
   if( sels == null || sels.length == 0 )
    return;
   
   int [] vIDs = new int[sels.length];
   
   int i=0;
   for( Record r : sels )
   {
    vIDs[i++]=r.getAsInteger("id");
   }
   en.setVariants(vIDs);
   
   if( lsnr != null )
    lsnr.filteredEnumerationSelected(en);
  }
 }
}
