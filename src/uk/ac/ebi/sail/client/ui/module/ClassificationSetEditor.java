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

import uk.ac.ebi.sail.client.ConfigManager;
import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.Classifiable;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.Tag;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridCellListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;



public class ClassificationSetEditor extends GridPanel
{
 private static GridHelper       helper             = new GridHelper();

 private static RecordDef        classifRecordDef   = new RecordDef(new FieldDef[] { 
   new StringFieldDef("classname"),
   new StringFieldDef("tagname"), 
   new StringFieldDef("info"), 
   new ObjectFieldDef("obj") });

 private static ColumnConfig     infoCol            = new ColumnConfig("Info", "info", 130, true, null, "info");
 private static ColumnModel      classifColumnModel = new ColumnModel(new ColumnConfig[] {
   new ColumnConfig("Classifier", "classname", 130, true, null, "classname"),
   new ColumnConfig("Tag", "tagname", 130, true, null, "tagname"), infoCol });
 
 static
 {
  infoCol.setRenderer(helper);
 }


 
 private Store tagStore;
 private Classifiable object;
 private Classifier.Target targetType;
 
 public ClassificationSetEditor( Classifier.Target tgType )
 {
  targetType=tgType;

  setTitle("Classification");
  setLayout(new FitLayout());
  setSelectionModel(new RowSelectionModel(true));

  setColumnModel(classifColumnModel);

  setFrame(true);
  setCollapsible(true);
  setStripeRows(true);
  setAutoExpandColumn("tagname");
  setAutoHeight(true);

  Editor edt = new Editor();
  
  Button bt = new Button("Add Tag", edt);
  bt.setStateId("add");
  
  addButton(bt);

  bt = new Button("Remove Tag", edt);
  bt.setStateId("remove");
  addButton(bt);

  tagStore = new Store(classifRecordDef);
  setStore(tagStore);

  addGridCellListener(helper);

 }
 
 public void setEditObject( Classifiable obj )
 {
  object=obj;
  
  tagStore.removeAll();

  if(object.getClassificationTags() != null)
  {

   for(Tag t : object.getClassificationTags())
   {
    Record r = classifRecordDef.createRecord(new Object[] { t.getClassifier().getName(), t.getName(),
      t.getDescription(), t });

    tagStore.add(r);
   }
  }
 }
 
 class Editor extends ButtonListenerAdapter
 {
 
 public void onClick(Button button, EventObject e)
 {
//  System.out.println("Click: "+button.getTitle());
  
  String act = button.getStateId();
  
  if("remove".equals(act))
  {
   removeTag();
   return;
  }

  final ClassifierSelectDialog clsSelDialog = ClassifierSelectDialog.getDialog(DataManager.getInstance().getClassifierManager(targetType));

  clsSelDialog.setObjectActionListener(new ObjectAction<Classifier>()
  {

   public void doAction(String actName, Classifier p)
   {
    clsSelDialog.dispose();

    if("cancel".equals(actName))
     return;

    final SelectTagDialog tagSelDialog = SelectTagDialog.getDialog();
    tagSelDialog.setClassifier(p);
    tagSelDialog.setObjectActionListener(new ObjectAction<Tag>()
    {
     public void doAction(String actName, Tag t)
     {
      tagSelDialog.hide();

      if("cancel".equals(actName))
       return;

      tagStore.add(classifRecordDef.createRecord(new Object[] { t.getClassifier().getName(), t.getName(),
        t.getDescription(), t }));
      object.addClassificationTag(t);
     }

     public void doMultyAction(String actName, List<Tag> lp)
     {
     }
    });

    tagSelDialog.show();
   }

   public void doMultyAction(String actName, List<Classifier> lp)
   {

   }
  });

  clsSelDialog.show(button.getButtonElement());

 }
 }
 
 private void removeTag()
 {
  Record r = getSelectionModel().getSelected();

  if(r != null)
  {
   object.removeClassificationTag((Tag) r.getAsObject("obj"));
   tagStore.remove(r);
  }
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
    else
     str=record.getAsString("tagname");
    PopupMessage.showMessage(record.getAsString("tagname"), str, e.getXY());
   }
  }

  public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store)
  {
   return "<img class=\"infotip\" src=\""+ConfigManager.SAIL_RESOURCE_PATH+"images/silk/information.gif\" />";
  }

 }

 }
