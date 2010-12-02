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

package uk.ac.ebi.sail.client.data;

import java.util.HashMap;
import java.util.Map;

import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StoreTraversalCallback;

public class MultiFilterStore1<T extends Attributed> extends Store implements StoreTraversalCallback, Filterable<T>
{
 private Map<String,TraversalCallback<T>> filters=new HashMap<String,TraversalCallback<T>>();
 
 public MultiFilterStore1(RecordDef recordDef)
 {
  super(recordDef);
 }

// public void addFilter(String fName, StoreTraversalCallback filt)
// {
//  filters.put(fName,filt);
//  refilter();
// }
 
 public void removeFilter(String fName)
 {
  filters.remove(fName);
  refilter();
 }

 private void refilter()
 {
  clearFilter();
  
  if( filters.size() > 0 )
   filterBy(this);
 }

 @SuppressWarnings("unchecked")
 public boolean execute(Record record)
 {
  for(TraversalCallback<T> cb : filters.values())
   if( ! cb.execute((T)record.getAsObject("obj")) )
    return false;

  return true;
 }

 public void addFilter(String fid, TraversalCallback<T> cb)
 {
  filters.put(fid,cb);
  refilter(); 
 }
}
