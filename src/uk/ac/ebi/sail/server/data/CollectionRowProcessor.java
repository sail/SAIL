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

import java.util.Arrays;

import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.SimpleCounter;

import com.pri.log.Log;
import com.pri.util.collection.IntMap;

public class CollectionRowProcessor extends RowProcessor
{
 private int[] pattern;
 private IntMap<SampleCollection> repos;
 
 public CollectionRowProcessor(int rid, int[] pattern,IntMap<SampleCollection> repos)
 {
  super(0, rid);
  this.pattern = pattern==null||pattern.length==0?null:pattern;
  this.repos=repos;
 }
 
 public int[] getCollectionIds()
 {
  return pattern;
 }

 public SimpleCounter processPatterns(RowProcessor[] pat, int offset, SimpleCounter cpos, Record row)
 {
  if( pattern == null || Arrays.binarySearch(pattern, row.getCollectionId()) >= 0 )
  {
   if(cpos.getRef() == null)
    cpos.setRef(new SimpleCounter());

   cpos = cpos.getRef();

   SampleCollection rep = repos.get(row.getCollectionId());
   String repName = null;
   
   if( rep == null )
   {
    Log.error("Unknown collection for record: "+row.getId());
    repName="Unknown";
   }
   else
    repName=rep.getName();
   
   SimpleCounter sc = cpos.getRef(repName);
   
   sc.setVariantID(row.getCollectionId());
   
   sc.inc();
   cpos = sc;
  }
  else
   return null;

  return cpos;
 }

 @Override
 public boolean matchRecord(Record row)
 {
  return pattern == null || Arrays.binarySearch(pattern, row.getCollectionId()) >= 0;
 }
}
