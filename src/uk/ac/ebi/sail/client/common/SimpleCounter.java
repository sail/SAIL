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
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;



public class SimpleCounter implements Serializable,IsSerializable
{
 public static final int NOT_USED=-1;
 public static final int ANY=-2;
 
 private int count=0;
 private SimpleCounter ref;
 private Map<String,SimpleCounter> map;
 private int[] paramIDs;
 private transient int split=1;
 
 private int parameterID;
 private int partID=NOT_USED;
 private int variantID=NOT_USED;

 public SimpleCounter()
 {}
 
 public int getSplit()
 {
  return split;
 }


 public void setSplit(int split)
 {
  this.split = split;
 }



 public int getCount()
 {
  return count;
 }
 
 public void setCount(int count)
 {
  this.count = count;
 }
 
 public SimpleCounter getRef()
 {
  if( ref != null )
   return ref;
  
  return null;
 }
 
 public Map<String,SimpleCounter> getRefMap()
 {
  if( map != null )
   return map;
  
  return null;
 }
 
 public SimpleCounter getRef( String key )
 {
  SimpleCounter sc=null;
  
  if( map == null )
   map=new HashMap<String, SimpleCounter>();
  else
   sc=map.get(key);
   
  if( sc == null )
  {
   sc = new SimpleCounter();
   map.put(key, sc);
  }
  
  
  ref=null;
  
  return sc;
 }

 
 public void setRef(SimpleCounter ref)
 {
  this.ref = ref;
  map=null;
 }
 
 public void addRef(String str, SimpleCounter ref)
 {
  if( map == null )
   map=new HashMap<String, SimpleCounter>();
  
  map.put(str, ref);
  ref=null;
 }

 
 public void inc()
 {
  count++;
 }
 
 public String toString()
 {
  return "Counter: "+count;
 }

 public int getParameterID()
 {
  return parameterID;
 }

 public void setParameterID(int parameterID)
 {
  this.parameterID = parameterID;
 }

 public int getPartID()
 {
  return partID;
 }

 public void setPartID(int partID)
 {
  this.partID = partID;
 }

 public int getVariantID()
 {
  return variantID;
 }

 public void setVariantID(int variantID)
 {
  this.variantID = variantID;
 }

 public void setParamIDs(int[] paramIDs)
 {
  this.paramIDs = paramIDs;
 }

 public int[] getParamIDs()
 {
  return paramIDs;
 }
}
