package uk.ac.ebi.sail.client;

import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterPart;

import com.google.gwt.user.client.rpc.IsSerializable;


public class FilteredEnumeration implements IsSerializable
{
 private Parameter param;
 private ParameterPart enumeration;
 private int[] values;
 private transient int nVaris=0;
 
 public FilteredEnumeration()
 {}
 
 public FilteredEnumeration(Parameter p, ParameterPart qualifier, int[] values)
 {
  super();
  this.enumeration = qualifier;
  this.values = values;
  param=p;
 }

 public ParameterPart getEnumeration()
 {
  return enumeration;
 }

 public void setEnumeration(ParameterPart qualifier)
 {
  this.enumeration = qualifier;
 }
 
 public int[] getVariants()
 {
  if( values == null || nVaris == values.length )
   return values;
  
  int[] res = new int[nVaris];
  
  for( int i=0; i < nVaris; i++ )
   res[i]=values[i];
  
  values=res;
  
  return res;
 }
 
 public void setVariants(int[] values)
 {
  this.values = values;
  nVaris=values.length;
 }

 public void addVariant(int v)
 {
  if( values == null || nVaris == values.length )
  {
   int[] newvals = new int[nVaris+10];
   
   if( nVaris > 0 )
   {
    for( int i=0; i < nVaris; i++ )
     newvals[i]=values[i];
   }
   
   values = newvals;
  }
  
  values[nVaris++]=v;
 }

 public Parameter getParameter()
 {
  return param;
 }

 public void setParameter(Parameter param)
 {
  this.param = param;
 }
}
