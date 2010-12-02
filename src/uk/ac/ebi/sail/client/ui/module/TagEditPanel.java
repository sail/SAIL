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
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.AnchorLayoutData;

public class TagEditPanel extends FormPanel
{

 private Tag tag;
 
 private TextField nameField;
 private TextArea descriptionArea;
 
 private BtnListener buttonLsnr;
  
 public TagEditPanel()
 {
  setFrame(true);
  setPaddings(5, 5, 5, 0);
  setWidth(400);
  setHeight(200);
  setAutoScroll( false );
  
  FormPanel fp = new FormPanel();
  fp.setAutoHeight(true);

  add(nameField = new TextField("Name", "name", 300), new AnchorLayoutData("100%"));


  descriptionArea = new TextArea("Description");
  add(descriptionArea, new AnchorLayoutData("100% 50%"));
  

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
 
  if( tag == null )
   tag=new Tag();
  
   
  String str=nameField.getValueAsString();
  
  if( str == null || str.length() == 0 )
   errs[en++]="Name can't be empty";
  
  tag.setName(str);

  
  if( en != 0 )
  {
   String errMsg = "There are errors: <br /><ul>";
   
   for( int i=0; i < en; i++ )
    errMsg+="<li>"+errs[i]+"</li>";
   
   errMsg+="</ul>";
   
   ErrorBox.showError(errMsg);

   return false;
  }
  
  tag.setDescription(descriptionArea.getValueAsString());
  
  return true;
 }
 
 
 public void setEditPanelListener( ObjectAction<Tag> l )
 {
  buttonLsnr.setListener(l);
 }
 
// public Qualifier getQualifier()
// {
//  return qualifier;
// }

 public void setTag( Tag v )
 {
  if( v == null )
   tag = new Tag();
  else
   tag=v;
  
  nameField.setValue(tag.getName());
  descriptionArea.setValue( tag.getDescription() );
 }
 
 class BtnListener extends ButtonListenerAdapter
 {
  private ObjectAction<Tag> lsnr;
  
  public BtnListener()
  {
   super();
  }
  
  public void setListener( ObjectAction<Tag> l )
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
      lsnr.doAction( state, tag );
    }
    else
     lsnr.doAction( state, null );
   }
  }
 }
 
}
