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
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.Annotated;
import uk.ac.ebi.sail.client.common.Annotation;
import uk.ac.ebi.sail.client.common.Classifier;

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



public class AnnotatedEditor extends GridPanel
{
 private static GridHelper       helper             = new GridHelper();

 private static RecordDef        descRecordDef   = new RecordDef(new FieldDef[] { 
   new StringFieldDef("classname"),
   new StringFieldDef("tagname"), 
   new StringFieldDef("info"), 
   new ObjectFieldDef("obj") });

 private static ColumnConfig     infoCol            = new ColumnConfig("Info", "info", 130, true, null, "info");
 private static ColumnModel      descColumnModel = new ColumnModel(new ColumnConfig[] {
   new ColumnConfig("Classifier", "classname", 130, true, null, "classname"),
   new ColumnConfig("Tag", "tagname", 130, true, null, "tagname"), infoCol });
 
 static
 {
  infoCol.setRenderer(helper);
 }


 
 private Store descStore;
 private Annotated object;
 private Classifier.Target anntType;
 
 public AnnotatedEditor( Classifier.Target antClass )
 {
  anntType = antClass;
  
  setTitle("Structured description");
  setLayout(new FitLayout());
  setSelectionModel(new RowSelectionModel(true));

  setColumnModel(descColumnModel);

  setFrame(true);
  setCollapsible(true);
  setStripeRows(true);
  setAutoExpandColumn("tagname");
  setAutoHeight(true);

  Editor edt = new Editor();
  
  Button bt = new Button("Add", edt);
  bt.setStateId("add");
  
  addButton(bt);

  bt = new Button("Edit", edt);
  bt.setStateId("edit");
  addButton(bt);

  bt = new Button("Remove", edt);
  bt.setStateId("remove");
  addButton(bt);

  descStore = new Store(descRecordDef);
  setStore(descStore);

  addGridCellListener(helper);

 }
 
 public void setAnnotatedObject( Annotated obj )
 {
  object=obj;
  
  descStore.removeAll();
  if(object.getAnnotations() != null)
  {

   for(Annotation d : object.getAnnotations())
   {
    Record r = descRecordDef.createRecord(new Object[] { d.getTag().getClassifier().getName(), d.getTag().getName(),
      d.getText(), d });

    descStore.add(r);
   }
  }
 }
 
 class Editor extends ButtonListenerAdapter
 {
 
 public void onClick(Button button, EventObject e)
 {
//  System.out.println("Click: "+button.getTitle());
  
  final String act = button.getStateId();
  
  if("remove".equals(act))
  {
   removeAnnotation();
   return;
  }

  final Record sr = getSelectionModel().getSelected();

  final Annotation selAnnt = sr != null?(Annotation) sr.getAsObject("obj"):null;

  if( selAnnt==null && ! "add".equals(act) )
   return;
  
  final AnnotationEditDialog annDialog = AnnotationEditDialog.getDialog(anntType);


  annDialog.setObjectActionListener(new ObjectAction<Annotation>()
  {

   public void doAction(String actName, Annotation p)
   {
    annDialog.dispose();

    if("cancel".equals(actName))
     return;


    if( "add".equals(act) )
    {
     object.addAnnotation(p);
     descStore.add(descRecordDef.createRecord(new Object[]{ p.getTag().getClassifier().getName(), p.getTag().getName(),p.getText(),p}));
    }
    else
    {
     selAnnt.setText(p.getText());
     selAnnt.setTag(p.getTag());
     
     sr.set("tagname", p.getTag().getName());
     sr.set("classname", p.getTag().getClassifier().getName());
     sr.set("info", p.getText() );
    }
   }

   public void doMultyAction(String actName, List<Annotation> lp)
   {

   }
  });

  if( "edit".equals(act) )
  {
   if(selAnnt != null)
    annDialog.setAnnotation( new Annotation(selAnnt) );
   else
    return;
  }
  else
   annDialog.setAnnotation(new Annotation());
   
  
  annDialog.show(button.getButtonElement());

 }
 }
 
 private void removeAnnotation()
 {
  Record r = getSelectionModel().getSelected();

  if(r != null)
  {
   object.removeAnnotation((Annotation) r.getAsObject("obj"));
   descStore.remove(r);
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
