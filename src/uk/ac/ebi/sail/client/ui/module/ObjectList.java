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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.SAILObject;
import uk.ac.ebi.sail.client.data.Attributed;
import uk.ac.ebi.sail.client.data.Filterable;
import uk.ac.ebi.sail.client.data.TraversalCallback;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.ActionHelper;
import uk.ac.ebi.sail.client.ui.ListActionListenerAdapter;
import uk.ac.ebi.sail.client.ui.ObjectSelectionListener;
import uk.ac.ebi.sail.client.ui.widget.SearchToolbar;

import com.gwtext.client.core.Position;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StoreTraversalCallback;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Container;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.event.ContainerListenerAdapter;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.RowSelectionListener;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.menu.Menu;


public class ObjectList<T extends SAILObject & Attributed> extends Panel implements RowSelectionListener, Filterable<T>
{
 public enum Selection
 {
  SINGLE,
  MULTIPLE,
  CHECKBOX
 }

 private Store           store;
 private GridPanel       grid;

 private TraversalCallback<T> filter;
 
 private ListActionListenerAdapter<T> actListener;
 private ObjectSelectionListener<T> selListener;
 
 private ColumnModel columnModel;
 private RecordDef recordDef;
 
 private Action[] actions;
 
 public ObjectList()
 {}
 
 
 public ObjectList(Selection selMod, Action[] bttns, RecordDef recordDef,
   ColumnModel columnModel, String expand)
 {
  super();
  

  build(selMod, bttns, recordDef, columnModel, expand);

 }
 
 protected void fireAction( String actState )
 {
  actListener.onAction(actState);
 }

 protected void build(Selection selMod, Action[] bttns, RecordDef recordDef, ColumnModel columnModel, String expand)
 {
  actions=bttns;
  this.columnModel=columnModel;
  this.recordDef=recordDef;

  setBorder(false);
  setFrame(true);

  setLayout(new FitLayout());

  grid = new GridPanel();

  grid.setAutoScroll(true);
  
  store = new Store(recordDef);
  grid.setStore(store);

  grid.setTopToolbar(getToolbar());
  
  grid.addListener( new ContainerListenerAdapter(){

   public boolean doBeforeRender(Component component)
   {
    MessageBox.wait("Preparing...", "Preparing data"); 
    return true;
   }

   public void onRender(Component component)
   {
    MessageBox.hide();
   }

  } );
 
  
  if( selMod == Selection.CHECKBOX )
  {
   CheckboxSelectionModel sMod = new CheckboxSelectionModel();
   
   BaseColumnConfig[] oCC = columnModel.getColumnConfigs();
   
   BaseColumnConfig[] nCC = new BaseColumnConfig[ oCC.length +1 ];
   nCC[0] = new CheckboxColumnConfig(sMod);
   
   for(int i=0; i < oCC.length; i++ )
    nCC[i+1]=oCC[i];
   
   columnModel = new ColumnModel(nCC);
   grid.setSelectionModel(sMod);
  }
  else
   grid.setSelectionModel(new RowSelectionModel(selMod == Selection.SINGLE));
  
  grid.setColumnModel(columnModel);



  
  grid.setFrame(false);
  grid.setStripeRows(true);
  
  grid.setAutoWidth(true);
  grid.setMonitorWindowResize(true);

  
//  GridSearchPlugin gridSearch = new GridSearchPlugin(GridSearchPlugin.TOP);
//  gridSearch.setMode(GridSearchPlugin.LOCAL);
//  grid.addPlugin(gridSearch);

  if( expand != null )
   grid.setAutoExpandColumn(expand);
  

  add(grid);

  actListener = new ListActionListenerAdapter<T>( grid );
  
  for(Action btid : actions)
  {
   Button bt = new Button(btid.getText(), actListener);
   bt.setStateId(btid.getAction());
   
   if( btid.getCls() != null )
    bt.setIconCls(btid.getCls());
   
   Menu mnu = ActionHelper.createSubActions(btid, actListener);

   if( mnu != null )
   {
    bt.setMenu(mnu);
   }
   
   btid.setComponent(bt);
   addButton(bt);
  }

  setButtonAlign(Position.CENTER);

  grid.addListener(new ContainerListenerAdapter()
  {
   public void onAfterLayout(Container self)
   {
    grid.getView().refresh();
   }
  });


  
  addRowSelectionListener(this);
  
 }
 
 public void doLayout()
 {
  grid.doLayout();
  super.doLayout();
 }
 
 protected String[][] getSearchFields()
 {
  List<Integer> colNums = new ArrayList<Integer>(5);
  
  int n = columnModel.getColumnConfigs().length;
  
  for(int i=0; i < n; i++)
  {
   String dx = columnModel.getDataIndex(i);
   
   for( FieldDef fd : recordDef.getFields() )
   {
    if( fd.getName().equals(dx) )
    {
     if( fd instanceof StringFieldDef )
      colNums.add(i);
     
     break;
    }
   }

  }

  n = colNums.size();
  
  String[][] fields = new String[ n ][];
  
  for(int i=0; i < n; i++)
  {
   fields[i] = new String[2];
   fields[i][0]=columnModel.getColumnHeader(colNums.get(i));
   fields[i][1]=columnModel.getDataIndex(colNums.get(i));
  }

  return fields;
 }
 
 protected Toolbar getToolbar()
 {
  return new SearchToolbar<T>(this,getSearchFields());
 }
 


 public void addFilter(String fid, TraversalCallback<T> cb)
 {
  filter = cb;
  store.filterBy( new StoreTraversalCallback() {

   @SuppressWarnings("unchecked")
   public boolean execute(Record record)
   {
    return filter.execute((T)record.getAsObject("obj"));
   }} );
 }


 public void removeFilter(String fid)
 {
  filter=null;
  store.clearFilter();
 }
/*
 protected Toolbar getToolbar1()
 {
  Toolbar topToolbar = new Toolbar();
  
  topToolbar.addFill();

  Collection<CheckItem> items = new ArrayList<CheckItem>(10);
  
  Menu colMenu = new Menu();
  for( int i=0; i < columnModel.getColumnCount(); i++ )
  {
   CheckItem ci  =  new CheckItem();
   ci.setChecked(true);
   ci.setText(columnModel.getColumnHeader(i));
   ci.setStateId(columnModel.getDataIndex(i));
   colMenu.addItem( ci );
   items.add(ci);
  }
  
  ToolbarButton menuButton = new ToolbarButton("Columns");
  menuButton.setMenu(colMenu);
  
  topToolbar.addButton( menuButton );
  
  SearchField<T> sf = new SearchField<T>(store, items);
  
  topToolbar.addField( sf );
  
  return topToolbar;
 }
 */
 
 public void setActionEnabled(String actState, boolean b)
 {
  Action a = ActionHelper.findAction(actState, actions);
  
  if( a != null && a.getComponent() != null )
   a.getComponent().setDisabled(!b);
 }
 
 public Action getAction(String actState)
 {
  return ActionHelper.findAction(actState, actions);
 }
 
 protected Store getStore()
 {
  return store;
 }
 
 protected void setData( Record[] data )
 {
  store.removeAll();
  store.add( data );
  
  if( filter != null )
   store.filterBy( new StoreTraversalCallback() {

    @SuppressWarnings("unchecked")
    public boolean execute(Record record)
    {
     return filter.execute((T)record.getAsObject("obj"));
    }});
 }


 protected GridPanel getGrid()
 {
  return grid;
 }
 
 
 public void setObjectActionListener(ObjectAction<T> lsnr)
 {
  actListener.setListener(lsnr);
 }

 public ObjectAction<T> getObjectActionListener()
 {
  return actListener.getListener();
 }

 
 public void addRowSelectionListener(RowSelectionListener lsnr)
 {
  grid.getSelectionModel().addListener(lsnr);
 }

 @SuppressWarnings("unchecked")
 public List<T> getSelection()
 {
  Record[] sel = grid.getSelectionModel().getSelections();
  
  if( sel == null || sel.length == 0 )
   return null;
  
  List<T> res = new ArrayList<T>( sel.length );
  
  for( Record r : sel )
   res.add( (T)r.getAsObject("obj") );
  
  return res;
 }
 
 @SuppressWarnings("unchecked")
 public Collection<T> getVisible()
 {
  Record[] sel = grid.getStore().getRecords();
  
  if( sel == null || sel.length == 0 )
   return null;
  
  List<T> res = new ArrayList<T>( sel.length );
  
  for( Record r : sel )
   res.add( (T)r.getAsObject("obj") );
  
  return res;
 }

 
 @SuppressWarnings("unchecked")
 public void addToSelection(T p)
 {
  int i=0;
  for( Record r : getStore().getRecords() )
  {
   if( ((T)r.getAsObject("obj")).getId() == p.getId() )
   {
    grid.getSelectionModel().selectRow(i,true);
    break;
   }
   i++;
  }
 }

 @SuppressWarnings("unchecked")
 public void addToSelection(List<T> lp)
 {
  int[] ids = new int[lp.size()];
  
  int i=0;
  for( T p : lp )
   ids[i++]=p.getId();
  
  Arrays.sort(ids);
  
  i=0;
  for( Record r : getStore().getRecords() )
  {
   if( Arrays.binarySearch(ids,((T)r.getAsObject("obj")).getId()) >= 0 )
   {
    grid.getSelectionModel().selectRow(i,true);
   }
   i++;
  }
 }
 
 public void setSelectionListener(ObjectSelectionListener<T> lsnr)
 {
  selListener=lsnr;
 }

 
 public boolean doBeforeRowSelect(RowSelectionModel sm, int rowIndex, boolean keepExisting, Record record)
 {
  return true;
 }

 public void onRowDeselect(RowSelectionModel sm, int rowIndex, Record record)
 {
 }

 public void onRowSelect(RowSelectionModel sm, int rowIndex, Record record)
 {
 }

 @SuppressWarnings("unchecked")
 public void onSelectionChange(RowSelectionModel sm)
 {
  if( selListener == null )
   return;
  
  List<T> sel=null;
  
  if( sm.getCount() > 0 )
  {
   sel=new ArrayList<T>(sm.getCount());
   
   for( Record r : sm.getSelections() )
    sel.add( (T)r.getAsObject("obj") );
  }
  
  selListener.selectionChanged(sel);
 }

}