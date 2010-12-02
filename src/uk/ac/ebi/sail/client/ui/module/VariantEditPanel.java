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

import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.Variant;
import uk.ac.ebi.sail.client.ui.widget.ConfirmBox;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.ValidationException;
import com.gwtext.client.widgets.form.Validator;
import com.gwtext.client.widgets.layout.AnchorLayoutData;

public class VariantEditPanel extends FormPanel
{
 private TextField name;
 private TextField code;
 
 private Variant variant;
 private Collection<Variant> otherVars;
 private ObjectAction<Variant> listener;
 
 public VariantEditPanel()
 {
  setFrame(true);
  
  name = new TextField("Name");
  name.setValidator(new NameValidator());
  name.setAllowBlank(false);
  add(name, new AnchorLayoutData("94%"));
  
  
  code = new TextField("Code");
  code.setValidator(new CodeValidator());
  code.setAllowBlank(false);
  add(code, new AnchorLayoutData("94%"));

  Button bt;
  ButtonLsnr btlsnr = new ButtonLsnr();
  
  
  bt = new Button("OK");
  
  bt.setStateId("ok");
  bt.addListener(btlsnr);
  addButton(bt);

  bt=new Button("Cancel");
  bt.setStateId("cancel");
  bt.addListener(btlsnr);
  addButton(bt);
  
 }
 
 public void setVariant( Variant v, Collection<Variant> othv )
 {
  variant = v;
  otherVars = othv;
  
  if( v.getName() != null )
   name.setValue( v.getName() );
  else
   name.setValue("");
  
  code.setValue(String.valueOf(v.getCoding()));
  
 }
 
 public void setListener(ObjectAction<Variant> listener)
 {
  this.listener = listener;
 }
 

 private boolean validate()
 {
  final String edName = name.getValueAsString().trim();
  final String edCode = code.getValueAsString().trim();
  
  StringBuilder problems = new StringBuilder();
  problems.append("<ul>");
  

  if( edName.length() == 0 || ! (edName.matches(".*[\\D].*") ) )
   problems.append("<li>Name must contain at least one letter</li>");


  try
  {
   if( ! code.getValidator().validate(edCode) )
    problems.append("<li>Invalid value of the 'Code' field</li>");
  }
  catch (Exception e)
  {
   problems.append("<li>").append(e.getMessage()).append("</li>");
  }
  
  if( otherVars != null )
  {
   for( Variant ov: otherVars )
    if( ov != variant && ov.getName().equals(edName) )
    {
     if( ov.isPredefined() || variant.getId() > 0 )
      problems.append("<li>This name has been already taken by the other variant</li>");
     else
     {
      if( problems.length() <= 4 )
      {
       final Variant origV = ov;
       
       ConfirmBox.confirm("Non-predefined variant with such name already exists. Do you want to make it predefined?", new MessageBox.PromptCallback(){

        public void execute(String arg0, String arg1)
        {
         if( "yes".equals(arg0) )
         {
          variant.setName(edName);
          variant.setCoding( Integer.parseInt(edCode) );
          variant.setPredefined(true); 
          variant.setId(origV.getId());

          if( listener != null )
           listener.doAction("ok", variant);

         }
        }});
       
       return false;
      }
     }
    }
  }
  
  if( problems.length() > 4 )
  {
   problems.append("</ul>");
   
   ErrorBox.showError(problems.toString());
   
   return false;
  }
  
  
  variant.setName(edName);
  variant.setCoding( Integer.parseInt(edCode) );
  variant.setPredefined(true);
  
  return true;
 } 
 
 class NameValidator implements Validator
 {

  public boolean validate(String value) throws ValidationException
  {
   value = value.trim();
   
   if( value.length() == 0 || ! (value.matches(".*[\\D].*") ) )
    throw new ValidationException("Name must contain at least one letter");
   
   if( otherVars != null )
   {
    for( Variant ov: otherVars )
     if( ov != variant && ov.getName().equals(value) )
      throw new ValidationException("This name has been already taken by the other variant");
   }
    
   return true;
  }
  
 }
 
 class CodeValidator implements Validator
 {

  public boolean validate(String value) throws ValidationException
  {
   value = value.trim();
   
   int intVal=0;
   
   try
   {
    intVal = Integer.parseInt(value); 
   }
   catch (Exception e)
   {
    throw new ValidationException("Coding must be an integer number");
   }
   
   if( otherVars != null )
   {
    for( Variant ov: otherVars )
     if( ov != variant && ov.isPredefined() && ov.getCoding() == intVal )
      throw new ValidationException("This code has been already taken by the '"+ov.getName()+"' variant");
   }
  
    
   return true;
  }
  
 }
 
 class ButtonLsnr extends ButtonListenerAdapter
 {
  public void onClick(Button button, EventObject e)
  {
   String state = button.getStateId();
   
   if( "ok".equals(state) )
   {
    if( ! validate() )
     return;
   }

   if( listener != null )
   {
    listener.doAction(state, variant);
   }
  }

 }


}
