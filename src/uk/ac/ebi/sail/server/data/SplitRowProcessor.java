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
import uk.ac.ebi.sail.client.common.Variant;

public class SplitRowProcessor extends ParameterRowProcessor
{
 private int pattern;
 
 public SplitRowProcessor(int prmID, int rid, int[] paramPat, int pattern)
 {
  super( prmID, rid, paramPat );
  this.pattern = pattern;
 }

 public SimpleCounter processPatterns(RowProcessor[] pat, int offset, SimpleCounter cpos, Record row)
 {
  if( !  super.matchRecord( row ) )
   return null;

  int ptid = pattern;
  // String key = row[col]==null?"[not set]":row[col];

//  PartValue pv = row.getPartValue(pattern.getPartID());
//  
//  if( ! (pv instanceof VariantPartValue) )
//   return false;

  
  VariantPartValue pv = (VariantPartValue)row.getPartValue(ptid);

  if(pv == null)
   return null;

  Variant key = pv.getPart().getVariant(pv.getVariant());

  if(key != null)
  {
   if(cpos.getRef() == null)
    cpos.setRef(new SimpleCounter());

   cpos = cpos.getRef();

   SimpleCounter sc = cpos.getRef(key.getName());

   sc.setParameterID(paramID);
   sc.setPartID(pattern);
   sc.setVariantID(key.getId());

   sc.inc();
   
   
   SimpleCounter asc = cpos.getRef("[ANY]");
   asc.setParameterID(SimpleCounter.ANY);

   asc.inc();

   SimpleCounter rsc = asc;
   for(int i = offset + 1; i < pat.length; i++)
   {
    rsc = pat[i].processPatterns(pat, i, rsc, row);
    if( rsc == null )
     break;
   }

   cpos = sc;
  }
  else
   return null;

  return cpos;
 }

 @Override
 public boolean matchRecord(Record row)
 {
  if( !  super.matchRecord( row ) )
   return false;

  int ptid = pattern;
  // String key = row[col]==null?"[not set]":row[col];

  VariantPartValue pv = (VariantPartValue)row.getPartValue(ptid);

  if(pv == null)
   return false;

  Variant key = pv.getPart().getVariant(pv.getVariant());

  return key != null;
 }
}
