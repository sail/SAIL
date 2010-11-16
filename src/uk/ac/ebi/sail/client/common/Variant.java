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

public class Variant implements Serializable, Identifiable
{
 int id=-1;
 private String name;
 private int coding;
 private boolean predefined;

 private int    count = 0;
 private boolean dirty=false;

 public Variant()
 {
  count = 0;
 }

 public Variant( Variant vr )
 {
  id=vr.getId();
  name=vr.getName();
  count = vr.getCount();
  coding=vr.getCoding();
  predefined=vr.isPredefined();
 }
 
 public Variant(String nm, int cod, boolean predef)
 {
  name = nm;
  count = 0;
  coding=cod;
  predefined=predef;
 }

 public Variant(int id, String nm, int cod, boolean predef)
 {
  this.id=id;
  name = nm;
  count = 0;
  coding=cod;
  predefined=predef;
 }

 public String getName()
 {
  return name;
 }

 public void setName(String name)
 {
  this.name = name;
 }

 public int getCount()
 {
  return count;
 }

 public void setCount(int count)
 {
  this.count = count;
 }

 public int getId()
 {
  return id;
 }

 public void setId(int id)
 {
  this.id = id;
 }

 public boolean isDirty()
 {
  return dirty;
 }
 
 public void setDirty( boolean d)
 {
  dirty=d;
 }

 public void incCount()
 {
  count++;
 }
 
 public void decCount()
 {
  count--;
 }

 public int getCoding()
 {
  return coding;
 }

 public void setCoding(int coding)
 {
  this.coding = coding;
 }

 public void setPredefined(boolean predefined)
 {
  this.predefined = predefined;
 }

 public boolean isPredefined()
 {
  return predefined;
 }

 public static boolean match( Variant v1, String str )
 {
  try
  {
   return  v1.getName().equals(str) || v1.getCoding() == Integer.parseInt(str) ;
  }
  catch (Exception e) 
  {
  }
  
  return false;
 }
}
