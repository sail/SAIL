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


public class Relation
{
 private int id;
 

 private Parameter hostParameter;
 private Parameter targetParameter;
 private Tag tag;
 
 public int getId()
 {
  return id;
 }
 
 public void setId(int id)
 {
  this.id = id;
 }

 public Parameter getHostParameter()
 {
  return hostParameter;
 }
 
 public void setHostParameter(Parameter hostParameter)
 {
  this.hostParameter = hostParameter;
 }
 
 public Parameter getTargetParameter()
 {
  return targetParameter;
 }
 
 public void setTargetParameter(Parameter targetParameter)
 {
  this.targetParameter = targetParameter;
 }
 
 public Tag getTag()
 {
  return tag;
 }
 
 public void setTag(Tag tag)
 {
  this.tag = tag;
 }
 
}
