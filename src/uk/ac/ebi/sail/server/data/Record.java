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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Record
{
// private static Comparator<PartValue> partComparator = new Comparator<PartValue>()
// {
//   public int compare(PartValue o1, PartValue o2)
//   {
//    return o1.getPartID()-o2.getPartID();
//   }
// };
 
 private int id;
 private int counter;
// private List<String> sampleIDs;
 private int collectionID;

 private int[] preStudyIDs;
 private int[] postStudyIDs;
 
 private String reposIDs; 

 private PartValue[] values;
 private List<PartValue> pvs;
 
 public Record()
 {
 }

 public void setCount(int c)
 {
  counter=c;
 }
 
 public int getCount()
 {
  return counter;
 }


 public void setId(int i)
 {
  id=i;
 }

 public void setCollectionId(int int1)
 {
  collectionID=int1;
 }
 
 public int getCollectionId()
 {
  return collectionID;
 }

 public String getCollectionRecordIDs()
 {
  return reposIDs;
 }

 public void setCollectionRecordIDs(String ids)
 {
  reposIDs=ids;
 }

 public int getId()
 {
  return id;
 }

 public void addPartValue(PartValue pv)
 {
  if( pvs == null )
  {
   pvs = new ArrayList<PartValue>(20);
  }
  
  if( values != null )
  {
   pvs.addAll(Arrays.asList(values));
   values=null;
  }
  
  pvs.add(pv);
 }
 
 public void completeRecord()
 {
  if( pvs == null )
  {
   if( values == null )
    return;
   
   int emptyCount=0;
   
   for(PartValue pv : values)
   {
    if( pv instanceof EmptyPartValue )
     emptyCount++;
   }
   
   if( emptyCount > 0 )
   {
    PartValue[] newar = new PartValue[values.length-emptyCount];
    
    int i=0;
    for( PartValue pv : values )
     if( ! ( pv instanceof EmptyPartValue ) )
      newar[i++]=pv;
    
    values=newar;
   }
   
   return;
  }
  
  int emptyCount=0;
  
  for(PartValue pv : pvs)
  {
   if( pv instanceof EmptyPartValue )
    emptyCount++;
  }

  
  values = new PartValue[pvs.size()-emptyCount];
//  pids = new int[pvs.size()];
  
  int i=0;
  for(PartValue pv : pvs)
  {
   if( ! ( pv instanceof EmptyPartValue ) )
    values[i++]=pv;
  }
  
  Arrays.sort(values);
  
  pvs=null;
  
  if( preStudyIDs != null )
   Arrays.sort(preStudyIDs);

  if( postStudyIDs != null )
   Arrays.sort(postStudyIDs);
 }

// public PartValue getPartValue(int ptid)
// {
//  int idx = Arrays.binarySearch(pids, ptid);
//  
//  if( idx >= 0 )
//   return values[idx];
//  
//  return null;
// }
 
 public PartValue getPartValue(int ptid)
 {
  if( pvs != null )
  {
   for( int i=0; i < pvs.size(); i++ )
   {
    PartValue pv = pvs.get(i);
    if( pv.getPartID() == ptid )
     return pv;
   }
   
   return null;
  }
  
  if( values == null )
   return null;
  
  int idx = binarySearchPart(ptid);
  
  if( idx >= 0 )
   return values[idx];
  
  return null;
 }
 
 public boolean hasPart(int ptid)
 {
  return getPartValue( ptid ) != null ;
 }
 
 public Collection<PartValue> getPartValues()
 {
  if( pvs != null )
   return pvs;
  
  if( values != null )
   return Arrays.asList(values);
  
  return Collections.emptyList();
 }
 
// public int[] getPartIds()
// {
//  return pids;
// }
 
 private int binarySearchPart(int partIdKey) 
 {
  int low = 0;
  int high = values.length - 1;

  while(low <= high)
  {
   int mid = (low + high) >>> 1;
   int cmp = values[mid].getPartID()-partIdKey;

   if(cmp < 0)
    low = mid + 1;
   else if(cmp > 0)
    high = mid - 1;
   else
    return mid; // key found
  }
  return -(low + 1); // key not found.
 }

 public void removePartValue(PartValue opv)
 {
  if( pvs != null )
  {
   pvs.remove(opv);
   return;
  }
  
  pvs = new ArrayList<PartValue>( values.length );
  
  for( PartValue pv : values )
  {
   if( pv != opv )
    pvs.add(pv);
  }
  
  values=null;
 }
 
 public int[] getPreStudies()
 {
  return preStudyIDs;
 }

 public void setPreStudies(int[] preStudyIDs)
 {
  this.preStudyIDs = preStudyIDs;
 }

 public int[] getPostStudies()
 {
  return postStudyIDs;
 }

 public void setPostStudies(int[] postStudyIDs)
 {
  this.postStudyIDs = postStudyIDs;
 }
}
