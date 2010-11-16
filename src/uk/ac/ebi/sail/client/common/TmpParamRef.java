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


public class TmpParamRef
{
 Parameter param;
 List<Inh> inherit;
 List<Rel> relations;
 
 public TmpParamRef( Parameter pa )
 {
  param=pa;
 }
 
 public static class Rel
 {
  String param;
  Tag relTag;
  int ln;
  
  public String getParameterCode()
  {
   return param;
  }
  
  public Tag getTag()
  {
   return relTag;
  }
  
  public int getLineNumber()
  {
   return ln;
  }
 }
 
 public static class Inh
 {
  String param;
  int lineNm;
  
  public String getParameter()
  {
   return param;
  }
  
  public int getLineNumber()
  {
   return lineNm;
  }
 }

 
 public void addRelation( String p, Tag t, int ln )
 {
  if( relations == null )
   relations = new ArrayList<Rel>(5);
  
  Rel r = new Rel();
  r.param=p;
  r.relTag=t;
  r.ln=ln;
  
  relations.add(r);
 }

 public Parameter getParameter()
 {
  return param;
 }

 public List<Inh> getInherits()
 {
  return inherit;
 }

 public List<Rel> getRelations()
 {
  return relations;
 }

 public void addInherit(String prm, int ln)
 {
  if( inherit == null )
   inherit = new ArrayList<Inh>(5);
  
  Inh inh = new Inh();
  
  inh.lineNm=ln;
  inh.param=prm;
  
  inherit.add(inh);
 }
}
