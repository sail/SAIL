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
import java.util.List;

import uk.ac.ebi.sail.client.common.ComplexFilter;
import uk.ac.ebi.sail.client.common.IntRange;
import uk.ac.ebi.sail.client.common.Range;
import uk.ac.ebi.sail.client.common.SimpleCounter;
import uk.ac.ebi.sail.client.common.Variant;

public class ComplexFilteredRowProcessor extends ParameterRowProcessor
{
 private int[] parts;
 private int [][] vrnts;
 private List<Range> realRanges;
 private List<IntRange> intRanges;
 
 public ComplexFilteredRowProcessor(int prmID, int rid, int[] ppat, ComplexFilter cf )
 {
  super( prmID, rid, ppat );
  
  realRanges=cf.getRealRanges();
  intRanges = cf.getIntRanges();
  
  List<List<Integer>> variants = cf.getVariants();
  if( variants != null )
  {
   parts = new int[ variants.size() ];
   vrnts = new int[ variants.size() ][];
   
   for( int i=0; i < variants.size(); i++)
   {
    List<Integer> cp = variants.get(i);
    
    parts[i] = cp.get(0);
    
    vrnts[i] = new int[cp.size()-1];
    for( int j=1; j < cp.size(); j++ )
     vrnts[i][j-1] = cp.get(j);
    
    Arrays.sort(vrnts[i]);
   }
  }
  

 }

 
// public ComplexFilteredRowProcessor(int prmID, int[] ppat, int[][] variants)
// {
//  super( prmID, ppat );
//  
//  parts = new int[ variants.length ];
//  vrnts = new int[ variants.length ][];
//  
//  for( int i=0; i < variants.length; i++)
//  {
//   parts[i] = variants[i][0];
//   
//   vrnts[i] = new int[variants[i].length-1];
//   for( int j=1; j < variants[i].length; j++ )
//    vrnts[i][j-1] = variants[i][j];
//   
//   Arrays.sort(vrnts[i]);
//  }
//
// }

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
  if( ! super.matchRecord(row) )
   return false;
  
  if( parts != null )
  {
   for(int i = 0; i < parts.length; i++)
   {
    VariantPartValue pv = (VariantPartValue) row.getPartValue(parts[i]);

    if(pv == null)
     return false;

    Variant key = pv.getPart().getVariant(pv.getVariant());

    if(key == null || Arrays.binarySearch(vrnts[i], key.getId()) < 0)
     return false;
   }
  }
  
  if( realRanges != null )
  {
   for( Range rg : realRanges )
   {
    PartValue pv = row.getPartValue(rg.getPartID());
    
    if(pv == null)
     return false;
    
    if( pv instanceof RealPartValue )
    {
     float val = ((RealPartValue)pv).getRealValue();
     if( ( ! Float.isNaN(rg.getLimitLow()) ) && val < rg.getLimitLow() )
      return false;
     
     if( ( ! Float.isNaN(rg.getLimitHigh()) ) && val > rg.getLimitHigh() )
      return false;
    }
    else if( pv instanceof IntPartValue )
    {
     int val = ((IntPartValue)pv).getIntValue();
     if( ( ! Float.isNaN(rg.getLimitLow()) ) && val < rg.getLimitLow() )
      return false;
     
     if( ( ! Float.isNaN(rg.getLimitHigh()) ) && val > rg.getLimitHigh() )
      return false;
    }

    else
     return false;
   }

  }
  
  if( intRanges != null )
  {
   for( IntRange rg : intRanges )
   {
    PartValue pv = row.getPartValue(rg.getPartID());
    
    if(pv == null)
     return false;
    
    if( pv instanceof IntPartValue )
    {
     int val = ((IntPartValue)pv).getIntValue();
     if( val < rg.getLimitLow() || val > rg.getLimitHigh() )
      return false;
    }
    else if( pv instanceof RealPartValue )
    {
     float val = ((RealPartValue)pv).getRealValue();
     if( val < rg.getLimitLow() || val > rg.getLimitHigh() )
      return false;
    }
    else
     return false;
   }

  }
  return true;
 }
}
