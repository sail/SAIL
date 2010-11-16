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

package uk.ac.ebi.sail.server.util;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.sail.client.common.Identifiable;

public class SetComparator<T extends Identifiable>
{
 private Collection<T> newItems;
 private Collection<T> delItems;
 private Collection<T> updateItems;



 public static <T extends Identifiable> SetComparator<T> compare( Collection<T> origset, Collection<T> newset )
 {
  SetComparator<T> result = new SetComparator<T>();
  
  if( origset == null && newset == null )
   return result;
  
  result.updateItems=new ArrayList<T>(newset.size());
  
  if( newset != null )
  {
   if( origset == null )
    result.newItems = newset;
   else
   {
    result.newItems = new ArrayList<T>(5);
    
    for( T nv : newset )
    {
     boolean found = false;
     
     for( T ov : origset )
     {
      if( nv.getId() == ov.getId() )
      {
       found=true;
       break;
      }
     }
     
     if( ! found )
     {
      result.newItems.add(nv);
     }
     else
      result.updateItems.add(nv);
    }
    
   }
  }
  
  
  if( origset != null )
  {
   if( newset == null )
    result.delItems = origset;
   else
   {
    result.delItems = new ArrayList<T>(5);
    
    for( T ov : origset )
    {
     boolean found = false;
     
     for( T nv : newset )
     {
      if( nv.getId() == ov.getId() )
      {
       found=true;
       break;
      }
     }
     
     if( ! found )
     {
      result.delItems.add(ov);
     }
    }
    
   }
  }
  
  if( result.updateItems != null && result.updateItems.size() == 0 )
   result.updateItems = null;
  
  if( result.delItems != null && result.delItems.size() == 0 )
   result.delItems = null;

  if( result.newItems != null && result.newItems.size() == 0 )
   result.newItems = null;
  
  return result;
 }
 
 public Collection<T> getNewItems()
 {
  return newItems;
 }
 
 public Collection<T> getItemsToDelete()
 {
  return delItems;
 }

 public Collection<T> getUpdateItems()
 {
  return updateItems;
 }
}
