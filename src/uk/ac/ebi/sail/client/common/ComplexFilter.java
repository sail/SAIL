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

public class ComplexFilter implements Serializable, IsSerializable
{
 private transient Parameter parameter;
 private List<IntRange>      intLimits;
 private List<Range>         realLimits;
 private List<List<Integer>> variants;

 public List<List<Integer>> getVariants()
 {
  return variants;
 }

 public void setVariants( List<List<Integer>> vs )
 {
  variants=vs;
 }

 
 public List<Range> getRealRanges()
 {
  return realLimits;
 }
 
 public void setRealRanges( List<Range> rs )
 {
  realLimits=rs;
 }

 
 public List<IntRange> getIntRanges()
 {
  return intLimits;
 }

 public void setIntRanges( List<IntRange> ils )
 {
  intLimits=ils;
 }

 
 public void clear()
 {
  if(variants != null)
   variants.clear();

  if(realLimits != null)
   realLimits.clear();
 
  if(intLimits != null)
   intLimits.clear();
 }

 public void addRealRange(Range r)
 {
  if(realLimits == null)
   realLimits = new ArrayList<Range>(3);

  realLimits.add(r);
 }

 public void addVariant(int partId, int varId)
 {
  if(variants == null)
   variants = new ArrayList<List<Integer>>(3);

  for(List<Integer> il : variants)
  {
   if(il.get(0) == partId)
   {
    il.add(varId);
    return;
   }
  }

  List<Integer> nl = new ArrayList<Integer>(5);

  nl.add(partId);
  nl.add(varId);

  variants.add(nl);
 }

 public boolean isClean()
 {
  return ( variants == null || variants.size() == 0 ) && ( realLimits == null || realLimits.size() == 0 )&& ( intLimits == null || intLimits.size() == 0 ); 
 }
 
 public Parameter getParameter()
 {
  return parameter;
 }
 
 public void setParameter(Parameter parameter)
 {
  this.parameter = parameter;
 }

 public void addIntRange(IntRange ir)
 {
  if(intLimits == null)
   intLimits = new ArrayList<IntRange>(3);

  intLimits.add(ir);
 }


}
