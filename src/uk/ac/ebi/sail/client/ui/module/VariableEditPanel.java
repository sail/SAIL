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
import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.client.common.Variant;
import uk.ac.ebi.sail.client.common.Variable.Type;
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
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.layout.RowLayoutData;

public class VariableEditPanel extends Panel
{

 private static RecordDef typeRecordDef = new RecordDef(new FieldDef[] { 
   new StringFieldDef("type") });

 private static RecordDef variRecordDef = new RecordDef(new FieldDef[] { 
   new StringFieldDef("vari"),
   new IntegerFieldDef("coding"),
   new IntegerFieldDef("count"),
   new ObjectFieldDef("obj")
   });

 private static ColumnModel variColumnModel = new ColumnModel( new ColumnConfig[]
                                                                             {
   new ColumnConfig("Variant", "vari", 130,true, null, "vari"),
   new ColumnConfig("Coding", "coding", 50, true, null, "coding"),
   new ColumnConfig("Count", "count", 50, true, null, "count")
                                                                             }
   );

 
 private Store variStore = new Store( variRecordDef );
 final GridPanel variGrid;
 private Variable var;
 
 private TextField nameField;
 private ComboBox type;
 private TextArea descriptionArea;
 private Checkbox predefVariants;
 
 private BtnListener buttonLsnr;
  
 public VariableEditPanel()
 {
  setLayout(new RowLayout());
  
  setFrame(true);
  setPaddings(5, 5, 5, 0);
  setWidth(400);
  setHeight(480);
  setAutoScroll( false );
  
  FormPanel fp = new FormPanel();
  fp.setAutoHeight(true);

  fp.add(nameField = new TextField("Name", "name", 300), new AnchorLayoutData("100%"));

  Store store = new Store(typeRecordDef);
  

  type = new ComboBox();
  type.setFieldLabel("Type");
  type.setHiddenName("type");
  type.setStore(store);
  type.setDisplayField("type");
  type.setTypeAhead(true);
  type.setMode(ComboBox.LOCAL);
  type.setTriggerAction(ComboBox.ALL);
  type.setEmptyText("Select a type...");
  type.setSelectOnFocus(true);
  type.setWidth(190);

  for( Variable.Type t : Variable.Type.values() )
  {
   store.add( typeRecordDef.createRecord(new Object[]{t.name()}));
  }
  
  fp.add(type, new AnchorLayoutData("100%"));
  
  
  descriptionArea = new TextArea("Description");
  fp.add(descriptionArea, new AnchorLayoutData("100%"));
  
  predefVariants=new Checkbox("Predefined variants only");
  predefVariants.setDisabled(true);
  fp.add(predefVariants, new AnchorLayoutData("100%"));
  
  
  variGrid = new GridPanel();
  variGrid.setTitle("Variants");
  variGrid.setColumnModel(variColumnModel);
  variGrid.setLayout( new FitLayout() );
  variGrid.setSelectionModel(new RowSelectionModel(true));
  variGrid.setStore(variStore);

  variGrid.setFrame(true);
  variGrid.setCollapsible(false);
  variGrid.setCollapsed(true);
  variGrid.setStripeRows(true);
  variGrid.setAutoExpandColumn("vari");
  //variGrid.setAutoHeight(true);
  //variGrid.setHeight("50%");
  variGrid.setAutoScroll(true);
 
  /*
  Button addButton = new Button("Add", new ButtonListenerAdapter()
  {
   public void onClick(Button button, EventObject e)
   {
    MessageBox.prompt("Variant", "Enter variant in form: name[=code]", new MessageBox.PromptCallback()
    {
     public void execute(String btnID, String text)
     {
      if( text == null || text.length() == 0 )
      {
       if( ! "cancel".equals( btnID ) )
        ErrorBox.showError("Variant can't be empty");
       
       return;
      }
      
      String vrStr=null;
      int vrEnc=-1;
      
      int eqPos = text.indexOf('=');
      if( eqPos != -1 )
      {
       vrStr = text.substring(0,eqPos);
       
       try
       {
        vrEnc = Integer.parseInt(text.substring(eqPos+1));
       }
       catch( Exception e )
       {
        ErrorBox.showError("Invalid variant string");
        return;
       }
       
      }
      else
       vrStr=text;
      
      int maxEnc = 0;
      for(Record r : variStore.getRecords() )
      {
       Variant v = (Variant)r.getAsObject("obj");
       
       if( vrStr.equals(v.getName()) )
       {
        ErrorBox.showError("Variant <b>'"+text+"'</b> already exists");
        return;
       }

       if( vrEnc != -1 && vrEnc == v.getCoding() )
       {
        ErrorBox.showError("Variant coding <b>'"+vrEnc+"'</b>  is already used by another variant");
        return;
       }
       
       if( v.getCoding() > maxEnc)
        maxEnc=v.getCoding();
      }
      
      if( vrEnc == -1)
       vrEnc=maxEnc+1;
      
      Variant nv = new Variant();
      
      nv.setCoding(vrEnc);
      nv.setName(vrStr);
      
      if( "ok".equals(btnID) )
       variStore.add(variRecordDef.createRecord(new Object[] { vrStr, vrEnc, 0, nv }));
     }
    });
   }
  });  

  Button removeButton = new Button("Remove", new ButtonListenerAdapter()
  {
   public void onClick(Button button, EventObject e)
   {
    Record[] selr = variGrid.getSelectionModel().getSelections();
    
    if( selr != null )
    {
     if( selr[0].getAsInteger("count") > 0 )
     {
      ErrorBox.showError("Variant with non-zero count can not be deleted");
      return;
     }
     
     variStore.remove(selr[0]);
    }

   }
  });  

  variGrid.addButton( addButton );
  variGrid.addButton( removeButton );
  */
  Button addButton = new Button("Add", new ButtonListenerAdapter()
  {
   public void onClick(Button button, EventObject e)
   {
    final VariantEditDialog dlg = VariantEditDialog.getInstance();
    
    Variant newVari = new Variant();
    
    int max=0;
    if( var.getVariants() != null )
    {
     for( Variant ev : var.getVariants() )
      if( ev.getCoding() > max && ev.isPredefined() )
       max=ev.getCoding();
    }
    
    newVari.setCoding( max+1 );
    
    dlg.setVariant(newVari, var.getVariants());
    
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
       var.addVariant(vr);
      }
      else
      {
       for( Variant v : var.getVariants() )
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
     var.removeVariant((Variant)(selr[0].getAsObject("obj")));
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
     
     dlg.setVariant((Variant)selr[0].getAsObject("obj"), var.getVariants());
     
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
       selr[0].set("coding", vr.getCoding());
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

  type.addListener( new ComboBoxListenerAdapter(){
   public void onSelect(ComboBox comboBox, Record record, int index)
   {
//    boolean was = variGrid.isCollapsed();
    boolean is = ! Variable.Type.ENUM.name().equals( record.getAsString("type") );
    variGrid.setCollapsed( is );
    predefVariants.setDisabled(is);
    
//    if( is != was )
//     variStore.removeAll();
   }
  } );
  
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
 
  if( var == null )
   var=new Variable();
  
   
  String str=nameField.getValueAsString();
  
  if( str == null || str.length() == 0 )
   errs[en++]="Name can't be empty";
  
  var.setName(str);

  Type t = null;
  try
  { 
   t = Type.valueOf(type.getValue());
  }
  catch (Exception e) 
  {
  }
  
  if( t == null )
   errs[en++]="Type must be selected";
  
  
  if( en != 0 )
  {
   String errMsg = "There are errors: <br /><ul>";
   
   for( int i=0; i < en; i++ )
    errMsg+="<li>"+errs[i]+"</li>";
   
   errMsg+="</ul>";
   
   ErrorBox.showError(errMsg);

   return false;
  }
  
  var.setType(t);
  var.setDescription(descriptionArea.getValueAsString());
  var.setPredefined(predefVariants.getValue());
  
//  if( t == Type.ENUM )
//  {
//   var.clearVariants();
//   
//   Record[] rds = variStore.getRecords();
//
//   if( rds != null && rds.length != 0 )
//   {
//    for( Record r : rds )
//    {
//     var.addVariant( (Variant)r.getAsObject( "obj" ) ); //TODO variant coding handling
//    }
//   }
//  }
   
  return true;
 }
 
 
 public void setEditPanelListener( ObjectAction<Variable> l )
 {
  buttonLsnr.setListener(l);
 }
 
 public Variable getVariable()
 {
  return var;
 }

 public void setVariable( Variable v )
 {
  if( v == null )
   var = new Variable();
  else
   var=v;
  
  nameField.setValue(var.getName()!=null?var.getName():"");
  descriptionArea.setValue( var.getDescription()!=null?var.getDescription():"" );
  
  variStore.removeAll();
  if( var.getType() != null )
  {
   //type.selectByValue(v.getType().name(), true);
   type.setValue(v.getType().name());
   type.setDisabled(true);
  
   if( var.getType() == Variable.Type.ENUM )
   {
    if( var.getVariants() != null )
    {
     for( Variant vr : var.getVariants() )
     {
      if( vr.isPredefined() )
       variStore.add(variRecordDef.createRecord(new Object[]{ vr.getName(), vr.getCoding(), vr.getCount(),vr}));
     }
    }
    
    variGrid.setCollapsed(false);
    predefVariants.setDisabled(false);
    predefVariants.setChecked(var.isPredefined());
   }
   else
   {
    variGrid.setCollapsed(true);
   }
  }
  else
  {
   predefVariants.setChecked(false);
   predefVariants.setDisabled(true);
   type.setValue(null);
   type.setDisabled(false);
   variGrid.setCollapsed(true);
  }
 }
 
 class BtnListener extends ButtonListenerAdapter
 {
  private ObjectAction<Variable> lsnr;
  
  public BtnListener()
  {
   super();
  }
  
  public void setListener( ObjectAction<Variable> l )
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
      lsnr.doAction( state, var );
    }
    else
     lsnr.doAction( state, null );
   }
  }
 }
 
}
