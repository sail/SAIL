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

import uk.ac.ebi.sail.client.common.SimpleCounter;

public class ParameterAlternativeRowProcessor extends RowProcessor
{
 private Collection<RowProcessor> patterns;
 private int[] pids;
 
 protected ParameterAlternativeRowProcessor(int mainP, int rid, int[] pids, Collection<RowProcessor> pats)
 {
  super(mainP, rid);
  patterns=pats;
  this.pids=pids;
 }

 public void merge( ParameterAlternativeRowProcessor mp )
 {
  patterns.addAll(mp.patterns);
  
  int[] pidList = new int[pids.length+mp.pids.length];
  
  System.arraycopy(pids, 0, pidList, 0, pids.length);
  System.arraycopy(mp.pids, 0, pidList, pids.length, mp.pids.length);
  
  pids = pidList;
 }
 
 @Override
 public boolean matchRecord(Record row)
 {
  for( RowProcessor pt : patterns )
   if( pt.matchRecord( row ) )
    return true;
  
  return false;
 }

 @Override
 public SimpleCounter processPatterns(RowProcessor[] pat, int offset, SimpleCounter cpos, Record row)
 {
  boolean matched = false;
  
  for( RowProcessor pt : patterns )
  {
   if( pt.matchRecord( row ) )
   {
    matched = true;
    break;
   }
  }
  
  if( ! matched )
   return null;

  if(cpos.getRef() == null)
   cpos.setRef(new SimpleCounter());

  cpos = cpos.getRef();
  
  cpos.setParameterID(paramID);
  cpos.setParamIDs(pids);
  
  cpos.inc();
  
  return cpos;
 }

 public void add(RowProcessor crp)
 {
  patterns.add(crp);
  
  int[] pidList = new int[pids.length+1];
  
  System.arraycopy(pids, 0, pidList, 0, pids.length);
  pidList[pids.length]=crp.paramID;
  
  pids = pidList;
 }

 public Collection<RowProcessor> getAlternatives()
 {
  return patterns;
 }

}
