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

import uk.ac.ebi.sail.server.util.Counter;

import com.pri.util.collection.IntIterator;
import com.pri.util.collection.IntMap;

//public class StudySampleRecordProcessor implements RecordProcessor
public class StudySampleRecordProcessor extends CollectionSummaryRecordProcessor
{
 private int studyID;
 private boolean isEligible;

 public StudySampleRecordProcessor(int studyID, boolean isEligible, IntMap<Counter> res)
 {
  super(res);
  
  this.studyID=studyID;
  this.isEligible=isEligible;
 }

 @Override
 public void process(Record r)
 {
  int[] sts = null;
  
  if( isEligible )
   sts = r.getPreStudies();
  else
   sts = r.getPostStudies();

  if( sts == null || Arrays.binarySearch(sts, studyID) < 0 )
   return;
  
  super.process(r);
  
  
  IntMap<Counter> paramCnt = getCounterMap();
  IntMap<IntMap<Void>> tagCMap = getTagMap();
  
  Counter cn = paramCnt.get(-r.getCollectionId());

  if(cn == null)
   paramCnt.put(-r.getCollectionId(), cn = new Counter(1));
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
