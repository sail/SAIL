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

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


public class ReportRequest implements IsSerializable
{
 private List<Integer> relations;
 private boolean allRelations;
 private boolean collectionSplit=false;
 private int[] collections;

// private boolean isAndOp;

 private GroupRequestItem rootGroup = new GroupRequestItem();
 
// private List<ReportRequestItem> rrItems=new ArrayList<ReportRequestItem>();

 public void add(RequestItem rspl)
 {
  rootGroup.addItem(rspl);
 }

 public void setRelations(List<Integer> rels)
 {
  relations = rels;
 }

 public List<Integer> getRelations()
 {
  return relations;
 }

// public List<ReportRequestItem> getReportRequestItems()
// {
//  return rrItems;
// }

 public GroupRequestItem getRootGroup()
 {
  return rootGroup;
 }
 

 public void setRootGroup(GroupRequestItem ri)
 {
  rootGroup=ri;
 }
 
 public boolean isAllRelations()
 {
  return allRelations;
 }

 public void setAllRelations(boolean allRelations)
 {
  this.allRelations = allRelations;
 }

// public void setAndOperation(boolean isStateAnd)
// {
//  isAndOp = isStateAnd;
// }
//
// public boolean isAndOperation()
// {
//  return isAndOp;
// }
 

 public void setCollectionSplit(boolean b)
 {
  collectionSplit=b;
 }

 public boolean isCollectionSplit()
 {
  return collectionSplit;
 }

 public int[] getCollections()
 {
  return collections;
 }

 public void setCollections(int[] collections)
 {
  this.collections = collections;
 }

 public String toSerialString()
 {
  StringBuilder sb = new StringBuilder();
  
  sb.append("REQ:");
  
  if( isAllRelations() )
   sb.append("REL:-1:");
  else if( relations != null && relations.size() > 0 )
  {
   sb.append("REL:");
   
   for( int rt : relations )
    sb.append(rt).append(',');
   
   sb.setCharAt(sb.length()-1, ':');
  }
  
  if( isCollectionSplit() )
  {
   if(collections != null && collections.length > 0)
   {
    sb.append("COL:");

    for(int rt : collections)
     sb.append(rt).append(',');

    sb.setCharAt(sb.length() - 1, ';');
   }
   else
    sb.append("COL:-1;");
  }
  sb.setCharAt(sb.length()-1, ';');

  String gReq = rootGroup.toSerialString();
  
  if( gReq.length() > 0 )
   sb.append(gReq);
  else
   sb.setLength(sb.length()-1);
  
  return sb.toString();
 }

}
