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

import com.pri.util.collection.IntMap;
import com.pri.util.collection.IntTreeMap;

public class ExportReport2
{
 private IntMap<ExportReport> exMap = new IntTreeMap<ExportReport>();

 private int cKh=-1;
 private ExportReport cBunch;
 
 public void add(int khId, Record row)
 {
  if( khId != cKh )
  {
   cBunch = exMap.get(khId);
   
   if( cBunch == null )
   {
    cBunch = new ExportReport();
    exMap.put(khId, cBunch);
   }
   
   cKh=khId;
  }
  
  cBunch.add(row);
  
 }

 public IntMap<ExportReport> getResult()
 {
  return exMap;
 }

 public int getRecordsCount()
 {
  int acc = 0;
  
  for( ExportReport er : exMap.values() )
   acc+=er.getRecords().size();

  return acc;
 }

}
