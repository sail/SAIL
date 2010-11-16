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

import uk.ac.ebi.sail.client.InitListener;
import uk.ac.ebi.sail.client.common.Classifier.Target;

import com.google.gwt.core.client.EntryPoint;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Viewport;


public class TestPanel implements InitListener, EntryPoint
{
 static TabPanel appPanel;
 
 public void onModuleLoad()
 {
  doInit();
 }
 
 public void doInit()
 {
  appPanel = new TabPanel();
  appPanel.setResizeTabs(true);
  appPanel.setMinTabWidth(50);
//  appPanel.setResizeTabsRendered(true);
//  appPanel.setTabWidth(160);
  appPanel.setLayoutOnTabChange(true);
//  appPanel.setEnableTabScroll(true);
  appPanel.setActiveTab(0);

  Panel annEditTab = new Panel();
  annEditTab.setPaddings(15);
  annEditTab.setTitle("Edit annotated");
  
  Panel aep = new AnnotatedEditor( Target.PARAMETER_ANN );
  aep.setTitle("Edit annotated");
  aep.setSize(300, 500);
  
  annEditTab.add(aep);
  appPanel.add(annEditTab);
  
  appPanel.doLayout();

  new Viewport(appPanel);
  
  try
  {
  }
  catch (Exception e) {
   e.printStackTrace();
  }
 }

}
