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

import java.util.List;

import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.ClassifiableManager;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.module.ObjectList.Selection;

import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.layout.FitLayout;

public class ClassifierSelectPanel extends Panel
{
 private ObjectAction<Classifier> listener;
 
 public ClassifierSelectPanel(boolean single, Action[] buttons, ClassifiableManager<Classifier> m)
 {
  TabPanel tabPanel = new TabPanel();

  Action[] buttonsCp = new Action[buttons.length];
  
  for( int i=0; i < buttons.length; i++ )
   buttonsCp[i]=buttons[i].copy();
  
  ClassifierList list = new ClassifierList(Selection.SINGLE, buttons, m);
  ClassifierTree tree = new ClassifierTree(single, buttonsCp, m);
  
  ActLsnr l = new ActLsnr();
  
  list.setObjectActionListener(l);
  tree.setObjectActionListener(l);
  
  tabPanel.add(list);
  tabPanel.add(tree);

  setLayout(new FitLayout());
  add(tabPanel);
 }

 public void setObjectActionListener( ObjectAction<Classifier> l )
 {
  listener=l;
 }
 
 private class ActLsnr implements ObjectAction<Classifier>
 {

  public void doAction(String actName, Classifier p)
  {
   if( listener != null )
    listener.doAction(actName, p);
  }

  public void doMultyAction(String actName, List<Classifier> lp)
  {
   if( listener != null )
    listener.doMultyAction(actName, lp);
  }
 }
}
