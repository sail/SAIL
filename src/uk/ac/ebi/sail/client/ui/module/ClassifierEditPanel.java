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
import java.util.List;

import uk.ac.ebi.sail.client.ConfigManager;
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.common.Classifier.Target;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridCellListenerAdapter;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

public class ClassifierEditPanel extends FormPanel
{
 private TextField                nameField;
 private TextArea                 descriptionArea;
 private Checkbox                 allowMulty;
 private Checkbox                 isMandatory;
 private ComboBox                 typeSelector;

 private Classifier               classifier;

 private GridPanel tagsGrid;
 private Store                    tagsStore;
 private static GridHelper        helper           = new GridHelper();

 private BtnListener buttonListener = new BtnListener();
 
 private static RecordDef typeRecordDef = new RecordDef(new FieldDef[] { 
   new StringFieldDef("typeCode"),
   new StringFieldDef("type")
   });

 private static RecordDef         tagsRecordDef    = new RecordDef(new FieldDef[] { 
   new StringFieldDef("tagname"),
   new StringFieldDef("info"), 
   new ObjectFieldDef("obj") });

// private static RecordDef         classifRecordDef = new RecordDef(new FieldDef[] { new StringFieldDef("classname"),
//   new StringFieldDef("tagname"), new StringFieldDef("info"), new ObjectFieldDef("obj") });

 private ClassificationSetEditor clsEdit;
 
 public ClassifierEditPanel()
 {
  setFrame(true);
  setPaddings(5, 5, 5, 0);
  setWidth(450);

  add(nameField = new TextField("Name", "name", 300),new AnchorLayoutData("95%"));

  descriptionArea = new TextArea("Description");
  add(descriptionArea, new AnchorLayoutData("95%"));

  Store store = new Store(typeRecordDef);
  
  typeSelector = new ComboBox();
  typeSelector.setFieldLabel("Type");
  typeSelector.setHiddenName("type");
  typeSelector.setStore(store);
  typeSelector.setDisplayField("type");
  typeSelector.setTypeAhead(true);
  typeSelector.setMode(ComboBox.LOCAL);
  typeSelector.setTriggerAction(ComboBox.ALL);
  typeSelector.setEmptyText("Select a type...");
  typeSelector.setSelectOnFocus(true);
  typeSelector.setWidth(190);

  for( Classifier.Target t : Classifier.Target.values() )
  {
   store.add( typeRecordDef.createRecord(new Object[]{t.name(), t.getText()}));
  }
  
  add(typeSelector, new AnchorLayoutData("95%"));

  allowMulty = new Checkbox("Allow multiple tags");
  add(allowMulty);

  isMandatory = new Checkbox("This classifier is mandatory");
  add(isMandatory);

  ColumnConfig tnCol = new ColumnConfig("Tag name", "tagname", 130, true, null, "tagname");

  ColumnConfig infoCol = new ColumnConfig("Info", "info", 55);

  infoCol.setRenderer(helper);
  // {
  // public String render(Object value, CellMetadata cellMetadata, Record
  // record, int rowIndex, int colNum, Store store)
  // {
  // return "<img src=\"images/silk/information.gif\" />";
  // }
  // });

  tagsGrid = new GridPanel();
  tagsGrid.setTitle("Tags");
  tagsGrid.setLayout(new FitLayout());
  tagsGrid.setSelectionModel(new RowSelectionModel(true));

  tagsGrid.setColumnModel(new ColumnModel(new ColumnConfig[] { tnCol, infoCol }));

  tagsGrid.setFrame(false);
  tagsGrid.setStripeRows(true);
  tagsGrid.setAutoExpandColumn("tagname");
  tagsGrid.setAutoHeight(true);

  TagEditor te = new TagEditor();
  
  Button bt = new Button("Add Tag",te);
  bt.setStateId("add");
  tagsGrid.addButton(bt);
  
  bt = new Button("Edit Tag",te);
  bt.setStateId("edit");
  tagsGrid.addButton(bt);

  bt = new Button("Remove Tag",te);
  bt.setStateId("remove");
  tagsGrid.addButton(bt);


  tagsGrid.addGridCellListener(helper);

  tagsStore = new Store(tagsRecordDef);

  tagsGrid.setStore(tagsStore);

  add(tagsGrid);

  clsEdit = new ClassificationSetEditor( Target.CLASSIFIER );
  add(clsEdit);
//  GridPanel classifGrid = new GridPanel();
//  classifGrid.setTitle("Classification");
//  // classifGrid.setLayout( new FitLayout() );
//  classifGrid.setSelectionModel(new RowSelectionModel(true));
//
//  infoCol = new ColumnConfig("Info", "info");
//
//  infoCol.setRenderer(helper);
//
//  classifGrid.setColumnModel(new ColumnModel(new ColumnConfig[] { new ColumnConfig("Classificator", "classname"),
//    new ColumnConfig("Tag", "tagname", 130, true, null, "tagname"), infoCol }));
//
//  classifGrid.setFrame(false);
//  classifGrid.setStripeRows(true);
//  classifGrid.setAutoExpandColumn("tagname");
//  classifGrid.setAutoHeight(true);
//  classifGrid.setCollapsible(true);
//  classifGrid.setCollapsed(true);
//
//  classifGrid.addButton(new Button("Add Tag",new ButtonListenerAdapter(){
//  public void onClick(Button button, EventObject e)
//  {
//   System.out.println("Click: "+button.getTitle());
//  }
// }));
//  classifGrid.addButton(new Button("Remove Tag"));
//
//  classifStore = new Store(classifRecordDef);
//
//  classifGrid.setStore(classifStore);
//
//  classifGrid.addGridCellListener(helper);
//
//  add(classifGrid);
  bt = new Button("Save",buttonListener);
  bt.setStateId("ok");
  addButton(bt);

  bt = new Button("Cancel",buttonListener);
  bt.setStateId("cancel");
  addButton(bt);
 }

 public void setClassifier(Classifier cl)
 {
  if(cl == null)
   cl = new Classifier();

  classifier = cl;

  nameField.setValue(cl.getName() != null ? cl.getName() : "");
  descriptionArea.setValue(cl.getDescription() != null ? cl.getDescription() : "");

  // tagsMap.clear();

  if(cl.getTags() != null)
  {
   tagsStore.removeAll();

   for(Tag t : cl.getTags())
   {
    Record r = tagsRecordDef.createRecord(new Object[] { t.getName(), t.getDescription(), t });
    tagsStore.add(r);
   }
  }

  clsEdit.setEditObject(cl);
  clsEdit.setCollapsed(cl.getClassificationTags() == null);
  
  if( cl.getTarget() != null )
  {
   typeSelector.setValue(cl.getTarget().getText());
   typeSelector.setDisabled(true);
  }
  
//  if(cl.getClassificationTags() != null)
//  {
//   classifStore.removeAll();
//
//   for(Tag t : cl.getClassificationTags())
//   {
//    Record r = classifRecordDef.createRecord(new Object[] { t.getClassifier().getName(), t.getName(),
//      t.getDescription(), t });
//
//    classifStore.add(r);
//   }
//  }

  isMandatory.setChecked(cl.isMandatory());
  allowMulty.setChecked(cl.isAllowMulty());

  classifier = cl;
  doLayout();
 }

 private boolean validate()
 {
  String[] errs = new String[5];
  int en=0;
 
  if( classifier == null )
   classifier=new Classifier();
  
   
  String str=nameField.getValueAsString();
  
  if( str == null || str.length() == 0 )
   errs[en++]="Name can't be empty";
  
  classifier.setName(str);

  Classifier.Target t = null;
  String val = typeSelector.getValue();

  for(Classifier.Target tg : Classifier.Target.values())
  {
   if(tg.getText().equals(val))
   {
    t = tg;
    break;
   }
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
  
  classifier.setTarget(t);
  classifier.setDescription(descriptionArea.getValueAsString());
  classifier.setMandatory(isMandatory.getValue());
  classifier.setAllowMulty(allowMulty.getValue());  

  
  return true;
 }


 public Classifier getClassifier()
 {
  return classifier;
 }

 static class GridHelper extends GridCellListenerAdapter implements Renderer
 {
  public void onCellClick(GridPanel grid, int rowIndex, int colIndex, EventObject e)
  {
   if(grid.getColumnModel().getDataIndex(colIndex).equals("info") && e.getTarget(".infotip", 1) != null)
   {
    Record record = grid.getStore().getAt(rowIndex);

    String str = record.getAsString("info");
    if(str != null && str.length() > 1)
     str = str.replaceAll("\n", "<br />");
    PopupMessage.showMessage(record.getAsString("tagname"), str, e.getXY());
   }
  }

  public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store)
  {
   return "<img class=\"infotip\" src=\""+ConfigManager.SAIL_RESOURCE_PATH+"images/silk/information.gif\" />";
  }

 }

 public void setObjectActionListener(ObjectAction<Classifier> ls)
 {
  buttonListener.setListener(ls);
 }

 class BtnListener extends ButtonListenerAdapter
 {
  private ObjectAction<Classifier> lsnr;
  
  public BtnListener()
  {
   super();
  }
  
  public void setListener( ObjectAction<Classifier> l )
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
     try
     {
     if( validate() )
      lsnr.doAction( state, classifier );
     }
     catch (Exception ex) {
      ex.printStackTrace();
     }
    }
    else
     lsnr.doAction( state, null );
   }
  }

 }
 
 class TagEditor extends ButtonListenerAdapter implements ObjectAction<Tag>
 {
  TagEditPanel tep;
  Window        window;

  public void onClick(Button button, EventObject e)
  {
   String state = button.getStateId();
   
   if(tep == null)
   {
    tep = new TagEditPanel();
    tep.setEditPanelListener(this);
   }

   if("add".equals(state))
   {
    try{
     
     tep.setTag(null);
    }
    catch (Exception ex) {
     ErrorBox.showError("Error: "+ex);
    }
   }
   else
   {
    final Record rec = tagsGrid.getSelectionModel().getSelected();

    if(rec == null)
     return;

    Tag t = (Tag) rec.getAsObject("obj");

    if("edit".equals(state))
    {
     tep.setTag( new Tag( t ) );
    }
    else
    {
     tagsStore.remove(rec);
     classifier.removeTag(t);
     return;
    }
   }


   if(window == null)
   {
    window = new Window();

    window.setTitle("Edit Tag");
    window.setClosable(true);
    window.setWidth(400);
    window.setHeight(400);
    window.setPlain(true);
    window.setLayout(new FitLayout());
    window.add(tep);
    window.setCloseAction(Window.HIDE);
    window.setModal(true);
   }

   //window.show(button.getElement());
   window.show();
  }

  public void doAction(String actName, Tag t)
  {
   window.hide();

   if(!"ok".equals(actName))
    return;
   
   Collection<Tag> vs = classifier.getTags();
   
   if( vs != null && t != null )
   {
    for( Tag ev : vs )
    {
     if( ev.getName().equals(t.getName()) && ev.getId() != t.getId() )
     {
      ErrorBox.showError("Tag with name '"+t.getName()+"' already exists");
      return;
     }
    }
   }
   


   if(t.getId() == 0)
   {
    t.setId(-1);
    classifier.addTag(t);
    
    Record r = tagsRecordDef.createRecord(new Object[] { t.getName(), t.getDescription(), t });
    tagsStore.add(r);

   }
   else
   {
    Record rec = tagsGrid.getSelectionModel().getSelected();
    
    Tag origTag = (Tag) rec.getAsObject("obj");
    origTag.setName(t.getName());
    origTag.setDescription(t.getDescription());
    
    rec.set("tagname", t.getName());
    rec.set("info", origTag.getDescription());
   }

  }

  public void doMultyAction(String actName, List<Tag> lp)
  {
  }
 }
 
}