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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.sail.client.DataChangeListener;
import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.ClassifiableManager;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.ClientParameterAuxInfo;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.common.Classifier.Target;
import uk.ac.ebi.sail.client.data.Filterable;
import uk.ac.ebi.sail.client.data.TraversalCallback;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.ActionHelper;
import uk.ac.ebi.sail.client.ui.ObjectSelectionListener;
import uk.ac.ebi.sail.client.ui.StudyCollectionStateListener;
import uk.ac.ebi.sail.client.ui.TreeActionListenerAdapter;
import uk.ac.ebi.sail.client.ui.TreeHelper;
import uk.ac.ebi.sail.client.ui.widget.ClassifiableSearchToolbar;

import com.gwtext.client.core.Position;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.tree.DefaultSelectionModel;
import com.gwtext.client.widgets.tree.MultiSelectionModel;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.TreeSelectionModel;
import com.gwtext.client.widgets.tree.event.DefaultSelectionModelListener;
import com.gwtext.client.widgets.tree.event.MultiSelectionModelListener;

public class ParameterHierarchyTree extends Panel implements DataChangeListener,
                                                             MultiSelectionModelListener,
                                                             DefaultSelectionModelListener,
                                                             Filterable<Parameter>,
                                                             StudyCollectionStateListener
{
 private ClassifiableManager<Parameter> mngr;


 
 private TreePanel tree;

 private Action[] actions;
 private TreeActionListenerAdapter<Parameter> actListener;
 private boolean singleSelect;
 
 private ObjectSelectionListener<Parameter> selListener;
 
 private Map<String,TraversalCallback<Parameter>> filters=new HashMap<String,TraversalCallback<Parameter>>();
// private TraversalCallback<Parameter> filter;
 private ClassifiableSearchToolbar<Parameter> toolbar;
 
 public ParameterHierarchyTree( boolean single, Action[] bttns, ClassifiableManager<Parameter> m )
 {
  setTitle("Parameter hierarchy");
  
  singleSelect=single;
  mngr=m;
  
  setFrame(false);
  setLayout(new FitLayout());
  
  setTopToolbar(toolbar = new ClassifiableSearchToolbar<Parameter>(this,DataManager.getInstance().getClassifierManager(Target.PARAMETER),
    new String[][]{
     new String[]{"Code","code"},
     new String[]{"Name","name"},
     new String[]{"Description","description"},
     
  }));

  actListener = new TreeActionListenerAdapter<Parameter>( tree );
  rebuildTree();
  
  setButtonAlign(Position.CENTER);
  
  actions=bttns;
 
  for(Action btid : actions)
  {
   Button bt = new Button(btid.getText(), actListener);
   bt.setStateId(btid.getAction());
   
   if( btid.getCls() != null )
    bt.setIconCls(btid.getCls());
   
   Menu mnu = ActionHelper.createSubActions(btid,actListener);

   if( mnu != null )
   {
    bt.setMenu(mnu);
   }
   
   btid.setComponent(bt);
   addButton(bt);
  }


  m.addClassifiableChangeListener(this);
  

 }


 private void rebuildTree( )
 {
  if( tree != null )
   remove(tree, true);
  
  tree = new TreePanel();
  tree.setEnableDrag(true);
  tree.setDdGroup("sailDDGroup");
  tree.setAutoScroll(true);
  
  if( ! singleSelect )
  {
   MultiSelectionModel msmod = new MultiSelectionModel();
   tree.setSelectionModel(msmod);
   
   msmod.addSelectionModelListener(this);
  }
  else
  {
   DefaultSelectionModel dsm = new DefaultSelectionModel();
   tree.setSelectionModel(dsm);
   
   dsm.addSelectionModelListener(this);
  }
  
  TreeNode rt = new TreeNode("Root");
  
  for( Parameter p : mngr.getClassifiable() ) //DataManager.getInstance().getParameters()
  {
   if( p.getInheritedParameters() == null )
    createBranch(p, rt);

  }
  

  tree.setRootVisible(false);
  rt.setExpanded(true); 
  tree.setRootNode(rt);  
  
  
  
  add( tree );
  actListener.setTree(tree);
//  tree.expandAll();
  doLayout();
 }
 
 private boolean createBranch(Parameter p, Node parent)
 {
  Collection<Parameter> descen = ((ClientParameterAuxInfo)p.getAuxInfo()).getChildren();
 
  boolean suitable = isSuitable(p);
  
  if( ( !suitable ) && (descen==null || descen.size() == 0) )
   return false;
  
  TreeNode ntn = new TreeNode(p.getName()+" ("+p.getCode()+")");
  ntn.setUserObject(p);
 
  boolean hasVisible=false;
  
  if( descen != null && descen.size() > 0 )
  {
   ntn.setExpanded(true);
   for( Parameter inh : descen )
   {
    if( createBranch(inh,ntn) )
     hasVisible=true;
   }
  }
  else
  {
   ntn.setExpandable(false);
  }
  
  if( suitable || hasVisible )
   parent.appendChild(ntn);
  else
   return false;
  
  Collection<Parameter> inhs = p.getInheritedParameters();
  
  if( !suitable )
   ntn.setIconCls("invisibleParameterNode");   
  else if( inhs == null || inhs.size() == 0 )
   ntn.setIconCls("baseParameterNode");
  else if( inhs.size() == 1 )
   ntn.setIconCls("derivedParameterNode");
  else 
   ntn.setIconCls("multyDerivedParameterNode");
  
  return true;

 }


 
 public void setObjectActionListener(ObjectAction<Parameter> lsnr)
 {
  actListener.setListener(lsnr);
 }


 public void dataChanged()
 {
  rebuildTree();
 }
 
 public void setActionEnabled(String actState, boolean b)
 {
  Action a = ActionHelper.findAction(actState, actions);
  
  if( a != null && a.getComponent() != null )
   a.getComponent().setDisabled(!b);
 }
 
 public void setSelectionListener(ObjectSelectionListener<Parameter> lsnr)
 {
  selListener=lsnr;
 }

 public void destroy()
 {
  mngr.removeClassifiableChangeListener(this);
 }


 public void onSelectionChange(MultiSelectionModel sm, TreeNode[] nodes)
 {
  if(selListener != null)
  {
   if( nodes.length == 0 )
    selListener.selectionChanged(null);
   else
   {
    List<Parameter> sel = new ArrayList<Parameter>(nodes.length);
    
    for( TreeNode tn : nodes )
     sel.add((Parameter) tn.getUserObject());
    
    selListener.selectionChanged(sel);
   }
  }
  
 }


 public boolean doBeforeSelect(DefaultSelectionModel sm, TreeNode newNode, TreeNode oldNode)
 {
  return true;
 }


 public void onSelectionChange(DefaultSelectionModel sm, TreeNode node)
 {
  if(selListener != null)
  {
   List<Parameter> sel = new ArrayList<Parameter>(1);
   
   sel.add((Parameter) node.getUserObject());

   selListener.selectionChanged(sel);
  }
 }
 
 
 public void addToSelection(Parameter p)
 {
  TreeSelectionModel tsm = tree.getSelectionModel();
  
  if(! (tsm instanceof MultiSelectionModel) )
   return;
  
  MultiSelectionModel msm = (MultiSelectionModel)tsm;
  
//  TreeHelper.printTree(tree.getRootNode(), 0);
  
  TreeNode stn = TreeHelper.findNodeByUserObject(tree.getRootNode(), p);
  
  if( stn != null )
  {
   stn.ensureVisible();
   msm.select(stn, true);
  }
  else
   System.out.println("Node "+p.toString()+" not found");
 }
 

 
 public void addToSelection(List<Parameter> lp)
 {
  TreeSelectionModel tsm = tree.getSelectionModel();
  
  if(! (tsm instanceof MultiSelectionModel) )
   return;
  
  MultiSelectionModel msm = (MultiSelectionModel)tsm;
  
//  TreeHelper.printTree(tree.getRootNode(), 0);
  
  for( Parameter p : lp )
  {

   TreeNode stn = TreeHelper.findNodeByUserObject(tree.getRootNode(), p);

   if(stn != null)
   {
    stn.ensureVisible();
    msm.select(stn, true);
   }
   else
    System.out.println("Node " + p.toString() + " not found");

  }
 }


 public void setTagFilter(Classifier cl, Tag tg)
 {
  toolbar.setTagFilter(cl, tg);
 }

 @Override
 public void studyCollectionChanged(final int stID, final int khID)
 {
  if( stID==0 && khID == 0 )
  {
   removeFilter("_CollectionFilter");
   return;
  }
  
  addFilter("_CollectionFilter", new TraversalCallback<Parameter>()
  {
   @Override
   public boolean execute(Parameter obj)
   {
    return ((ClientParameterAuxInfo)obj.getAuxInfo()).getCountContext()!=null;
   }
  });
  
  /*
  if( stID==0 && khID == 0 )
  {
   removeFilter("_CollectionFilter");
   return;
  }
  
  AsyncCallback<Summary> asCB = new AsyncCallback<Summary>()
  {
   @Override
   public void onSuccess(final Summary arg0)
   {
    ParameterSelector ps = ParameterSelector.getInstance();
    
    if( khID != 0 )
     ps.setPattern(arg0);
    else
     ps.setPattern(arg0.getTagCounters()[0]);
    
    addFilter("_CollectionFilter", ps );
   }
   
   @Override
   public void onFailure(Throwable arg0)
   {
   }
  };
  
  
  if( khID != 0 )
   DataManager.getInstance().getCollectionSummary(khID, asCB);
  else
   DataManager.getInstance().getStudySummary(stID, asCB);
*/
 }
 
 public void removeFilter(String fName)
 {
  filters.remove(fName);
  rebuildTree();
 }
 
 public void addFilter(String fid, TraversalCallback<Parameter> cb)
 {
  filters.put(fid,cb);
  rebuildTree();
 }

 private boolean isSuitable( Parameter p )
 {
  
  for( TraversalCallback<Parameter> f : filters.values() )
   if( !f.execute(p) )
    return false;
  
  return true;
 }
}
