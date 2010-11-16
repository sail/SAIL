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

import com.google.gwt.user.client.rpc.IsSerializable;

public class AlternativeRequestItem extends RequestItem implements IsSerializable
{
 private int[] alt;
 private int parameterId;
 
 public AlternativeRequestItem()
 {
  setType(Type.ALT);
 }
 
 public int[] getAlternativeParameters()
 {
  return alt;
 }
 
 public void setAlternativeParameters( int[] al )
 {
  alt=al;
 }

 protected void toSerialString( StringBuilder sb )
 {
  super.toSerialString(sb);
  
  sb.append(':');
  sb.append(parameterId);
  sb.append(':');
  
  if( alt != null && alt.length > 0)
  {
   for( int v : alt )
   {
    sb.append(v);
    sb.append(',');
   }
   
   sb.setLength( sb.length()-1 );
  }
 }
 


 @Override
 public String getIconClass()
 {
  return "";
 }

 public int getParameterId()
 {
  return parameterId;
 }

 public void setParameterId(int parameterId)
 {
  this.parameterId = parameterId;
 }

}
