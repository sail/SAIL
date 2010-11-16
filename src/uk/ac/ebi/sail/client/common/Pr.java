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

import com.google.gwt.user.client.rpc.IsSerializable;

public class Pr implements IsSerializable
{

 private int id = getNewId();
 private String name;
 private String description;
 
 private static int idGen=1;

 private Collection<Variable> variables;
 private Collection<Qualifier> qualifiers;
 private Collection<Pr> inheritedParameters;
 
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
 
 public void addVariable(Variable v)
 {
  if( variables == null )
   variables = new ArrayList<Variable>(5);
  
  variables.add(v);
 }
 
 public Collection<Qualifier> getQualifiers()
 {
  return qualifiers;
 }
 
 public void addQualifier(Qualifier v)
 {
  if( qualifiers == null )
   qualifiers = new ArrayList<Qualifier>(5);
  
  qualifiers.add(v);
//  v.setParameter( this );
 }


 public void setName(String name)
 {
  this.name = name;
 }

 public void setDescription(String description)
 {
  this.description = description;
 }

 public void remove(Qualifier q)
 {
  if( qualifiers != null )
   qualifiers.remove(q);
 }

 public void remove(Variable v)
 {
  if( variables != null )
   variables.remove(v);
 }

 public Collection<Pr> getInheritedParameters()
 {
  return inheritedParameters;
 }

 public void addInheritedParameter(Pr p2)
 {
  if( inheritedParameters == null )
   inheritedParameters=new ArrayList<Pr>(3);
  
  inheritedParameters.add(p2);
 }
 
 public int countVariables( )
 {
  return countVariables( this );
 }

 public int countQualifiers( )
 {
  return countQualifiers( this );
 }

 public static int countVariables( Pr p )
 {
  int count=0;
  
  if( p.getVariables() != null )
   count+=p.getVariables().size();
  
  if( p.getInheritedParameters() != null )
   for( Pr ip : p.getInheritedParameters() )
    count+=countVariables(ip);
  
  return count;
 }
 
 private static int countQualifiers( Pr p )
 {
  int count=0;
  
  if( p.getQualifiers() != null )
   count+=p.getQualifiers().size();
  
  if( p.getInheritedParameters() != null )
   for( Pr ip : p.getInheritedParameters() )
    count+=countQualifiers(ip);
  
  return count;
 }

 
 public static int getNewId()
 {
  return idGen++;
 }
}
