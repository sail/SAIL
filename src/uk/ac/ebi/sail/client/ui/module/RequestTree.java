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
import java.util.List;

import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.FilteredEnumeration;
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.ComplexFilter;
import uk.ac.ebi.sail.client.common.ComplexFilteredRequestItem;
import uk.ac.ebi.sail.client.common.EnumFilteredRequestItem;
import uk.ac.ebi.sail.client.common.ExpressionRequestItem;
import uk.ac.ebi.sail.client.common.FilteredRequestItem;
import uk.ac.ebi.sail.client.common.GroupRequestItem;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterRequestItem;
import uk.ac.ebi.sail.client.common.PartRequestItem;
import uk.ac.ebi.sail.client.common.Qualifier;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.RequestItem;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.Study;
import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.client.ui.RequestItemSelectListener;
import uk.ac.ebi.sail.client.ui.StudyCollectionStateListener;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.data.Node;
import com.gwtext.client.data.Record;
import com.gwtext.client.dd.DragData;
import com.gwtext.client.dd.DragDrop;
import com.gwtext.client.dd.DragSource;
import com.gwtext.client.dd.DropTarget;
import com.gwtext.client.dd.DropTargetConfig;
import com.gwtext.client.util.JavaScriptObjectHelper;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.event.CheckboxListenerAdapter;
import com.gwtext.client.widgets.grid.GridDragData;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.RowLayout;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;
import com.gwtext.client.widgets.tree.DefaultSelectionModel;
import com.gwtext.client.widgets.tree.DropNodeCallback;
import com.gwtext.client.widgets.tree.TreeDragData;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.DefaultSelectionModelListenerAdapter;
import com.gwtext.client.widgets.tree.event.TreeNodeListenerAdapter;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

public class RequestTree extends Panel implements StudyCollectionStateListener
{
 private static final String rootNodeIcon="rootNodeIcon";
 private static final String rootNodeName="Request";
 
 
// private static int ids = 1;
 
 private List<MakeReportListener> listeners = new ArrayList<MakeReportListener>(3);
// private Store                    store;
// private ColumnTree                        grid;
 private TreePanel                        tree;
 private PredefQueryTable predef;
 private Checkbox useCollectionSplit;
 private Checkbox splitBySpecific;

// private Radio splitByAll;
// private Radio splitBySel;

 private Checkbox useRels;
 private Checkbox relsSpecific;

 private RequestItemSelectListener reqSelListener;
 private boolean isStateAnd=true;

 private Collection<Integer> collections = new ArrayList<Integer>(10);
 
 private NodeListener nodeListener = new NodeListener();
 
 public RequestTree()
 {
  super();

  setBorder(false);
  setFrame(true);
  // setMargins(10);
  setTitle("Report request");
//  setLayout(new AnchorLayout());
  setLayout(new RowLayout());

  Collection<ExpressionRequestItem> pexpr = DataManager.getInstance().getExpressions();
  
  tree = new TreePanel();
  tree.setId("SAILrequestTreePanel");
  tree.setCls("requestTree");
//  grid.setLayout(new FitLayout());
//  grid.setSelectionModel(new RowSelectionModel(true));

//  ColumnTree.Column cols[] = new ColumnTree.Column[2];  
//  
//  cols[0] = grid.new Column("Name", 270, "name");   
//  cols[1] = grid.new Column("Type", 50, "type");  
//
//  grid.setColumns(cols);

  tree.setFrame(false);
  
  DefaultSelectionModel sm = new DefaultSelectionModel();
  sm.addSelectionModelListener(new DefaultSelectionModelListenerAdapter(){
   public boolean doBeforeSelect(DefaultSelectionModel sm, TreeNode newNode, TreeNode oldNode)
   {
    if( newNode.getUserObject() == null )
     return false;
    
    return true;
   }
   
   public void onSelectionChange(DefaultSelectionModel sm, TreeNode node) 
   {
    if( reqSelListener != null )
     reqSelListener.itemSelected( node==null?null:(RequestItem)node.getUserObject() );
   }

  });
  tree.setSelectionModel(sm);
  
  tree.setRootVisible(false);
  TreeNode rootn = new TreeNode("Request");
  rootn.setIconCls("rootNodeIcon");
  tree.setRootNode(rootn);
//  grid.setTitle("Request");

  tree.setHeight("80%");
  tree.setCls("RequestTree");
  tree.setLines(false);

  TreeNode reqNode = new TreeNode(rootNodeName);
  reqNode.setIconCls(rootNodeIcon);
  reqNode.setLeaf(true);
  rootn.appendChild(reqNode);
  
  tree.setAutoScroll(true);
  // grid.setMonitorWindowResize
  
  Toolbar tb = new Toolbar();
  
  AndOrBtListener andOrLsnr = new AndOrBtListener();
  
  ToolbarButton tbBt = new ToolbarButton();
  tbBt.setIconCls("andIcon");
  tbBt.setEnableToggle(true);
  tbBt.setPressed(true);
  tbBt.setToggleGroup("andor");
  tbBt.setStateId("and");
  tbBt.addListener(andOrLsnr);
  tbBt.addListener( new ButtonListenerAdapter(){
   public void onClick(Button b,EventObject e)
   {
    b.setPressed(true);
   }
  });
  tb.addButton( tbBt );
 
  tbBt = new ToolbarButton();
  tbBt.setIconCls("orIcon");
  tbBt.setEnableToggle(true);
  tbBt.setToggleGroup("andor");
  tbBt.setStateId("or");
  tbBt.addListener(andOrLsnr);
  tbBt.addListener( new ButtonListenerAdapter(){
   public void onClick(Button b,EventObject e)
   {
    b.setPressed(true);
   }
  });
  tb.addButton( tbBt );

  tb.addFill();
  
  ButtonListenerAdapter edtLsnr = new ReportEditor(); 
  
  tbBt = new ToolbarButton();
  tbBt.setIconCls("deleteIcon");
  tbBt.setStateId("remove");
  tbBt.addListener(edtLsnr);
  tb.addButton( tbBt );
 
  tbBt = new ToolbarButton();
  tbBt.setIconCls("cleanIcon");
  tbBt.setStateId("clear");
  tbBt.addListener(edtLsnr);
  tb.addButton( tbBt );

  
  tree.setTopToolbar(tb);
  
  tree.setEnableDrop(true);
  tree.setDdGroup("sailDDGroup");
  
  tree.addListener(new TreePanelListenerAdapter()
  {
   public boolean doBeforeNodeDrop(TreePanel treePanel, TreeNode target, DragData dragData, String point,
     DragDrop source, TreeNode dropNode, DropNodeCallback dropDropNodeCallback)
   {
    String prop = null;
    
    prop = dragData.getProperty("uk.ac.ebi.dd");
    
    if( ! "true".equals(prop) )
     return false;
    
    JavaScriptObjectHelper.setAttribute(dragData.getJsObj(),"uk.ac.ebi.dd",false);
    
    if(dragData instanceof GridDragData)
    {
     GridDragData gridDragData = (GridDragData) dragData;
     Record[] recs = gridDragData.getSelections();

     if(treePanel.getRootNode().equals(target))
     {
      if(recs.length == 1)
      {
       Object dropObj = recs[0].getAsObject("obj");
       
       if( dropObj instanceof Parameter )
        addParameter((Parameter) dropObj);
       else if( dropObj instanceof GroupRequestItem )
        addPredefinedQuery((GroupRequestItem)dropObj);
      }
      else if(recs.length > 1)
      {
       List<Parameter> lst = new ArrayList<Parameter>(5);

       for(Record r : recs)
        lst.add((Parameter) r.getAsObject("obj"));

       addGroup(lst);
      }

      return true;
     }

     if(recs.length == 1)
     {
      Object dropObj = recs[0].getAsObject("obj");
      
      if( dropObj instanceof Parameter )
       addParameterToGroup((Parameter) dropObj, target);
      else if( dropObj instanceof GroupRequestItem )
       addPredefinedQueryToGroup((GroupRequestItem)dropObj, target);

     }
     else if(recs.length > 1)
     {
      List<Parameter> lst = new ArrayList<Parameter>(5);

      for(Record r : recs)
       lst.add((Parameter) r.getAsObject("obj"));

      addParameterToGroup(lst, target);
     }
    }
    else if(dragData instanceof TreeDragData)
    {
     Parameter p = (Parameter) ((TreeDragData) dragData).getTreeNode().getUserObject();

     if(treePanel.getRootNode().equals(target))
      addParameter(p);
     else
      addParameterToGroup(p, target);
     
     return false;
    }

    return true;
   }

   public boolean onNodeDragOver(TreePanel treePanel, TreeNode target, DragData dragData, String point,
     DragDrop source, TreeNode dropNode)
   {
    RequestItem rri = (RequestItem)target.getUserObject();
  
//    rri.getType() == Type.GROUP || 
//    ( (rri.getType() == Type.PARAMETER || rri.getType() == Type.FILTERED_SINGLE || rri.getType() == Type.FILTERED_COMPLEX) /*&& rri.getGroupItem() == null*/ ) )

    
    if( treePanel.getRootNode().equals(target)
        || rri instanceof GroupRequestItem
        || rri instanceof ParameterRequestItem
        || rri instanceof FilteredRequestItem )
      return true;
    
    return false;
   }

   public void onNodeDrop(TreePanel treePanel, TreeNode target, DragData dragData, java.lang.String point,
     DragDrop source, TreeNode dropNode)
   {
//    System.out.println("onNodeDrop: " + point);
   }
  });
  
  
  DropTargetConfig cfg = new DropTargetConfig();
  cfg.setdDdGroup("sailDDGroup");

  new DropTarget(tree, cfg)
  {
   public boolean notifyDrop(DragSource source, EventObject e, DragData data)
   {
//    System.out.println("notifyDrop " + e.getTarget().getId() + " dd: " + data.getJsObj());

//    if(!"DIV".equalsIgnoreCase(e.getTarget().getNodeName()))
//     return false;

    if(data instanceof GridDragData)
    {
     GridDragData gridDragData = (GridDragData) data;
     Record[] recs = gridDragData.getSelections();

     if(recs.length == 1)
     {
      Object dropObj = recs[0].getAsObject("obj");
      
      if( dropObj instanceof Parameter )
       addParameter((Parameter) dropObj);
      else if( dropObj instanceof GroupRequestItem )
       addPredefinedQuery((GroupRequestItem)dropObj);
     }
     else if(recs.length > 1)
     {
      List<Parameter> lst = new ArrayList<Parameter>(5);

      for(Record r : recs)
       lst.add((Parameter) r.getAsObject("obj"));

      addGroup(lst);
     }
    }
    else if(data instanceof TreeDragData)
    {
     Parameter p = (Parameter) ((TreeDragData) data).getTreeNode().getUserObject();

     addParameter(p);
    }

    return true;
   }

   public String notifyOver(DragSource source, EventObject e, DragData data)
   {
    return "x-dd-drop-ok";
   }

   public String notifyEnter( DragSource source, EventObject e, DragData dragData )
   {
    JavaScriptObjectHelper.setAttribute(dragData.getJsObj(),"uk.ac.ebi.dd",true);

    return "x-dd-drop-ok";
   }


  }; 
  
  Panel controlPanel = new Panel();
  
  controlPanel.setPaddings(3);
  
//  Button bt;
//  ButtonListenerAdapter edtLsnr = new ReportEditor();
//  
//  bt=new Button("Remove");
//  bt.setStateId("remove");
//  bt.addListener(edtLsnr);
//  bt.setIconCls("deleteIcon");
//  controlPanel.addButton(bt);
//  
//  bt=new Button("Clear");
//  bt.setStateId("clear");
//  bt.setIconCls("cleanIcon");
//  bt.addListener(edtLsnr);
//  controlPanel.addButton(bt);
//  
//  bt=new Button("Up");
//  bt.setStateId("up");
//  bt.setIconCls("upIcon");
//  bt.addListener(edtLsnr);
//  controlPanel.addButton(bt);
//  
//  bt=new Button("Down");
//  bt.setStateId("down");
//  bt.setIconCls("downIcon");
//  bt.addListener(edtLsnr);
//  controlPanel.addButton(bt);

  FormPanel leftPart = new FormPanel();
  FormPanel rightPart = new FormPanel();
  
  
  useCollectionSplit = new Checkbox("Use split by collection");
  splitBySpecific = new Checkbox("Specify collections");

  useCollectionSplit.setChecked(true);

  final Button persSelButton = new Button("Select");
  persSelButton.setDisabled(true);
  
  persSelButton.addListener(new ButtonListenerAdapter()
  {
   public void onClick(Button button, EventObject e)
   {
    CollectionDialog.getInstance().show();
   }
  }
  );
  
  splitBySpecific.addListener( new CheckboxListenerAdapter()
  {
   public void onCheck(Checkbox field, boolean checked)
   {
    persSelButton.setDisabled(!checked);
   }
  }
  );


//  splitByAll = new Radio("Split by all","split");
//  splitBySel = new Radio("Split by selected","split");
//  splitByAll.setChecked(true);
  
  
  final Button relSelButton = new Button("Select", new RelSelector() );
  relSelButton.setDisabled(true);
  
  useRels = new Checkbox("Use relations");
  relsSpecific = new Checkbox("Specific relations");
  
  relsSpecific.addListener( new CheckboxListenerAdapter()
  {
   public void onCheck(Checkbox field, boolean checked)
   {
    relSelButton.setDisabled(!checked);
   }
  }
  );

  
  useRels.addListener( new CheckboxListenerAdapter()
  {
   public void onCheck(Checkbox field, boolean checked)
   {
     if(!checked)
     {
      relsSpecific.setDisabled(true);
      relSelButton.setDisabled(true);
     }
     else
     {
      relsSpecific.setDisabled(false);
      relSelButton.setDisabled(!relsSpecific.getValue());
     }
    }
   }
  );
  
  useCollectionSplit.addListener(  new CheckboxListenerAdapter(){
   
  public void onCheck(Checkbox field, boolean checked)
  {
    if(!checked)
    {
     splitBySpecific.setDisabled(true);
     persSelButton.setDisabled(true);
    }
    else
    {
     splitBySpecific.setDisabled(false);
     persSelButton.setDisabled(!splitBySpecific.getValue());
    }
   }
  }
);
  
  controlPanel.setLayout( new ColumnLayout() );
  
  controlPanel.add(leftPart, new ColumnLayoutData(0.5));
  controlPanel.add(rightPart, new ColumnLayoutData(0.5));

  rightPart.setHideLabels(true);
  leftPart.setHideLabels(true);
  
  rightPart.add(useRels);
  rightPart.add(relsSpecific);
  rightPart.add(relSelButton);
  
  leftPart.add( useCollectionSplit );
  leftPart.add( splitBySpecific );
  leftPart.add( persSelButton );
//  leftPart.add( splitByAll );
//  leftPart.add( splitBySel );

  rightPart.setHeight(85);
  leftPart.setHeight(85);
  
  leftPart.setFrame(true);
  rightPart.setFrame(true);
  
  controlPanel.setHeight(100);
  
////  add(predef, new AnchorLayoutData("100% 20%"));
//  add(tree, new AnchorLayoutData("100% 80%"));
//
//  add(controlPanel, new AnchorLayoutData("100% 20%"));
 
  if( pexpr != null && pexpr.size() > 0 )
  {
   predef = new PredefQueryTable();
   predef.setWidth("100%");
   predef.setHeight("20%");

   predef.setListener( new ObjectAction<GroupRequestItem>()
   {

    @Override
    public void doAction(String actName, GroupRequestItem p)
    {
     if( "add".equals(actName) )
      addPredefinedQuery( p );
     else if( "addGrp".equals(actName) )
      addPredefinedQueryToGroup(p, null);
    }

    @Override
    public void doMultyAction(String actName, List<GroupRequestItem> lp)
    {
    }
   });
  }

  
  if( predef != null)
   add(predef);
  
  add(tree);

  add(controlPanel);

  
  setButtonAlign(Position.CENTER);

  Button b = new Button("Query");
  b.setIconCls("scienceIcon");
  addButton(b);

  b.addListener(new ReportBtListener());



//  grid.addListener(new ContainerListenerAdapter()
//  {
//   public void onAfterLayout(Container self)
//   {
//    grid.refresh();
//   }
//  });

 }

 private int setReqId( int id, RequestItem itm )
 {
  if( itm instanceof GroupRequestItem )
  {
   GroupRequestItem grit = (GroupRequestItem)itm;
   
   grit.setId(id++);
   
   for( RequestItem sitm : grit.getItems() )
    id = setReqId(id, sitm);
  }
  else
   itm.setId(id++);
  
  return id;
 }
 
 private ReportRequest prepareReportRequest()
 {
  ReportRequest rreq = new ReportRequest();

  GroupRequestItem groot = rreq.getRootGroup();
  
  groot.setDepth(isStateAnd?-1:1);

  if(useRels.getValue())
  {
   if(relsSpecific.getValue())
    rreq.setRelations(SelectRelationsDialog.getInstance().getRelations());

   rreq.setAllRelations(!relsSpecific.getValue());
  }

  if(useCollectionSplit.getValue())
  {
   rreq.setCollectionSplit( true );
   
   if( splitBySpecific.getValue() )
    rreq.setCollections( CollectionDialog.getInstance().getCollections() );
  
  }
  
  return rreq;
 }

 public void quickQuery(Parameter p)
 {
  ReportRequest rreq = prepareReportRequest();

  rreq.add( new ParameterRequestItem(p.getCode()+" ("+p.getName()+")", p) );
  
  setReqId(1, rreq.getRootGroup());

  for(MakeReportListener l : listeners)
   l.makeReport(rreq);
 }



 public void quickQuery(List<Parameter> lp)
 {
  ReportRequest rreq = prepareReportRequest();

  for( Parameter p : lp )
   rreq.add( new ParameterRequestItem(p.getCode()+" ("+p.getName()+")", p) );
  
  setReqId(1, rreq.getRootGroup());

  for(MakeReportListener l : listeners)
   l.makeReport(rreq);
 }
 
 private void addObject(RequestItem rri)
 {
  TreeNode reqNode = (TreeNode)tree.getRootNode().getChildNodes()[0];
  TreeNode spnode = null;
  
  if( reqNode.getChildNodes().length > 0 )
  {
   spnode = new TreeNode("");  
   spnode.setLeaf(true);
   
   if( isStateAnd )
   {
    spnode.setCls("andNodeExternal");
    spnode.setIconCls("andIcon");
   }
   else
   {
    spnode.setCls("orNodeExternal");
    spnode.setIconCls("orIcon");
   }
   
   tree.getRootNode().appendChild(spnode);
  }
  else
   spnode = reqNode;
  
  
  TreeNode tnode = new TreeNode(rri.getName());  
//  tnode.setLeaf(true);
  tnode.setUserObject(rri);
  tnode.setIconCls(rri.getIconClass());
  tnode.addListener(nodeListener);
  spnode.appendChild(tnode);
  
  spnode.expand();
 }

 
 private void addObjectToGroup(RequestItem rri, TreeNode sel)
 {
  if(sel == null)
   sel = ((DefaultSelectionModel) tree.getSelectionModel()).getSelectedNode();

  if(sel == null || sel.getUserObject() == null)
   return;

  RequestItem selRri = (RequestItem) sel.getUserObject();

  TreeNode grNode = (TreeNode) sel.getParentNode();

  if(grNode == tree.getRootNode())
  {
   addObject(rri);
   return;
  }

  TreeNode nn = new TreeNode(rri.getName());
  nn.setIconCls(rri.getIconClass());
  nn.setUserObject(rri);
  nn.addListener(nodeListener);

  if(selRri == null)
   grNode = sel;
  // rri.setGroupItem(selRri);

  TreeNode spnode = new TreeNode("");
  spnode.setLeaf(true);
  if(!isStateAnd)
  {
   spnode.setCls("andNodeInternal");
   spnode.setIconCls("andIcon");
  }
  else
  {
   spnode.setCls("orNodeInternal");
   spnode.setIconCls("orIcon");
  }

  grNode.appendChild(spnode);

  grNode.appendChild(nn);

  /*
   * else if( selRri.getType() == Type.PARAMETER || selRri.getType() ==
   * Type.FILTERED_SINGLE || selRri.getType() == Type.FILTERED_COMPLEX ) {
   * ReportRequestItem grr = new
   * ReportRequestItem(Type.GROUP,"Group "+(ids++),null);
   * 
   * // TreeNode nGrn = new TreeNode(grr.getObjectName()); TreeNode nGrn = sel;
   * nGrn.setLeaf(false);
   * 
   * nGrn.setText(grr.getObjectName()); nGrn.setIconCls(grr.getIconClass());
   * nGrn.setUserObject(grr); // sel.getParentNode().replaceChild(nGrn,sel);
   * 
   * selRri.setGroupItem(grr); TreeNode nn = new
   * TreeNode(selRri.getObjectName()); nn.setIconCls(selRri.getIconClass());
   * nn.setUserObject(selRri); nn.addListener(nodeListener);
   * nGrn.appendChild(nn);
   * 
   * TreeNode spnode = new TreeNode(""); spnode.setLeaf(true);
   * spnode.setCls("orSeparator"); spnode.setIconCls("orIcon");
   * nGrn.appendChild(spnode);
   * 
   * rri.setGroupItem(grr); nn = new TreeNode(rri.getObjectName());
   * nn.setIconCls(rri.getIconClass()); nn.setUserObject(rri);
   * nn.addListener(nodeListener); nGrn.appendChild(nn);
   * 
   * nGrn.expand(); // grid.getSelectionModel().select(nGrn); }
   */
 }

 
 
 public void addVariable( Variable v )
 {
  RequestItem rri = new PartRequestItem(v.getParameter().getCode()+"."+v.getName()+" ("+v.getParameter().getName()+")", v);
  
  addObject(rri);
 }

 public void addPredefinedQuery( GroupRequestItem g )
 {
//  ReportRequestItem rri = new ReportRequestItem(Type.PREDEF, g.getGroupName(), g);
  
  addObject(g);
 }
 
 public void addParameter(Parameter p)
 {
  RequestItem rri = new ParameterRequestItem(p.getCode()+" ("+p.getName()+")", p);
  
  addObject(rri);
 }

 public void addParameter(Qualifier q)
 {
  RequestItem rri = new PartRequestItem(q.getParameter().getCode()+"."+q.getName()+" ("+q.getParameter().getName()+")", q);
  
  addObject(rri);
 }
 
 public void addSplitFiltered( FilteredEnumeration fe)
 {
  RequestItem rri = new EnumFilteredRequestItem(fe.getParameter().getCode()+" ("+fe.getParameter().getName()+")", fe );
  
  addObject(rri);
 }

 public void addFiltered( ComplexFilter cf )
 {
  RequestItem rri = new ComplexFilteredRequestItem(cf.getParameter().getCode()+" ("+cf.getParameter().getName()+")", cf );
  
  addObject(rri);
 }
 
 public void addFilteredToGroup( ComplexFilter cf )
 {
  RequestItem rri = new ComplexFilteredRequestItem(cf.getParameter().getCode()+" ("+cf.getParameter().getName()+")", cf );
  
  addObjectToGroup(rri, null);
 }

// public void addFiltered( FilteredEnumeration fe )
// {
//  Parameter p = fe.getParameter();
//  
//  ReportRequestItem rri = new ReportRequestItem(Type.FILTERED_SINGLE, p.getCode()+" ("+p.getName()+")", fe );
//  
//  addObject(rri);
// }
 
// public void addFiltered(List<FilteredEnumeration> fel)
// {
//  Parameter p = fel.get(0).getParameter();
//  
//  ReportRequestItem rri = new ReportRequestItem(Type.FILTERED_COMPLEX, p.getCode()+" ("+p.getName()+")", fel );
//  
//  addObject(rri);
// }
 
// public void addFilteredToGroup(FilteredEnumeration fe)
// {
//  Parameter p = fe.getParameter();
//
//  ReportRequestItem rri = new ReportRequestItem(Type.FILTERED_SINGLE, p.getCode()+" ("+p.getName()+")", fe );
//  
//  addObjectToGroup(rri, null);
// }
 
// public void addFilteredToGroup(List<FilteredEnumeration> fel )
// {
//  Parameter p = fel.get(0).getParameter();
//  ReportRequestItem rri = null;
//  
//  if( fel.size() == 1 )
//   rri = new ReportRequestItem(Type.FILTERED_SINGLE, p.getCode()+" ("+p.getName()+")", fel.get(0) );
//  else
//   rri = new ReportRequestItem(Type.FILTERED_COMPLEX, p.getCode()+" ("+p.getName()+")", fel );
//
//  addObjectToGroup(rri, null);
// }
 
 public void addParameterToGroup(Parameter p)
 {
  addParameterToGroup(p,null);
 }

 private void addParameterToGroup(Parameter p, TreeNode dropNode)
 {
  RequestItem prri = new ParameterRequestItem(p.getCode()+" ("+p.getName()+")",p);
  
  addObjectToGroup(prri, dropNode);
 }
 
 private void addPredefinedQueryToGroup( GroupRequestItem g, TreeNode dropNode )
 {
  addObjectToGroup(g, dropNode);
 }


 public void addParameterToGroup(List<Parameter> lp)
 {
  addParameterToGroup(lp, null);
 }
 
 public void addParameterToGroup(List<Parameter> lp, TreeNode dropNode)
 {
  for(Parameter p : lp)
  {
   RequestItem rri = new ParameterRequestItem( p.getCode()+" ("+p.getName()+")", p );
   addObjectToGroup(rri, dropNode);
  }
 }
 

 public void addGroup(List<Parameter> lp)
 {
  if(lp == null)
   return;

//  int id = ids++;

  TreeNode reqNode = (TreeNode)tree.getRootNode().getChildNodes()[0];
  
  TreeNode tnode=null;
  if( reqNode.getChildNodes().length > 0 )
  {
   tnode = new TreeNode("");  
   tnode.setLeaf(true);
   if( isStateAnd )
   {
    tnode.setCls("andNodeExternal");
    tnode.setIconCls("andIcon");
   }
   else
   {
    tnode.setCls("orNodeExternal");
    tnode.setIconCls("orIcon");
   }
   
   tree.getRootNode().appendChild(tnode);
  }
  else
   tnode = reqNode;
  
//  TreeNode tnode = new TreeNode("Group "+id);  
//  tnode.setLeaf(false);
//  tnode.setUserObject(new ReportRequestItem(Type.GROUP,"Group "+id,null));
//  tnode.setIconCls(Type.GROUP.getIcon());
//  tnode.addListener(nodeListener);
  
  boolean first = true;
  for(Parameter pfl : lp)
  {
   if( !first )
   {
    TreeNode spnode = new TreeNode("");  
    spnode.setLeaf(true);
    
    if( !isStateAnd )
    {
     spnode.setCls("andNodeInternal");
     spnode.setIconCls("andIcon");
    }
    else
    {
     spnode.setCls("orNodeInternal");
     spnode.setIconCls("orIcon");
    }
    
    tnode.appendChild(spnode);
   }
   
   first=false;

   RequestItem nrri = new ParameterRequestItem(pfl.getCode()+" ("+pfl.getName()+")",pfl);
   
   TreeNode pnode = new TreeNode(nrri.getName());  
   pnode.setLeaf(true);
   pnode.setIconCls(nrri.getIconClass());
   pnode.setUserObject(nrri);
   pnode.addListener(nodeListener);
   
   tnode.appendChild(pnode);
  }
  

//  tree.getRootNode().appendChild(tnode);
  tnode.ensureVisible();
  tnode.expand();
 }

 public void addReportListener(MakeReportListener l)
 {
  listeners.add(l);
 }
 
 public void setRequestItemSelectListener( RequestItemSelectListener l )
 {
  reqSelListener=l;
 }

 private class ReportBtListener extends ButtonListenerAdapter
 {
  @Override
  public void onClick(Button button, EventObject e)
  {
   if( tree.getRootNode().getChildNodes().length == 0)
   {
    if( ! useCollectionSplit.getValue())
     return;
   }

   ReportRequest rreq = prepareReportRequest();


   for( Node nd : tree.getRootNode().getChildNodes() )
   {
    Node[] chlds = nd.getChildNodes();
    
    if( chlds.length == 1 )
     rreq.add((RequestItem) chlds[0].getUserObject());
    else
    {
     GroupRequestItem grp = new GroupRequestItem();
     grp.setDepth(isStateAnd?1:-1);
     rreq.add(grp);
     
     for(Node cnode : chlds)
     {
      if(cnode.getUserObject() != null)
       grp.addItem((RequestItem) cnode.getUserObject());
     }
    }
    

    // if( nd.getChildNodes().length == 1 )
    // rreq.add((RequestItem)nd.getChildNodes()[0].getUserObject());
    // else
    // {
    // List<ReportRequestItem> gris = new
    // ArrayList<ReportRequestItem>(nd.getChildNodes().length/2+1);
    // ReportRequestItem rri = new ReportRequestItem( Type.GROUP, null, gris);
    //    
    // for( Node gnode : nd.getChildNodes() )
    // {
    // if( gnode.getUserObject() == null )
    // continue;
    //      
    // gris.add((ReportRequestItem)gnode.getUserObject());
    // }
    // rreq.add(rri);
    // }
   }
    
   setReqId(1, rreq.getRootGroup());
   
   for(MakeReportListener l : listeners)
    l.makeReport(rreq);
  }
 }


 private class RelSelector extends ButtonListenerAdapter
 {
  @Override
  public void onClick(Button button, EventObject e)
  {
   SelectRelationsDialog.getInstance().show();
  }
 }
 
// private class ClearBtListener extends ButtonListenerAdapter
// {
//  @Override
//  public void onClick(Button button, EventObject e)
//  {
//   store.removeAll();
//  }
// }
 
 private class ReportEditor extends ButtonListenerAdapter
 {
  @Override
  public void onClick(Button button, EventObject e)
  {
   String state = button.getStateId();
   
   if( "clear".equals(state) )
   {
    boolean first = true;
    for( Node n : tree.getRootNode().getChildNodes() )
    {
     if( ! first )
      n.remove();
     else
     {
      for( Node rnc : n.getChildNodes() )
      {
//       System.out.println("Removing: "+((TreeNode)rnc).getText());
       rnc.remove();
      }
      
      first=false;
     }
    }
    
    if( reqSelListener != null )
     reqSelListener.itemSelected(null);
   }
   else if( "remove".equals(state) )
   {
    TreeNode sel = ((DefaultSelectionModel)tree.getSelectionModel()).getSelectedNode();
    
    if( sel == null || sel.getUserObject() == null )
     return;
    
//    if( sel.getParentNode().getChildNodes().length == 1 && ! sel.getParentNode().equals( tree.getRootNode() ) )
//     sel = (TreeNode)sel.getParentNode();

    if( reqSelListener != null && tree.getSelectionModel().isSelected(sel) )
     reqSelListener.itemSelected(null);

    removeNode(sel);

   }
   else if( "down".equals(state)  )
   {
    TreeNode sel = ((DefaultSelectionModel)tree.getSelectionModel()).getSelectedNode();
    
    if( sel == null )
     return;
    
    moveNodeDown(sel);
    
    tree.getSelectionModel().select(sel);
    
//    grid.getRootNode().appendChild(new TreeNode("Все козлы!!!"));
   }
   else if( "up".equals(state)  )
   {
    TreeNode sel = ((DefaultSelectionModel)tree.getSelectionModel()).getSelectedNode();
    
    if( sel == null )
     return;
    
    moveNodeUp(sel);
    
    tree.getSelectionModel().select(sel);
    
//    grid.getRootNode().appendChild(new TreeNode("Все козлы!!!"));
   }
  }
 
 }

 static void removeNode( Node sel )
 {
//  if( sel.getParentNode().getChildNodes().length == 1 )
//   sel = sel.getParentNode();

  TreeNode pNode = (TreeNode)sel.getParentNode();
  
  if( sel.getNextSibling() != null  ) // There are another items in the group. Just removing from group
  {
   sel.getNextSibling().remove();
   sel.remove();
   return;
  }

  if( pNode.getChildNodes().length > 1 ) //It means last item in the group where there are another items
  {
   sel.getPreviousSibling().remove();
   sel.remove();
   return;
  }
  
  if( pNode.getPreviousSibling() != null ) // Last and only item and there are another groups below
  {
   sel.getParentNode().remove();
   return;
  }
  
  TreeNode nextNode = (TreeNode)pNode.getNextSibling();

  if( nextNode == null ) //This is a last group with a single item
  {
   sel.remove();
   return;
  }
 
//  String icClass = pNode.getUI().getIconEl().getClassName();
  
  nextNode.setIconCls(rootNodeIcon);
  nextNode.setText(rootNodeName);
//  nextNode.getUI().getIconEl().setClassName( "rootNodeIcon" );
  
  nextNode.getUI().removeClass( "andNodeExternal");
  nextNode.getUI().removeClass( "orNodeExternal");

  pNode.remove();
  
//  Node rootNode = sel.getOwnerTree().getRootNode();
//  TreeNode reqNode = (TreeNode)rootNode.getChildNodes()[0];
//  
//  if( sel.getParentNode() == reqNode )
//  {
//   TreeNode nextNode = (TreeNode)reqNode.getNextSibling();
//   if( nextNode != null )
//   {
//    nextNode.getUI().getIconEl().setClassName(reqNode.getUI().getIconEl().getClassName());
//    nextNode.getUI().getEl().setClassName(nextNode.getUI().getEl().getClassName());
//   }
//   else
//    sel.remove();
//  }
//  else
//   sel.getParentNode().remove();
//  

 }
 
 private void moveNodeUp( Node nd )
 {
  TreeNode parentNode = (TreeNode)nd.getParentNode();
  
  Node[] sibs = parentNode.getChildNodes();
  
  
  if( sibs[0].equals(nd)  )
  {
   TreeNode rootNode = (TreeNode)parentNode.getParentNode();
   
   Node[] topNodes = rootNode.getChildNodes();

   int n = 0;
   
   for( Node opnd : topNodes )
   {
    if( opnd.equals(parentNode) )
     break;
    
    n++;
   }
   
   if( n==0 )
    return;
   
   Node[] upNds = topNodes[n-1].getChildNodes();

   for(Node mynd : sibs )
   {
    TreeNode ntn = new TreeNode(((TreeNode)mynd).getText());

    if( ((TreeNode)mynd).getUserObject() != null )
    {
     RequestItem rri = (RequestItem)((TreeNode)mynd).getUserObject();
     ntn.setUserObject(rri);
     ntn.setIconCls(rri.getIconClass());
     ntn.addListener(nodeListener);
    }
    else
    {
     if( !isStateAnd )
     {
      ntn.setCls("andNodeInternal");
      ntn.setIconCls("andIcon");
     }
     else
     {
      ntn.setCls("orNodeInternal");
      ntn.setIconCls("orIcon");
     }

    }
    
    topNodes[n-1].appendChild(ntn);
   }

   for(int k=0; k < upNds.length; k++)
    parentNode.appendChild(upNds[k]);

   for(Node mynd : sibs)
    mynd.remove();
   
   return;
  }
  
  int i=parentNode.indexOf(nd);
  
  parentNode.insertBefore(nd, parentNode.item(i-2));
  
  parentNode.insertBefore(parentNode.item(i), parentNode.item(i-1));
 }

 private void moveNodeDown( Node nd )
 {
  TreeNode parentNode = (TreeNode)nd.getParentNode();
  
  Node[] sibs = parentNode.getChildNodes();
  
  if( sibs[sibs.length-1].equals(nd)  )
  {
   TreeNode rootNode = (TreeNode)parentNode.getParentNode();
   
   Node[] topNodes = rootNode.getChildNodes();

   int n = 0;
   
   for( Node opnd : topNodes )
   {
    if( opnd.equals(parentNode) )
     break;
    
    n++;
   }
   
   if( n ==  (topNodes.length-1) )
    return;
   
   Node[] upNds = topNodes[n+1].getChildNodes();

   for(Node mynd : sibs )
   {
    TreeNode ntn = new TreeNode(((TreeNode)mynd).getText());

    if( ((TreeNode)mynd).getUserObject() != null )
    {
     RequestItem rri = (RequestItem)((TreeNode)mynd).getUserObject();
     ntn.setUserObject(rri);
     ntn.setIconCls(rri.getIconClass());
     ntn.addListener(nodeListener);
    }
    else
    {
     if( !isStateAnd )
     {
      ntn.setCls("andNodeInternal");
      ntn.setIconCls("andIcon");
     }
     else
     {
      ntn.setCls("orNodeInternal");
      ntn.setIconCls("orIcon");
     }

    }
    
    topNodes[n+1].appendChild(ntn);
   }

   for(int k=0; k < upNds.length; k++)
    parentNode.appendChild(upNds[k]);

   for(Node mynd : sibs)
    mynd.remove();
   
   return;
  }
  
  
  int i=parentNode.indexOf(nd);
  
  parentNode.insertBefore(nd, parentNode.item(i+2));
  parentNode.insertBefore(parentNode.item(i+2), parentNode.item(i));
 }

 
 private class NodeListener extends TreeNodeListenerAdapter
 {
  private MenuListener lsnr = new MenuListener();
  
  public void onContextMenu(Node node, EventObject e) 
  {
   RequestItem rItem = (RequestItem)node.getUserObject();
   
   Menu menu = new Menu();
   MyItem itm = null;
   
   if( rItem instanceof GroupRequestItem )
   {
    itm = new MyItem("Rename", (TreeNode) node);
    itm.setStateId("edit");
    itm.addListener(lsnr);
    itm.setIconCls("editIcon");

    menu.addItem(itm);

    menu.addSeparator();
   }
   else if( rItem instanceof ParameterRequestItem || rItem instanceof ComplexFilteredRequestItem )
   {
    itm = new MyItem("Change", (TreeNode) node);
    itm.setStateId("edit");
    itm.addListener(lsnr);
    itm.setIconCls("editFilterIcon");

    menu.addItem(itm);

    menu.addSeparator();
   }
  
   itm = new MyItem("Up",(TreeNode)node);
   itm.setStateId("up");
   itm.addListener( lsnr );
   itm.setIconCls("upIcon");
   menu.addItem(itm);
   
   
   itm = new MyItem("Down",(TreeNode)node);
   itm.setStateId("down");
   itm.addListener( lsnr );
   itm.setIconCls("downIcon");
   menu.addItem(itm);
   
   
   menu.addSeparator();
   
   itm = new MyItem("Remove",(TreeNode)node);
   itm.setStateId("remove");
   itm.addListener( lsnr );
   itm.setIconCls("deleteIcon");
   menu.addItem(itm);
   

   menu.showAt(e.getXY());
  }

  
  private class MenuListener extends BaseItemListenerAdapter
  {
   public void onClick(BaseItem item, EventObject e)
   {
    final MyItem mItm = (MyItem)item;
    
    if( "edit".equals(item.getStateId()) )
    {
     if( mItm.getRI() instanceof GroupRequestItem )
     {
      MessageBox.prompt("Enter group name", "Enter new group name", new MessageBox.PromptCallback(){

       public void execute(String arg0, String arg1)
       {
        if( "ok".equals(arg0) )
        {
         mItm.getNode().setText(arg1);
         mItm.getRI().setName(arg1);
        }
       }});
     }
     else if ( mItm.getRI() instanceof ParameterRequestItem )
     {
      ComplexFilter cf = new ComplexFilter();
      cf.setParameter((Parameter)((ParameterRequestItem)mItm.getNode().getUserObject()).getParameter());
      
      ParameterFilterDialog fDlg = new ParameterFilterDialog(cf, false);
      
      fDlg.setListener( new ObjectAction<ComplexFilter>()
        {
         @Override
         public void doMultyAction(String actName, List<ComplexFilter> lp)
         {
         }
         
         @Override
         public void doAction(String actName, ComplexFilter cf)
         {
          if( !cf.isClean() )
          {
           ComplexFilteredRequestItem cfri = new ComplexFilteredRequestItem( mItm.getRI().getName(), cf);
           
           mItm.setRI( cfri );
           mItm.getNode().getUI().getIconEl().setClassName("x-tree-node-icon "+cfri.getIconClass());
           
//           mItm.getRRI().setType(Type.FILTERED_COMPLEX);
//           mItm.getRRI().setObject(p);
          }
         }
        });

      fDlg.show();
     }
     else if (mItm.getRI() instanceof ComplexFilteredRequestItem )
     {
      ComplexFilter cf = (ComplexFilter)((ComplexFilteredRequestItem)mItm.getNode().getUserObject()).getFilter();
      
      ParameterFilterDialog fDlg = new ParameterFilterDialog(cf, false);
      
      fDlg.setListener( new ObjectAction<ComplexFilter>()
      {
       @Override
       public void doMultyAction(String actName, List<ComplexFilter> lp)
       {
       }
       
       @Override
       public void doAction(String actName, ComplexFilter p)
       {

        if( p.isClean() )
        {
         ParameterRequestItem pri = new ParameterRequestItem(mItm.getRI().getName(), p.getParameter());
         
         mItm.setRI( pri );
         mItm.getNode().getUI().getIconEl().setClassName("x-tree-node-icon "+pri.getIconClass());

//         mItm.getRRI().setType(Type.PARAMETER);
//         mItm.getRRI().setObject(p.getParameter());
        }
       }
      });
      
      fDlg.show();
     }
    }
    else if( "up".equals(item.getStateId()))
    {
     TreeNode sel = ((DefaultSelectionModel)tree.getSelectionModel()).getSelectedNode();

     moveNodeUp(mItm.getNode());

     if( mItm.getNode().equals(sel) )
      ((DefaultSelectionModel)tree.getSelectionModel()).select(mItm.getNode());
    }
    else if( "down".equals(item.getStateId()))
    {
     TreeNode sel = ((DefaultSelectionModel)tree.getSelectionModel()).getSelectedNode();

     moveNodeDown(mItm.getNode());

     if( mItm.getNode().equals(sel) )
      ((DefaultSelectionModel)tree.getSelectionModel()).select(mItm.getNode());
    }
    else if( "remove".equals(item.getStateId()))
    {
     removeNode(mItm.getNode());
    }
   }
  }
  
 }
 
 static class MyItem extends Item
 {
  TreeNode node;
  
  public MyItem(String name, TreeNode n)
  {
   super(name);
   node=n;
  }
  
  public void setRI(RequestItem cfri)
  {
   node.setUserObject(cfri);
  }

  RequestItem getRI()
  {
   return (RequestItem)node.getUserObject();
  }
  
  TreeNode getNode()
  {
   return node;
  }
 }
 
 class AndOrBtListener extends ButtonListenerAdapter
 {
  public void onClick(Button bt, EventObject eo)
  {
   String state = bt.getStateId();
   
   if( "or".equals(state) && isStateAnd )
   {
    Node[] cNodes = tree.getRootNode().getChildNodes();
    
    for( int i=0; i < cNodes.length; i++ )
    {
     TreeNode tn = (TreeNode)cNodes[i];

     if( i > 0 )
     {
      tn.setCls("orNodeExternal");
      tn.setIconCls("orIcon");
      tn.getUI().addClass("orNodeExternal");
      tn.getUI().removeClass("andNodeExternal");
      tn.getUI().getIconEl().setClassName(changeClass("andIcon", "orIcon", tn.getUI().getIconEl().getClassName()));
     }
     
     Node[] gNodes = cNodes[i].getChildNodes();
     for( int j=1; j < gNodes.length; j+=2 )
     {
      tn = ((TreeNode)gNodes[j]);
      tn.setCls("andNodeInternal");
      tn.setIconCls("andIcon");
      tn.getUI().getIconEl().setClassName(changeClass("orIcon","andIcon",tn.getUI().getIconEl().getClassName()));
     }
    }
    
    isStateAnd = false;
   }

   if( "and".equals(state) && ! isStateAnd )
   {
    Node[] cNodes = tree.getRootNode().getChildNodes();
    
    for( int i=0; i < cNodes.length; i++ )
    {
     TreeNode tn = (TreeNode)cNodes[i];
     
     if( i > 0 )
     {
      tn.setCls("andNodeExternal");
      tn.setIconCls("andIcon");
      tn.getUI().addClass("andNodeExternal");
      tn.getUI().removeClass("orNodeExternal");
      tn.getUI().getIconEl().setClassName(changeClass("orIcon", "andIcon", tn.getUI().getIconEl().getClassName()));
     }
     
     Node[] gNodes = cNodes[i].getChildNodes();
     for( int j=1; j < gNodes.length; j+=2 )
     {
      tn = ((TreeNode)gNodes[j]);
      tn.setCls("orNodeInternal");
      tn.setIconCls("orIcon");
      tn.getUI().getIconEl().setClassName(changeClass("andIcon","orIcon",tn.getUI().getIconEl().getClassName()));
     }
    }

    tree.doLayout();
    isStateAnd = true;
   }

  }
  
 }
 
 public static String changeClass(String fromClass, String toClass, String classStr )
 {
  String parts[] = classStr.split("\\s+");
  
  StringBuilder sb = new StringBuilder(classStr.length());
  
  for( int i=0; i < parts.length; i++ )
   if( parts[i].equals(fromClass) )
    sb.append(toClass).append(' ');
   else
    sb.append(parts[i]).append(' ');
  
  return sb.toString();
 }



 @Override
 public void studyCollectionChanged(int stID, int khID)
 {
  collections.clear();

  if( khID != 0 )
   collections.add(khID);
  else if( stID != 0 )
  {
   Study st=null;
   for( Study s : DataManager.getInstance().getStudies() )
   {
    if( s.getId() == stID )
    { 
     st=s;
     break;
    }
   }
   
   if( st != null )
   {
    if( st.getCollections() != null )
    {
     for(SampleCollection c : st.getCollections())
      collections.add(c.getId());
    }
   }
  }
 }



}
