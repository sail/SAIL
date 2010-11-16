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

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.sail.client.ConfigManager;
import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.ClassifiableManager;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.ObjectSelectionListener;
import uk.ac.ebi.sail.client.ui.StudyCollectionStateListener;
import uk.ac.ebi.sail.client.ui.module.ObjectList.Selection;

import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.event.TabPanelListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;

public class BasicParameterSelectPanel extends Panel implements StudyCollectionStateListener
{
 private enum Tab
 {
  LIST,
  TREE,
  HIERARCHY
 }
 
 private ObjectAction<Parameter> actListener;
// private ObjectSelectionListener<Parameter> selListener;
 private ReportConstructorList list;
 private ParameterTree tree;
 private ParameterHierarchyTree hierarchy;
 private Tab activeTab=Tab.TREE;
 
 public BasicParameterSelectPanel(boolean single, Action[] buttons, ClassifiableManager<Parameter> m)
 {
  TabPanel tabPanel = new TabPanel();
  tabPanel.setBorder(false);
  
  Action[] buttonsCp = new Action[buttons.length];
  Action[] buttonsCp2 = new Action[buttons.length];
  
  for( int i=0; i < buttons.length; i++ )
  {
   buttonsCp[i]=buttons[i].copy();
   buttonsCp2[i]=buttons[i].copy();
  }
  
  list = new ReportConstructorList(single?Selection.SINGLE:Selection.MULTIPLE, buttons, m);
  tree = new ParameterTree(single, buttonsCp, m);
  hierarchy = new ParameterHierarchyTree(single, buttonsCp2, m);
  
  list.setIconCls("listIcon");
  tree.setIconCls("treeIcon");
  hierarchy.setIconCls("hierIcon");
  
  ActLsnr l = new ActLsnr();
  
  list.setObjectActionListener(l);
  tree.setObjectActionListener(l);
  hierarchy.setObjectActionListener(l);
  
  tabPanel.add(list);
  tabPanel.add(tree);
  //tabPanel.add(hierarchy);

  setLayout(new FitLayout());
  add(tabPanel);
 
  tabPanel.addListener(new TabListener());
  
  switch( activeTab )
  {
   case LIST:
    tabPanel.activate(0);
    break;
   case TREE:
    tabPanel.activate(1);
    break;
 //  case HIERARCHY:
 //   tabPanel.activate(2);
 //   break;
  }
  
  String flt = ConfigManager.getParameterTagFilter();
  
  if( flt != null && flt.length() > 0 )
  {
   int pos = flt.indexOf(':');
   
   String clsName=null;
   String tgName = null;
   if( pos == -1 )
    clsName = flt;
   else
   {
    clsName = flt.substring(0,pos);
    tgName = flt.substring(pos+1);
   }
   
   Classifier cl=null;
   Tag tg = null;
   for( Classifier c : DataManager.getInstance().getClassifiers() )
   {
    if( c.getName().equals(clsName) )
    {
     if( tgName != null )
     {
      for( Tag t : c.getTags() )
      {
       if( t.getName().equals(tgName) )
       {
        cl=c;
        tg=t;
        
        break;
       }
      }
     }
     else
      cl=c;
     
     break;
    }
   }
   
   if( cl != null )
   {
    list.setTagFilter(cl, tg);
    hierarchy.setTagFilter(cl, tg);
   }
  }
  
 }

 public void setObjectActionListener( ObjectAction<Parameter> l )
 {
  actListener=l;
 }
 
 public void setSelectionListener( ObjectSelectionListener<Parameter> lsnr )
 {
  list.setSelectionListener(lsnr);
  tree.setSelectionListener(lsnr);
  hierarchy.setSelectionListener(lsnr);
  
//  selListener=lsnr;
 }
 
// public void setEnumerationSelectListener(EnumerationSelectLsnr enumLsnr)
// {
//  list.setEnumerationSelectListener(enumLsnr);
// }
 
 private class ActLsnr implements ObjectAction<Parameter>
 {

  public void doAction(String actName, Parameter p)
  {
   if( actListener != null )
    actListener.doAction(actName, p);
  }

  public void doMultyAction(String actName, List<Parameter> lp)
  {
   if( actListener != null )
    actListener.doMultyAction(actName, lp);
  }
 }

 public void setButtonEnabled(String btState, boolean enabled, boolean global )
 {
  if( global )
  {
   list.setActionEnabled(btState, enabled);
   tree.setActionEnabled(btState, enabled);
   hierarchy.setActionEnabled(btState, enabled);
  }
  else
  {
   if( activeTab == Tab.LIST )
    list.setActionEnabled(btState, enabled);
   else if( activeTab == Tab.TREE )
    tree.setActionEnabled(btState, enabled);
   else
    hierarchy.setActionEnabled(btState, enabled);
  }
  

 }

 public void addToSelection(Parameter p)
 {
  if( activeTab == Tab.LIST )
   list.addToSelection(p);
  else if( activeTab == Tab.TREE )
   tree.addToSelection(p);
  else
   hierarchy.addToSelection(p);
 }

 public void addToSelection(List<Parameter> lp)
 {
  if( activeTab == Tab.LIST )
   list.addToSelection(lp);
  else if( activeTab == Tab.TREE )
   tree.addToSelection(lp);
  else
   hierarchy.addToSelection(lp);
 }
 
 private class TabListener extends TabPanelListenerAdapter
 {
  public void onTabChange(TabPanel source, Panel tab)
  {
   if( tab == tree )
   {
    activeTab = Tab.TREE;
   }
   else if( tab == list )
   {
    activeTab = Tab.LIST;
   }
   else
    activeTab = Tab.HIERARCHY;
  }
 }

 public Collection<Parameter> getVisible()
 {
  if( activeTab == Tab.LIST )
   return list.getVisible();

  return null;
 }


 @Override
 public void studyCollectionChanged(int stID, int khID)
 {
  list.studyCollectionChanged(stID,khID);
  tree.studyCollectionChanged(stID,khID);
  hierarchy.studyCollectionChanged(stID,khID);
 }


}
