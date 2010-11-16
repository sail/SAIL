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
import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.rpc.IsSerializable;

public class StudyShadow implements IsSerializable,Serializable
{
 private int id;
 private String name;
 private Collection<AnnotationShadow> annts;
 private Collection<Integer> collections=new ArrayList<Integer>( 5 );
 private int collectionsSamples=0;
 private int eligibleSamples=0;
 private int selectedSamples=0;
 private int individuals=0;
 private long updateTime;
 
 public StudyShadow()
 {}
 
 public StudyShadow(Study rp)
 {
  id=rp.getId();
  name = rp.getName();
  
  collectionsSamples = rp.getCollectionsSamples();
  eligibleSamples = rp.getEligibleSamples();
  selectedSamples = rp.getSelectedSamples();
  
  individuals = rp.getIndividuals();
  updateTime = rp.getUpdateTime();
  
  if( rp.getAnnotations() != null )
  {
   annts = new ArrayList<AnnotationShadow>(  rp.getAnnotations().size() );
   
   for( Annotation an :  rp.getAnnotations() )
   {
    annts.add( new AnnotationShadow( an ) );
   }
  }
  
  if( rp.getCollections() != null )
  {
   collections = new ArrayList<Integer>( rp.getCollections().size() );
   
   for( SampleCollection ch : rp.getCollections() )
    collections.add(ch.getId());
  }
 }
 
 public Collection<Integer> getCollections()
 {
  return collections;
 }
 
 public void addCollection( int cohId )
 {
  if( collections == null )
   collections = new ArrayList<Integer>(5);
  
  collections.add(cohId);
 }

 public void setUpdateTime(long updateTime)
 {
  this.updateTime = updateTime;
 }

 public Study createStudy()
 {
  Study rp = new Study();
  
  rp.setId(id);
  rp.setName(name);
  rp.setCollectionsSamples(collectionsSamples);
  rp.setEligibleSamples(eligibleSamples);
  rp.setSelectedSamples(selectedSamples);
  rp.setIndividuals(individuals);
  rp.setUpdateTime(updateTime);
  
  return rp;
 }

 public Collection<AnnotationShadow> getAnnotations()
 {
  return annts;
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

 public void addAnotationShadow(AnnotationShadow ans)
 {
  if( annts == null )
   annts = new ArrayList<AnnotationShadow>(5);
  
  annts.add(ans);
  
 }

 public void setAnnotations(Collection<AnnotationShadow> annotations)
 {
  annts=annotations;
 }


 public int getIndividuals()
 {
  return individuals;
 }

 public long getUpdateTime()
 {
  return updateTime;
 }
 
 public void incIndividuals()
 {
  this.individuals++;
 }


 public void setCollections(Collection<Integer> collss2)
 {
  collections=collss2;
 }

 public int getCollectionsSamples()
 {
  return collectionsSamples;
 }

 public int getEligibleSamples()
 {
  return eligibleSamples;
 }

 public int getSelectedSamples()
 {
  return selectedSamples;
 }

 public void setCollectionsSamples(int collectionsSamples)
 {
  this.collectionsSamples = collectionsSamples;
 }

 public void setEligibleSamples(int eligibleSamples)
 {
  this.eligibleSamples = eligibleSamples;
 }

 public void setSelectedSamples(int selectedSamples)
 {
  this.selectedSamples = selectedSamples;
 }
}
