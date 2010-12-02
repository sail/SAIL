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

import java.util.Date;

import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.IntRange;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.DateField;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.layout.AnchorLayoutData;

public class DateRangePanel extends FormPanel
{
 private static DateTimeFormat format = DateTimeFormat.getFormat("yyyyMMdd");

 
 private DateField lowLim;
 private DateField upLim;
 
 private IntRange range;
 private ObjectAction<IntRange> listener;
 
 public DateRangePanel()
 {
  setFrame(true);
  
  
  lowLim = new DateField("Lower limit (YYYY-MM-DD)","Y-m-d");
  lowLim.setAllowBlank(true);
  lowLim.setEmptyText("-\u221e");
  add(lowLim, new AnchorLayoutData("94%"));
  
  
  upLim = new DateField("Upper limit  (YYYY-MM-DD)","Y-m-d");
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
   lowLim.setValue( format.parse( String.valueOf(v.getLimitLow()) ) );
  else
   lowLim.setValue("");
  
  if( v.getLimitHigh() != Integer.MAX_VALUE )
   upLim.setValue( format.parse( String.valueOf(v.getLimitHigh()) ) );
  else
   upLim.setValue("");
  
 }
 
 
 public void setListener(ObjectAction<IntRange> listener)
 {
  this.listener = listener;
 }
 

 private boolean validate()
 {
  final Date llVal = lowLim.getValue();
  final Date ulVal = upLim.getValue();
  
  if(  ! lowLim.isValid() )
   return false;

  if(  ! upLim.isValid() )
   return false;

  if( llVal != null)
  {
   int val = (llVal.getYear()+1900)*10000;
   val+=(llVal.getMonth()+1)*100;
   val+=llVal.getDate();
   range.setLimitLow(val);
  }
  else
   range.setLimitLow(Integer.MIN_VALUE);
  
  if( ulVal != null)
   range.setLimitHigh((ulVal.getYear()+1900)*10000+(ulVal.getMonth()+1)*100+ulVal.getDate());
  else
   range.setLimitHigh(Integer.MAX_VALUE);
  
//  try
//  {
//   if( ! upLim.getValidator().validate(ulVal) )
//   {
//    ErrorBox.showError("Upper limit is invalid");
//    return false;
//   }
//   
//   if( ! lowLim.getValidator().validate(llVal) )
//   {
//    ErrorBox.showError("Lower limit is invalid");
//    return false;
//   }
//  }
//  catch(ValidationException e)
//  {
//   return false;
//  }
//  
//  float ll = llVal.length()>0?Float.parseFloat(llVal):Float.NaN;
//  float ul = ulVal.length()>0?Float.parseFloat(ulVal):Float.NaN;
//
//  if( ! Float.isNaN(ll) && ! Float.isNaN(ul) && ll >= ul )
//  {
//   ErrorBox.showError("Upper limit must be greater than lower limit");
//   return false;
//  }
//  
//  range.setLimitLow(ll);
//  range.setLimitHigh( ul );
  
  return true;
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
