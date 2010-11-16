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

public class Tag implements IsSerializable,Serializable, Identifiable
{
 private int id;
 private String name;
 private String description;
 private Classifier classifier;
 private int count;
 
 public Tag( Classifier c )
 {
  classifier=c;
 }
 
 public Tag(String string)
 {
  name=string;
 }

 public Tag()
 {
 }

 public Tag(Tag t)
 {
  id=t.getId();
  name=t.getName();
  description=t.getDescription();
  count=t.getCount();
 }

 
 public int getId()
 {
  return id;
 }
 public void setId(int id)
 {
  this.id = id;
 }
 public String getName()
 {
  return name;
 }
 public void setName(String name)
 {
  this.name = name;
 }
 public String getDescription()
 {
  return description;
 }
 public void setDescription(String description)
 {
  this.description = description;
 }
 public Classifier getClassifier()
 {
  return classifier;
 }
 public void setClassifier(Classifier classifier)
 {
  this.classifier = classifier;
 }

 public int getCount()
 {
  return count;
 }
 
 public void setCount( int c )
 {
  count=c;
 }

}
