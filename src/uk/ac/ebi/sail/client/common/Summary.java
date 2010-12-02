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

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Summary implements Serializable, IsSerializable, Comparable<Summary>
{
 private int id;
 private int count;
 private String comment;
 private Summary[] tagCounters;
 private Summary[] relatedCounters;
  
 public Summary()
 {}

 public Summary( int id )
 {
  this.id = id;
 }
  
 public Summary(int id, int count)
 {
  this.id = id;
  this.count = count;
 }

 public Summary(int id, int count, String cm)
 {
  this.id = id;
  this.count = count;
  comment=cm;
 }

 
 public int getId()
 {
  return id;
 }
 
 public void setId(int id)
 {
  this.id = id;
 }
 
 public int getCount()
 {
  return count;
 }
 
 public void setCount(int count)
 {
  this.count = count;
 }
 
 public Summary[] getTagCounters()
 {
  return tagCounters;
 }

 public void setTagCounters(Summary[] tagCounters)
 {
  this.tagCounters = tagCounters;
 }

 public Summary[] getRelatedCounters()
 {
  return relatedCounters;
 }

 public void setRelatedCounters(Summary[] relatedCounters)
 {
  this.relatedCounters = relatedCounters;
 }

 @Override
 public int compareTo(Summary o)
 {
  return id-o.getId();
 }

 public String getComment()
 {
  return comment;
 }

 public void setComment(String comment)
 {
  this.comment = comment;
 }
 
 

}
