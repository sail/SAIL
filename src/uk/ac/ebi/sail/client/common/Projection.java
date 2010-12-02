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

import uk.ac.ebi.sail.client.data.Attributed;


public class Projection implements SAILObject, Attributed
{
 private String name;
 private String description;
 private List<Classifier> projLst;
 private int id;
 transient private String clsListStr=null; 
 
 public Projection()
 {}
 
 public Projection(Projection p)
 {
  name=p.getName();
  description=p.getDescription();
  id=p.getId();
  
  if( p.getClassifiers() != null )
   projLst = new ArrayList<Classifier>(p.getClassifiers());
 }

 public String getName()
 {
  return name;
 }
 
 public void setName(String name)
 {
  this.name = name;
 }
 
 public List<Classifier> getClassifiers()
 {
  return projLst;
 }
 
 public void setClassifiers(List<Classifier> projLst)
 {
  this.projLst = projLst;
  clsListStr=null;
 }

 public void addClassifier(Classifier classifier)
 {
  if( projLst == null )
   projLst = new ArrayList<Classifier>(5);
  
  projLst.add(classifier);
  
  clsListStr=null;
 }

 public void setId(int int1)
 {
  id=int1;
 }

 public String getDescription()
 {
  return description;
 }

 public void setDescription(String description)
 {
  this.description = description;
 }

 public int getId()
 {
  return id;
 }
 
 public String getClassifiersString()
 {
  if( clsListStr != null )
   return clsListStr;
  
  StringBuilder sb = new StringBuilder();

  sb.append(" [");
  
  for( Classifier cl :  getClassifiers() )
   sb.append(cl.getName()).append(", ");
  
  sb.setCharAt(sb.length()-2, ']');
  sb.setLength(sb.length()-1);
  return clsListStr = sb.toString();
 }

 public void removeClassifier(Classifier cl)
 {
  if( projLst != null )
   projLst.remove(cl);
  
  clsListStr=null;
 }

 public void moveClassifier(int i, Classifier cl)
 {
  if( projLst == null )
   return;  
  
  projLst.remove(cl);
  projLst.add(i, cl);
  
  clsListStr=null;
 }

 public String getAttribute(String atName)
 {
  if( "name".equals(atName) )
   return getName();
  else if( "description".equals(atName) )
   return getDescription();
  else if( "classifiers".equals(atName) )
   return getClassifiersString();
  
  return null;
 }
 
}
