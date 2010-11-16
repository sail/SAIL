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

import uk.ac.ebi.sail.client.common.EnumFilteredRequestItem;
import uk.ac.ebi.sail.client.common.SimpleCounter;
import uk.ac.ebi.sail.client.common.Variant;

public class FilteredRowProcessor extends ParameterRowProcessor
{
 private EnumFilteredRequestItem pattern;
 
 public FilteredRowProcessor(int prmID, int rid, int[] paramPat, EnumFilteredRequestItem pattern)
 {
  super( prmID, rid, paramPat );
  this.pattern = pattern;
 }

 public SimpleCounter processPatterns(RowProcessor[] pat, int offset, SimpleCounter cpos, Record row)
 {
  if( ! matchRecord(row) )
   return null;
  
  if(cpos.getRef() == null)
   cpos.setRef(new SimpleCounter());

  cpos = cpos.getRef();
  
  cpos.setParameterID(paramID);
  
  cpos.inc();
  
  return cpos;
 }

 @Override
 public boolean matchRecord(Record row)
 {
  if( !  super.matchRecord( row) )
   return false;
  
//  PartValue pv = row.getPartValue(pattern.getPartID());
//  
//  if( ! (pv instanceof VariantPartValue) )
//   return false;
  
  VariantPartValue pv = (VariantPartValue)row.getPartValue(pattern.getPartID());
  
  if(pv == null)
   return false;

  Variant key = pv.getPart().getVariant( ((VariantPartValue)pv).getVariant());

  if(key != null && Arrays.binarySearch(pattern.getVariants(), key.getId()) >= 0 )
   return true;
  
  return false;
 }
 
 
}
