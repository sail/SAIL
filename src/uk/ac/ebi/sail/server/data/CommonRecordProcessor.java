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

import java.util.Collection;

import uk.ac.ebi.sail.server.util.Counter;

import com.pri.util.collection.ArrayIntList;
import com.pri.util.collection.IntIterator;
import com.pri.util.collection.IntList;
import com.pri.util.collection.IntMap;

public class CommonRecordProcessor implements RecordProcessor
{
 // private IntMap<IntMap<Void>> paramCMap = new IntTreeMap<IntMap<Void>>();
 private IntList                                     tagList  = new ArrayIntList();
 private IntMap<Counter>                             paramCnt;
 private RowProcessor                             pat;
 private Collection<RowProcessor>                    tags;

 private static Class<GroupRowProcessor>             grpClass = GroupRowProcessor.class;
 private static Class<ParameterRelationRowProcessor> relClass = ParameterRelationRowProcessor.class;

 public CommonRecordProcessor(RowProcessor pat, Collection<RowProcessor> tags, IntMap<Counter> res)
 {
  paramCnt = res;
  this.pat = pat;
  this.tags = tags;
 }

 private boolean processRelation( Record rec, ParameterRelationRowProcessor rproc )
 {
  int lrid = rproc.getRequestId();
  
  RowProcessor mainPt = rproc.getMainPattern();

  Counter cn = paramCnt.get(lrid);

  boolean localRes = false;
  
  if(mainPt.matchRecord(rec))
  {
   localRes = true;
  }
  else
  {
   for(RowProcessor srp : rproc.getAlternatives())
   {
    if(srp.matchRecord(rec))
    {
     localRes = true;

     if(cn == null)
      paramCnt.put(lrid, cn = new Counter(1));

     int srid = srp.getRequestId();

     cn.inc(srid);

     if(tagList.size() > 0)
     {
      IntIterator iiter = tagList.listIterator();
      while(iiter.hasNext())
       cn.incTagFor(srid, iiter.next());
     }
    }
   }
  }

  if(localRes)
  {
   if(cn == null)
    paramCnt.put(lrid, cn = new Counter(1));
   else
    cn.inc();

   if(tagList.size() > 0)
   {
    IntIterator iiter = tagList.listIterator();
    while(iiter.hasNext())
     cn.incTag(iiter.next());
   }
  }

  return localRes;
 }
 
 private boolean processGroup( Record rec, GroupRowProcessor gproc )
 {
  int trueCnt=0;

  for(RowProcessor rp : gproc.getSubProcessors())
  {
   int lrid = rp.getRequestId();

   if(rp.getClass() == relClass)
   {
    if( processRelation(rec, (ParameterRelationRowProcessor) rp ) )
     trueCnt++;
   }
   else if( rp.getClass() == grpClass )
   {
    if( processGroup(rec, (GroupRowProcessor)rp) )
     trueCnt++;
   }
   else if(rp.matchRecord(rec))
   {
    trueCnt++;
    
    Counter cn = paramCnt.get(lrid);

    if(cn == null)
     paramCnt.put(lrid, cn = new Counter(1));
    else
     cn.inc();

    if(tagList.size() > 0)
    {
     IntIterator iiter = tagList.listIterator();
     while(iiter.hasNext())
      cn.incTag(iiter.next());
    }
   }
  }
  
 
  return trueCnt >= gproc.getDepth();
 }

 
 public void process(Record rec)
 {
  boolean res = false;

  tagList.clear();

  for(RowProcessor trp : tags)
  {
   if(trp.matchRecord(rec))
    tagList.add(trp.getId());
  }


   int reqId = pat.getRequestId();

   if(pat.getClass() == grpClass)
   {
    res = processGroup(rec, (GroupRowProcessor)pat);
   }
   else if(pat.getClass() == relClass)
   {
    res = processRelation(rec, (ParameterRelationRowProcessor)pat);
   }
   else
   {
    if(pat.matchRecord(rec))
    {
     res = true;

     Counter cn = paramCnt.get(reqId);

     if(cn == null)
      paramCnt.put(reqId, cn = new Counter(1));
     else
      cn.inc();

     if(tagList.size() > 0)
     {
      IntIterator iiter = tagList.listIterator();
      while(iiter.hasNext())
       cn.incTag(iiter.next());
     }

    }
   }

  Counter cn = paramCnt.get(0);

  if(cn == null)
   paramCnt.put(0, cn = new Counter(1));
  else
   cn.inc();

  if(tagList.size() > 0)
  {
   IntIterator iiter = tagList.listIterator();
   while(iiter.hasNext())
    cn.incTag(iiter.next());
  }

  if(res)
  {
   cn = paramCnt.get(-1);

   if(cn == null)
    paramCnt.put(-1, cn = new Counter(1));
   else
    cn.inc();

   if(tagList.size() > 0)
   {
    IntIterator iiter = tagList.listIterator();
    while(iiter.hasNext())
     cn.incTag(iiter.next());
   }

  }
 }
}
