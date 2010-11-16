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

package uk.ac.ebi.sail.server.data;

import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.client.common.Variable.Type;
import uk.ac.ebi.sail.server.util.Counter;
import uk.ac.ebi.sail.server.util.IntMapDepot;

import com.pri.util.collection.IntIterator;
import com.pri.util.collection.IntMap;
import com.pri.util.collection.IntTreeMap;

public class CollectionSummaryRecordProcessor implements RecordProcessor
{
 private IntMap<IntMap<Void>> paramCMap = new IntTreeMap<IntMap<Void>>();
 private IntMap<IntMap<Void>> tagCMap   = new IntTreeMap<IntMap<Void>>();
 private IntMap<Counter>      paramCnt;

 public CollectionSummaryRecordProcessor(IntMap<Counter> res)
 {
  paramCnt = res;
 }
 
 protected IntMap<Counter> getCounterMap()
 {
  return paramCnt;
 }

 protected IntMap<IntMap<Void>> getTagMap()
 {
  return tagCMap;
 }
 
 public void process(Record rec)
 {
  tagCMap.clear();
  
  for(PartValue pv : rec.getPartValues())
  {
   int pid = pv.getPart().getParameter().getId();
   
   if( ! pv.getPart().isEnum() )  // This is not qualifier
   {
    Variable vr = (Variable)pv.getPart();
    
    if( vr.getType() == Type.TAG )
    {
     tagCMap.put(vr.getParameter().getId(),null);
    }
    
    if( !paramCMap.containsKey(pid) )
     paramCMap.put(pid, null);
   }
   else
   {
    int vid = pv.getPart().getVariant(((VariantPartValue)pv).getVariant()).getId();
    
    if( vid == 0 )
     vid = - pv.getPart().getId();
    
    IntMap<Void> map = paramCMap.get(pid);
    
    if( map == null )
     paramCMap.put(pid, map = IntMapDepot.getMap());
    
    map.put(vid, null);
   }
   
  }

  for(IntMap.Entry<IntMap<Void>> me : paramCMap.entrySet())
  {
   Counter cn = paramCnt.get(me.getKey());

   if(cn == null)
    paramCnt.put(me.getKey(), cn = new Counter(1));
   else
    cn.inc();

   if( me.getValue() != null )
   {
    IntIterator iit = me.getValue().keyIterator();
    
    while( iit.hasNext() )
    {
     Counter scn = cn.getOrCreateSubCounter(iit.next());
     scn.inc();

     if(tagCMap.size() > 0)
     {
      IntIterator iiter = tagCMap.keyIterator();

      while(iiter.hasNext())
       scn.incTag(iiter.next());
     }
    }
    
    IntMapDepot.recycleMap(me.getValue());
   }
   
   if( tagCMap.size() > 0 )
   {
    IntIterator iiter = tagCMap.keyIterator();
    while( iiter.hasNext() )
     cn.incTag(iiter.next());
   }
   
   
  }

  paramCMap.clear();

  Counter cn = paramCnt.get(0);

  if(cn == null)
   paramCnt.put(0, cn = new Counter(1));
  else
   cn.inc();
  
  if( tagCMap.size() > 0 )
  {
   IntIterator iiter = tagCMap.keyIterator();
   while( iiter.hasNext() )
    cn.incTag(iiter.next());
  }
  

 }
}
