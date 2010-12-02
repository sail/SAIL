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
import uk.ac.ebi.sail.client.common.ComplexFilter;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;

public class ParameterFilterDialog extends Window
{
// private static ParameterFilterDialog instance;
// 
// public static ParameterFilterDialog getInstance()
// {
//  if( instance == null )
//   instance = new ParameterFilterDialog();
//  
//  return instance;
// }
 
// private ComplexFilter filter;
 private ObjectAction<ComplexFilter> listener;
 private ParameterFilterPanel filterPanel;
 
 public ParameterFilterDialog( ComplexFilter flt, boolean addGroupState )
 {
//  filter = flt;
  setTitle("Parameter filters");
  setSize(300, 300);
  setLayout(new FitLayout() );
  
  add( filterPanel = new ParameterFilterPanel(flt) );

  BtListener btL = new BtListener();
  
  Button bt = new Button("OK");
  bt.setStateId("add");
  bt.addListener(btL);
  
  addButton(bt);
 
  if( addGroupState )
  {
   bt = new Button("Add to group");
   bt.setStateId("addToGroup");
   bt.addListener(btL);

   addButton(bt);
  }
  
  bt = new Button("Cancel");
  bt.setStateId("cancel");
  bt.addListener(btL);
  
  addButton(bt);

 }

 public void setListener( ObjectAction<ComplexFilter> l )
 {
  listener=l;
 }
 
 private class BtListener extends ButtonListenerAdapter
 {
  public void onClick( Button b, EventObject e)
  {
   String state = b.getStateId();
   
   ComplexFilter cf = filterPanel.getFilter();
   
   close();
   
   if( listener != null )
    listener.doAction(state, cf);
  }
 }
}
