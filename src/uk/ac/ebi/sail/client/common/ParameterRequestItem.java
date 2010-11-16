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

public class ParameterRequestItem extends RequestItem implements IsSerializable
{
 private transient Parameter param;
 private int parameterID;
 
 public ParameterRequestItem( )
 {
  setType(Type.PARAM);
 }
 
 public ParameterRequestItem( int id )
 {
  setType(Type.PARAM);
  parameterID = id;
 }
 
 public ParameterRequestItem(String string, Parameter p)
 {
  setType(Type.PARAM);
  setName(string);
  param = p;
  parameterID = p.getId();
 }

 @Override
 public String getIconClass()
 {
  return "paramIcon";
 }

 public int getParameterID()
 {
  return parameterID;
 }

 public void setParameterID( int pid )
 {
  parameterID = pid;
 }

 
 public Parameter getParameter()
 {
  return param;
 }

 @Override
 protected void toSerialString( StringBuilder sb )
 {
  super.toSerialString(sb);
  sb.append(':').append(parameterID);
 }

 public void setParameter(Parameter param)
 {
  this.param = param;
  parameterID = param.getId();
 }
}
