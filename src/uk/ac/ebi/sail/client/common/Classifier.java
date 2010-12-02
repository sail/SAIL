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

import uk.ac.ebi.sail.client.data.Attributed;
import uk.ac.ebi.sail.client.ui.TreeModelNode;

public class Classifier implements Serializable, Classifiable, SAILObject, Attributed
{
 public static enum Target
 {
  PARAMETER("Parameter"),
  CLASSIFIER("Classifier"),
  RELATION("Relation"),
  COLLECTION_ANN("Collection annotation"),
  STUDY_ANN("Study annotation"),
  PARAMETER_ANN("Parameter annotation");
  
  private final String text;
  Target( String nm )
  {
   text=nm;
  }
  
  public String getText()
  {
   return text;
  }
 }
 
 private int id;
 private String name;
 private String description;
 
 private Collection<Tag> tags;
 private Collection<Tag> classificationTags;
 private boolean isMandatory;
 private boolean allowMulty;
 private Target target;
 

 public Classifier()
 {}
 
 public Classifier(Classifier cl)
 {
  id=cl.getId();
  name = cl.getName();
  description = cl.getDescription();
  isMandatory=cl.isMandatory();
  allowMulty = cl.isAllowMulty();
  target=cl.getTarget();
  
  if( cl.getTags() != null )
  {
   tags = new ArrayList<Tag>(cl.getTags().size());
   for( Tag t : cl.getTags() )
   {
    Tag nt = new Tag(t);
    nt.setClassifier(this);
    tags.add(nt);
   }
  }
  
  if( cl.getClassificationTags() != null )
   classificationTags = new ArrayList<Tag>( cl.getClassificationTags() );
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
 
 public void addTag( Tag t )
 {
  if( tags == null )
   tags=new ArrayList<Tag>(10);
  
  tags.add(t);
  t.setClassifier(this);
 }

 public void removeTag(Tag t)
 {
  if( tags != null )
   tags.remove(t);
 }
 public void addClassificationTag( Tag t )
 {
  if( classificationTags == null )
   classificationTags=new ArrayList<Tag>(10);
  
  classificationTags.add(t);
 }

 public Collection<Tag> getTags()
 {
  return tags;
 }

 public Collection<Tag> getClassificationTags()
 {
  return classificationTags;
 }

 public void setMandatory(boolean m)
 {
  isMandatory=m;
 }
 
 public void setAllowMulty( boolean m )
 {
  allowMulty=m;
 }
 
 public boolean isMandatory()
 {
  return isMandatory;
 }

 public boolean isAllowMulty()
 {
  return allowMulty;
 }

 public void setTags(Collection<Tag> tagset)
 {
  if( tagset != null )
  {
   tags=tagset;
   for( Tag t : tags )
    t.setClassifier(this);
  }
  else
   tags=null;
  
 }

 public void setClassificationTags(Collection<Tag> classificationTags2)
 {
  classificationTags=classificationTags2;
 }

 public Target getTarget()
 {
  return target;
 }

 public void setTarget(Target target)
 {
  this.target = target;
 }

 public void removeClassificationTag(Tag asObject)
 {
  if( classificationTags != null )
   classificationTags.remove(asObject);
  
 }

 public String toString()
 {
  return name;
 }

 public String getAttribute(String atName)
 {
  if("name".equals(atName))
   return getName();
  else if("description".equals(atName))
   return getDescription();
  else if("type".equals(atName))
   return getTarget().getText();
   
  return null;
 }

 @Override
 public TreeModelNode getStructure()
 {
  TreeModelNode cn = new TreeModelNode(getName(),"classifierIcon");
  
  if( tags != null )
  {
   for( Tag t : tags )
    cn.addSubNode(new TreeModelNode(t.getName(),"tagIcon") );
  }
  
  return cn;
 }
 
}
