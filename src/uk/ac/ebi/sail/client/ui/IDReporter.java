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

package uk.ac.ebi.sail.client.ui;

import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.common.IDBunch;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.FitLayout;

public class IDReporter implements AsyncCallback<IDBunch[]>
{
 private static IDReporter instance=null;

 public static IDReporter getInstance()
 {
  if( instance == null )
   instance = new IDReporter();
  
  return instance;
 }
 
 
 public void report( ReportRequest request)
 {
  MessageBox.wait("Loading IDs", "loading...");
  DataManager.getInstance().selectIDs( request, this );
 }

 public void onFailure(Throwable caught)
 {
  MessageBox.hide();
  ErrorBox.showError("Error: "+caught.getMessage());  
 }

 public void onSuccess(IDBunch[] result)
 {
  
  if( result == null )
  {
   ErrorBox.showError("No results");
   return;
  }
  
  Window wnd = new Window();
  
  wnd.setSize(600, 600);
  
  String out = "<table class=\"paramexport\"><tr><td><b>No</b></td><td><b>ID in original collection</b></td></tr>";
//  System.out.println(out);
  
  int n=1;
  for( IDBunch ib : result )
  {
   SampleCollection r = DataManager.getInstance().getCollection(ib.getCollectionID());
   
   if( r == null )
   {
    System.out.println("Invalid collection ID="+ib.getCollectionID());
    continue;
   }
   
   out+="<tr><td colspan=2>&nbsp</td></tr><tr><td colspan=2><b>"+r.getName()+"</b></td></tr>";
   
   for( String sId : ib.getIds())
    out+="<tr><td>"+(n++)+"</td><td>"+sId+"</td></tr>";
    
  }
  
  Panel pnl = new Panel();
  pnl.setAutoScroll(true);
  
  wnd.setLayout(new FitLayout());
  wnd.add(pnl);
  
  out+="</table>";
  
  pnl.setHtml(out);

  MessageBox.hide();
  wnd.show();
 }

}
