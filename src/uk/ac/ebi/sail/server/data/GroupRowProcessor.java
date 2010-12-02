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

import uk.ac.ebi.sail.client.common.SimpleCounter;

public class GroupRowProcessor extends RowProcessor
{
 private RowProcessor[] procs;
 private int[] pids;
 private int depth;
 
 public GroupRowProcessor(int rid, RowProcessor[] pattern, int d)
 {
  super(0, rid);
  procs = pattern;
  
  pids = new int[ pattern.length ];
  
  for(int i=0; i < pattern.length; i++ )
   pids[i]=pattern[i].paramID;
  
  depth = d==-1?pattern.length:d;
 }
 
 public RowProcessor[] getSubProcessors()
 {
  return procs;
 }
 
 public SimpleCounter processPatterns(RowProcessor[] pat, int offset, SimpleCounter cpos, Record row)
 {

  boolean matched=false;
  
  for( int k=0; k < procs.length; k++ )
  {
   if( procs[k]==null )
    break;
   
   if( procs[k].matchRecord(row) )
   {
    matched=true;
    
    if( cpos.getRef() == null )
     cpos.setRef( new SimpleCounter() );

    SimpleCounter sc = cpos.getRef().getRef(procs[k].getName());
    sc.setParameterID(procs[k].paramID);
    sc.inc();

    for(int i = offset+1; i < pat.length; i++)
    {
     pat[i].processPatterns(pat, i, sc, row);
    }
   }
   
  }

  if( matched )
  {
   SimpleCounter sc = cpos.getRef().getRef("[ANY]");
   sc.setParameterID(SimpleCounter.ANY);
   sc.setParamIDs(pids);
   sc.inc();

   for(int i = offset+1; i < pat.length; i++)
   {
    pat[i].processPatterns(pat, i, sc, row);
   }
  }
  
  return null;
 }
 
 /*
 public SimpleCounter processPatterns1(RowProcessor[] pat, int offset, SimpleCounter cpos, Record row)
 {
  GpPat[] gpp = pattern;

  boolean matched=false;
  
  for( int k=0; k < gpp.length; k++ )
  {
   if( gpp[k]==null )
    break;
   
   if( matchPattern(gpp[k].pat, row) )
   {
    matched=true;
    
    if( cpos.getRef() == null )
     cpos.setRef( new SimpleCounter() );

    SimpleCounter sc = cpos.getRef().getRef(gpp[k].name);
    sc.setParameterID(gpp[k].pid);
    sc.inc();

    for(int i = offset+1; i < pat.length; i++)
    {
     pat[i].processPatterns(pat, i, sc, row);
    }
   }
   
  }

  if( matched )
  {
   SimpleCounter sc = cpos.getRef().getRef("[ANY]");
   sc.setParameterID(SimpleCounter.ANY);
   sc.setParamIDs(pids);
   sc.inc();

   for(int i = offset+1; i < pat.length; i++)
   {
    pat[i].processPatterns(pat, i, sc, row);
   }
  }
  
  return null;
 }

 */

 @Override
 public boolean matchRecord(Record row)
 {
  int matched=0;
  
  for( int k=0; k < procs.length; k++ )
  {
   if( procs[k] == null )
    break;
   
   if( procs[k].matchRecord(row) )
   {
    matched++;
    
    if( matched >= depth )
     break;
   }
   
  }

  return matched >= depth;
 }

 public int getDepth()
 {
  return depth;
 }
}
