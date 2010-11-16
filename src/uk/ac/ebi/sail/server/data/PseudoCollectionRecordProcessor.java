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
import java.util.Collection;

import uk.ac.ebi.sail.server.util.Counter;

import com.pri.util.collection.IntMap;

public class PseudoCollectionRecordProcessor extends CommonRecordProcessor
{
 private int studyID;
 private boolean isEligible;

 public PseudoCollectionRecordProcessor(RowProcessor pat, Collection<RowProcessor> tags, IntMap<Counter> res, int studyID, boolean isEligible)
 {
  super(pat, tags, res);

  this.studyID=studyID;
  this.isEligible=isEligible;
 }

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
 }
}
