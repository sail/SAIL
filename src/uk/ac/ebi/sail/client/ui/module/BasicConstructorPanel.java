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
import java.util.List;
import java.util.Vector;

import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.EnumerationSelectListener;
import uk.ac.ebi.sail.client.FilteredEnumeration;
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.Annotation;
import uk.ac.ebi.sail.client.common.ComplexFilter;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterFormat;
import uk.ac.ebi.sail.client.common.Qualifier;
import uk.ac.ebi.sail.client.common.RequestItem;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.Study;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.ActionFlags;
import uk.ac.ebi.sail.client.ui.ActionHelper;
import uk.ac.ebi.sail.client.ui.ObjectSelectionListener;
import uk.ac.ebi.sail.client.ui.RequestItemSelectListener;
import uk.ac.ebi.sail.client.ui.StudyCollectionStateListener;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.PaddedPanel;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

public class BasicConstructorPanel extends Panel
{
 private static RecordDef cBRecordDef = new RecordDef(new FieldDef[] { 
   new StringFieldDef("name"),
   new ObjectFieldDef("obj")
 });

 private ComboBox  studyCBox;
 private ToolTip studyCBoxTT;
 private ToolbarButton studyInfoButton;
 private ToolTip studyInfoTT;
 
 private ComboBox  collectionCBox;
 private ToolTip collectionCBoxTT;
 private ToolbarButton collectionInfoButton;
 private ToolTip collectionInfoTT;
 
 private Vector<StudyCollectionStateListener> studySelListeners = new Vector<StudyCollectionStateListener>(5);

 private BasicRequestTree basicRequestTree;
 private BasicParameterSelectPanel ptbl;
 
 private ObjectAction<Parameter> listener;
 private ObjectAction<SampleCollection> collectionInfoListener;
 private ObjectAction<Study> studyInfoListener;

 private EnumerationSelectLsnr enumLsnr =  new EnumerationSelectLsnr();
 
 private boolean addGroupState;
 
 public BasicConstructorPanel( )
 {
  this(null);
 }
 
 public BasicConstructorPanel( Action[] auxButtons )
 {
  super();
  
  setAutoScroll(true);
  setTitle("Basic View");
  setIconCls("tab-icon");
  setClosable(false);
  setAutoWidth(true);

  BorderLayout blay = new BorderLayout();
  
  setLayout(blay);
  setMargins(2);

  BorderLayoutData eastData = new BorderLayoutData(RegionPosition.EAST);  
  eastData.setSplit(true);  
  eastData.setMinSize(175);  
  eastData.setMargins(new Margins(0, 0, 5, 0));  

  BorderLayoutData centerData = new BorderLayoutData(RegionPosition.CENTER);  
  centerData.setMinSize(175);  
  centerData.setMargins(new Margins(0, 5, 0, 0));  
  

  Action[] btns=new Action[]
                           {
  //  new Action("Quick query","qquery"+ActionFlags.separator+ActionFlags.ALLOW_MULTIPLE,"scienceIcon",null),
    new Action("Add","add"+ActionFlags.separator+ActionFlags.ALLOW_MULTIPLE,"paramIcon",null)//,
 //   new Action("Add to group","addGrp"+ActionFlags.separator+ActionFlags.ALLOW_MULTIPLE,"groupIcon",null),
//    new Action("Add Split","enum","splitIcon",null),
 //   new Action("Relations","rel","relationIcon",null),
 //   new Action("Extra","extra","extraIcon",new Action[]{
  //    new Action("Export visible","exportvis"+ActionFlags.separator+ActionFlags.ALLOW_EMPTY+ActionFlags.ALLOW_MULTIPLE,"paramsExport", null),
  //    new Action("Export selected","exportsel"+ActionFlags.separator+ActionFlags.ALLOW_MULTIPLE,"paramExport", null)
  //  })
                           };
  
 // if( auxButtons != null )
 // btns = ActionHelper.mergeActions(btns,auxButtons);
  
  ptbl = new BasicParameterSelectPanel(false, btns, DataManager.getInstance().getParameterManager() );

  ptbl.setObjectActionListener(new SelectParamLsnr());
  ptbl.setSelectionListener(new SelectionLsnr() );
 // ptbl.setEnumerationSelectListener(enumLsnr);
  
  //ptbl.setButtonEnabled( "enum", false, true );
  //ptbl.setButtonEnabled( "rel", false, true );
  //ptbl.setButtonEnabled( "addGrp", false, true );

  
  PaddedPanel pp = new PaddedPanel(ptbl,4,4,2,4);
  pp.setBorder(true);
  pp.setLayout( new FitLayout() );
  add(pp, centerData);

  basicRequestTree = new BasicRequestTree();
  
  pp = new PaddedPanel(basicRequestTree,4,2,4,4);
  pp.setLayout(new FitLayout());
  pp.setWidth(350);
  add(pp, eastData);
  
  studySelListeners.add( basicRequestTree );
  
  Toolbar tb = new Toolbar();
   
  studyCBox = new ComboBox();
  studyCBox.setDisplayField("name");
  studyCBox.setTypeAhead(true);
  studyCBox.setMode(ComboBox.LOCAL);
  studyCBox.setEmptyText("Select study");
  studyCBox.setTriggerAction(ComboBox.ALL);
  studyCBox.setSelectOnFocus(true);

  Store stStore = new Store(cBRecordDef);
  stStore.add(cBRecordDef.createRecord(new Object[] { " [ANY] ", null }));

  for(Study cl : DataManager.getInstance().getStudies())
   stStore.add(cBRecordDef.createRecord(new Object[] { cl.getName(), cl }));

  studyCBox.setStore(stStore);
  studyCBox.setValue(stStore.getAt(0).getAsString("name"));
  studyCBox.addListener(new ComboBoxListenerAdapter()
  {
  public void onSelect(ComboBox comboBox, Record record, int index)
   {
    Study cl = (Study) record.getAsObject("obj");

    selectStudy(cl, null);
    
    final int id = cl == null?0:cl.getId();
    
    DataManager.getInstance().setCountContext(id, 0, new AsyncCallback<Void>()
    {
     
     @Override
     public void onSuccess(Void arg0)
     {
      for(StudyCollectionStateListener lsnr : studySelListeners)
       lsnr.studyCollectionChanged(id, 0);
     }
     
     @Override
     public void onFailure(Throwable arg0)
     {
     }
    });
    

   }
  });
  
  studyCBoxTT = new ToolTip();
  studyCBoxTT.setHtml("Select study");
  studyCBoxTT.applyTo(studyCBox);
  
  tb.addText( "Study: ");
  tb.addField( studyCBox );
  studyInfoButton = new ToolbarButton();
  studyInfoButton.setIconCls("infoIcon");
  studyInfoButton.setDisabled(true);
  studyInfoTT = new ToolTip();
  studyInfoTT.applyTo(studyInfoButton);
  tb.addButton(studyInfoButton);
  studyInfoButton.addListener( new ButtonListenerAdapter(){
   @Override
   public void onClick(Button button, EventObject e)
   {
    if( studyInfoListener == null )
     return;
    
    String stName = studyCBox.getValue();
    
    Study st=null;
    for( Record r : studyCBox.getStore().getRecords() )
    {
     if( r.getAsString("name").equals(stName) )
     {
      st=(Study)r.getAsObject("obj");
      break;
     }
    }
    
    studyInfoListener.doAction("info", st);
   }
  });
  
  collectionCBox = new ComboBox();
  collectionCBox.setDisplayField("name");
  collectionCBox.setTypeAhead(true);
  collectionCBox.setMode(ComboBox.LOCAL);
  collectionCBox.setEmptyText("Select collection");
  collectionCBox.setTriggerAction(ComboBox.ALL);
  collectionCBox.setSelectOnFocus(true);

  Store khStore = new Store(cBRecordDef);
  khStore.add(cBRecordDef.createRecord(new Object[] { " [ANY] ", null }));

  for(SampleCollection cl : DataManager.getInstance().getCollections())
   khStore.add(cBRecordDef.createRecord(new Object[] { cl.getName(), cl }));

  collectionCBox.setStore(khStore);

  collectionCBox.setValue(khStore.getAt(0).getAsString("name"));
  collectionCBox.addListener(new ComboBoxListenerAdapter()
  {
   public void onSelect(ComboBox comboBox, Record record, int index)
   {
    SampleCollection kh = (SampleCollection) record.getAsObject("obj");

    final int khId = kh == null?0:kh.getId();

    
    Study st=null;
    
    if( khId == 0 )
    {
     String stVal = studyCBox.getValue();
     
     for( Record r : studyCBox.getStore().getRecords() )
      if( stVal.equals(r.getAsString("name")) )
      {
       st = (Study) r.getAsObject("obj");
       break;
      }
    }
    
    final int stId = st == null?0:st.getId();

    collectionInfoButton.setDisabled(khId == 0);
    collectionInfoTT.setDisabled(khId == 0);
    
    if( khId == 0 )
    {
     if(stId==0)
      collectionCBoxTT.setHtml("<b>Any collection</b>");
     else
      collectionCBoxTT.setHtml("<b>Any collection in the current study</b>");
    }
    else if( khId < 0 && khId%2 == 0 )
     collectionCBoxTT.setHtml("<b>Samples that were selected for the study</b>");
    else if( khId < 0 && khId%2 != 0 )
     collectionCBoxTT.setHtml("<b>Samples that were used for the study</b>");
    else
    {
     collectionCBoxTT.setHtml(kh.getName());
     
     String khDescr = "<h2>" + kh.getName() + "</h2>";
     if( kh.getAnnotations() != null )
     {
      for( Annotation an : kh.getAnnotations() )
      {
       Tag t = an.getTag();
       
       if( t.getDescription() != null && t.getDescription().length() > 0  )
        khDescr+="<hr><h1>"+t.getDescription()+"</h1><br>";
       else
        khDescr+="<hr><h1>"+t.getName()+"</h1><br>";
       
       khDescr+=an.getText().replaceAll("\n", "<br>");
      }
     }
     
     collectionInfoTT.setHtml(khDescr);
    }
    
    DataManager.getInstance().setCountContext(stId, khId, new AsyncCallback<Void>()
    {

     @Override
     public void onFailure(Throwable arg0)
     {
     }

     @Override
     public void onSuccess(Void arg0)
     {
      for(StudyCollectionStateListener lsnr : studySelListeners)
       lsnr.studyCollectionChanged(stId, khId);
     }
    });


   }
  });
  
  collectionCBoxTT = new ToolTip();
  collectionCBoxTT.setHtml("Select collection");
  collectionCBoxTT.applyTo(collectionCBox);
 
  tb.addText( "&nbsp;&nbsp;&nbsp;&nbsp;Collection: ");
  tb.addField( collectionCBox );
  collectionInfoButton = new ToolbarButton();
  collectionInfoButton.setIconCls("infoIcon");
  collectionInfoButton.setDisabled(true);
  collectionInfoTT = new ToolTip();
  collectionInfoTT.applyTo(collectionInfoButton);
  tb.addButton(collectionInfoButton);
  collectionInfoButton.addListener( new ButtonListenerAdapter(){
   @Override
   public void onClick(Button button, EventObject e)
   {
    if( collectionInfoListener == null )
     return;
    
    String stName = collectionCBox.getValue();
    
    SampleCollection kh=null;
    for( Record r : collectionCBox.getStore().getRecords() )
    {
     if( r.getAsString("name").equals(stName) )
     {
      kh=(SampleCollection)r.getAsObject("obj");
      break;
     }
    }
    
    collectionInfoListener.doAction("info", kh);
   }
  });
 
 // ptbl.setTopToolbar(tb);
  
  addStudyCollectionStateListener(ptbl);
  
  basicRequestTree.setRequestItemSelectListener( new RequestItemSelectListenerImpl() );
 }
 
 private boolean selectStudy( Study study, SampleCollection coll )
 {
  if( study != null )
  {
   Store cst = studyCBox.getStore();

   Record clRecord = null;

   for(Record r : cst.getRecords())
   {
    if(study.equals(r.getAsObject("obj")))
    {
     clRecord = r;
     break;
    }
   }

   if(clRecord == null)
    study = null;
  }
  
  Store tst = collectionCBox.getStore();
  tst.removeAll();


  if(study == null)
  {
   studyCBoxTT.setHtml("Select study");
   collectionCBox.reset();

   studyInfoButton.setDisabled(true);
   studyInfoTT.setDisabled(true);

   collectionInfoButton.setDisabled(true);
   collectionInfoTT.setDisabled(true);

   tst.add( cBRecordDef.createRecord( new Object[]{" [ANY] ",null} ));
   for( SampleCollection ch : DataManager.getInstance().getCollections() )
    tst.add( cBRecordDef.createRecord( new Object[]{ch.getName(),ch} ));
    
   collectionCBox.setValue(tst.getAt(0).getAsString("name"));
   collectionCBoxTT.setHtml("<b>Any collection</b>");

   return false;
  }

  studyCBoxTT.setHtml("<b>" + study.getName() + "</b>");
  studyInfoButton.setDisabled(false);
  studyInfoTT.setDisabled(false);
  
  String stDescr = "<h2>" + study.getName() + "</h2>";
  if( study.getAnnotations() != null )
  {
   for( Annotation an : study.getAnnotations() )
   {
    Tag t = an.getTag();
    
    if( t.getDescription() != null && t.getDescription().length() > 0  )
     stDescr+="<hr><h1>"+t.getDescription()+"</h1><br>";
    else
     stDescr+="<hr><h1>"+t.getName()+"</h1><br>";
    
    stDescr+=an.getText().replaceAll("\n", "<br>");
   }
  }
  
  studyInfoTT.setHtml(stDescr);

  int collecttionIdx=0;
  
  tst.add(cBRecordDef.createRecord(new Object[] { "[ANY IN THE STUDY]", null }));
  tst.add(cBRecordDef.createRecord(new Object[] { "[Eligible for study]", DataManager.getInstance().getCollection(-study.getId()*2) }));
  tst.add(cBRecordDef.createRecord(new Object[] { "[Used in study]", DataManager.getInstance().getCollection(-(study.getId()*2+1)) }));

  
  int n=0;
  if(study.getCollections() != null)
  {
   for(SampleCollection t : study.getCollections())
   {
    n++;
    tst.add(cBRecordDef.createRecord(new Object[] { t.getName(), t }));
    
    if( t.equals(coll))
     collecttionIdx=n;
   }
  }

  collectionCBox.setValue(tst.getAt(collecttionIdx).getAsString("name"));

  String collectionTTText=null;
  
  switch(collecttionIdx)
  {
   case 0:
    collectionTTText="<b>Any collection in the current study</b>";
    break;
   case 1:
    collectionTTText="<b>Samples that were selected for the study</b>";
    break;
   case 2:
    collectionTTText="<b>Samples that were used for the study</b>";
    break;
   default:
    collectionTTText=tst.getAt(collecttionIdx).getAsString("name");
  }
  
  collectionCBoxTT.setHtml(collectionTTText);
  
  if( collecttionIdx < 3 )
  {
   collectionInfoButton.setDisabled(true);
   collectionInfoTT.setDisabled(true);
  }
  else
  {
   collectionInfoButton.setDisabled(false);
   collectionInfoTT.setDisabled(false);
   
   String str = "<h2>"+tst.getAt(collecttionIdx).getAsString("name")+"</h2><hr>";
   
   collectionInfoTT.setHtml(str+"Some collection text");
  }
  
  
  return true;
 }
 
 public void addReportListener( MakeReportListener lsnr )
 {
  basicRequestTree.addReportListener(lsnr);
 }
 
 private class SelectParamLsnr implements ObjectAction<Parameter>
 {

  public void doAction(String actType, Parameter p)
  {
   if("add".equals(actType))
    basicRequestTree.addParameter(p);
   else if("qquery".equals(actType))
    basicRequestTree.quickQuery(p);
   else if( "rel".equals(actType) )
   {
    if( p.getRelations() == null )
     return;
    
    RelationsDialog rd = RelationsDialog.getIntance();// RelationsDialog();
    rd.setObjectActionListener(new RelationSelectLsnr());
    rd.setGroupState( addGroupState );
    rd.setParameter(p);
    rd.show();
   }
   else if( "addGrp".equals(actType) )
   {
    basicRequestTree.addParameterToGroup(p);
   }
   else if( "enum".equals(actType) )
   {
    if(p.countEnumerations() == 0)
     return;
    else if(p.countEnumerations() == 1+1000) //// TODO check!
    {
     if(p.getQualifiers() != null && p.getQualifiers().size() > 0)
     {
      Qualifier q = p.getQualifiers().iterator().next();
      basicRequestTree.addParameter(q);
     }
     else
     {
      for(Variable v : p.getVariables())
       if(v.getType() == Variable.Type.ENUM)
       {
        basicRequestTree.addVariable(v);
        break;
       }
     }
    }
    else
    {
     SelectEnumerationDialog dlg;
     
     dlg = SelectEnumerationDialog.getInstance();
     dlg.setEnumerationSelectListener(enumLsnr);

     dlg.setParameter(p);
     dlg.show();
    }
   }
   else if( "exportvis".equals(actType) )
   {
    exportParameters(ptbl.getVisible());
   }
   else if( "exportsel".equals(actType) )
   {
    exportParameters(Arrays.asList(new Parameter[]{p}));
   }
   else if( "filter".equals(actType) )
   {
    final ComplexFilter cf = new ComplexFilter();
    cf.setParameter(p);

    ParameterFilterDialog fDlg = new ParameterFilterDialog( cf, addGroupState );
    
    fDlg.setListener( new ObjectAction<ComplexFilter>()
      {

       public void doAction(String actName, ComplexFilter cfp)
       {
        if( "cancel".equals(actName) )
         return;

        boolean toGrp = "addToGroup".equals(actName);
        
        if( cf.isClean() )
        {
         if( toGrp )
          basicRequestTree.addParameterToGroup(cfp.getParameter());
         else
          basicRequestTree.addParameter(cfp.getParameter());
        }
        else
        {
         if( toGrp )
          basicRequestTree.addFilteredToGroup(cfp);
         else
          basicRequestTree.addFiltered(cfp);
        }
        
       }

       public void doMultyAction(String actName, List<ComplexFilter> lp)
       {
       }
     
      }  
    );
    
    fDlg.show();
   }
   else
   {
    if( listener != null )
     listener.doAction(actType, p);
   }
  }

  public void doMultyAction(String actType, List<Parameter> lp)
  {
   if( "add".equals(actType) )
    basicRequestTree.addGroup(lp);
   else if("qquery".equals(actType))
    basicRequestTree.quickQuery(lp);
   else if( "addGrp".equals(actType) )
   {
    basicRequestTree.addParameterToGroup(lp);
   }
   else if( "exportvis".equals(actType) )
   {
    exportParameters(ptbl.getVisible());
   }
   else if( "exportsel".equals(actType) )
   {
    exportParameters(lp);
   }
   else
   {
    if( listener != null )
     listener.doMultyAction(actType, lp);
   }
  }

 }
 
 private void exportParameters( Collection<Parameter> prms )
 {
 // System.out.println("Height: "+getHeight());
  
  Window wnd = new Window();
  
  wnd.setSize(600, getHeight()-20);
  
  String out = "<table class=\"paramexport\"><tr><td>"+ParameterFormat.export(prms, "</td><td>", "</td></tr><tr><td>")+"</td></tr></table>";
//  System.out.println(out);
  
  Panel pnl = new Panel();
  pnl.setAutoScroll(true);
  
  wnd.setLayout(new FitLayout());
  wnd.add(pnl);
  
  pnl.setHtml(out);

  wnd.show();
 }
 
 
 class EnumerationSelectLsnr implements EnumerationSelectListener
 {
  
  public void filteredEnumerationSelected(FilteredEnumeration fq)
  {
//   System.out.println("OK "+fq.getQualifier().getParameter().getName());
   
   if( fq.getVariants() != null )
   {
    basicRequestTree.addSplitFiltered(fq);
   }
   else if( fq.getEnumeration() instanceof Variable )
    basicRequestTree.addVariable((Variable)fq.getEnumeration());
   else
    basicRequestTree.addParameter((Qualifier)fq.getEnumeration());
  }
 
//  public void filteredParameterSelected(List<FilteredEnumeration> fql, boolean group )
//  {
//   if( group )
//    requestPanel.addFilteredToGroup(fql);
//   else 
//    requestPanel.addFiltered(fql);
//
//  }

 }
 
 class SelectionLsnr implements ObjectSelectionListener<Parameter>
 {

  public void selectionChanged(List<Parameter> sel)
  {
   if( sel == null || sel.size() > 1 )
   {
//    ptbl.setButtonEnabled( "enum", false, false );
//    ptbl.setButtonEnabled( "rel", false, false );
   }
   else
   {
//    ptbl.setButtonEnabled("enum", sel.get(0).countEnumerations() != 0, false);
//    ptbl.setButtonEnabled("rel", sel.get(0).getRelations() != null, false);
   }
  }
  
 }

 class RelationSelectLsnr implements ObjectAction<Parameter>
 {

  public void doAction(String actName, Parameter p)
  {
   if( "select".equals(actName) )
    ptbl.addToSelection(p);
   else if( "addToReport".equals(actName) )
   {
    basicRequestTree.addParameter(p);
   }
   else if( "addToGroup".equals(actName) )
   {
    basicRequestTree.addParameterToGroup(p);
   }
  }

  public void doMultyAction(String actName, List<Parameter> lp)
  {
   if( "select".equals(actName) )
    ptbl.addToSelection(lp);
   else if( "addToReport".equals(actName) )
   {
    basicRequestTree.addGroup(lp);
   }
   else if( "addToGroup".equals(actName) )
   {
    basicRequestTree.addParameterToGroup(lp);
   }
  }
  
 }
 
 private class RequestItemSelectListenerImpl implements RequestItemSelectListener
 {

  public void itemSelected(RequestItem rri)
  {
   addGroupState = rri != null;
   ptbl.setButtonEnabled("addGrp", addGroupState, true);

   /*
    * if( rri == null ) { addGroupState=false; ptbl.setButtonEnabled("addGrp",
    * false, true); } else if( rri.getType() == Type.GROUP || ( (rri.getType()
    * == Type.PARAMETER || rri.getType() == Type.FILTERED_SINGLE ||
    * rri.getType() == Type.FILTERED_COMPLEX) && rri.getGroupItem() == null ) )
    * { ptbl.setButtonEnabled("addGrp", true, true); addGroupState=true; } else
    * { ptbl.setButtonEnabled("addGrp", false, true); addGroupState=false; } }
    */
  }
 }

 public ObjectAction<Parameter> getListener()
 {
  return listener;
 }

 public void setListener(ObjectAction<Parameter> listener)
 {
  this.listener = listener;
 }
 
 public void addStudyCollectionStateListener(StudyCollectionStateListener ls)
 {
  studySelListeners.add(ls);
 }
 
 public void removeStudyCollectionStateListener(StudyCollectionStateListener ls)
 {
  studySelListeners.remove(ls);
 }

 public void setCollectionInfoListener(ObjectAction<SampleCollection> collInfoListener)
 {
  this.collectionInfoListener = collInfoListener;
 }

 public void setStudyInfoListener(ObjectAction<Study> studyInfoListener)
 {
  this.studyInfoListener = studyInfoListener;
 }
 
}
