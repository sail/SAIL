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

package uk.ac.ebi.sail.server.service;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.sail.client.common.AlternativeRequestItem;
import uk.ac.ebi.sail.client.common.ComplexFilter;
import uk.ac.ebi.sail.client.common.ComplexFilteredRequestItem;
import uk.ac.ebi.sail.client.common.EnumFilteredRequestItem;
import uk.ac.ebi.sail.client.common.ExpressionRequestItem;
import uk.ac.ebi.sail.client.common.GroupRequestItem;
import uk.ac.ebi.sail.client.common.IntRange;
import uk.ac.ebi.sail.client.common.M1codec;
import uk.ac.ebi.sail.client.common.ParameterRequestItem;
import uk.ac.ebi.sail.client.common.PartRequestItem;
import uk.ac.ebi.sail.client.common.Range;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.RequestItem;
import uk.ac.ebi.sail.server.FormatException;

public class ReqStr2NReq
{
 public static ReportRequest convert(String reqStr) throws FormatException
 {
  ReportRequest nreq = new ReportRequest();

  String[] reqparts = reqStr.split(";");

  int n=1;
  
  String[] args = reqparts[0].split(":");

  while( n < args.length )
  {
   if(args[n].equals("REL"))
   {
    if("-1".equals(args[n + 1]))
    {
     nreq.setAllRelations(true);
     continue;
    }

    String[] rTagStr = args[n + 1].split(",");

    List<Integer> relIDs = new ArrayList<Integer>(rTagStr.length);
    for(String tids : rTagStr)
    {
     try
     {
      relIDs.add(Integer.parseInt(tids));
     }
     catch(Exception e)
     {
      throw new FormatException("Invalid tag id in relations section: '" + tids + "'. Layer: " + 1);
     }
    }

    nreq.setRelations(relIDs);
   }
   else if(args[n].equals("COL"))
   {
    nreq.setCollectionSplit(true);

    if(!"-1".equals(args[n + 1]))
    {
     String[] rTagStr = args[n + 1].split(",");
     int[] colls = new int[rTagStr.length];

     int k = 0;
     for(String tids : rTagStr)
     {
      try
      {
       colls[k++] = Integer.parseInt(tids);
      }
      catch(Exception e)
      {
       throw new FormatException("Invalid collection id in collection section: '" + tids + "'. Layer: " + 1);
      }
     }

     nreq.setCollections(colls);
    }

   }
   
   n += 2;
  }

  
  
  int layer = 1;
  args = reqparts[1].split(":");
  RequestItem ri = convertItem(args, layer);

  if( ! (ri instanceof GroupRequestItem) )
   throw new FormatException("Root item type must be group. Layer: 1");
  else
   nreq.setRootGroup( (GroupRequestItem)ri );
 
  return nreq;
 }
 
 private static RequestItem convertItem(String[] args, int layer) throws FormatException
 {
  RequestItem.Type type = null;
  
  try
  {
   type = RequestItem.Type.valueOf(args[0]);
  }
  catch (Exception e) 
  {
   throw new FormatException("Invalid argument name: '"+args[0]+"'");
  }
  
  int reqID;
  
  try
  {
   reqID = Integer.parseInt(args[1]);
  }
  catch (Exception e) 
  {
   throw new FormatException("Request parsing error (Invalid integer: '"+args[1]+"'). Layer: "+layer);
  }

  
  if( RequestItem.Type.PARAM == type )
  {
   if( args.length != 3 )
   {
    throw new FormatException("Invalid request string (Invalid number of arguments). Layer: "+layer);
   }
   
   ParameterRequestItem ri = null;
   
   try
   {
    ri = new ParameterRequestItem( Integer.parseInt(args[2]) );
   }
   catch (Exception e) 
   {
    throw new FormatException("Request parsing error (Invalid integer: '"+args[2]+"'). Layer: "+layer);
   }
   
   ri.setId(reqID);
   
   return ri;
  }
  else if( RequestItem.Type.GROUP == type )
  {
   GroupRequestItem gri = new GroupRequestItem();
   
   gri.setName(args[3]);

   try
   {
    gri.setDepth(Integer.parseInt(args[2]));
   }
   catch (Exception e) 
   {
    throw new FormatException("Request parsing error (Invalid integer: '"+args[2]+"'). Layer: "+layer);
   }
   
   for( int k=4; k < args.length; k++ )
   {
    gri.addItem(convertItem((M1codec.decode(args[k])).split(":"), layer));
   }
   
   gri.setId(reqID);
   
   return gri;
  }
  else if( RequestItem.Type.EXPRESSION == type )
  {
   ExpressionRequestItem gri = new ExpressionRequestItem();
   
   gri.setName(args[3]);

   try
   {
    gri.setDepth(Integer.parseInt(args[2]));
   }
   catch (Exception e) 
   {
    throw new FormatException("Request parsing error (Invalid integer: '"+args[2]+"'). Layer: "+layer);
   }
   
   for( int k=4; k < args.length; k++ )
   {
    gri.addItem(convertItem((M1codec.decode(args[k])).split(":"), layer));
   }
   
   gri.setId(reqID);
   
   return gri;
  }

  else if( RequestItem.Type.SPLIT == type || RequestItem.Type.PART == type )
  {
   if( args.length != 4 )
   {
    throw new FormatException("Invalid request string (Invalid number of arguments). Layer: "+layer);
   }
   
   int paramId, partId;
   
   int n=2;
   try
   {
    paramId = Integer.parseInt(args[n]);
    partId = Integer.parseInt(args[++n]);
   }
   catch (Exception e) 
   {
    throw new FormatException("Request parsing error (Invalid integer: '"+args[n]+"'). Layer: "+layer);
   }
   
   RequestItem ri = new PartRequestItem(paramId,partId);
   
   ri.setId(reqID);
   
   return ri;
  }
  else if( RequestItem.Type.CFLT == type )
  {
   if( args.length != 4  )
   {
    throw new FormatException("Invalid request string (Invalid number of arguments). Layer: "+layer);
   }
   
   ComplexFilter cf = new ComplexFilter();
   
   String vps[] = args[3].split("!");
   
//   int variants[][] = new int[vps.length][];
   
   for( String vp : vps )
   {
    String[] vs = vp.split(",");
    
    if( vs[0].charAt(0) == 'R' )
    {
     int partId;
     
     try
     {
      partId = Integer.parseInt( vs[0].substring(1) );
     }
     catch(Exception e)
     {
      throw new FormatException("Request parsing error (Invalid integer: '"+vs[0].substring(1)+"'). Layer: "+layer);
     }
     
     float lLim = Float.NaN, hLim = Float.NaN;
     
     if( vs[1].length() > 0 )
     {
      try
      {
       lLim = Float.parseFloat(vs[1]);
      }
      catch(Exception e)
      {
       throw new FormatException("Request parsing error (Invalid float: '"+vs[1]+"'). Layer: "+layer);
      }
     }

     if( vs[2].length() > 0 )
     {
      try
      {
       hLim = Float.parseFloat(vs[2]);
      }
      catch(Exception e)
      {
       throw new FormatException("Request parsing error (Invalid float: '"+vs[2]+"'). Layer: "+layer);
      }
     }
     
     cf.addRealRange( new Range(partId, lLim, hLim) );
    }
    else if( vs[0].charAt(0) == 'I' )
    {
     int partId;
     
     try
     {
      partId = Integer.parseInt( vs[0].substring(1) );
     }
     catch(Exception e)
     {
      throw new FormatException("Request parsing error (Invalid integer: '"+vs[0].substring(1)+"'). Layer: "+layer);
     }
     
     int lLim = Integer.MIN_VALUE, hLim = Integer.MAX_VALUE;
     
     if( vs[1].length() > 0 )
     {
      try
      {
       lLim = Integer.parseInt(vs[1]);
      }
      catch(Exception e)
      {
       throw new FormatException("Request parsing error (Invalid int: '"+vs[1]+"'). Layer: "+layer);
      }
     }

     if( vs[2].length() > 0 )
     {
      try
      {
       hLim = Integer.parseInt(vs[2]);
      }
      catch(Exception e)
      {
       throw new FormatException("Request parsing error (Invalid int: '"+vs[2]+"'). Layer: "+layer);
      }
     }
     
     cf.addIntRange( new IntRange(partId, lLim, hLim) );
    }
    else if( vs[0].charAt(0) == 'V' )
    {
     int partId;
     
     try
     {
      partId = Integer.parseInt( vs[0].substring(1) );
     }
     catch(Exception e)
     {
      throw new FormatException("Request parsing error (Invalid integer: '"+vs[0].substring(1)+"'). Layer: "+layer);
     }
     
     for(int i=1; i < vs.length; i++ )
     {
      try
      {
       cf.addVariant(partId, Integer.parseInt( vs[i] ));
      }
      catch(Exception e)
      {
       throw new FormatException("Request parsing error (Invalid integer: '"+vs[i]+"'). Layer: "+layer);
      }
     }
    }    
   }
    
   int paramId;
   try
   {
    paramId = Integer.parseInt(args[2]);
   }
   catch (Exception e) 
   {
    throw new FormatException("Request parsing error (Invalid integer: '"+args[2]+"'). Layer: "+layer);
   }
   
   RequestItem ri = new ComplexFilteredRequestItem(paramId, cf);
   
   ri.setId(reqID);
   
   return ri;
  }
  else
  {
   if( args.length != 5 )
   {
    throw new FormatException("Invalid request string (Invalid number of arguments). Layer: "+layer);
   }
   
   String[] alts = args[4].split(",");
   int[] altsIDs = new int[alts.length];
   
   int i=0;
   try
   {
    for( ; i < alts.length; i++ )
     altsIDs[i]=Integer.parseInt(alts[i]);
   }
   catch (Exception e) 
   {
    throw new FormatException("Request parsing error (Invalid integer: '"+alts[i]+"'). Layer: "+layer);
   }
   
   if(RequestItem.Type.ALT == type )
   {
    AlternativeRequestItem ri = new AlternativeRequestItem();
    ri.setAlternativeParameters(altsIDs);
    ri.setId(reqID);
    return ri;
   }
   else if(RequestItem.Type.FILTERED == type )
   {
    int paramId, partId;
    
    int n=2;
    try
    {
     paramId = Integer.parseInt(args[n]);
     partId = Integer.parseInt(args[++n]);
    }
    catch (Exception e) 
    {
     throw new FormatException("Request parsing error (Invalid integer: '"+args[n]+"'). Layer: "+layer);
    }

    RequestItem ri = new EnumFilteredRequestItem(paramId,partId,altsIDs);
    
    ri.setId(reqID);
    
    return ri;
   
   }

  }
  return null;
 }

}
