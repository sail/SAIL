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
import uk.ac.ebi.sail.client.common.IntRange;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.ValidationException;
import com.gwtext.client.widgets.form.Validator;
import com.gwtext.client.widgets.layout.AnchorLayoutData;

public class IntRangePanel extends FormPanel
{
 private TextField lowLim;
 private TextField upLim;
 
 private IntRange range;
 private ObjectAction<IntRange> listener;
 
 public IntRangePanel()
 {
  setFrame(true);
  
  lowLim = new TextField("Lower limit");
  lowLim.setValidator(new NumValidator());
  lowLim.setAllowBlank(true);
  lowLim.setEmptyText("-\u221e");
  add(lowLim, new AnchorLayoutData("94%"));
  
  
  upLim = new TextField("Upper limit");
  upLim.setValidator(new NumValidator());
  upLim.setAllowBlank(true);
  upLim.setEmptyText("+\u221e");
  add(upLim, new AnchorLayoutData("94%"));

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
 
 public void setRange( IntRange v )
 {
  range = v;
  
  if( v.getLimitLow() != Integer.MIN_VALUE )
   lowLim.setValue( String.valueOf(v.getLimitLow()) );
  else
   lowLim.setValue("");
  
  if( v.getLimitHigh() != Integer.MAX_VALUE )
   upLim.setValue(String.valueOf(v.getLimitHigh()));
  else
   upLim.setValue("");
  
 }
 
 public void setListener(ObjectAction<IntRange> listener)
 {
  this.listener = listener;
 }
 

 private boolean validate()
 {
  final String llVal = lowLim.getValueAsString().trim();
  final String ulVal = upLim.getValueAsString().trim();
  
  if( llVal.length() == 0 && ulVal.length() == 0 )
  {
   ErrorBox.showError("At least one limit must be not empty");
   return false;
  }
  
  try
  {
   if( ! upLim.getValidator().validate(ulVal) )
   {
    ErrorBox.showError("Upper limit is invalid");
    return false;
   }
   
   if( ! lowLim.getValidator().validate(llVal) )
   {
    ErrorBox.showError("Lower limit is invalid");
    return false;
   }
  }
  catch(ValidationException e)
  {
   return false;
  }
  
  int ll = llVal.length()>0?Integer.parseInt(llVal):Integer.MIN_VALUE;
  int ul = ulVal.length()>0?Integer.parseInt(ulVal):Integer.MIN_VALUE;

  if(  ll >= ul )
  {
   ErrorBox.showError("Upper limit must be greater than lower limit");
   return false;
  }
  
  range.setLimitLow(ll);
  range.setLimitHigh( ul );
  
  return true;
 } 
 
 class NumValidator implements Validator
 {

  public boolean validate(String value) // throws ValidationException
  {
   value = value.trim();

   if( value.length() == 0 )
    return true;
   
   try
   {
    Integer.parseInt(value);
   }
   catch (Exception e) 
   {
    return false;
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
    listener.doAction(state, range);
   }
  }

 }


}
