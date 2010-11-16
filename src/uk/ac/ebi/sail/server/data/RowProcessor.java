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

public abstract class RowProcessor
{
 protected int requestID;
 protected int paramID;
 private String name;
 
 protected RowProcessor( int pid, int rid )
 {
  paramID=pid;
  requestID=rid;
 }
 
 public abstract SimpleCounter processPatterns( RowProcessor[] pat, int offset, SimpleCounter cpos, Record row);
 public abstract boolean matchRecord( Record row );
 
 protected boolean matchPattern(int[] cls, Record row )
 {
  if( cls == null || cls.length == 0 )
   return false;
  
  for(int j = 0; j < cls.length; j++)
  {
   if( ! row.hasPart(cls[j]) )
    return false;
  }
  
  return true;
 }

 public void setName(String name)
 {
  this.name = name;
 }

 public String getName()
 {
  return name;
 }
 
 public int getId()
 {
  return paramID;
 }

 public int getRequestId()
 {
  return requestID;
 }

}
