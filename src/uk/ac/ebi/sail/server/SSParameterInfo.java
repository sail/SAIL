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

package uk.ac.ebi.sail.server;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterAuxInfo;
import uk.ac.ebi.sail.client.common.ParameterShadow;

public class SSParameterInfo implements ParameterAuxInfo
{
 private int[] pattern;
 private ParameterShadow shadow;
 private Collection<Parameter> children;

 public void setPattern(int[] pattern)
 {
  this.pattern = pattern;
 }

 public int[] getPattern()
 {
  return pattern;
 }

 public ParameterShadow getShadow()
 {
  return shadow;
 }

 public void setShadow(ParameterShadow shadow)
 {
  this.shadow = shadow;
 }
 
 public Collection<Parameter> getChildren()
 {
  return children;
 }
 
 public void addChildren( Parameter chp )
 {
  if( children == null )
   children = new ArrayList<Parameter>(5);
 
  children.add(chp);
 }

 public void removeChildren(Parameter origP)
 {
  if( children != null )
   children.remove(origP);
 }

}
