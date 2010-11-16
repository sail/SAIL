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
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterPart;
import uk.ac.ebi.sail.client.common.Qualifier;
import uk.ac.ebi.sail.client.common.Variable;

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
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

public class SelectEnumerationDialog extends Window
{
 private GridPanel grid;
 private Store store;
 private EnumerationSelectListener lsnr = null;
 private Parameter param;
 
 private static RecordDef         recordDef = new RecordDef(new FieldDef[] {
   new IntegerFieldDef("id"),
   new StringFieldDef("param"),
   new StringFieldDef("enum"),
   new IntegerFieldDef("records"),
   new ObjectFieldDef("enumObj") });
 
 public SelectEnumerationDialog()
 {
  super("Select enumeration");
  
  setLayout(new BorderLayout()); 
  setModal( true );
  setWidth(600);  
  setHeight(225);
//  setPlain(true);  
  
  BorderLayoutData centerData = new BorderLayoutData(RegionPosition.CENTER);  
  centerData.setMargins(3, 0, 3, 3); 
  
  grid = new GridPanel();
  grid.setSelectionModel(new RowSelectionModel(true));


  ColumnConfig[] columns = new ColumnConfig[] { 
    new ColumnConfig("ID", "id", 40),
    new ColumnConfig("Parameter", "param", 160, true, null, "param"),
    new ColumnConfig("Name", "enum", 160, true, null, "enum"),
    new ColumnConfig("Records", "records", 55)
  };

  ColumnModel columnModel = new ColumnModel(columns);
  grid.setColumnModel(columnModel);

  grid.setFrame(false);
  grid.setStripeRows(true);
  grid.setAutoExpandColumn("enum");

  grid.setHeight("100%");
  grid.setWidth("100%");

  store = new Store(recordDef);
  grid.setStore(store);

  add( grid, centerData );
  
  Button bt = new Button("Select");
  bt.addListener( new SelectLsnr() );
//  bt.addListener( new ButtonListenerAdapter() 
//  {
//   public void onClick(Button button, EventObject e)
//   {
//    Record selr = grid.getSelectionModel().getSelected();
//
////  System.out.println(((Qualifier)selr.getAsObject("qualifierObj")).getName());
//  
////  if( selr == null )
////   return;
//  
//  FilteredEnumaration fc = new FilteredEnumaration( (Qualifier)selr.getAsObject("qualifierObj"), null);
//  for( EnumerationSelectListener lnr : lsnr )
//  {
//   lnr.enumarationSelected(fc);
//  }
//  
//  System.out.println("Hide");
//  hide();
//  
//   } 
//  } );
  addButton( bt );
  
  bt = new Button("Select with filter");
  bt.addListener(new VariantFilterListener());
  addButton( bt );

  bt = new Button("Cancel");
  bt.addListener( new ButtonListenerAdapter() 
  {
   public void onClick(Button button, EventObject e)
   {
    close();
   } 
  } );
  addButton( bt );
 }

 public void setParameter(Parameter p)
 {
//  System.out.println("Set parameter: "+p.getName());
  param = p;
  
  
  if( p.countEnumerations() == 0 )
   return;
  
  store.removeAll();
  
  Collection<Variable> vars = p.getAllVariables(); 
  if( vars != null )
  {
   for( Variable v : vars )
    if( v.getType() == Variable.Type.ENUM )
    {
     Record rec = recordDef.createRecord( new Object[]{v.getId(),p==v.getParameter()?"this":v.getParameter().getName(),v.getName(),p.getRecordsCount(),v} );
     store.add(rec);
    }
  }
  
  Collection<Qualifier> quals = p.getAllQualifiers(); 
  if( quals != null )
  {
   for( Qualifier q : quals )
   {
    Record rec = recordDef.createRecord( new Object[]{q.getId(),p==q.getParameter()?"this":q.getParameter().getName(),q.getName(),p.getRecordsCount(),q} );
    store.add(rec);
   }
  }
  

 }
 
 public void show( )
 {
  super.show();
  grid.getSelectionModel().selectFirstRow();
 }
 
 public void setEnumerationSelectListener( EnumerationSelectListener lnr )
 {
  lsnr=lnr;
 }
 

 class SelectLsnr extends ButtonListenerAdapter
 {
  public void onClick(Button button, EventObject e)
  {
   Record selr = grid.getSelectionModel().getSelected();

//   System.out.println(selr.getAsObject("enumObj").getClass().getName());
   
//   System.out.println(((Qualifier)selr.getAsObject("enumObj")).getName());
   
   if( selr == null )
    return;

//   System.out.println("Selected:" +selr.getAsString("enum"));
  
   FilteredEnumeration fc = new FilteredEnumeration( param, (ParameterPart)selr.getAsObject("enumObj"), null);
   
   if( lsnr != null )
   {
    try
    {
     lsnr.filteredEnumerationSelected(fc);
    }
    catch(Exception e2)
    {
     System.out.println("Error in listener: "+e2);
     e2.printStackTrace();
    }
   }
   
   close();
   
  } 
 }
 
 class VariantFilterListener extends ButtonListenerAdapter
 {
  public void onClick(Button button, EventObject e)
  {
   Record selr = grid.getSelectionModel().getSelected();

   if( selr == null )
    return;
   
   System.out.println(selr.getAsObject("enumObj").getClass().getName());
   
   FilteredEnumeration fc = new FilteredEnumeration( param, (ParameterPart)selr.getAsObject("enumObj"), null);

   VariantSelectionDialog vsd = new VariantSelectionDialog();
   vsd.setEnumeration( fc );
   vsd.setEnumerationSelectListener(lsnr);
   
   vsd.show();
   
   close();
   
  } 
 }

 public static SelectEnumerationDialog getInstance()
 {
  return new SelectEnumerationDialog();
 }

}
