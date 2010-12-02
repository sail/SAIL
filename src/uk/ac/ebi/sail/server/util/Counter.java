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

package uk.ac.ebi.sail.server.util;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.pri.util.collection.IntMap;
import com.pri.util.collection.IntTreeMap;

public class Counter implements Serializable, IsSerializable
{
 private int count=0;
 
 private IntMap<Counter> subCounters;
 private IntMap<Counter> tagCounters;
 
 public Counter()
 {}

 public Counter(int i)
 {
  count=i;
 }

 public int getValue()
 {
  return count;
 }
 

 public void inc()
 {
  count++;
 }

 public void add(int i)
 {
  count+=i;
 }

 public Counter getOrCreateSubCounter( int id )
 {
  Counter cnt=null;
  
  if( subCounters == null )
  {
   subCounters = new IntTreeMap<Counter>();
   subCounters.put(id, cnt = new Counter());
  }
  else
  {
   cnt = subCounters.get(id);
   
   if( cnt == null )
    subCounters.put(id, cnt = new Counter());
  }
  
  return cnt;
 }
 
 public void inc(int next)
 {
  getOrCreateSubCounter(next).inc();
 }
 
 
 public Counter getSubcounter( int id )
 {
  if( subCounters == null )
   return null;
  
  return subCounters.get(id);
 }
 
 public IntMap<Counter> getSubcounters()
 {
  return subCounters;
 }

 public IntMap<Counter> getTagCounters()
 {
  return tagCounters;
 }

 public void incTag(int id)
 {
  Counter cnt=null;
  
  if( tagCounters == null )
  {
   tagCounters = new IntTreeMap<Counter>();
   tagCounters.put(id, cnt = new Counter(1));
  }
  else
  {
   cnt = tagCounters.get(id);
   
   if( cnt == null )
    tagCounters.put(id, cnt = new Counter(1));
   else
    cnt.inc();
  }
 }

 public void incTagFor(int subid, int tagId)
 {
  getOrCreateSubCounter(subid).incTag(tagId);
 }
}
