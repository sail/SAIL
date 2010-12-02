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

package uk.ac.ebi.sail.client.common;

import java.util.List;


public class ComplexFilteredRequestItem extends FilteredRequestItem
{
 private ComplexFilter filter;
 
// public ComplexFilteredRequestItem()
// {
//  setType(Type.CFLT);
// }
 
// public ComplexFilteredRequestItem( ComplexFilter f )
// {
//  setType(Type.CFLT);
//  filter = f;
// }
 protected ComplexFilteredRequestItem()
 {
  super(0);
 }

 public ComplexFilteredRequestItem(String string, ComplexFilter cf)
 {
  super(cf.getParameter().getId());
  
  setType(Type.CFLT);
  filter = cf;
  setName(string);
  
 }

 public ComplexFilteredRequestItem(int paramId, ComplexFilter cf)
 {
  super(paramId);
  setType(Type.CFLT);
  filter = cf;
 }

 public ComplexFilter getFilter()
 {
  return filter;
 }

 public void setFilter(ComplexFilter f)
 {
  filter = f;
  setParameterID(f.getParameter().getId());
 }
 
 public Parameter getParameter()
 {
  return filter.getParameter();
 }
 
 public void setParameter( Parameter p )
 {
  super.setParameter(p);
  filter.setParameter(p);
 }
 
 protected void toSerialString( StringBuilder sb )
 {
  super.toSerialString(sb);
  
  sb.append(':');
  
  if( filter.getRealRanges() != null )
  {
   for( Range rg : filter.getRealRanges() )
   {
    sb.append("R").append(rg.getPartID());
    sb.append(',');

    if( ! Float.isNaN( rg.getLimitLow() ) )
     sb.append(rg.getLimitLow());
    
    sb.append(',');

    if( ! Float.isNaN( rg.getLimitHigh() ) )
     sb.append(rg.getLimitHigh());
    
    sb.append('!');    
   }
  }
  
  if( filter.getIntRanges() != null )
  {
   for( IntRange rg : filter.getIntRanges() )
   {
    sb.append("I").append(rg.getPartID());
    sb.append(',');

    if( rg.getLimitLow() != Integer.MIN_VALUE )
     sb.append(rg.getLimitLow());
    
    sb.append(',');

    if( rg.getLimitHigh() != Integer.MAX_VALUE )
     sb.append(rg.getLimitHigh());
    
    sb.append('!');    
   }
  }

  
  if( filter.getVariants() != null )
  {
   for( List<Integer> vl : filter.getVariants() )
   {
    sb.append("V");
    
    for( int vid : vl )
     sb.append(vid).append(',');
    
    sb.setCharAt(sb.length()-1, '!');
   }
  }
  
  sb.setLength(sb.length()-1);
 }

 @Override
 public String getIconClass()
 {
  return "filteredIcon";
 }

}
