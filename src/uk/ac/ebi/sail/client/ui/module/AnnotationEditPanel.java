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

import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.Annotation;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.common.Classifier.Target;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.layout.AnchorLayout;
import com.gwtext.client.widgets.layout.AnchorLayoutData;


public class AnnotationEditPanel extends Panel
{
 private static final String typeStr = "Type: "+"&nbsp;&nbsp;"; 
 
 private Annotation object;
 private Panel tagLbl;
 private TextArea text;
 private ObjectAction<Annotation> lsnr;
 private Classifier.Target annotationClass;
 
 public AnnotationEditPanel( Classifier.Target antClass )
 {
  annotationClass=antClass;
  
  setLayout( new AnchorLayout() );
  setPaddings(10);
  
  add( tagLbl = new Panel(), new AnchorLayoutData("100% 10%") );
  add( text =  new TextArea(), new AnchorLayoutData("100% 90%") );
  tagLbl.setHtml("<h2>"+typeStr+"</h2>");
  tagLbl.setBorder(false);
//  tagLbl.setHeight(50);
  
  Button bt;
  BtListener btl = new BtListener();
  
  bt = new Button("Ok",btl);
  bt.setStateId("ok");
  addButton( bt );
  
  bt = new Button("Cancel",btl);
  bt.setStateId("cancel");
  addButton( bt );
  
  bt = new Button("Change Tag",btl);
  bt.setStateId("tag");
  addButton( bt );
  
 }
 
 public void setAnnotaton( Annotation ant )
 {
  if( ant.getTag() != null )
   tagLbl.setHtml("<h2>"+typeStr+"<b>"+ant.getTag().getClassifier().getName()+":"+ant.getTag().getName()+"</b></h2>");
  else
   tagLbl.setHtml("<h2>"+typeStr+"</h2>");
  
  text.setValue(ant.getText());
  
  object=ant;
 }
 
 private class BtListener extends ButtonListenerAdapter
 {
  public void onClick(Button button, EventObject e)
  {
   String act = button.getStateId();
   
   if( "cancel".equals(act) )
   {
    if( lsnr != null )
     lsnr.doAction(act, null);
    
    return;
   }
   else if( "tag".equals(act) )
   {
    final ClassifierSelectDialog clsSelDialog = ClassifierSelectDialog.getDialog(DataManager.getInstance().getClassifierManager(annotationClass));

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

        tagLbl.setHtml("<h2>"+typeStr+"<b>"+t.getClassifier().getName()+":"+t.getName()+"</b></h2>");
        object.setTag(t);
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
   else if( "ok".equals(act) )
   {
    object.setText(text.getValueAsString());
    lsnr.doAction(act, object);
   }
  }
 }

 public void setObjectActionListener(ObjectAction<Annotation> objectAction)
 {
  lsnr=objectAction;
 }

 public void setAnnotationTarget(Target antClass)
 {
  annotationClass=antClass;
 }
}
