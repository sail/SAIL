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

package uk.ac.ebi.sail.client.ui.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.sail.client.RegExp;
import uk.ac.ebi.sail.client.data.Attributed;
import uk.ac.ebi.sail.client.data.Filterable;
import uk.ac.ebi.sail.client.data.TraversalCallback;

import com.gwtext.client.core.EventCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.menu.CheckItem;

public class SearchField<T extends Attributed> extends TwinTriggerField implements EventCallback, TraversalCallback<T>
{
// private MultiFiltered store;
 private Filterable<T> fStore;
 private Collection<CheckItem> items;
 private List<String> fields= new ArrayList<String>(10);
 private RegExp rx;
 
 private static final String FILTER_ID="_RXSearchFileld";
// private Matcher matcher;
 
 public SearchField( Filterable<T> flt, Collection<CheckItem> itm )
 {
//  store=stor;
  items=itm;
  fStore=flt;
  
  setTrigger1Class("x-form-clear-trigger");
  setTrigger2Class("x-form-search-trigger");
  
  addKeyPressListener( this );
 }

 
 protected void onTrigger1Click(EventObject event)
 {
  setValue("");
  fStore.removeFilter(FILTER_ID);
 }

 protected void onTrigger2Click(EventObject event)
 {
  fields.clear();
  
  for(CheckItem bi : items )
  {
   if( bi.isChecked() )
    fields.add(bi.getStateId());
  }
  
  if( getValueAsString() == null || getValueAsString().length() ==  0 )
  {
   rx=null;
   fStore.removeFilter(FILTER_ID);
  }
  else
  {
   rx = RegExp.compile(getValueAsString(),"i");
   fStore.addFilter(FILTER_ID, this);
  }
 }

 public void execute(EventObject e)
 {
  if( e.getKey() == EventObject.ENTER )
   onTrigger2Click(e);
  else if( e.getKey() == EventObject.ESC )
   onTrigger1Click(e);
 }


// public boolean execute(Record record)
// {
//  if( rx == null )
//   return true;
//  
//  for( String key : fields )
//  {
//   if( rx.test(record.getAsString(key)) )
//    return true;
//  }
//   
//  return false;
// }


 public boolean execute(T obj)
 {
  if( rx == null )
   return true;
  
  for( String key : fields )
  {
   String attr = obj.getAttribute(key);
   if( attr != null && rx.test(attr) )
    return true;
  }
   
  return false;
 }

}
