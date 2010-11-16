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
import java.util.Iterator;
import java.util.List;

import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterPart;

public class DataStore
{
 private static final char SID_SEP=';';
 private static final int reservedFields=2;
 
 private ParameterManager paramMngr;
 private List<Row> data = new ArrayList<Row>(1000);
 
 
 public DataStore( ParameterManager pm )
 {
  paramMngr=pm;
 }
 
 public void init( SailDataSource ds ) throws DbFormatException
 {
  List<String> sHdr = ds.getHeader();
  
  List<Column> cols = new ArrayList<Column>(sHdr.size());
  
  int n=0;
  for( String h : sHdr )
  {
   n++;
   int pos = h.indexOf('_');
   
   if( pos == -1 )
    throw new DbFormatException("Invalid header at "+n+" ("+h+")");
   
   int paramId;
   int partId;
   try
   {
    paramId = Integer.parseInt( h.substring(0, pos) );
    partId = Integer.parseInt( h.substring(pos+1) );
   }
   catch( Exception e)
   {
    throw new DbFormatException("Invalid header at "+n+" ("+h+")");
   }
   
   Parameter p = paramMngr.getParameter("");  // TODO to be implemented paramMngr.getParameter(paramId);
   
   if( p == null )
    throw new DbFormatException("No parameter with ID="+paramId);
   
   ParameterPart pp = p.getPart(partId);

   if( pp == null )
    throw new DbFormatException("No parameter part with ID="+paramId+" in parameter '"+p.getName()+"' ID="+paramId);
   
   Column cl = new Column(n-1,p,pp);
   cols.add( cl );
   
   pp.setProperty("column", cl);
   
  }
  
  Iterator<List<Object>> rowit = ds.rowIterator();
  
  while( rowit.hasNext() )
  {
   List<Object> rowObs = rowit.next();
   
   n=-1;
   Row r = new Row( cols.size() );
   data.add(r);
   for( Object ro : rowObs )
   {
    n++;
    
    if( n == 0 )
    {
     r.setCount((Integer)ro);
     continue;
    }
    else if( n == 1 )
    {
     r.setSampleIDs((String)ro, SID_SEP );
    }
    
    if( ro instanceof Boolean )
    {
     if( ((Boolean)ro).booleanValue() )
      r.set(n-reservedFields,(byte)(Byte.MIN_VALUE+1));
     else
      r.set(n-reservedFields,Byte.MIN_VALUE);
    }
    else if( ro instanceof String )
    {
     Column col = cols.get(n-reservedFields);
     byte varInd = col.getIndex( (String)ro );
     r.set(n-reservedFields,(byte)(Byte.MIN_VALUE+varInd));
    }
     
    
   }
  }
  
 }
 
}
