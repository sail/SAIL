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
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Expression implements IsSerializable, SAILObject
{
 private int id;
 private int depth;
 private String name;

 private List<SAILObject> subExpressions = new ArrayList<SAILObject>(5);

 @Override
 public int getId()
 {
  return id;
 }

 public void setId(int int1)
 {
  id = int1;
 }

 public int getDepth()
 {
  return depth;
 }

 public void setDepth(int depth)
 {
  this.depth = depth;
 }

 public List<SAILObject> getSubExpressions()
 {
  return subExpressions;
 }

 public void setSubExpressions(List<SAILObject> subExpressions)
 {
  this.subExpressions = subExpressions;
 }
 
 public void addParameter( Parameter p )
 {
  subExpressions.add( p );
 }

 public void addExpression( Expression e )
 {
  subExpressions.add( e );
 }

 public void setName(String string)
 {
  name = string;
 }
 
 public String getName()
 {
  return name;
 }

}
