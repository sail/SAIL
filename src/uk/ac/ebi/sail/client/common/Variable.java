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
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Variable extends ParameterPart implements Serializable, IsSerializable
{
 public static enum Type
 {
  STRING,
  INTEGER,
  REAL,
  BOOLEAN,
  ENUM,
  DATE,
  TAG
 }
 
 private Type type;

 
 public Variable()
 {}
 
 public Variable( Variable v )
 {
  setName(v.getName());
  setDescription(v.getDescription());
  setPredefined(v.isPredefined());
  setId(v.getId());
  setType(v.getType());
  
  if( v.getVariants() != null )
  {
   List<Variant> variants = new ArrayList<Variant>( v.getVariants().size() );
   
   for( Variant vr : v.getVariants() )
    variants.add(new Variant(vr));
   
   setVariants(variants);
  }
 }
 
 public void update( Variable v )
 {
  setName(v.getName());
  setDescription(v.getDescription());
  setType(v.getType());
  setPredefined( v.isPredefined());
  setVariants( v.getVariants() );
 }

 public Type getType()
 {
  return type;
 }


 public void setType(Type type)
 {
  this.type = type;
 }

 public boolean isEnum()
 {
  return type==Type.ENUM;
 }

}
