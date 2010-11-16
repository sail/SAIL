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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.sail.client.DataChangeListener;
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.Classifiable;
import uk.ac.ebi.sail.client.common.ClassifiableManager;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.Projection;
import uk.ac.ebi.sail.client.data.Filterable;
import uk.ac.ebi.sail.client.data.TraversalCallback;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.ActionHelper;
import uk.ac.ebi.sail.client.ui.ObjectSelectionListener;
import uk.ac.ebi.sail.client.ui.TreeActionListenerAdapter;
import uk.ac.ebi.sail.client.ui.TreeHelper;
import uk.ac.ebi.sail.client.ui.TreeModelNode;

import com.gwtext.client.core.Position;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.tree.DefaultSelectionModel;
import com.gwtext.client.widgets.tree.MultiSelectionModel;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.TreeSelectionModel;
import com.gwtext.client.widgets.tree.event.DefaultSelectionModelListener;
import com.gwtext.client.widgets.tree.event.MultiSelectionModelListener;

public class ClassifiableTree<T extends Classifiable> extends Panel implements DataChangeListener,
                                                                               MultiSelectionModelListener,
                                                                               DefaultSelectionModelListener,
                                                                               Filterable<T>
{
 private static RecordDef recordDef = new RecordDef(new FieldDef[] { 
   new StringFieldDef("proj"),
   new ObjectFieldDef("obj")
   });

 
 private ClassifiableManager<T> mngr;
 private TreePanel tree;
 
// private ObjectAction<T> lsnr;
 private Action[] actions;
 private TreeActionListenerAdapter<T> actListener;
 private boolean singleSelect;
 private ComboBox projs;
 
 private Store projStore;
 private DataChangeListener projLsnr;
 private ObjectSelectionListener<T> selListener;

 private Map<String,TraversalCallback<T>> filters=new HashMap<String,TraversalCallback<T>>();

// private T selected;
 
 public ClassifiableTree( boolean single, Action[] bttns, ClassifiableManager<T> m )
 {
  singleSelect=single;
  mngr=m;
  
  setFrame(false);
  setLayout(new FitLayout());
  
  Toolbar topToolbar = new Toolbar();
//  topToolbar.addFill();
  setTopToolbar(topToolbar);
 
  projStore = new Store(recordDef);
  
  projs = new ComboBox();
  projs.setTypeAhead(true);
  projs.setMode(ComboBox.LOCAL);
  projs.setTriggerAction(ComboBox.ALL);
  projs.setEmptyText("Select a projection...");
  projs.setSelectOnFocus(true);
  projs.setWidth(450);
  
  int n = 0;
  String defVal=null;
  Projection defProj=null;
  
  for(Projection pj : m.getProjections() )
  {
   String str = pj.getName()+" "+pj.getClassifiersString();

   if( defVal == null )
   {
    defProj = pj;
    defVal = str;
   }
   
   
   projStore.add( recordDef.createRecord(new Object[]{ str  , pj } ) );
   
   if( pj == m.getDefaultProjection() )
   {
    defProj = pj;
    defVal=str;
   }
   
   n++;
  }
  
  projs.setStore(projStore);
  projs.setDisplayField("proj");
  
  if( defVal != null)
   projs.setValue(defVal);
  
  topToolbar.addField(projs);

  if( defProj == null )
  {
   defProj = new Projection();
   defProj.setClassifiers(new ArrayList<Classifier>(1));
  }
  
//  add( tree = new TreePanel() );
//  tree.setFrame(false);
//  tree.setAutoScroll(true);
//
//  if( ! single )
//   tree.setSelectionModel(new MultiSelectionModel());
//  
//  createProjection(defProj);

  actListener = new TreeActionListenerAdapter<T>( tree );
  rebuildTree(defProj);
  
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

  
  projs.addListener( new ComboBoxListenerAdapter(){
   public void onSelect(ComboBox comboBox, Record record, int index)
   {
    System.out.println("Selected "+((Projection)record.getAsObject("obj")).getName());
    try
    {
     rebuildTree((Projection)record.getAsObject("obj"));
    }
    catch (Exception e) 
    {
     e.printStackTrace();
    }
   }
  } );

  m.addClassifiableChangeListener(this);
  m.addProjectionChangeListener( projLsnr=new DataChangeListener(){

   public void dataChanged()
   {
    String defVal=null;
    
    projStore.removeAll();
    
    for(Projection pj : mngr.getProjections() )
    {
     String str = pj.getName()+" "+pj.getClassifiersString();
     
     projStore.add( recordDef.createRecord(new Object[]{ str  , pj } ) );
     
     if( pj == mngr.getDefaultProjection() )
      defVal=str;

    }   
    
    if( defVal != null)
     projs.setValue(defVal);

   }} );
 }


 private void rebuildTree( Projection prj )
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
  
  createProjection( prj );
  add( tree );
  actListener.setTree(tree);
  doLayout();
 }
 
 private void createProjection( Projection clsfs )
 {
  TreeModelNode root = new TreeModelNode("Root");
  
  for( T p : mngr.getClassifiable() ) //DataManager.getInstance().getParameters()
  {
//   TreeModelNode node = root;
   
   if( isSuitable(p) )
    TreeHelper.classify(root,p,clsfs,0);
  }
  
  TreeNode rt = new TreeNode("Root");
  TreeHelper.createTree(root,rt);
  tree.setRootVisible(false);
  rt.setExpanded(true); 
  tree.setRootNode(rt);  
 }
 
 public void setObjectActionListener(ObjectAction<T> lsnr)
 {
  actListener.setListener(lsnr);
 }


 public void dataChanged()
 {
  String sel = projs.getValue();
  
  for( Record r : projs.getStore().getRecords() )
  {
   if( r.getAsString("proj").equals(sel) )
   {
    rebuildTree((Projection)r.getAsObject("obj"));
    return;
   }
  }
  
 }
 
 public void setActionEnabled(String actState, boolean b)
 {
  Action a = ActionHelper.findAction(actState, actions);
  
  if( a != null && a.getComponent() != null )
   a.getComponent().setDisabled(!b);
 }
 
 public void setSelectionListener(ObjectSelectionListener<T> lsnr)
 {
  selListener=lsnr;
 }

 public void destroy()
 {
  mngr.removeClassifiableChangeListener(this);
  mngr.removeProjectionChangeListener(projLsnr);
 }


 @SuppressWarnings("unchecked")
 public void onSelectionChange(MultiSelectionModel sm, TreeNode[] nodes)
 {
  if(selListener != null)
  {
   if( nodes.length == 0 )
    selListener.selectionChanged(null);
   else
   {
    List<T> sel = new ArrayList<T>(nodes.length);
    
    for( TreeNode tn : nodes )
     sel.add((T) tn.getUserObject());
    
    selListener.selectionChanged(sel);
   }
  }
  
 }


 public boolean doBeforeSelect(DefaultSelectionModel sm, TreeNode newNode, TreeNode oldNode)
 {
  return true;
 }


 @SuppressWarnings("unchecked")
 public void onSelectionChange(DefaultSelectionModel sm, TreeNode node)
 {
  if(selListener != null)
  {
   List<T> sel = new ArrayList<T>(1);
   
   sel.add((T) node.getUserObject());

   selListener.selectionChanged(sel);
  }
 }
 
 
 public void addToSelection(T p)
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
 

 
 public void addToSelection(List<T> lp)
 {
  TreeSelectionModel tsm = tree.getSelectionModel();
  
  if(! (tsm instanceof MultiSelectionModel) )
   return;
  
  MultiSelectionModel msm = (MultiSelectionModel)tsm;
  
//  TreeHelper.printTree(tree.getRootNode(), 0);
  
  for( T p : lp )
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


 
  public void removeFilter(String fName)
  {
   filters.remove(fName);
   dataChanged();
  }
  
  public void addFilter(String fid, TraversalCallback<T> cb)
  {
   filters.put(fid,cb);
   dataChanged();
  }

  private boolean isSuitable( T p )
  {
   
   for( TraversalCallback<T> f : filters.values() )
    if( !f.execute(p) )
     return false;
   
   return true;
  }

}
