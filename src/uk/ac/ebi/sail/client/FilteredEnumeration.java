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

package uk.ac.ebi.sail.client;

import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterPart;

import com.google.gwt.user.client.rpc.IsSerializable;


public class FilteredEnumeration implements IsSerializable
{
 private Parameter param;
 private ParameterPart enumeration;
 private int[] values;
 private transient int nVaris=0;
 
 public FilteredEnumeration()
 {}
 
 public FilteredEnumeration(Parameter p, ParameterPart qualifier, int[] values)
 {
  super();
  this.enumeration = qualifier;
  this.values = values;
  param=p;
 }

 public ParameterPart getEnumeration()
 {
  return enumeration;
 }

 public void setEnumeration(ParameterPart qualifier)
 {
  this.enumeration = qualifier;
 }
 
 public int[] getVariants()
 {
  if( values == null || nVaris == values.length )
   return values;
  
  int[] res = new int[nVaris];
  
  for( int i=0; i < nVaris; i++ )
   res[i]=values[i];
  
  values=res;
  
  return res;
 }
 
 public void setVariants(int[] values)
 {
  this.values = values;
  nVaris=values.length;
 }

 public void addVariant(int v)
 {
  if( values == null || nVaris == values.length )
  {
   int[] newvals = new int[nVaris+10];
   
   if( nVaris > 0 )
   {
    for( int i=0; i < nVaris; i++ )
     newvals[i]=values[i];
   }
   
   values = newvals;
  }
  
  values[nVaris++]=v;
 }

 public Parameter getParameter()
 {
  return param;
 }

 public void setParameter(Parameter param)
 {
  this.param = param;
 }
}
