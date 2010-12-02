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
import java.util.Collection;

import uk.ac.ebi.sail.client.data.Attributed;

public class SampleCollection implements Annotated,SAILObject,Attributed
{
 private int id;

 private String name; 
 private Collection<Annotation> annots;
 private int samples=0;
 private int individuals=0;
 private long updateTime;
 
 public SampleCollection()
 {
 }
 
 public SampleCollection(int id, String name)
 {
  this.id=id;
  this.name=name;
 }

 public SampleCollection(SampleCollection p)
 {
  id=p.getId();
  name=p.getName();
  
  if( p.annots != null )
  {
   annots = new ArrayList<Annotation>( p.annots.size() );
   
   for( Annotation an : p.annots )
    annots.add( new Annotation(an) );
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

 
 
 public void addAnnotation(Annotation p)
 {
  if( annots == null )
   annots=new ArrayList<Annotation>();
  
  annots.add(p);
 }

 public Collection<Annotation> getAnnotations()
 {
  return annots;
 }

 public void removeAnnotation(Annotation ant)
 {
  if( annots != null )
   annots.remove(ant);
 }

 public int getId()
 {
  return id;
 }

 public void setId(int id)
 {
  this.id = id;
 }

 public void setAnnotations(Collection<Annotation> annotations)
 {
  annots = annotations;
 }

 public Collection<Annotation> getAnnots()
 {
  return annots;
 }

 public void setAnnots(Collection<Annotation> annots)
 {
  this.annots = annots;
 }

 public int getSampleCount()
 {
  return samples;
 }

 public void setSampleCount(int samples)
 {
  this.samples = samples;
 }

 public void incSampleCount()
 {
  this.samples++;
 }
 
 public int getIndividualCount()
 {
  return individuals;
 }

 public void setIndividualCount(int individuals)
 {
  this.individuals = individuals;
 }
 
 public void incIndividualCount()
 {
  this.individuals++;
 }

 public long getUpdateTime()
 {
  return updateTime;
 }

 public void setUpdateTime(long updateTime)
 {
  this.updateTime = updateTime;
 }

 public String getAttribute(String atName)
 {
  if( "name".equals(atName) )
   return getName();

  
  return null;
 }


}
