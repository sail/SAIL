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

import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.MenuItem;
import com.gwtext.client.widgets.menu.event.BaseItemListener;

public class ActionHelper
{
 public static Action findAction( String actState, Action[] acts )
 {
  for(Action a : acts)
  {
   if( a.getSubActions() != null )
   {
    Action fa = findAction(actState, a.getSubActions());
    if( fa != null) 
     return fa;
   }
   
   String aSt = a.getAction();
   
   if( aSt == null )
    continue;
   
   if( aSt.startsWith(actState) && (aSt.length()==actState.length() || aSt.charAt(actState.length()) == ActionFlags.separator ) )
    return a;
  }
  
  return null;
 }
 
 public static Menu createSubActions( Action act, BaseItemListener actListener  )
 {
  if(act.getSubActions() == null)
   return null;

  Menu mnu = new Menu();

  for(Action a : act.getSubActions())
  {
   if( a.getText() == null )
   {
    mnu.addSeparator();
   }
   else if(a.getSubActions() == null)
   {
    Item itm = new Item(a.getText());

    if(a.getCls() != null)
     itm.setIconCls(a.getCls());

    itm.setStateId(a.getAction());
    
    itm.addListener(actListener);
    a.setComponent(itm);
    mnu.addItem(itm);
   }
   else
   {
    MenuItem itm = new MenuItem(a.getText(),createSubActions(a,actListener));

    if(a.getCls() != null)
     itm.setIconCls(a.getCls());

    itm.setStateId(a.getAction());
    a.setComponent(itm);
    mnu.addItem(itm);
   }
  }
  
  return mnu;
 }

 public static Action[] mergeActions(Action[] btns, Action[] auxButtons)
 {
  List<Action> act = new ArrayList<Action>(btns.length+5);
  
  for( Action a : btns )
   act.add(a);
  
  
  mainLoop: for( Action aa : auxButtons )
  {
   for(int i=0; i < act.size(); i++ )
   {
    Action ma = act.get(i);
    
    if( aa.getAction()!= null && aa.getAction().equals(ma.getAction()) )
    {
     if( aa.getSubActions() != null && ma.getSubActions() != null )
      ma.setSubActions( mergeActions(ma.getSubActions() , aa.getSubActions()) );
     else
      act.set(i, aa);
     
     continue mainLoop;
    }
   }
   act.add( aa );
  }
  
  return act.toArray( new Action[ act.size() ] );

 }

}
