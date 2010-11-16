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



public class ProjectionShadow implements IsSerializable,Serializable
{
 private String name;
 private String description;
 private int[] projLst;
 private int id;
 
 public ProjectionShadow()
 {}
 
 public ProjectionShadow( Projection pj )
 {
  name=pj.getName();
  description=pj.getDescription();
  id=pj.getId();
  
  if( pj.getClassifiers() != null )
  {
   projLst = new int[pj.getClassifiers().size()];
   
   int i=0;
   for( Classifier cl  : pj.getClassifiers())
    projLst[i++]=cl.getId();
  }
 }
 
 public String getName()
 {
  return name;
 }
 
 public void setName(String name)
 {
  this.name = name;
 }
 
 public int[] getClassifiers()
 {
  return projLst;
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

 public Projection createProjection()
 {
  Projection pj = new Projection();
  
  pj.setId(getId());
  pj.setName(getName());
  pj.setDescription(getDescription());
  
  return pj;
 }
}
