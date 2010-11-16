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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.sail.client.common.ParameterPart;

import com.pri.util.collection.IntMap;
import com.pri.util.collection.IntTreeMap;

public class ExportReport
{
 private IntMap<ParameterPart> pMap = new IntTreeMap<ParameterPart>();
 private Collection<Record> records = new ArrayList<Record>();
 private transient List<ParameterPart> pList = null;
 
 public void add(Record row)
 {
  records.add(row);
  
  for( PartValue pv : row.getPartValues() )
  {
   pMap.put(pv.getPartID(), pv.getPart());
   pList=null;
  }
 }

 public Collection<Record> getRecords()
 {
  return records;
 }

 public List<ParameterPart> getParameterParts()
 {
  if( pList != null )
   return pList;
  
  pList = new ArrayList<ParameterPart>( pMap.values() );
  
  return pList;
 }

}
