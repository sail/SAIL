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
import uk.ac.ebi.sail.client.common.Qualifier;
import uk.ac.ebi.sail.client.common.Variant;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.layout.RowLayoutData;

public class QualifierEditPanel extends Panel
{

 private static RecordDef variRecordDef = new RecordDef(new FieldDef[] { 
   new StringFieldDef("vari"),
   new IntegerFieldDef("code"),
   new IntegerFieldDef("count"),
   new ObjectFieldDef("obj")
   });

 private static ColumnModel variColumnModel = new ColumnModel( new ColumnConfig[]
                                                                             {
   new ColumnConfig("Variant", "vari", 130,true, null, "vari"),
   new ColumnConfig("Coding", "code", 50, true, null, "code"),
   new ColumnConfig("Count", "count", 50, true, null, "count")
                                                                             }
   );

 
 private Store variStore = new Store( variRecordDef );
 final GridPanel variGrid;
 private Qualifier qualifier;
 
 private TextField nameField;
 private TextArea descriptionArea;
 private Checkbox mandatory;
 private Checkbox predefVariants;
 
 private BtnListener buttonLsnr;
  
 
 public QualifierEditPanel()
 {
  setLayout(new RowLayout());
  
  setFrame(true);
  setPaddings(5, 5, 5, 0);
  setWidth(400);
  setHeight(580);
  setAutoScroll( false );
  
  FormPanel fp = new FormPanel();
  fp.setAutoHeight(true);

  fp.add(nameField = new TextField("Name", "name", 300), new AnchorLayoutData("100%"));


  descriptionArea = new TextArea("Description");
  fp.add(descriptionArea, new AnchorLayoutData("100%"));
  
  mandatory=new Checkbox("Mandatory");
  fp.add(mandatory, new AnchorLayoutData("100%"));

  predefVariants=new Checkbox("Predefined variants only");
  fp.add(predefVariants, new AnchorLayoutData("100%"));
  
  
  variGrid = new GridPanel();
  variGrid.setTitle("Variants");
  variGrid.setColumnModel(variColumnModel);
  variGrid.setLayout( new FitLayout() );
  variGrid.setSelectionModel(new RowSelectionModel(true));
  variGrid.setStore(variStore);

  variGrid.setFrame(true);
  variGrid.setCollapsible(false);
  variGrid.setCollapsed(false);
  variGrid.setStripeRows(true);
  variGrid.setAutoExpandColumn("vari");
  //variGrid.setAutoHeight(true);
  //variGrid.setHeight("50%");
  variGrid.setAutoScroll(true);
  
  Button addButton = new Button("Add", new ButtonListenerAdapter()
  {
   public void onClick(Button button, EventObject e)
   {
    final VariantEditDialog dlg = VariantEditDialog.getInstance();
    
    Variant newVari = new Variant();
    
    int max=0;
    if( qualifier.getVariants() != null )
    {
     for( Variant ev : qualifier.getVariants() )
      if( ev.getCoding() > max && ev.isPredefined()  )
       max=ev.getCoding();
    }
    
    newVari.setCoding( max+1 );
    
    dlg.setVariant(newVari, qualifier.getVariants());
    
    dlg.setListener( new ObjectAction<Variant>() {

     public void doAction(String actName, Variant vr)
     {
      if( ! "ok".equals(actName) )
      {
       dlg.hide();
       return;
      }
      
      if( vr.getId() < 0 ) // It means really new variant. Otherwise variant was merged with non-predefined one.
      {
       qualifier.addVariant(vr);
      }
      else
      {
       for( Variant v : qualifier.getVariants() )
       {
        if( v.getId() == vr.getId() )
        {
         v.setPredefined(true);
         v.setDirty(true);
         v.setCoding(vr.getCoding());
         
         vr.setCount(v.getCount());
         break;
        }
       }
      }
      
      variStore.add(variRecordDef.createRecord(new Object[] { vr.getName(), vr.getCoding(), vr.getCount(), vr }));
      dlg.hide();
     }

     public void doMultyAction(String actName, List<Variant> lp)
     {}
    
    } ); 
    
    dlg.show();
    
   }
  });  

  Button removeButton = new Button("Remove", new ButtonListenerAdapter()
  {
   public void onClick(Button button, EventObject e)
   {
    Record[] selr = variGrid.getSelectionModel().getSelections();
    
    if( selr != null && selr.length == 1 )
    {
     if( selr[0].getAsInteger("count") > 0 )
     {
      ErrorBox.showError("There are records ("+selr[0].getAsInteger("count")+") that are annotated by this variant. The variant can't be deleted");
      return;
     }
     
     variStore.remove(selr[0]);
     qualifier.removeVariant((Variant)(selr[0].getAsObject("obj")));
    }

   }
  });  
  
  Button editButton = new Button("Edit", new ButtonListenerAdapter()
  {
   public void onClick(Button button, EventObject e)
   {
    final Record[] selr = variGrid.getSelectionModel().getSelections();
    
    if( selr != null && selr.length == 1)
    {
     final VariantEditDialog dlg = VariantEditDialog.getInstance();
     
     dlg.setVariant((Variant)selr[0].getAsObject("obj"), qualifier.getVariants());
     
     dlg.setListener( new ObjectAction<Variant>() {

      public void doAction(String actName, Variant vr)
      {
       if( ! "ok".equals(actName) )
       {
        dlg.hide();
        return;
       }
       
       vr.setDirty(true);
       dlg.hide();
       
       selr[0].set("vari", vr.getName());
       selr[0].set("code", vr.getCoding());
      }

      public void doMultyAction(String actName, List<Variant> lp)
      {}
     
     } ); 
     
     dlg.show();
     
    }

   }
  }); 
  
  variGrid.addButton( addButton );
  variGrid.addButton( editButton );
  variGrid.addButton( removeButton );
  
  add(fp);
  add(variGrid, new RowLayoutData("50%"));


  Button okButton = new Button("Save");
  okButton.setStateId("ok");
  buttonLsnr = new BtnListener();
  okButton.addListener(buttonLsnr);
  
  addButton( okButton );
  
  okButton= new Button("Cancel",buttonLsnr);
  okButton.setStateId("cancel");
  addButton( okButton );
  
 }
 
 private boolean validate()
 {
  String[] errs = new String[5];
  int en=0;
 
  if( qualifier == null )
   qualifier=new Qualifier();
  
   
  String str=nameField.getValueAsString();
  
  if( str == null || str.length() == 0 )
   errs[en++]="Name can't be empty";
  
  qualifier.setName(str);

  
  if( en != 0 )
  {
   String errMsg = "There are errors: <br /><ul>";
   
   for( int i=0; i < en; i++ )
    errMsg+="<li>"+errs[i]+"</li>";
   
   errMsg+="</ul>";
   
   ErrorBox.showError(errMsg);

   return false;
  }
  
  qualifier.setDescription(descriptionArea.getValueAsString());
  qualifier.setPredefined(predefVariants.getValue());
  qualifier.setMandatory(mandatory.getValue());
  

//  qualifier.clearVariants();
//   
//  Record[] rds = variStore.getRecords();
//
//   if( rds != null && rds.length != 0 )
//   {
//    for( Record r : rds )
//    {
//     qualifier.addVariant( new Variant( r.getAsString( "vari" ), 1, true ) ); //TODO variant coding handling
//    }
//   }

  return true;
 }
 
 
 public void setEditPanelListener( ObjectAction<Qualifier> l )
 {
  buttonLsnr.setListener(l);
 }
 
// public Qualifier getQualifier()
// {
//  return qualifier;
// }

 public void setQualifier( Qualifier v )
 {
  if( v == null )
   qualifier = new Qualifier();
  else
   qualifier=v;
  
  nameField.setValue(qualifier.getName());
  descriptionArea.setValue( qualifier.getDescription() );
  
  variStore.removeAll();

  if(qualifier.getVariants() != null)
  {
   for(Variant vr : qualifier.getVariants())
   {
    if( vr.isPredefined() )
     variStore.add(variRecordDef.createRecord(new Object[] { vr.getName(), vr.getCoding(), vr.getCount(), vr }));
   }
  }
  
  predefVariants.setChecked(qualifier.isPredefined());
  mandatory.setChecked(qualifier.isMandatory());
  
 }
 
 class BtnListener extends ButtonListenerAdapter
 {
  private ObjectAction<Qualifier> lsnr;
  
  public BtnListener()
  {
   super();
  }
  
  public void setListener( ObjectAction<Qualifier> l )
  {
   lsnr=l;
  }

  public void onClick(Button button, EventObject e)
  {
   String state = button.getStateId();
   
   if( lsnr != null )
   {
    if( "ok".equals(state) )
    {
     if( validate() )
      lsnr.doAction( state, qualifier );
    }
    else
     lsnr.doAction( state, null );
   }
  }
 }
 
}
