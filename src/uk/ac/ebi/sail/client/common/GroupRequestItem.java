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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GroupRequestItem extends RequestItem implements IsSerializable
{
 private List<RequestItem> items = new ArrayList<RequestItem>(5);
 private int depth=-1;
 private String description;
 
 public GroupRequestItem()
 {
  setType(Type.GROUP);
 }
 
// public String getGroupName()
// {
//  return groupName;
// }
//
// public void setGroupName(String groupName)
// {
//  this.groupName = groupName;
// }

 public void addItem(RequestItem it)
 {
  items.add(it);
 }
 
 public List<RequestItem> getItems()
 {
  return items;
 }

 public int getDepth()
 {
  return depth;
 }
 
 public void setDepth( int dp )
 {
  depth = dp;
 }
 
 public void toSerialString( StringBuilder sb )
 {
  if( items.size() == 0 )
   return ;
  
  super.toSerialString(sb);
  sb.append(':');
  sb.append(depth);
  sb.append(':');
  if(getName() != null)
   sb.append(M1codec.encode(getName(), ":;"));
  sb.append(':');
  
  if( items != null && items.size() > 0)
  {
   for( RequestItem v : items )
   {
    sb.append(M1codec.encode(v.toSerialString(),":;"));
    sb.append(':');
   }
   
   sb.setLength( sb.length()-1 );
  }
 }

 @Override
 public String getIconClass()
 {
  return "groupIcon";
 }

 public int getDimention()
 {
  if( items == null )
   return 0;
  
  int dim=0;
  for( RequestItem ri : items )
  {
   if( ri instanceof GroupRequestItem )
    dim+= ((GroupRequestItem)ri).getDimention();
   else
    dim++;
  }
  
  return dim;
 }

 public void setDescription(String string)
 {
  description=string;
 }

 public String getDescription()
 {
  return description;
 }

}
