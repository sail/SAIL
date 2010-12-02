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

package uk.ac.ebi.sail.client.ui;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.sail.client.ObjectAction;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.RowSelectionListener;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.event.BaseItemListener;


public class ListActionListenerAdapter<T> extends ButtonListenerAdapter implements BaseItemListener,RowSelectionListener
{
 private ObjectAction<T> lsnr;
 private GridPanel grid;
 
 public ListActionListenerAdapter( GridPanel g )
 {
  grid=g;
//  g.getSelectionModel().addListener(this); // Future implementation of disable/enable buttons according to selection
 }
 
 public ObjectAction<T> getListener()
 {
  return lsnr;
 }

 public void setListener( ObjectAction<T> l)
 {
  lsnr=l;
 }

 public void onClick(Button button, EventObject e)
 {
  onAction(button.getStateId());
 } 
 
 public void onAction(String actState)
 {
  if( actState == null )
   return;
  
  String flags = ActionFlags.defaultFlags;
  
  int pos = actState.indexOf(ActionFlags.separator);
  
  if( pos != -1 )
  {
   flags=actState.substring(pos+1);
   actState=actState.substring(0,pos);
  }
  
  if(lsnr != null)
  {
   if( flags.indexOf(ActionFlags.EMPTY ) != -1  )
   {
    lsnr.doAction(actState, null);
    return;
   }

   Record[] chcks = grid.getSelectionModel().getSelections();

   if( chcks == null || chcks.length == 0 )
   {
    if( flags.indexOf(ActionFlags.ALLOW_EMPTY ) != -1  )
    {
     lsnr.doAction(actState, null);
     return;
    }
   }
   else if( chcks.length == 1 )
   {
    if( flags.indexOf(ActionFlags.REQUIRE_MULTIPLE ) != -1  )
     return;
    /*else if( flags.indexOf(ButtonFlags.ALLOW_MULTIPLE ) != -1  )
    {
     List<T> lst = new ArrayList<T>(1);
     lst.add((T) chcks[0].getAsObject("obj"));

     lsnr.doMultyAction(btState, lst);
    }*/
    else
    {
     lsnr.doAction(actState, (T) chcks[0].getAsObject("obj"));
     return;
    }
   }
   else
   {
    if( flags.indexOf(ActionFlags.ALLOW_MULTIPLE ) == -1 && flags.indexOf(ActionFlags.REQUIRE_MULTIPLE ) == -1 )
     return;
    
    List<T> lst = new ArrayList<T>(chcks.length);
    for(Record tn : chcks)
     lst.add((T) tn.getAsObject("obj"));

    lsnr.doMultyAction(actState, lst);
   }
   
  }
 }

 public boolean doBeforeRowSelect(RowSelectionModel sm, int rowIndex, boolean keepExisting, Record record)
 {
  return true;
 }

 public void onRowDeselect(RowSelectionModel sm, int rowIndex, Record record)
 {
 }

 public void onRowSelect(RowSelectionModel sm, int rowIndex, Record record)
 {
 }

 public void onSelectionChange(RowSelectionModel sm)
 {
  System.out.println("Selection changed. Selected: "+sm.getCount());
 }

 public void onActivate(BaseItem item)
 {}

 public void onClick(BaseItem item, EventObject e)
 {
  onAction(item.getStateId());  
 }

 public void onDeactivate(BaseItem item)
 {}

}
