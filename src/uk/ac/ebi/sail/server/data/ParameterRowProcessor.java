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

public class ParameterRowProcessor extends RowProcessor
{
 private int[] pattern;
 
 public ParameterRowProcessor(int prmID, int rid, int[] pattern)
 {
  super( prmID, rid );
  this.pattern = pattern;
 }

 public SimpleCounter processPatterns(RowProcessor[] pat, int offset, SimpleCounter cpos, Record row)
 {
  if( ! matchPattern(pattern, row))
   return null;
  
  SimpleCounter sc = cpos.getRef();
 
  if(sc == null)
  {
   sc=new SimpleCounter();
   sc.setParameterID(paramID);
   cpos.setRef(sc);
  }
   
  sc.inc();

  
  return sc;
 }

 @Override
 public boolean matchRecord(Record row)
 {
  return matchPattern(pattern, row);
 }
}
