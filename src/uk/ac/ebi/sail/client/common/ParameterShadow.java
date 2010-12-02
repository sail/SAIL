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

public class ParameterShadow implements IsSerializable, Serializable
{
 private int id;
 private String code;
 private String name;
 private String desc;
 private Collection<Variable> vars;
 private Collection<Qualifier> quals;
 private int rCount;
 private Collection<AnnotationShadow> annts;
 
 private int[] inh;
 
 private int[] tags;
 
 private int[][] rels;
 
 public ParameterShadow( )
 {
 }
 
 public ParameterShadow( Parameter p )
 {
  update(p);
 }

 public int getId()
 {
  return id;
 }

 public void setId(int nid)
 {
  id=nid;
 }

 public Collection<Variable> getVariables()
 {
  return vars;
 }

 public Collection<Qualifier> getQualifiers()
 {
  return quals;
 }

 public int[] getInheritedParameters()
 {
  return inh;
 }

 public int[] getTags()
 {
  return tags;
 }

 public int[][] getRelations()
 {
  return rels;
 }

 public String getCode()
 {
  return code;
 }

 public String getName()
 {
  return name;
 }

 public String getDesc()
 {
  return desc;
 }

 public Parameter createParameter()
 {
  Parameter p = new Parameter();

  p.setId(getId());
  p.setCode(getCode());
  p.setName(getName());
  p.setDescription(getDesc());
  p.setVariables(getVariables());
  p.setQualifiers(getQualifiers());
  p.setRecordsCount(rCount);
  
  return p;
 }
 
 public void set( Parameter p )
 {
  p.setName( getName() );
  p.setCode( getCode() );
  p.setDescription( getDesc() );
  
  p.setVariables(null);
  if( getVariables() != null )
  {
   for( Variable v : getVariables() )
    p.addVariable(v);
  }

  
  p.setQualifiers(null);
  if( getQualifiers() != null )
  {
   for( Qualifier v : getQualifiers() )
    p.addQualifier(v);
  }
  
  p.setRecordsCount(rCount);
 }

 public void setRecordsCount(int n)
 {
  rCount=n;
 }

 public void setRelations(int[][] rs)
 {
  rels=rs;
 }

 public void update(Parameter p)
 {
  id=p.getId();
  
  code=p.getCode();
  name=p.getName();
  desc=p.getDescription();
  
  vars=p.getVariables();
  quals=p.getQualifiers();
  
  inh=null;
  Collection<Parameter> ips = p.getInheritedParameters();
  if( ips != null && ips.size() > 0 )
  {
   inh = new int[ips.size()];
   
   int i=0;
   for( Parameter ip : ips )
    inh[i++]=ip.getId();
  }

  tags=null;
  Collection<Tag> orgtags = p.getClassificationTags();
  if( orgtags != null && orgtags.size() > 0 )
  {
   tags = new int[orgtags.size()];
   
   int i=0;
   for( Tag ip : orgtags )
    tags[i++]=ip.getId();
  }

  rels=null;
  Collection<Relation> rl = p.getRelations();
  if( rl != null && rl.size() > 0 )
  {
   rels = new int[rl.size()][];
   
   int i=0;
   for( Relation r : rl )
   {
    int [] relSh = new int[3];
    
    relSh[0]=r.getId();
    relSh[1]=r.getTargetParameter().getId();
    relSh[2]=r.getTag().getId();
    
    rels[i++]=relSh;
   }
  }
  
  if( p.getAnnotations() != null )
  {
   annts = new ArrayList<AnnotationShadow>(  p.getAnnotations().size() );
   
   for( Annotation an :  p.getAnnotations() )
   {
    annts.add( new AnnotationShadow( an ) );
   }
  }
 }

 public Collection<AnnotationShadow> getAnnotations()
 {
  return annts;
 }

 public void setAnnotations(Collection<AnnotationShadow> annts)
 {
  this.annts = annts;
 }
}
