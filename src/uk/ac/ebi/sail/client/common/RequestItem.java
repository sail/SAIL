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

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class RequestItem implements IsSerializable
{
 public enum Type
 {
  PARAM,
  SPLIT,
  FSPLIT,
  GROUP,
  FILTERED,
  PART,
  ALT,
  CFLT, 
  EXPRESSION;
      
 }
 
 private Type type;
 private int id;
 private String name;
 
 protected RequestItem()
 {}
 
// public int getParamID()
// {
//  return paramID;
// }
//
// public void setParamID(int paramID)
// {
//  this.paramID = paramID;
// }



 public void setType(Type type)
 {
  this.type = type;
 }


 public Type getType()
 {
  return type;
 }
 
 public String toSerialString()
 {
  StringBuilder sb = new StringBuilder();
  
  toSerialString(sb);
  
  return sb.toString();
 }
 
 protected void toSerialString( StringBuilder sb )
 {
  sb.append(getType().name()).append(':').append(id);
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
 
 public abstract String getIconClass();
}
