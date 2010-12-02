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
import uk.ac.ebi.sail.server.util.IntMapDepot;

import com.pri.util.collection.ArrayIntList;
import com.pri.util.collection.IntIterator;
import com.pri.util.collection.IntList;
import com.pri.util.collection.IntMap;
import com.pri.util.collection.IntTreeMap;

public class CommonRecordProcessorO implements RecordProcessor
{
 private IntMap<IntMap<Void>> paramCMap = new IntTreeMap<IntMap<Void>>();
 private IntList tagList = new ArrayIntList();
 private IntMap<Counter> paramCnt;
 private RowProcessor[] pat;
 private Collection<RowProcessor> tags;
 
 private boolean andOp;

 private static Class<GroupRowProcessor> grpClass = GroupRowProcessor.class;
 private static Class<ParameterRelationRowProcessor> relClass = ParameterRelationRowProcessor.class;


 public CommonRecordProcessorO( RowProcessor[] pat, Collection<RowProcessor> tags, boolean andOp, IntMap<Counter> res)
 {
  this.andOp=andOp;
  paramCnt=res;
  this.pat=pat;
  this.tags=tags;
 }
 

  public void process(Record rec)
  {
   boolean res = andOp;

   for(int k = 0; k < pat.length; k++)
   {
    if(pat[k] == null)
     continue;

    if(pat[k].getClass() == grpClass)
    {
     boolean groupRes = !andOp;
     for(RowProcessor rp : ((GroupRowProcessor) pat[k]).getSubProcessors())
     {

      if(rp.getClass() == relClass)
      {
       boolean localRes = false;
       
       RowProcessor mainPt=((ParameterRelationRowProcessor)rp).getMainPattern();
       
       if(mainPt.matchRecord(rec))
       {
        localRes = true;
        paramCMap.put(rp.getId(), null);
       }
       else
       {
        for(RowProcessor srp : ((ParameterRelationRowProcessor)rp).getAlternatives())
        {
         if(srp.matchRecord(rec))
         {
          localRes = true;
   
          IntMap<Void> ils = paramCMap.get(rp.getId());
   
          if(ils == null)
          {
           ils = IntMapDepot.getMap();
           paramCMap.put(rp.getId(), ils);
          }
   
          ils.put(srp.getId(), null);
         }
   
        }
       }

       if(localRes)
       {
        if(andOp)
         groupRes = true;
       }
       else
       {
        if(!andOp)
         groupRes = false;
       }
      }
      else if(rp.matchRecord(rec))
      {
       if(andOp)
        groupRes = true;

       paramCMap.put(rp.getId(), null);
      }
      else
      {
       if(!andOp)
        groupRes = false;
      }
     }

     if(groupRes)
     {
      if(!andOp)
       res = true;
     }
     else if(andOp)
      res = false;
    }
    else if(pat[k].getClass() == relClass)
    {
     boolean found = false;
     
     RowProcessor mainPt=((ParameterRelationRowProcessor) pat[k]).getMainPattern();
     
     if(mainPt.matchRecord(rec))
     {
      found = true;
      paramCMap.put(pat[k].getId(), null);
     }
     else
     {
      for(RowProcessor rp : ((ParameterRelationRowProcessor) pat[k]).getAlternatives())
      {
       if(rp.matchRecord(rec))
       {
        found = true;
 
        IntMap<Void> ils = paramCMap.get(pat[k].getId());
 
        if(ils == null)
        {
         ils = IntMapDepot.getMap();
 
         paramCMap.put(pat[k].getId(), ils);
        }
 
        ils.put(rp.getId(), null);
       }
 
      }
     }
     
     if(found)
     {
      if(!andOp)
       res = true;
     }
     else if(andOp)
      res = false;
    }
    else
    {
     if(pat[k].matchRecord(rec))
     {
      if(!andOp)
       res = true;

      paramCMap.put(pat[k].getId(), null);
     }
     else
     {
      if(andOp)
       res = false;
     }
    }

   }

   tagList.clear();
   
   for( RowProcessor trp : tags )
   {
    if( trp.matchRecord(rec) )
     tagList.add( trp.getId() );
   }
   
   
   for(IntMap.Entry<IntMap<Void>> me : paramCMap.entrySet())
   {
    Counter cn = paramCnt.get(me.getKey());

    if(cn == null)
     paramCnt.put(me.getKey(), cn = new Counter(1) );
    else
     cn.inc();
    
    if( tagList.size() > 0 )
    {
     IntIterator iiter = tagList.listIterator();
     while( iiter.hasNext() )
      cn.incTag(iiter.next());
    }

    
    if(me.getValue() != null)
    {
     IntIterator iter = me.getValue().keyIterator();

     while(iter.hasNext())
     {
      int id = iter.next();
      
      cn.inc(id);
      
      if( tagList.size() > 0 )
      {
       IntIterator iiter = tagList.listIterator();
       while( iiter.hasNext() )
        cn.incTagFor( id, iiter.next());
      }
     }

     IntMapDepot.recycleMap(me.getValue());
    }
   }

   paramCMap.clear();

   Counter cn = paramCnt.get(0);

   if(cn == null)
    paramCnt.put(0, cn = new Counter(1));
   else
    cn.inc();

   if( tagList.size() > 0 )
   {
    IntIterator iiter = tagList.listIterator();
    while( iiter.hasNext() )
     cn.incTag(iiter.next());
   }


   if( res )
   {
    cn = paramCnt.get(-1);

    if(cn == null)
     paramCnt.put(-1, cn = new Counter(1));
    else
     cn.inc();
    
    if( tagList.size() > 0 )
    {
     IntIterator iiter = tagList.listIterator();
     while( iiter.hasNext() )
      cn.incTag(iiter.next());
    }


   }
  }
}
