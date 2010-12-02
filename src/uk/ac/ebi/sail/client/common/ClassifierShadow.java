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
import java.util.Collection;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ClassifierShadow implements IsSerializable, Serializable
{
 private int id;
 private String name;
 private String desc;
 
 private boolean allowMulty;
 private boolean mandatory;
 
 private Collection<Tag> tags;
 private Classifier.Target target;
 
 
 private int[] clsfTags;
 
 public ClassifierShadow( )
 {
 }
 
 public ClassifierShadow( Classifier p )
 {
  id=p.getId();
  
  name=p.getName();
  desc=p.getDescription();
  target = p.getTarget();
  allowMulty=p.isAllowMulty();
  mandatory=p.isMandatory();
  
  tags=p.getTags();
  
  Collection<Tag> orgtags = p.getClassificationTags();
  if( orgtags != null && orgtags.size() > 0 )
  {
   clsfTags = new int[orgtags.size()];
   
   int i=0;
   for( Tag ip : orgtags )
    clsfTags[i++]=ip.getId();
  }

 }

 public void setId( int id)
 {
  this.id=id;
 }

 
 public int getId()
 {
  return id;
 }

 public Collection<Tag> getTags()
 {
  return tags;
 }

 public int[] getClassificationTags()
 {
  return clsfTags;
 }

 public String getName()
 {
  return name;
 }

 public String getDesc()
 {
  return desc;
 }

 public Classifier createClassifier()
 {
  Classifier p = new Classifier();

  p.setId(getId());
  p.setName(getName());
  p.setDescription(getDesc());
  p.setTags(getTags());
  p.setTarget(getTarget());
  p.setAllowMulty(isAllowMulty());
  p.setMandatory(isMandatory());
  
  return p;
 }
 
 public void set( Classifier p )
 {
  p.setName( getName() );
  p.setDescription( getDesc() );
  p.setTarget(getTarget());
  p.setAllowMulty(isAllowMulty());
  p.setMandatory(isMandatory());
  
  p.setTags(null);
  if( getTags() != null )
  {
   for( Tag v : getTags() )
    p.addTag(v);
  }
 }

 public Classifier.Target getTarget()
 {
  return target;
 }

 public void setTarget(Classifier.Target target)
 {
  this.target = target;
 }

 public boolean isAllowMulty()
 {
  return allowMulty;
 }

 public void setAllowMulty(boolean allowMulty)
 {
  this.allowMulty = allowMulty;
 }

 public boolean isMandatory()
 {
  return mandatory;
 }

 public void setMandatory(boolean mandatory)
 {
  this.mandatory = mandatory;
 }
}
