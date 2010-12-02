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

package uk.ac.ebi.sail.server.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil
{
 public static void splitExcelString(String line, String sep, List<String> accum)
 {
  int spos;
  StringBuilder sb=null;
  
  while( line.length() > 0 )
  {
   
   if( line.charAt(0) != '"' ) // This cell is not quoted
   {
    spos = line.indexOf(sep);
    
    if( spos < 0 )
    {
     accum.add(line);
     return;
    }
    
    accum.add(line.substring(0,spos));
    line = line.substring(spos+sep.length());
   }
   else
   {
    int qpos;
    int beg = 1;
    
    while( true ) // A quoted cell can contain double quote inside. Let'd build the cell content from parts
    {
     qpos = line.indexOf('"',beg);
     
     if( qpos == -1 ) // actually this is the erroneous situation - quoted part is not finished by the quotation symbol. 
     {
      if( sb != null && sb.length() > 0 )
      {
       sb.append(line.substring(beg));
       accum.add(sb.toString());
      }
      else
       accum.add(line.substring(beg));
      
      return;
     }
     else if( qpos == line.length()-1 )
     {
      if( sb != null && sb.length() > 0 )
      {
       sb.append(line.substring(beg,line.length()-1));
       accum.add(sb.toString());
      }
      else
       accum.add(line.substring(beg,line.length()-1));

      return;
     }
     
     if( line.charAt(qpos+1) == '"' ) // We have found a double quote
     {
      if( sb == null )
       sb = new StringBuilder(200);
      
      sb.append(line.substring(beg, qpos+1)); // adding part of the cell to the buffer and continue
      beg = qpos+2;
     }
     else
     {
      if( line.startsWith(sep, qpos+1) )
      {
       if( sb != null && sb.length() > 0 )
       {
        sb.append(line.substring(beg, qpos) );
        accum.add(sb.toString());
        sb.setLength(0);
       }
       else
        accum.add(line.substring(beg, qpos));
        
       line=line.substring(qpos+sep.length()+1);
       break;
      }
      else // actually this is the erroneous situation - quotation symbol have to be followed by separator or to be doubled . 
      {
       if( sb == null )
        sb = new StringBuilder(200);
       
       sb.append(line.substring(beg, qpos+1));
       beg = qpos+1;
      }
     }
     
    }
   }
  }
 }

 
 public static List<String> splitExcelString(String line, String sep)
 {
  List<String> res = new ArrayList<String>(50);
  
  splitExcelString(line, sep, res);
  
  return res;
 }
}
