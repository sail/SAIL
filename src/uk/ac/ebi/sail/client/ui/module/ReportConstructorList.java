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

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import uk.ac.ebi.sail.client.ConfigManager;
import uk.ac.ebi.sail.client.DataChangeListener;
import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.common.ClassifiableManager;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.ClientParameterAuxInfo;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.client.common.Timer;
import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.client.common.Variable.Type;
import uk.ac.ebi.sail.client.data.TraversalCallback;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.StudyCollectionStateListener;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.TextAlign;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.GridCellListener;

public class ReportConstructorList extends ClassifiableList<Parameter> implements DataChangeListener, StudyCollectionStateListener
{
 private static FieldDef[] fieldDef = new FieldDef[] { 
  new IntegerFieldDef("id"),
  new StringFieldDef("code"),
  new StringFieldDef("name"),
  new StringFieldDef("description"),
  new IntegerFieldDef("recs"),
  new IntegerFieldDef("v"),
  new IntegerFieldDef("e"),
  new IntegerFieldDef("f"),
  new ObjectFieldDef("obj")
  };
 
 private static RecordDef recordDef = new RecordDef( fieldDef );

 private static ColumnModel columnModel;
 
 static
 {
  ColumnConfig[] columns = new ColumnConfig[7];

  int k = 0;
  ColumnConfig cc = new ColumnConfig("Code", "code", 100);
  cc.setId("code");
  cc.setAlign(TextAlign.LEFT);
  cc.setSortable(true);

  columns[k++] = cc;

  cc = new ColumnConfig("Name", "name");
  cc.setId("parameter");
  cc.setWidth(150);
  cc.setAlign(TextAlign.LEFT);
  cc.setSortable(true);

  columns[k++] = cc;

  cc = new ColumnConfig("Description", "description");
  cc.setId("description");
  cc.setAlign(TextAlign.LEFT);
  cc.setSortable(false);

  columns[k++] = cc;

  cc = new ColumnConfig("Filter", "f", 50);
  cc.setSortable(false);
  cc.setId("filter");

  cc.setRenderer(new Renderer()
  {
   public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store)
   {
    boolean has = ((Integer) value).intValue() > 0;
    
    if( ! has )
     return "";
    
    return "<img class=\"filterIcon\" src=\""+ConfigManager.SAIL_RESOURCE_PATH+"images/icons/funnel_plus.gif\" />";
   }

  }); 
  
  columns[k++] = cc;

  cc = new ColumnConfig("Records", "recs", 50);
  cc.setAlign(TextAlign.RIGHT);
  cc.setSortable(false);

  columns[k++] = cc;

  cc = new ColumnConfig("V", "v", 30);
  cc.setAlign(TextAlign.RIGHT);
  cc.setSortable(false);

  columns[k++] = cc;

  cc = new ColumnConfig("E", "e", 30);
  cc.setAlign(TextAlign.RIGHT);
  cc.setSortable(false);

  columns[k++] = cc;

  columnModel = new ColumnModel(columns);
 }

 private ClassifiableManager<Parameter> manager;
// private EnumerationSelectListener enumLsnr;
 private boolean addGrpState;


 public ReportConstructorList(Selection single, Action[] buttons, ClassifiableManager<Parameter> m )
 {
  super(single, buttons, recordDef, columnModel,"desc",
    DataManager.getInstance().getClassifierManager( Classifier.Target.PARAMETER));
  
  setTitle("Parameter list");
  getGrid().setAutoExpandColumn("description");
  manager=m;
  
  getGrid().setEnableDragDrop(true);
  getGrid().setDdGroup("sailDDGroup");
  
  manager.addClassifiableChangeListener( this );
  
  loadData();
  
  getGrid().addGridCellListener( new AddFilteredListener() );
  
 }

// public void setEnumerationSelectListener( EnumerationSelectListener ls )
// {
//  enumLsnr = ls;
// }
 
 public void setActionEnabled(String btState, boolean enabled)
 {
  super.setActionEnabled( btState, enabled );
  
  if( btState.equals("addGrp") )
   addGrpState=enabled;
 }
 
 
 public void loadData()
 {
  getStore().removeAll();

  Timer.reportEvent("Adding parameters into grid. start. ");

  Record[] recs = new Record[ manager.getClassifiable().size() ];
  
  int i=0;
  for(Parameter p : manager.getClassifiable())
  {
   int fN = p.countEnumerations();
   
   Collection<Variable> vl = p.getAllVariables();
   
   if( vl != null )
   {
    for( Variable v : vl )
     if( v.getType()== Type.INTEGER || v.getType()== Type.REAL || v.getType()== Type.BOOLEAN || v.getType()== Type.DATE )
      fN++;
   }
   
   Object[] data = new Object[9];

   data[0] = p.getId();
   data[1] = p.getCode();
   data[2] = p.getName();
   data[3] = p.getDescription();
   data[4] = p.getRecordsCount();
   data[5] = new Integer(p.countVariables());
   data[6] = new Integer(p.countEnumerations());
   data[7] = fN;
   data[8] = p;

   recs[i++]=recordDef.createRecord(data);
  }
  
  Arrays.sort(recs, new Comparator<Record>()
  {
   @Override
   public int compare(Record o1, Record o2)
   {
    return o1.getAsString("name").compareTo(o2.getAsString("name"));
   }
  });
  
  setData(recs);
  
  //getStore().sort("name");
  Timer.reportEvent("Adding parameters into grid. stop. ");
//  Timer.reportEvent("Filling store. start. ");
//
//  Object[][] array = new Object[manager.getClassifiable().size()][];
//  
//  int i=0;
//  for(Parameter p : manager.getClassifiable())
//  {
//   Object[] data = new Object[8];
//
//   data[0] = p.getId();
//   data[1] = p.getCode();
//   data[2] = p.getName();
//   data[3] = p.getDescription();
//   data[4] = p.getRecordsCount();
//   data[5] = new Integer(p.countVariables());
//   data[6] = new Integer(p.countEnumerations());
//   data[7] = p;
//
//   array[i++]=data;
//  }
//  Timer.reportEvent("Filling store. cp1. ");
//
//  SimpleStore store = new SimpleStore(0,fieldDef,array);
//  Timer.reportEvent("Filling store. cp2. ");
// 
//  setStore(store);
//  Timer.reportEvent("Filling store. stop. ");
//  
//  getGrid().getView().refresh(true);
 }

/*
 void prepareFilterContext( Summary pt )
 {
  filterContext = new Summary[pt.getRelatedCounters().length];
  
  int i=0;
  for( Summary pc : pt.getRelatedCounters() )
   filterContext[i++]=pc;
  
  Arrays.sort(filterContext);
  
  cachedSummary=null;
 }
*/

 
 @Override
 protected void updateRecord(Record record, Parameter obj)
 {
  Summary sm = ((ClientParameterAuxInfo)obj.getAuxInfo()).getCountContext();
  
  
  if( sm == null )
   record.set("recs", obj.getRecordsCount());
  else
   record.set("recs", sm.getCount());
 }

 
 public void dataChanged()
 {
  loadData();
 }
 
 public void destroy()
 {
  manager.removeClassifiableChangeListener( this );

  super.destroy();
 }

 class AddFilteredListener implements GridCellListener
 {
  
  public void onCellClick(GridPanel grid, int rowIndex, int colIndex, EventObject e)
  {
//   Element iconEl = e.getTarget(".filterIcon", 1);
   
   if( grid.getColumnModel().getColumnId(colIndex).equals("filter") ) //&& iconEl != null)
   {
    Record record = grid.getStore().getAt(rowIndex);
    
    if( record.getAsInteger("f") <= 0 )
     return;
    
    Parameter p = (Parameter)record.getAsObject("obj");
    
    getObjectActionListener().doAction("filter", p);
    

    
//    final Window w = new Window();
//    w.setClosable(false);
//    
//    
//    final TreePanel tp = new TreePanel();
//    tp.setRootVisible(true);
//    
//    final TreeNode rn = new TreeNode(p.getCode()+" ("+p.getName()+")");
//    rn.setIconCls("parameterNode");
//    tp.setRootNode(rn);
//    
//    if(p.getAllVariables() != null)
//    {
//     for(Variable v : p.getAllVariables())
//     {
//      if(v.isEnum())
//      {
//       TreeNode tn = new TreeNode(v.getName());
//       tn.setIconCls("variableNode");
//       tn.setUserObject(v);
//       rn.appendChild(tn);
//
//       for(Variant vr : v.getVariants())
//       {
//        TreeNode vn = new TreeNode(vr.getName() + " (" + vr.getCount() + ")");
//        vn.setChecked(false);
//        vn.setUserObject(vr);
//        tn.appendChild(vn);
//       }
//      }
//      else if( v.getType() == Type.INTEGER || v.getType() == Type.REAL )
//      {
//        TreeNode tn = new TreeNode(v.getName());
//        tn.setIconCls("variableNode");
//        tn.setUserObject(v);
//        rn.appendChild(tn);
//
//        TreeNode vn = new TreeNode("<..>");
//        vn.setChecked(false);
//        tn.setUserObject(new ComplexFilter.Range(v.getId(),Float.NaN,Float.NaN));
//        tn.appendChild(vn);
//      }
//     }
//    }
//    if(p.getAllQualifiers() != null)
//    {
//     for(Qualifier v : p.getAllQualifiers())
//     {
//      if(v.isEnum())
//      {
//       TreeNode tn = new TreeNode(v.getName());
//       tn.setIconCls("qualifierNode");
//       tn.setUserObject(v);
//       rn.appendChild(tn);
//
//       for(Variant vr : v.getVariants())
//       {
//        TreeNode vn = new TreeNode(vr.getName() + " (" + vr.getCount() + ")");
//        vn.setChecked(false);
//        vn.setUserObject(vr);
//        tn.appendChild(vn);
//       }
//      }
//     }
//    }
//    
//    tp.expandAll();
//    w.setLayout( new FitLayout() );
//    w.add(tp);
//    w.setModal(true);
//    
//    w.setSize(300, 250);
//    
//
//    ButtonListener btLsnr = new ButtonListenerAdapter()
//    {
//     public void onClick(Button button, EventObject e)
//     {
//      if( enumLsnr == null )
//       return;
//      
//      List<FilteredEnumeration> feLst = new ArrayList<FilteredEnumeration>(3);
//      
//      TreeNode[] chknds = tp.getChecked();
//      
//      FilteredEnumeration lastFE=null;
//      for( TreeNode tnd : chknds )
//      {
//       ParameterPart pp = (ParameterPart)tnd.getParentNode().getUserObject();
//       
//       if( lastFE == null || lastFE.getEnumeration() != pp )
//       {
//        lastFE=null;
//        for( FilteredEnumeration fe : feLst )
//        {
//         if( fe.getEnumeration() == pp )
//         {
//          lastFE=fe;
//          break;
//         }
//        }
//        
//        if(lastFE == null)
//        {
//         lastFE = new FilteredEnumeration();
//         lastFE.setParameter(p);
//         lastFE.setEnumeration(pp);
//         feLst.add(lastFE);
//        }
//       }
//
//       lastFE.addVariant(((Variant)tnd.getUserObject()).getId());
//      }
//      
//      w.close();
//      
//      enumLsnr.filteredParameterSelected(feLst, "addGrp".equals(button.getStateId()) );
//
//     }
//    };
//    
//    Button bt = new Button("Add");
//    bt.addListener(btLsnr);
//    bt.setStateId("add");
//    w.addButton( bt );
//    
//    bt = new Button("Add to Group");
//    bt.addListener(btLsnr);
//    bt.setStateId("addGrp");
//    bt.setDisabled(!addGrpState);
//    w.addButton( bt );
//    
//    bt = new Button("Cancel");
//    bt.addListener(new ButtonListenerAdapter()
//    {
//     public void onClick(Button button, EventObject e)
//     {
//      w.close();
//     }
//    });
//    w.addButton( bt );
//
//    w.show();
//    
   }
  }

  public void onCellDblClick(GridPanel grid, int rowIndex, int colIndex, EventObject e)
  {
   fireAction("add");
  }

  public void onCellContextMenu(GridPanel grid, int rowIndex, int colIndex, EventObject e)
  {
   System.out.println("Menu");
   e.stopEvent();

  }
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
  
  AsyncCallback<Summary> asCB = new AsyncCallback<Summary>()
  {
   @Override
   public void onSuccess(final Summary arg0)
   {
//    ParameterSelector ps = ParameterSelector.getInstance();
    
    if( khID != 0 )
     prepareFilterContext(arg0);
    else
     prepareFilterContext(arg0.getTagCounters()[0]);
    
    addFilter("_CollectionFilter", new TraversalCallback<Parameter>()
    {
     @Override
     public boolean execute(Parameter obj)
     {
      tmpPC.setId(obj.getId());
      
      int ind = Arrays.binarySearch(filterContext, tmpPC);
      if( ind >= 0 )
      {
       cachedSummary=filterContext[ind];
       return true;
      }
      
      return false;
     }
    });
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

 /*  
 static class ParameterSelector implements TraversalCallback<Parameter>
 {
  private static ParameterSelector instance;
  
  private Summary[] pattern;
  private Parameter tmpPrm = new Parameter();
  private Summary tmpPC = new Summary();
  
  public static ParameterSelector getInstance()
  {
   if( instance == null )
    instance = new ParameterSelector();
   
   return instance;
  }
  
  void setPattern( Summary pt )
  {
   pattern = new Summary[pt.getRelatedCounters().length];
   
   int i=0;
   for( Summary pc : pt.getRelatedCounters() )
    pattern[i++]=pc;
   
   Arrays.sort(pattern);
  }

  @Override
  public boolean execute(Parameter obj)
  {
   tmpPC.setId(obj.getId());
   
   int ind = Arrays.binarySearch(pattern, tmpPC);
   return ind >= 0;
  }

  @Override
  public boolean execute(Parameter obj)
  {
   tmpPC.setId(obj.getId());
   
   int ind = Arrays.binarySearch(pattern, tmpPC);
   if( ind >= 0 )
   {
    tmpPrm.setRecordsCount(pattern[ind].getCount());
    tmpPrm.setCode(obj.getCode());
    tmpPrm.setName(obj.getName());
    tmpPrm.setDescription(obj.getDescription());
    tmpPrm.setAnnotations(obj.getAnnotations());
    tmpPrm.setClassificationTags(obj.getClassificationTags());

    obj.setInheritedParameters( obj.getInheritedParameters() );
    
    return tmpPrm;
   }
   
   return null;
  }
 
 }
*/ 
}
