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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.json.JSONObject;

import uk.ac.ebi.sail.client.common.ParseException;
import uk.ac.ebi.sail.server.data.DataManager;

import com.pri.util.stream.StreamPump;

public class UploadSvc extends HttpServlet
{

 /**
     * 
     */
 private static final long serialVersionUID = 1L;

 protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
 {
  // Check that we have a file upload request
  boolean isMultipart = ServletFileUpload.isMultipartContent(req);

  // Create a new file upload handler
  ServletFileUpload upload = new ServletFileUpload();

  String res=null;
  int studyID=-1;
  int collectionID=-1;

  String fileContent=null;
  
  String upType=null;
  
  try
  {
   // Parse the request
   FileItemIterator iter = upload.getItemIterator(req);
   while(iter.hasNext())
   {
    FileItemStream item = iter.next();
    String name = item.getFieldName();
    InputStream stream = item.openStream();
    if(item.isFormField())
    {
     if("CollectionID".equals(name))
     {
      try
      {
       collectionID=Integer.parseInt(Streams.asString(stream));
      }
      catch (Exception e)
      {
      }
     }
     if("StudyID".equals(name))
     {
      try
      {
       studyID=Integer.parseInt(Streams.asString(stream));
      }
      catch (Exception e)
      {
      }
     }
     else if("UploadType".equals(name))
     {
      upType = Streams.asString(stream);
     }
     
//     System.out.println("Form field " + name + " with value " + Streams.asString(stream) + " detected.");
    }
    else
    {
//     System.out.println("File field " + name + " with file name " + item.getName() + " detected.");
     InputStream uploadedStream = item.openStream();
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     
     StreamPump.doPump(uploadedStream, baos);

     byte[] barr = baos.toByteArray();
     
     if( (barr[0] == -1 && barr[1] == -2) || (barr[0] == -2 && barr[1] == -1) )  
      fileContent = new String(barr,"UTF-16");
     else
      fileContent = new String(barr);
    }
   }
  }
  catch(Exception ex)
  {
   res=ex.getMessage();
   ex.printStackTrace();
  }

  if( fileContent != null)
  {
   if( "AvailabilityData".equals(upType) )
   {
    if(collectionID != -1)
    {
     try
     {
      DataManager.getInstance().importData(fileContent, collectionID);
     }
     catch(Exception ex)
     {
      res = ex.getMessage();
      ex.printStackTrace();
     }
    }
    else
     res = "Invalid or absent collection ID";
   }
   else if( "RelationMap".equals(upType) )
   {
    try
    {
     DataManager.getInstance().importRelations( new String(fileContent) );
    }
    catch(Exception ex)
    {
     res = ex.getMessage();
     ex.printStackTrace();
    }
   }
   else if( "Study2SampleRelation".equals(upType) )
   {
    try
    {
     DataManager.getInstance().importSample2StudyRelations( new String(fileContent), studyID, collectionID );
    }
    catch(Exception ex)
    {
     res = ex.getMessage();
     ex.printStackTrace();
    }
   }
   else
   {
    try
    {
     DataManager.getInstance().importParameters( fileContent );
    }
    catch(ParseException pex )
    {
     res = "Line "+pex.getLineNumber()+": "+pex.getMessage();
    }
    catch(Exception ex)
    {
     res = ex.getMessage();
     ex.printStackTrace();
    }

   }
   
  }
  else
  {
   res = "File content not found";
  }
  
  JSONObject response = null;
  try
  {
   response = new JSONObject();
   response.put("success", res==null);
   response.put("error", res==null?"uploaded successfully":res);
   response.put("code", "232");
  }
  catch(Exception e)
  {

  }

  Writer w = new OutputStreamWriter(resp.getOutputStream());
  w.write(response.toString());
  System.out.println(response.toString());
  w.close();
  resp.setStatus(HttpServletResponse.SC_OK);
 }

}
