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

package uk.ac.ebi.sail.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.sail.server.util.StringUtil;

import com.pri.util.stream.StreamPump;

public class TestSplit
{

 /**
  * @param args
  */
 public static void main(String[] args)
 {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  try
  {
   FileInputStream fis = new FileInputStream("c:\\tmp\\split.txt");
   String sep = "\t";
   String lnbr="\r\n";
   
   StreamPump.doPump(fis, baos);
   
   String txt = new String(baos.toByteArray(),"UTf-16");
   
   List<String> lst = new ArrayList<String>(50);
   
   int bpos=0,cpos;
   
   while( bpos < txt.length()-1 )
   {
    lst.clear();
    cpos = txt.indexOf(lnbr, bpos);
    
    String line=null;
    
    if( cpos == -1 )
    {
     line=txt.substring(bpos);
     bpos = txt.length();
    }
    else
    {
     line = txt.substring(bpos,cpos);
     bpos = cpos+lnbr.length();
    }
    
    StringUtil.splitExcelString(line, sep, lst);
    
    for( String s : lst)
     System.out.print("["+s+"];");
    
    System.out.print("\n");
   }
   
  }
  catch(FileNotFoundException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
  catch(IOException e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }

 }

}
