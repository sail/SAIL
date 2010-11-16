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

public class Date2Str
{
 public static String int2DateStr( int dt )
 {
  if( dt == Integer.MIN_VALUE )
   return "-\u221e";

  if( dt == Integer.MAX_VALUE )
   return "+\u221e";
  
  String res=String.valueOf(dt/10000);
  
  int num = (dt%10000)/100;
  
  res+="-";
  
  if( num < 10 )
   res+="0";
  
  res+=num;
  
  num = dt%100;
  
  res+="-";
  
  if( num < 10 )
   res+="0";
  
  res+=num;

  return res;
 }

}
