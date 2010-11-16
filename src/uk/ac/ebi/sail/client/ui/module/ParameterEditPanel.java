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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.sail.client.ConfigManager;
import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.ClientParameterAuxInfo;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.Qualifier;
import uk.ac.ebi.sail.client.common.Relation;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.client.common.Classifier.Target;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.NameValuePair;
import com.gwtext.client.data.BooleanFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.MessageBox.PromptCallback;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.Validator;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridCellListenerAdapter;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;

public class ParameterEditPanel extends FormPanel
{
 private TextField               codeField;
 private TextField               nameField;
 private TextArea                descriptionArea;

 private Parameter               param;

 private AnnotatedEditor         annEdit;
 private GridPanel               vGrid;
 private GridPanel               qGrid;
 private GridPanel               ipGrid;
// private GridPanel               tagGrid;
 private ClassificationSetEditor clsEditor;
 private GridPanel               rGrid;

 private Store                   vStore;
 private Store                   qStore;
 private Store                   ipStore;
// private Store                   tagStore;
 private Store                   rStore;

 private Button                  addVarButton;
 private Button                  editVarButton;
 private Button                  addQButton;
 private Button                  editQButton;
// private Button                  addTButton;

 private BtnListener btnListener;
 
// private static GridHelper       helper             = new GridHelper();

 private static RecordDef        vRecordDef         = new RecordDef(new FieldDef[] { 
   new StringFieldDef("pcode"),
   new StringFieldDef("varname"), 
   new StringFieldDef("vartype"), 
   new StringFieldDef("variants"),
   new ObjectFieldDef("obj"),
   new IntegerFieldDef("level")
   });

 private static InheritHighlight nameRenderer       = new InheritHighlight();

 private static ColumnConfig     varNameColumn      = new ColumnConfig("Name", "varname", 130, true, null, "varname");

 static
 {
  varNameColumn.setRenderer(nameRenderer);
 }

 private static ColumnModel      vColumnModel       = new ColumnModel(new ColumnConfig[] {
   new ColumnConfig("Parameter", "pcode", 100, true, null, "pcode"), varNameColumn,
   new ColumnConfig("Type", "vartype", 80, true, null, "vartype"),
   new ColumnConfig("Variants", "variants", 130, true, null, "variants") });

 private static ColumnConfig     qNameColumn        = new ColumnConfig("Name", "qname", 100, true, null, "qname");

 static
 {
  qNameColumn.setRenderer(nameRenderer);
 }

 private static RecordDef        qRecordDef         = new RecordDef(new FieldDef[] { 
   new StringFieldDef("pcode"),
   new StringFieldDef("qname"),
   new BooleanFieldDef("predef"),
   new BooleanFieldDef("mandat"),
   new StringFieldDef("variants"),
   new ObjectFieldDef("obj"),
   new IntegerFieldDef("level")
   });

 private static ColumnModel      qColumnModel       = new ColumnModel(new ColumnConfig[] {
   new ColumnConfig("Parameter", "pcode", 120, true, null, "pcode"),
   qNameColumn,
   new ColumnConfig("Predefined","predef",70,true,null,"predef"),
   new ColumnConfig("Mandatory","mandat",60,true,null,"mandat"),
   new ColumnConfig("Variants", "variants", 140, true, null, "variants") });

 private static RecordDef        ipRecordDef        = new RecordDef(new FieldDef[] { 
   new StringFieldDef("code"),
   new StringFieldDef("name"), 
   new StringFieldDef("by"), 
   new IntegerFieldDef("Nv"), 
   new IntegerFieldDef("Nq"),
   new ObjectFieldDef("obj"),
   new IntegerFieldDef("level")
   });

 private static ColumnConfig ipName = new ColumnConfig("Name", "name", 130, true, null, "name");
 
 static
 {
  ipName.setRenderer(nameRenderer);
 }
 
 private static ColumnModel      ipColumnModel      = new ColumnModel(new ColumnConfig[] {
   new ColumnConfig("Code", "code", 130, true, null, "code"),
   ipName,
   new ColumnConfig("Inherited by", "by", 130, true, null, "by"),
   new ColumnConfig("V", "Nv", 30, true, null, "Nv"),
   new ColumnConfig("Q", "Nq", 30, true, null, "Nq") });

// private static RecordDef        classifRecordDef   = new RecordDef(new FieldDef[] { 
//   new StringFieldDef("classname"),
//   new StringFieldDef("tagname"), 
//   new StringFieldDef("info"), 
//   new ObjectFieldDef("obj") });
//
// private static ColumnConfig     infoCol            = new ColumnConfig("Info", "info", 130, true, null, "info");
// private static ColumnModel      classifColumnModel = new ColumnModel(new ColumnConfig[] {
//   new ColumnConfig("Classifier", "classname", 130, true, null, "classname"),
//   new ColumnConfig("Tag", "tagname", 130, true, null, "tagname"), infoCol });
//
// static
// {
//  infoCol.setRenderer(helper);
// }

 private static RecordDef        rRecordDef         = new RecordDef(new FieldDef[] { 
   new StringFieldDef("pcode"),
   new StringFieldDef("classname"), 
   new StringFieldDef("tagname"), 
   new ObjectFieldDef("obj"),
   });

 private static ColumnModel      rColumnModel       = new ColumnModel(new ColumnConfig[] {
   new ColumnConfig("Parameter", "pcode", 130, true, null, "pcode"),
   new ColumnConfig("Classifier", "classname", 130, true, null, "classname"),
   new ColumnConfig("Tag", "tagname", 130, true, null, "tagname") });

 public ParameterEditPanel()
 {
  this( true );
 }
 
 public ParameterEditPanel( boolean editMode )
 {
  setFrame(true);
  setPaddings(5, 5, 5, 0);
  setWidth(600);

  add(codeField = new TextField("Code", "code", 300), new AnchorLayoutData("90%"));
  add(nameField = new TextField("Name", "name", 300), new AnchorLayoutData("90%"));

  codeField.setValidator( new Validator(){

   public boolean validate(String value)
   {
    if( value.length() == 0 || value.indexOf('.') >= 0 )
     return false;
    
    return true;
   }});
  
  codeField.setAllowBlank(false);
  nameField.setAllowBlank(false);
  
  nameField.setInvalidText("Name can't be empty");
  codeField.setInvalidText("Code can't be empty");
  codeField.setValidationEvent("keyup");
  
  descriptionArea = new TextArea("Description");
  descriptionArea.setWidth(300);
  add(descriptionArea, new AnchorLayoutData("90%"));

  Panel gridsPanel = new Panel();
  gridsPanel.setLayout(new VerticalLayout(10));
//  gridsPanel.setLayout(new AnchorLayout());
  gridsPanel.setWidth(getWidth() - 20);
  add(gridsPanel);
  
  annEdit = new AnnotatedEditor( Target.PARAMETER_ANN );
  annEdit.setWidth(gridsPanel.getWidth());
  gridsPanel.add( annEdit, new AnchorLayoutData("100%") );
  
  vGrid = new GridPanel();
  vGrid.setTitle("Variables");
  vGrid.setLayout(new FitLayout());
  vGrid.setSelectionModel(new RowSelectionModel(true));

  vGrid.setColumnModel(vColumnModel);

  vGrid.setFrame(true);
  vGrid.setCollapsible(true);
  vGrid.setStripeRows(true);
  vGrid.setAutoExpandColumn("variants");
  vGrid.setAutoHeight(true);

  VariableEditor vedit = new VariableEditor();

  addVarButton = new Button("Add Variable", vedit);
  editVarButton = new Button("Edit Variable", vedit);

  vGrid.addButton(addVarButton);
  vGrid.addButton(editVarButton);
  vGrid.addButton(new Button("Remove Variable", vedit));

  vStore = new Store(vRecordDef);
  vGrid.setStore(vStore);

  vGrid.setWidth(gridsPanel.getWidth());
  gridsPanel.add(vGrid);

  qGrid = new GridPanel();
  qGrid.setTitle("Qualifiers");
  qGrid.setLayout(new FitLayout());
  qGrid.setSelectionModel(new RowSelectionModel(true));

  qGrid.setColumnModel(qColumnModel);

  qGrid.setFrame(true);
  qGrid.setCollapsible(true);
  qGrid.setStripeRows(true);
  qGrid.setAutoExpandColumn("qname");
  qGrid.setAutoHeight(true);

  QualifierEditor qedit = new QualifierEditor();

  qGrid.addButton(addQButton = new Button("Add Qualifier", qedit));
  qGrid.addButton(editQButton = new Button("Edit Qualifier", qedit));
  qGrid.addButton(new Button("Remove Qualifier", qedit));

  qStore = new Store(qRecordDef);
  qGrid.setStore(qStore);

  qGrid.setWidth(gridsPanel.getWidth());
  gridsPanel.add(qGrid);

  ipGrid = new GridPanel();
  ipGrid.setTitle("Inherited parameters");
  ipGrid.setLayout(new FitLayout());
  ipGrid.setSelectionModel(new RowSelectionModel(true));

  ipGrid.setColumnModel(ipColumnModel);

  ipGrid.setFrame(true);
  ipGrid.setCollapsible(true);
  ipGrid.setStripeRows(true);
  ipGrid.setAutoExpandColumn("name");
  ipGrid.setAutoHeight(true);

  InheritSetEditor lsnr = new InheritSetEditor();
  Button bt = new Button("Add Parameter",lsnr);
  bt.setStateId("add");
  ipGrid.addButton(bt);

  bt = new Button("Remove Parameter",lsnr);
  bt.setStateId("remove");
  ipGrid.addButton(bt);

  ipStore = new Store(ipRecordDef);
  ipGrid.setStore(ipStore);

  ipGrid.setWidth(gridsPanel.getWidth());
  gridsPanel.add(ipGrid);

  clsEditor = new ClassificationSetEditor( Target.PARAMETER );
  clsEditor.setWidth(gridsPanel.getWidth());
  gridsPanel.add(clsEditor);

  rGrid = new GridPanel();
  rGrid.setTitle("Relations");
  rGrid.setLayout(new FitLayout());
  rGrid.setSelectionModel(new RowSelectionModel(true));

  rGrid.setColumnModel(rColumnModel);

  rGrid.setFrame(true);
  rGrid.setCollapsible(true);
  rGrid.setStripeRows(true);
  rGrid.setAutoExpandColumn("pcode");
  rGrid.setAutoHeight(true);

  RelationSetEditor rse = new RelationSetEditor();
  
  bt = new Button("Add relation",rse);
  bt.setStateId("add");
  rGrid.addButton(bt);
  
  bt = new Button("Remove relation",rse);
  bt.setStateId("remove");
  rGrid.addButton(bt);


  rStore = new Store(rRecordDef);
  rGrid.setStore(rStore);

  rGrid.setWidth(gridsPanel.getWidth());
  gridsPanel.add(rGrid);

  btnListener = new BtnListener();
  
  if( editMode )
  {
   bt = new Button("Save", btnListener );
   bt.setStateId("ok");
   addButton(bt);

   bt = new Button("Delete", btnListener );
   bt.setStateId("delete");
   addButton(bt);
  }
  
  bt = new Button("Cancel", btnListener );
  bt.setStateId("cancel");
  addButton(bt);
 }

 public void setParameter(Parameter prm)
 {
  param = prm;

  String str = prm.getCode();
  codeField.setValue(str);

  str = prm.getName();
  nameField.setValue(str);

  str = prm.getDescription();
  descriptionArea.setValue(str);

  annEdit.setAnnotatedObject(prm);

  if( prm.getAnnotations() == null || prm.getAnnotations().size() == 0 )
   annEdit.setCollapsed(true);
  
  vStore.removeAll();
  qStore.removeAll();
  ipStore.removeAll();

//  addInheritedFull(param, param, null);
  
  Collection<Parameter> inh = param.getInheritedParameters();
  
  if( inh != null )
  {
   Map<Parameter,Object> mp = new HashMap<Parameter, Object>();

   if(addVariablesR(param, 0, mp) == 0)
    vGrid.setCollapsed(true);
   
   mp.clear();
   if(addQualifiersR(param, 0, mp) == 0)
    qGrid.setCollapsed(true);
  
   if(addInherited(param, null, -1) == 0)
    ipGrid.setCollapsed(true);

  }
  else
  {
   if(addVariables(param, 0) == 0)
    vGrid.setCollapsed(true);


   if(addQualifiers(param, 0) == 0)
    qGrid.setCollapsed(true);
   
   ipGrid.setCollapsed(true);
  }


//  vGrid.setCollapsed(vStore.getCount() == 0 );
//  qGrid.setCollapsed(qStore.getCount() == 0 );
//  ipGrid.setCollapsed(ipStore.getCount() == 0 );

  clsEditor.setEditObject(param);
  clsEditor.setCollapsed(param.getClassificationTags() == null);

//  if(param.getClassificationTags() != null)
//  {
//   for(Tag t : param.getClassificationTags())
//   {
//    Record rd = classifRecordDef.createRecord(new Object[] { t.getClassifier().getName(), t.getName(), t.getDescription(), t });
//    tagStore.add(rd);
//   }
//   clsEditor.setCollapsed(false);
//  }
//  else
//   clsEditor.setCollapsed(true);

  rStore.removeAll();
  if(param.getRelations() != null)
  {
   for(Relation r : param.getRelations())
   {
    Record rd = rRecordDef.createRecord(new Object[] { r.getTargetParameter().getCode(),
      r.getTag().getClassifier().getName(), r.getTag().getName(), r });
    rStore.add(rd);
   }

   rGrid.setCollapsed(false);
  }
  else
   rGrid.setCollapsed(true);
 }

 private int addVariables(Parameter p, int level )
 {
  int count = 0;
  if(p.getVariables() != null)
  {
   for(Variable v : p.getVariables())
   {
    Record rd = vRecordDef.createRecord(new Object[] { level==0 ? "this" : p.getCode(), v.getName(),
      v.getType().name(), v.getVariantsString(), v, level });
    vStore.add(rd);
    count++;
   }
  }
  
  return count;
 }

 private int addVariablesR(Parameter p, int level, Map<Parameter,Object> mp )
 {
  int count = addVariables(p, level);

  if(p.getInheritedParameters() == null)
   return count;

  for(Parameter ip : p.getInheritedParameters())
  {
   if( mp.containsKey(ip) )
    continue;
   
   mp.put(ip, null);
   count += addVariablesR(ip, level+1, mp);
  }

  return count;
 }

 
 private int addQualifiers(Parameter p, int level )
 {
  int count = 0;
  if(p.getQualifiers() != null)
  {
   for(Qualifier v : p.getQualifiers())
   {
    Record rd = qRecordDef.createRecord(new Object[] { level==0 ? "this" : p.getCode(), v.getName(),
      v.isPredefined(), v.isMandatory(),
      v.getVariantsString(), v, level });
    qStore.add(rd);
    count++;
   }
  }
  
  return count;
 }

 private int addQualifiersR(Parameter p, int level, Map<Parameter,Object> mp )
 {
  int count = addQualifiers(p, level);

  if(p.getInheritedParameters() == null)
   return count;

  for(Parameter ip : p.getInheritedParameters())
  {
   if( mp.containsKey(ip) )
    continue;
   
   mp.put(ip, null);
   count += addQualifiersR(ip, level+1, mp);
  }

  return count;
 }

 

 private int addInherited(Parameter p, Parameter pp, int level)
 {
  int n=0;
  
  if( level >= 0 )
  {
   Record rd = ipRecordDef.createRecord(new Object[] { p.getCode(), p.getName(), level==0 ? "this" : pp.getCode(),
     p.countVariables(), p.countQualifiers(), p, level });
   ipStore.add(rd);
   n++;
  }
  
  if(p.getInheritedParameters() == null)
   return n;


  for(Parameter ip : p.getInheritedParameters())
  {
   n += addInherited(ip, p, level+1);
  }

  return n;
 }

 private Parameter checkHierarchy( Parameter cur, Parameter toChk )
 {
  if( cur.getInheritedParameters() == null )
   return null;
  
  for( Parameter inh : cur.getInheritedParameters() )
  {
   if( toChk == inh )
    return cur;
   
   Parameter res = checkHierarchy(inh, toChk);
   if( res != null )
    return res;
  }
  
  return null;
 }
 
 private Parameter addInherited(Parameter ip)
 {
  Parameter res = checkHierarchy(param, ip);
  
  if( res != null )
  {
   ErrorBox.showError("Parameter '"+ip.getCode()+"' is already in hierarchy");
   return res;
  }
  
  res = checkHierarchy(ip, param);
 
  if( res != null )
  {
   ErrorBox.showError("Parameter '"+ip.getCode()+"' has parameter '"+param.getCode()+"' in its hierarchy");
   return res;
  }

  param.addInheritedParameter(ip);
  
  addInherited(ip, param, 0);
 
  Map<Parameter,Object> mp = new HashMap<Parameter, Object>();

  vStore.removeAll();
  qStore.removeAll();
  
  addVariablesR(param, 0, mp);
  
  mp.clear();
  addQualifiersR(param, 0, mp);
  
  return null;
 }

 /*
 private void addInheritedFull(Parameter ip, Parameter master, Parameter root)
 {
  boolean has =false;
  
  for( Record r : ipStore.getRecords() )
  {
   Parameter cp = (Parameter)r.getAsObject("obj");
   
   if( ip == cp )
   {
    has=true;
    break;
   }
  }
  
  if( ! has )
  {
   if(ip.getVariables() != null)
   {
    for(Variable v : ip.getVariables())
    {
     Record rd = vRecordDef.createRecord(new Object[] { ip==master?"this":ip.getCode(), v.getName(),
       v.getType().name(), v.getVariantsString(), v });
     vStore.add(rd);
    }
   }

   if(ip.getQualifiers() != null)
   {
    for(Qualifier v : ip.getQualifiers())
    {
     Record rd = qRecordDef.createRecord(new Object[] { ip==master?"this":ip.getCode(), v.getName(),
       v.getVariantsString(), v });

     qStore.add(rd);
    }
   }

  }
  
  if( ip != master )
   ipStore.add( ipRecordDef.createRecord(new Object[] { ip.getCode(), ip.getName(), master==null?"this":master.getCode(),
    ip.countVariables(), ip.countQualifiers(), ip, root==ip?null:root }));

  if( ip.getInheritedParameters() == null )
   return;
  
  for( Parameter inh : ip.getInheritedParameters() )
   addInheritedFull(inh, ip==master?null:ip, root==null?inh:root);
  
 }
 */
 
 private void removeInherited( Parameter rp )
 {
  param.removeInherited(rp);
  
  
//  boolean done = false;
//  while( ! done )
//  {
//   done = true;
//   for(Record r : ipStore.getRecords() )
//   {
//    if( r.getAsObject("obj") == rp || r.getAsObject("root") == rp )
//    {
//     ipStore.remove(r);
//     done=false;
//     break;
//    }
//   }
//  }

  ipStore.removeAll();
  addInherited(param, null, -1);
  
  vStore.removeAll();

  if( param.getInheritedParameters() == null || param.getInheritedParameters().size()==0 )
   addVariables(param, 0);
  else
  {
   Map<Parameter, Object> mp = new HashMap<Parameter, Object>();
   mp.put(param, null);
   addVariablesR(param, 0, mp);
  }
  

  qStore.removeAll();

  if( param.getInheritedParameters() == null || param.getInheritedParameters().size()==0 )
   addQualifiers(param, 0);
  else
  {
   Map<Parameter, Object> mp = new HashMap<Parameter, Object>();
   mp.put(param, null);
   addQualifiersR(param, 0, mp);
  }

 }
 
 private boolean validate()
 {
  if( ! codeField.isValid() )
  {
   ErrorBox.showError("Code field has invalid value");
   return false;
  }

  if( ! nameField.isValid() )
  {
   ErrorBox.showError("Name field has invalid value");
   return false;
  }
  
  if( param == null )
   param= new Parameter();
 
  Collection<Tag> tags = param.getClassificationTags();

  for( Classifier clsf : DataManager.getInstance().getClassifiers() )
  {
   if( clsf.isMandatory() )
   {
    boolean found=false;
    
    if( tags != null )
    {
     for( Tag t : tags )
     {
      if( t.getClassifier().getId() == clsf.getId() )
      {
       found=true;
       break;
      }
     }
    }
    
    if( !found )
    {
     ErrorBox.showError("Parameter must have tag of mandatory classifier '"+clsf.getName()+"'");
     return false;
    }
    
   }
  }
  
  
  param.setCode(codeField.getValueAsString());
  param.setName(nameField.getValueAsString());
  param.setDescription(descriptionArea.getValueAsString());
  
  return true;
 }


 
 public Parameter getParameter()
 {
  return param;
 }

 class VariableEditor extends ButtonListenerAdapter implements ObjectAction<Variable>
 {
  VariableEditPanel vp;
  Window        window;

  public void onClick(Button button, EventObject e)
  {
   if(vp == null)
   {
    vp = new VariableEditPanel();
    vp.setEditPanelListener(this);
   }

   if(button == addVarButton)
   {
    try{
     
     vp.setVariable(null);
    }
    catch (Exception ex) {
     ErrorBox.showError("Error: "+ex);
    }
   }
   else
   {
    Record r = vGrid.getSelectionModel().getSelected();

    if(r == null)
     return;

    Variable v = (Variable) r.getAsObject("obj");

    if(button == editVarButton)
    {
     if(v.getParameter() != param)
     {
      ErrorBox.showError("Inherited variable can't be edited");
      return;
     }

     vp.setVariable( new Variable( v ) );
    }
    else
    {
     if(v.getParameter() != param)
     {
      ErrorBox.showError("Inherited variable can't be removed");
      return;
     }

     vStore.remove(r);
     param.removeVariable(v);
     return;
    }
   }


   if(window == null)
   {
    window = new Window();

    window.setTitle("Edit Variable");
    window.setClosable(true);
    window.setWidth(400);
    window.setHeight(455);
    window.setPlain(true);
    window.setLayout(new FitLayout());
    window.add(vp);
    window.setCloseAction(Window.HIDE);
    window.setModal(true);
   }

   //window.show(button.getElement());
   window.show();
  }

  public void doAction(String actName, Variable v)
  {
   Collection<Variable> vs = param.getVariables();
   
   if( vs != null && v != null )
   {
    for( Variable ev : vs )
    {
     if( ev.getName().equals(v.getName()) && ev.getId() != v.getId() )
     {
      ErrorBox.showError("Variable with name '"+v.getName()+"' already exists");
      return;
     }
    }
   }
   
   window.hide();

   if(!"ok".equals(actName))
    return;

   if(v.getId() == 0)
   {
    v.setId(-1);
    param.addVariable(v);
    vStore.insert(0, vRecordDef.createRecord(new Object[] { "this", v.getName(), v.getType().name(),
      v.getVariantsString(), v, 0 }));
   }
   else
   {
    for(Record r : vStore.getRecords())
    {
     Variable origVar = (Variable) r.getAsObject("obj");
     if( origVar.getId() == v.getId())
     {
      origVar.setDirty(true);
      origVar.update(v);
      
      r.set("varname", v.getName());
      r.set("variants", origVar.getVariantsString());
      break;
     }
    }
   }

  }

  public void doMultyAction(String actName, List<Variable> lp)
  {
  }
 }

 class QualifierEditor extends ButtonListenerAdapter implements ObjectAction<Qualifier>
 {
  QualifierEditPanel qp;
  Window         window;

  public void onClick(Button button, EventObject e)
  {
   if(qp == null)
    qp = new QualifierEditPanel();


   if(button == addQButton)
   {
    qp.setQualifier(null);
   }
   else
   {
    Record r = qGrid.getSelectionModel().getSelected();

    if(r == null)
     return;

    Qualifier q = (Qualifier) r.getAsObject("obj");

    if(button == editQButton)
    {
     if(q.getParameter() != param)
     {
      ErrorBox.showError("Inherited Qualifier can't be edited");
      return;
     }

     qp.setQualifier( new Qualifier(q) );
    }
    else
    {
     if(q.getParameter() != param)
     {
      ErrorBox.showError("Inherited Qualifier can't be removed");
      return;
     }

     qStore.remove(r);
     param.removeQualifier(q);
     return;
    }
   }

   qp.setEditPanelListener(this);

   if(window == null)
   {
    window = new Window();

    window.setTitle("Edit Qualifier");
    window.setClosable(true);
    window.setWidth(400);
    window.setHeight(455);
    window.setPlain(true);
    window.setLayout(new FitLayout());
    window.add(qp);
    window.setCloseAction(Window.HIDE);
    window.setModal(true);
   }

   window.show(button.getElement());
  }

  public void doAction(String actName, Qualifier q)
  {
   Collection<Qualifier> qs = param.getQualifiers();
   
   if( qs != null && q != null )
   {
    for( Qualifier eq : qs )
    {
     if( eq.getName().equals(q.getName()) && eq.getId() != q.getId() )
     {
      ErrorBox.showError("Qualifier with name '"+q.getName()+"' already exists");
      return;
     }
    }
   }
   
   window.hide();

   if(!"ok".equals(actName))
    return;

//   System.out.println("ID: " + q.getId());
   q.setDirty(true);

   if(q.getId() == 0)
   {
    q.setId(-1);
    param.addQualifier(q);
    qStore.insert(0, qRecordDef.createRecord(new Object[] { "this", q.getName(),
      q.isPredefined(), q.isMandatory(), q.getVariantsString(), q, 0 }));
   }
   else
   {
    for(Record r : qStore.getRecords())
    {
     Qualifier origQ = (Qualifier) r.getAsObject("obj");
     if( origQ.getId() == q.getId() )
     {
      origQ.update(q);
      
      origQ.setDirty(true);
      
      r.set("qname", q.getName());
      r.set("mandat", q.isMandatory());
      r.set("predef", q.isPredefined());
      r.set("variants", origQ.getVariantsString());
      break;
     }
    }
   }

  }
  
  public void doMultyAction(String actName, List<Qualifier> lp)
  {
  }
 }

 static class GridHelper extends GridCellListenerAdapter implements Renderer
 {
  public void onCellClick(GridPanel grid, int rowIndex, int colIndex, EventObject e)
  {
   if(grid.getColumnModel().getDataIndex(colIndex).equals("info") && e.getTarget(".infotip", 1) != null)
   {
    Record record = grid.getStore().getAt(rowIndex);

    String str = record.getAsString("info");
    if(str != null && str.length() > 1)
     str = str.replaceAll("\n", "<br />");
    else
     str=record.getAsString("tagname");
    PopupMessage.showMessage(record.getAsString("tagname"), str, e.getXY());
   }
  }

  public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store)
  {
   return "<img class=\"infotip\" src=\""+ConfigManager.SAIL_RESOURCE_PATH+"images/silk/information.gif\" />";
  }

 }

 static class InheritHighlight implements Renderer
 {
  public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store)
  {

   if( record.getAsInteger("level") != 0 )
    cellMetadata.setCssClass("objInherited");
   else
    cellMetadata.setCssClass("objOwned");

   return value.toString();
  }
 }
 
 class InheritSetEditor extends ButtonListenerAdapter implements ObjectAction<Parameter>
 {
  ParameterSelectDialog prmSelDialog;
 
  public void onClick(Button button, EventObject e)
  {
   if( "remove".equals(button.getStateId()))
   {
    Record r = ipGrid.getSelectionModel().getSelected();
    
    if( r == null)
     return;
    
    if( r.getAsInteger("level") > 0 )
    {
     ErrorBox.showError("Only directly inherited parameters can be removed");
     return;
    }
    
    removeInherited((Parameter)r.getAsObject("obj"));
    return;
   }
   
   prmSelDialog=ParameterSelectDialog.getDialog(DataManager.getInstance().getParameterManager());
   prmSelDialog.setObjectActionListener(this);
   prmSelDialog.show(button.getElement());
  }

  public void doAction(String actName, Parameter p)
  {
   if( p == param )
   {
    ErrorBox.showError("Parameter can't inherit itself");
    return;
   }
   
   prmSelDialog.dispose();
   
   addInherited(p);
   
  }

  public void doMultyAction(String actName, List<Parameter> lp)
  {
   prmSelDialog.dispose();
  }
 }

/*
 class TagSetEditor extends ButtonListenerAdapter
 {
  public void onClick(Button button, EventObject e)
  {
   if(button != addTButton)
   {
    removeTag();
    return;
   }

   final ClassifierSelectDialog clsSelDialog = ClassifierSelectDialog.getDialog( ClassifierManagerAdapter.getInstance() );

   clsSelDialog.setObjectActionListener(new ObjectAction<Classifier>()
   {

    public void doAction(String actName, Classifier p)
    {
     clsSelDialog.hide();

     if("cancel".equals(actName))
      return;


     final SelectTagDialog tagSelDialog = SelectTagDialog.getDialog();
     tagSelDialog.setClassifier(p);
     tagSelDialog.setObjectActionListener(new ObjectAction<Tag>()
     {
      public void doAction(String actName, Tag t)
      {
       tagSelDialog.hide();

       if("cancel".equals(actName))
        return;

       tagStore.add(classifRecordDef.createRecord(new Object[] { t.getClassifier().getName(), t.getName(),
         t.getDescription(), t }));
       param.addClassificationTag(t);      }

      public void doMultyAction(String actName, List<Tag> lp)
      {
      }
     });

     tagSelDialog.show();
    }

    public void doMultyAction(String actName, List<Classifier> lp)
    {

    }
   });

   clsSelDialog.show(button.getButtonElement());

  }

  private void removeTag()
  {
   Record r = tagGrid.getSelectionModel().getSelected();

   if(r != null)
   {
    param.removeClassificationTag((Tag) r.getAsObject("obj"));
    tagStore.remove(r);
   }
  }

 }
*/
 
 class RelationSetEditor extends ButtonListenerAdapter
 {
  public void onClick(Button button, EventObject e)
  {
   String act = button.getStateId();
   
   if( "remove".equals(act) )
   {
    removeRelation();
    return;
   }
   
   try
   {
    final ParameterSelectDialog prmSelDialog=ParameterSelectDialog.getDialog( DataManager.getInstance().getParameterManager() );

    
    prmSelDialog.setObjectActionListener(new ObjectAction<Parameter>()
    {
     public void doAction(String actName, final Parameter selP)
     {
      prmSelDialog.dispose();

      if( "cancel".equals(actName) )
       return;
      
      
      final ClassifierSelectDialog clsSelDialog=ClassifierSelectDialog.getDialog( DataManager.getInstance().getClassifierManager(Target.RELATION) );
      clsSelDialog.setObjectActionListener(new ObjectAction<Classifier>()
      {

       public void doAction(String actName, Classifier cls)
       {
        clsSelDialog.dispose();

        if("cancel".equals(actName))
         return;


        final SelectTagDialog tagSelDialog = SelectTagDialog.getDialog();
        tagSelDialog.setClassifier(cls);
        tagSelDialog.setObjectActionListener(new ObjectAction<Tag>()
        {
         public void doAction(String actName, Tag t)
         {
          tagSelDialog.hide();

          if("cancel".equals(actName))
           return;

          Relation rel = new Relation();
          rel.setTargetParameter(selP);
          rel.setTag(t);
          param.addRelation(rel);
          
          rStore.add(rRecordDef.createRecord(new Object[] { selP.getCode(), t.getClassifier().getName(),
            t.getName(), rel }));
         }
         
         public void doMultyAction(String actName, List<Tag> lp)
         {
         }
        });

        tagSelDialog.show();       }

       public void doMultyAction(String actName, List<Classifier> lp)
       {
       }

      });
      
      clsSelDialog.show();     }

     public void doMultyAction(String actName, List<Parameter> lp)
     {
      
     }});
    
    prmSelDialog.show();
    
    
   }
   catch(Exception ex)
   {
    ex.printStackTrace();
   }
  }

  private void removeRelation()
  {
   Record r = rGrid.getSelectionModel().getSelected();
   
   if( r != null )
   {
    param.removeRelation( (Relation)r.getAsObject("obj"));
    rStore.remove(r);
   }
  }
  
 }

 public void setObjectActionListener(ObjectAction<Parameter> ls)
 {
  btnListener.setListener(ls);
 }

 class BtnListener extends ButtonListenerAdapter
 {
  private ObjectAction<Parameter> lsnr;
  
  public BtnListener()
  {
   super();
  }
  
  public void setListener( ObjectAction<Parameter> l )
  {
   lsnr=l;
  }

  public void onClick(Button button, EventObject e)
  {
   final String state = button.getStateId();
   
   if( lsnr != null )
   {
    if( "ok".equals(state) )
    {
     try
     {
      if( validate() )
       lsnr.doAction( state, param );
     }
     catch (Exception ex) {
      ex.printStackTrace();
     }
    }
    else if( "delete".equals(state) ) 
    {
     if( param == null || param.getId() <= 0 )
      return;
     
     final MessageBoxConfig confBox = new MessageBoxConfig();
 
     ClientParameterAuxInfo cpai = (ClientParameterAuxInfo)param.getAuxInfo();
     if( cpai != null && cpai.getChildren() != null && cpai.getChildren().size() > 0 )
     {
      StringBuilder sb = new StringBuilder();
      sb.append("This parameter is the base for some other parameters (");

      for(Parameter chp : cpai.getChildren())
      {
       sb.append(chp.getCode()).append(", ");
      }

      sb.setLength(sb.length() - 2);
      sb.append("). It can't be removed. Remove they first");

      confBox.setTitle("Delete error");
      confBox.setButtons(MessageBox.CANCEL);
      confBox.setIconCls(MessageBox.ERROR);
      confBox.setMsg(sb.toString());

      MessageBox.show(confBox);
      return;
     }
     
     confBox.setTitle("Confirm delete");
     confBox.setButtons(new NameValuePair[]{ new NameValuePair("yes","YES"), new NameValuePair("no","NO") });
     confBox.setIconCls(MessageBox.QUESTION);
     confBox.setMsg("You are going to delete the parameter. Are you sure?");
     confBox.setCallback( new PromptCallback() {

      public void execute(String btnID, String text)
      {
       if( "yes".equals(btnID) )
       {
        if( param.getRecordsCount() > 0 )
        {
         confBox.setMsg("There are samples annotated by this parameter. Such annotations will be removed. Are you sure?");
         confBox.setIconCls(MessageBox.WARNING);
         confBox.setCallback( new PromptCallback() {

          public void execute(String btnID, String text)
          {
           if( "yes".equals(btnID) )
           {
            lsnr.doAction( state, param );
           }
          }});
         MessageBox.show( confBox );
        }
        else
         lsnr.doAction( state, param );
       }
       
      }});
     MessageBox.show( confBox );

    }
    else
     lsnr.doAction( state, null );
   }
  }
 }

}
