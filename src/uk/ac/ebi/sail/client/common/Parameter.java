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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.sail.client.data.Attributed;
import uk.ac.ebi.sail.client.ui.TreeModelNode;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Parameter implements Serializable, IsSerializable, Classifiable, SAILObject, Annotated, Attributed
{
 
 private int id ;
 private String name;
 private String description;
 private String coding;
 
 private static int idGen=1;
 
 private transient int varsCount=-1;
 private transient int qualCount=-1;
 private transient int enumCount=-1;
 
 private int records=0;
 
 private Collection<Annotation> annots;
 private Collection<Variable> variables;
 private Collection<Qualifier> qualifiers;
 private Collection<Tag> tags;
 private Collection<Relation> relations;


 private Collection<Parameter> inheritedParameters;

 private transient ParameterAuxInfo auxInfo;

 
 
 public Parameter()
 {}

 public Parameter( Parameter p )
 {
  id=p.getId();
  name=p.getName();
  description=p.getDescription();
  coding=p.getCode();
  records=p.getRecordsCount();
  
  auxInfo = p.getAuxInfo();
  
  if( p.getVariables() != null )
  {
   variables = new ArrayList<Variable>( p.getVariables().size() );
   for( Variable v : p.getVariables() )
    addVariable( new Variable(v) );
  }
  
  if( p.getQualifiers() != null )
  {
   qualifiers = new ArrayList<Qualifier>( p.getQualifiers().size() );
   for( Qualifier v : p.getQualifiers() )
    addQualifier( new Qualifier(v) );
  }
  
  if( p.getInheritedParameters() != null )
   inheritedParameters = new ArrayList<Parameter>( p.getInheritedParameters() );

  if( p.getClassificationTags() != null )
   tags = new ArrayList<Tag>( p.getClassificationTags() );
  
  if( p.getRelations() != null )
   relations = new ArrayList<Relation>( p.getRelations() );

  if( p.getAnnotations() != null )
  {
   annots = new ArrayList<Annotation>( p.getAnnotations().size() );
   
   for(Annotation a : p.getAnnotations())
    annots.add( new Annotation(a) );
   
  }

 }
 
 public ParameterAuxInfo getAuxInfo()
 {
  return auxInfo;
 }


 public void setAuxInfo(ParameterAuxInfo ai)
 {
  auxInfo = ai;
 }

 
 public String getName()
 {
  return name;
 }

 public void setId(int id)
 {
  this.id = id;
 }

 public int getId()
 {
  return id;
 }

 public String getDescription()
 {
  return description;
 }

 public Collection<Variable> getVariables()
 {
  return variables;
 }
 
 public Collection<Variable> getAllVariables()
 {
  if( inheritedParameters == null )
   return variables;
  
  Map<Parameter,Collection<Variable>> vmap = new HashMap<Parameter, Collection<Variable>>();
  
  int n = collectVariables(vmap,this);
 
  List<Variable> vars = new ArrayList<Variable>(n);
  
  for( Collection<Variable> cl : vmap.values() )
   for( Variable v : cl )
    vars.add(v);

  return vars;
 }
 
 private int collectVariables( Map<Parameter,Collection<Variable>> vmap, Parameter p)
 {
  Collection<Variable> cl = p.getVariables(); 
  
  int n=0;
  if( cl != null )
  {
   n=cl.size();
   vmap.put(p, cl);
  }
  
  
  if( p.getInheritedParameters() != null )
  {
   for( Parameter ip : p.getInheritedParameters() )
    n+=collectVariables(vmap, ip);
  }
  
  return n;
 }
 
 private int collectQualifiers( Map<Parameter,Collection<Qualifier>> vmap, Parameter p)
 {
  Collection<Qualifier> cl = p.getQualifiers();
  
  int n=0;
  if( cl != null )
  {
   n=cl.size();
   vmap.put(p, cl);
  }
  
  if( p.getInheritedParameters() != null )
  {
   for( Parameter ip : p.getInheritedParameters() )
    n+=collectQualifiers(vmap, ip);
  }
  
  return n;
 }
 
 public void addVariable(Variable v)
 {
  if( variables == null )
   variables = new ArrayList<Variable>(5);
  
  variables.add(v);
  v.setParameter(this);
  
  varsCount=-1;
 }
 
 public Collection<Qualifier> getQualifiers()
 {
  return qualifiers;
 }
 
 public Collection<Qualifier> getAllQualifiers()
 {
  if( inheritedParameters == null )
   return qualifiers;
  
  Map<Parameter,Collection<Qualifier>> vmap = new HashMap<Parameter, Collection<Qualifier>>();
  
  int n = collectQualifiers(vmap,this);
 
  List<Qualifier> vars = new ArrayList<Qualifier>(n);
  
  for( Collection<Qualifier> cl : vmap.values() )
   for( Qualifier v : cl )
    vars.add(v);

  return vars;
 }
 


 
 public void addQualifier(Qualifier v)
 {
  if( qualifiers == null )
   qualifiers = new ArrayList<Qualifier>(5);
  
  qualifiers.add(v);
  v.setParameter( this );
  
  qualCount=-1;
 }


 public void setName(String name)
 {
  this.name = name;
 }

 public void setDescription(String description)
 {
  this.description = description;
 }

 public Collection<Parameter> getInheritedParameters()
 {
  return inheritedParameters;
 }

 public void addInheritedParameter(Parameter p2)
 {
  if( inheritedParameters == null )
   inheritedParameters=new ArrayList<Parameter>(3);
  
  inheritedParameters.add(p2);
 }
 
 public static int getNewId()
 {
  return idGen++;
 }

 public void setCode(String cd)
 {
  coding = cd;
 }
 
 public String getCode()
 {
  return coding;
 }
 
 public int countVariables()
 {
  if( varsCount == -1 )
   varsCount = countVariables(this);
  
  return varsCount;
 }
 
 public int countQualifiers()
 {
  if( qualCount == -1 )
   qualCount = countQualifiers(this);
  
  return qualCount;
 }

 public int countEnumerations()
 {
  if( enumCount == -1 )
   enumCount = countEnumerations(this);
  
  return enumCount;
 }
 
 private static int countVariables( Parameter p )
 {
  int count=0;
  
  if( p.getVariables() != null )
   count+=p.getVariables().size();
  
  if( p.getInheritedParameters() != null )
   for( Parameter ip : p.getInheritedParameters() )
    count+=countVariables(ip);
  
  return count;
 }
 
 private static int countQualifiers( Parameter p )
 {
  int count=0;
  
  if( p.getQualifiers() != null )
   count+=p.getQualifiers().size();
  
  if( p.getInheritedParameters() != null )
   for( Parameter ip : p.getInheritedParameters() )
    count+=countQualifiers(ip);
  
  return count;
 }

 private static int countEnumerations( Parameter p )
 {
  int count=0;
  
  if( p.getVariables() != null )
  {
   for( Variable v : p.getVariables() )
    if( Variable.Type.ENUM == v.getType() )
     count++;
  }
  
  if( p.getQualifiers() != null )
   count+=p.getQualifiers().size();
  
  if( p.getInheritedParameters() != null )
   for( Parameter ip : p.getInheritedParameters() )
    count+=countEnumerations(ip);
  
  return count;
 }

 public int getRecordsCount()
 {
  return records;
 }
 
 public void setRecordsCount( int rc )
 {
  records=rc;
 }

 public ParameterPart getPart(int partId)
 {
  if( variables != null )
  {
   for( Variable v : variables )
    if( v.getId() == partId )
     return v;
  }
  
  if( qualifiers != null )
  {
   for( Qualifier v : qualifiers )
    if( v.getId() == partId )
     return v;
  }
   
  if( inheritedParameters == null )
   return null;
  
  for( Parameter ip : inheritedParameters )
  {
   ParameterPart pp = ip.getPart(partId);
   
   if( pp != null )
    return pp;
  }
  
  return null;
 }


 public Collection<Tag> getClassificationTags()
 {
  return tags;
 }


 public Collection<Relation> getRelations()
 {
  return relations;
 }


 public void removeVariable(Variable v)
 {
  if( variables == null )
   return;
  
  variables.remove( v );
 }


 public void removeQualifier(Qualifier q)
 {
  if( qualifiers == null )
   return;
  
  qualifiers.remove( q );
 }


 public void addClassificationTag(Tag t)
 {
  if( tags == null )
   tags=new ArrayList<Tag>(10);
  
  tags.add(t);
  
 }

 public void removeClassificationTag(Tag t)
 {
  if( tags != null )
   tags.remove(t);
 }


 public void removeInherited(Parameter rp)
 {
  if( inheritedParameters != null )
   inheritedParameters.remove(rp);
 }


 public void removeRelation(Relation rel)
 {
  if( relations != null )
   relations.remove(rel);
 }


 public void addRelation(Relation rel)
 {
  if( relations == null )
   relations=new ArrayList<Relation>(5);
  
  relations.add(rel);
  
  rel.setHostParameter(this);
  
 }


 public void setVariables(Collection<Variable> vars)
 {
  variables=vars;
  
  if( vars != null )
  {
   for( Variable v : vars)
    v.setParameter(this);
  }
 }

 public void setQualifiers(Collection<Qualifier> quals)
 {
  qualifiers=quals;
  
  if( quals != null )
  {
   for( Qualifier v : quals)
    v.setParameter(this);
  } 
 }


 public void clearInherited()
 {
  if( inheritedParameters != null )
   inheritedParameters.clear();
 }


 public void setRelations(Collection<Relation> fullRels)
 {
  relations=fullRels;
 }


 public void clearTags()
 {
  if(tags != null)
   tags.clear();
 }

 public void setInheritedParameters(Collection<Parameter> ips)
 {
  inheritedParameters=ips;
  
 }

 public void setClassificationTags(Collection<Tag> tgs)
 {
  tags=tgs;
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

 public void setAnnotations(Collection<Annotation> annotations)
 {
  annots=annotations;
 }
 
 public String toString()
 {
  return coding+" ("+name+")";
 }

 public String getAttribute(String atName)
 {
  if( "code".equals(atName) )
   return getCode();
  else if( "name".equals(atName) )
   return getName();
  else if( "description".equals(atName) )
   return getDescription();

  return null;
 }

 @Override
 public TreeModelNode getStructure()
 {
  TreeModelNode tmn = new TreeModelNode(getCode()+" ("+getName()+")", "parameterIcon");
  tmn.setUserObject(this);
  
  List<TreeModelNode> subNodes = null;
  
  Collection<Variable> vars =  getAllVariables();
  
  if( vars != null )
  {
   subNodes = new ArrayList<TreeModelNode>(6);

   for(Variable v : vars)
    subNodes.add(new TreeModelNode(v.getName(), "variableIcon"));
  }
  Collection<Qualifier> quals =  getAllQualifiers();
  
  if( quals != null )
  {
   if( subNodes == null )
    subNodes = new ArrayList<TreeModelNode>(4);
   
   for(Qualifier q : quals)
    subNodes.add(new TreeModelNode(q.getName(), "qualifierIcon"));
  }
  
  tmn.setSubNodes(subNodes);
  
  return tmn;
 }

}
