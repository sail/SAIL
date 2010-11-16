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
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class ParameterPart implements Serializable, IsSerializable, Comparable<ParameterPart>, Identifiable
{
 public static final String SECURED_VARIANT_SIGN="@";
// static final String SECURED_VARIANT_NAME="HIDDEN";
// static final Variant SECURED_VARIANT=new Variant("[SECURED]",-1);
 
 public static final int NEW=1;
 public static final int REMOVED=2;
 
 private boolean dirty=false;

 private int id;

 private String name;
 private String description; 
 private List<Variant> variants;
 private int actionType;
 transient private Parameter parameter;
 transient private String variantsStr;
 private transient Map<String,Object> auxInfo;
 private boolean predef;
 protected boolean mandatory=true;
 
 private int count=0;
 
 public int getId()
 {
  return id;
 }

 public void setId(int id)
 {
  this.id = id;
 }

 public Parameter getParameter()
 {
  return parameter;
 }
 
 public void setParameter( Parameter p )
 {
  parameter=p;
 }

 public boolean isMandatory()
 {
  return mandatory;
 }
 
 public Object getProperty( String pName )
 {
  if( auxInfo == null )
   return null;
  
  return auxInfo.get( pName );
 }


 public Object setProperty(String pName, Object val)
 {
  if( auxInfo == null )
   auxInfo = new TreeMap<String, Object>();
  
  return auxInfo.put(pName, val);  
 }

 
 public String getName()
 {
  return name;
 }
 
 public void setName(String name)
 {
  this.name = name;
 }
 
 public int getActionType()
 {
  return actionType;
 }
 
 public void setActionType(int actionType)
 {
  this.actionType = actionType;
 }

 public String getDescription()
 {
  return description;
 }

 
 public void setDescription(String string)
 {
  description=string;
 }

 public void setPredefined(boolean value)
 {
  predef=value;
 }

 public boolean isPredefined()
 {
  return predef;
 }

 public short addVariant( Variant var )
 {
  if( variants == null )
  {
   variants = new ArrayList<Variant>(10);
   variants.add( new Variant(0,ParameterPart.SECURED_VARIANT_SIGN,Integer.MIN_VALUE,false) );
  }
  
  variants.add(var);
  variantsStr=null;
  
  return (short)(variants.size()-1);
 }
 
 public void removeVariant( Variant var )
 {
  if( variants != null )
  {
   for( Variant v : variants )
   {
    if( v == var )
    {
     variantsStr=null;
     variants.remove(v);
    }
   }
  }
 }
 
 public List<Variant> getVariants()
 {
  return variants;
 }

 public boolean isDirty()
 {
  return dirty;
 }

 public void setDirty( boolean dt )
 {
  dirty=dt;
 }

 public abstract boolean isEnum();

 public void count()
 {
  count++;
 }

 public short getVariantIndexByValue(String variantStr)
 {
  if( ParameterPart.SECURED_VARIANT_SIGN.equals(variantStr) )
  {
   if(variants == null)
   {
    variants = new ArrayList<Variant>(5);
    variants.add( new Variant(0,ParameterPart.SECURED_VARIANT_SIGN,0,false) );
   }
   
   return 0;
  }
  
  if( variants == null )
   return -1;
  
  short n=0;
  for(Variant vr : variants)
  {
   if( vr.getName().equals(variantStr) )
    return n;
   
   n++;
  }
  
  if( variantStr.matches("\\d+") )
  {
   n=0;
   
   int vc = Integer.parseInt(variantStr);
   
   for(Variant vr : variants)
   {
    if( vr.getCoding() == vc )
     return n;

    n++;
   }
  }

  return -1;
 }
 
 public short getVariantIndexByVariantID(int variID)
 {
  if( variID == 0 )
  {
   if(variants == null)
   {
    variants = new ArrayList<Variant>(5);
    variants.add( new Variant(0,ParameterPart.SECURED_VARIANT_SIGN,0,false) );
   }
   
   return 0;
  }

  if( variants == null )
   return -1;
  
  short n=0;
  for(Variant vr : variants)
  {
   if( vr.getId() == variID )
    return n;
   
   n++;
  }
  
  return -1;
 }
 
 public void countVariantByIndex(short vidx)
 {
  if( variants == null || vidx >= variants.size() )
   return;
  
  variants.get(vidx).incCount();
 }
 
 public void uncountVariantByIndex(short vidx)
 {
  if( variants == null || vidx >= variants.size() )
   return;
  
  variants.get(vidx).decCount();
 }

 
 @Deprecated
 public short getVariantID1(String variantStr)
 {
//  if( SECURED_VARIANT_SIGN.equals(variantStr) )
//   variantStr = SECURED_VARIANT_NAME;
  
  if( variants == null )
  {
   if( isPredefined() && ! SECURED_VARIANT_SIGN.equals(variantStr) )
     return -1;
   
   variants = new ArrayList<Variant>(10);
   Variant vr = new Variant();
   vr.setId(1);
   vr.setName(variantStr);
   vr.setCount(1);
   vr.setCoding(1);
   variants.add(vr);
   return 1;
  }
  
  Variant var = null;
  
  short n=1;
  for(Variant vr : variants)
  {
   if( vr.getName().equals(variantStr) )
   {
    var=vr;
    break;
   }
   
   n++;
  }
 
  if( var != null )
  {
   var.incCount();
   return n;
  }
  

  if( variantStr.matches("\\d+") )
  {
   n=1;
   
   int vc = Integer.parseInt(variantStr);
   
   for(Variant vr : variants)
   {
    if( vr.getCoding() == vc )
    {
     var=vr;
     break;
    }
    
    n++;
   }
  }

  if( var != null )
  {
   var.incCount();
   return n;
  }

  
  if( isPredefined() && ! SECURED_VARIANT_SIGN.equals(variantStr) )
   return -1;
  
  int maxCoding = Integer.MIN_VALUE;
  
  for( Variant v : variants )
   if( v.getCoding() > maxCoding )
    maxCoding = v.getCoding();
  
  Variant vr = new Variant();
  vr.setId(n);
  vr.setName(variantStr);
  vr.setCount(1);
  vr.setCoding(maxCoding+1);
  variants.add(vr);

  return n;
 }


 public Variant getVariant(short value) //Can produce Null pointer exceptions
 {
//  if( value == 0 )
//   return SECURED_VARIANT;
  
  return variants.get(value);
 }
 
 public String getVariantsString()
 {
  if( variantsStr != null )
   return variantsStr;
  
  if( variants == null || variants.size() == 0 )
   return variantsStr="";
  
  StringBuilder sb = new StringBuilder();
  
  for( Variant v : variants )
   if( v.isPredefined())
    sb.append(v.getName()).append(", ");
  
  if( sb.length() > 2)
   sb.setLength( sb.length()-2);
  
  return variantsStr=sb.toString();
 }
 
 public void addVariants(List<Variant> vrs)
 {
  variantsStr=null;
  
  if(variants == null)
  {
   variants = new ArrayList<Variant>(5);
   variants.add( new Variant(0,ParameterPart.SECURED_VARIANT_SIGN,0,false) );
  }

  for( Variant v : vrs )
   if( ! ParameterPart.SECURED_VARIANT_SIGN.equals(v.getName()))
    variants.add(v);
 }

 
 protected void setVariants(List<Variant> vrs)
 {
  variantsStr=null;
  
  variants=vrs;
 }

 public void clearVariants()
 {
  if( variants != null )
   variants.clear();
 }
 
 public int getVariantsCount()
 {
  if( variants == null )
   return 0;
  
  return variants.size();
 }
 
 public int compareTo(ParameterPart o)
 {
  return getId()-o.getId();
 }


}
