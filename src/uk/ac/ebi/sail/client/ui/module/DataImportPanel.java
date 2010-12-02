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

package uk.ac.ebi.sail.client.ui.module;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtextux.client.widgets.upload.UploadDialog;

public class DataImportPanel extends Panel
{
 public DataImportPanel()
 {
  setPaddings(20);
  
  Button button = new Button("Upload Vocabulary", new ButtonListenerAdapter()
  {
   public void onClick(final Button button, EventObject e)
   {
    UploadDialog dialog = new UploadDialog();
    dialog.setUrl("sail/UploadSvc");
    // dialog.setPermittedExtensions(new String[]{"jpg", "gif"});
    UrlParam param[] = new UrlParam[1];
    param[0] = new UrlParam("UploadType", "parameters");
    dialog.setBaseParams(param);
    dialog.setPostVarName("myvar");
    dialog.setPermittedExtensions(new String[]{"txt","TXT"});
    dialog.show();
   }
  });

  addButton(button);
  
  button = new Button("Upload Realtions", new ButtonListenerAdapter()
  {
   public void onClick(final Button button, EventObject e)
   {
    UploadDialog dialog = new UploadDialog();
    dialog.setUrl("sail/UploadSvc");
    // dialog.setPermittedExtensions(new String[]{"jpg", "gif"});
    UrlParam param[] = new UrlParam[1];
    param[0] = new UrlParam("UploadType", "RelationMap");
    dialog.setBaseParams(param);
    dialog.setPostVarName("myvar");
    dialog.show();
   }
  });

  addButton(button);
 }
}
