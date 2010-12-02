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

import uk.ac.ebi.sail.client.FilteredEnumeration;

public class EnumFilteredRequestItem extends PartRequestItem
{
 private int[] variants;
 
 public EnumFilteredRequestItem(String string, FilteredEnumeration fe)
 {
  super(string, fe.getEnumeration());
  setType(Type.FILTERED);
  variants=fe.getVariants();
 }


 public EnumFilteredRequestItem(int paramId, int partId, int[] altsIDs)
 {
  super(paramId, partId);
  
  variants = altsIDs;
 }


 public int[] getVariants()
 {
  return variants;
 }

 public void setVariants(int[] variants)
 {
  this.variants = variants;
 }
 
 protected void toSerialString( StringBuilder sb )
 {
  
  super.toSerialString(sb);
  sb.append(':');
  
  if( variants != null && variants.length > 0)
  {
   for( int v : variants )
   {
    sb.append(v);
    sb.append(',');
   }
   
   sb.setLength( sb.length()-1 );
  }
 }

}
