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

import uk.ac.ebi.sail.client.BackendService;
import uk.ac.ebi.sail.client.BackendServiceAsync;
import uk.ac.ebi.sail.client.CollectionManager;
import uk.ac.ebi.sail.client.DataChangeListener;
import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.InitListener;
import uk.ac.ebi.sail.client.LinkClickListener;
import uk.ac.ebi.sail.client.LinkManager;
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.ClassifierShadow;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterShadow;
import uk.ac.ebi.sail.client.common.Projection;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.Study;
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.ActionFlags;
import uk.ac.ebi.sail.client.ui.FileDownloader;
import uk.ac.ebi.sail.client.ui.module.ObjectList.Selection;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Viewport;
import com.gwtext.client.widgets.layout.TableLayout;
import com.gwtextux.client.widgets.upload.UploadDialog;


public class AdminPanel implements InitListener
{
// static RequestTable reportPrmTable;
 static TabPanel appPanel;
 static int reportCounter=1;
 
 public void doInit()
 {
  appPanel = new TabPanel();
//  appPanel.setResizeTabs(true);
//  appPanel.setTabWidth(160);
  appPanel.setEnableTabScroll(true);
  appPanel.setActiveTab(0);

  
  ReportConstructorPanel constructorTab = new ReportConstructorPanel(  
    new Action[] {new Action("Extra","extra","extraIcon",new Action[]{
    new Action(null,null),
    new Action("New parameter","addParam"+ActionFlags.separator+ActionFlags.ALLOW_EMPTY+ActionFlags.ALLOW_MULTIPLE, "paramAddIcon", null ),
    new Action("Edit parameter","editParam","paramEditIcon", null  ),
    new Action(null,null),
    new Action("Data template","genTemplate"+ActionFlags.separator+ActionFlags.ALLOW_MULTIPLE, "dataTemplateIcon", null )
    })});
  
  constructorTab.addReportListener( new ReportAction() );
  constructorTab.setListener( new ParameterEditAction() );
  constructorTab.setStudyInfoListener( new StudyInfoAction() );
  constructorTab.setCollectionInfoListener( new CollectionInfoAction() );
  constructorTab.setIconCls("constructIcon");
  appPanel.add(constructorTab);

//  ParameterSelectPanel psp = new ParameterSelectPanel(true, new Action[]{
//    new Action("Add","add"+ActionFlags.separator+ActionFlags.EMPTY),
//    new Action("Edit","edit")},
//    DataManager.getInstance().getParameterManager());
//  
//  psp.setObjectActionListener(new ParameterEditAction());
//  psp.setTitle("Parameters");
//  appPanel.add( psp );
  
  ClassifierSelectPanel csp = new ClassifierSelectPanel(true, new Action[]{
    new Action("Add","add"+ActionFlags.separator+ActionFlags.EMPTY),
    new Action("Edit","edit")},
    DataManager.getInstance().getClassifierManager(null) );
  
  csp.setIconCls("tagIcon");
  csp.setObjectActionListener(new ClassifierEditAction());
  csp.setTitle("Classifiers");
  appPanel.add( csp );
  
  ProjectionList pjl = new ProjectionList(Selection.SINGLE, new Action[]{
    new Action("Add","add"+ActionFlags.separator+ActionFlags.EMPTY),
    new Action("Edit","edit")},
    DataManager.getInstance().getProjectionManager());
  pjl.setObjectActionListener(new ProjectionEditAction());
  pjl.setTitle("Projections");
  appPanel.add( pjl );
  
//  DataImportPanel dip = new DataImportPanel();
//  dip.setTitle("Import");
//  appPanel.add( dip );
 
  StudyList stdl = new StudyList(Selection.SINGLE, new Action[]{
    new Action("Add","add"+ActionFlags.separator+ActionFlags.EMPTY),
    new Action("Edit","edit"),
    new Action("Import sample relation","import")},
    DataManager.getInstance().getStudyManager());
  stdl.setObjectActionListener(new StudyEditAction() );
  stdl.setTitle("Study");
  appPanel.add( stdl );

  CollectionList rsp = new CollectionList(Selection.SINGLE, new Action[]{
    new Action("Add","add"+ActionFlags.separator+ActionFlags.EMPTY),
    new Action("Edit","edit"),
    new Action("Import Data","import")},
    DataManager.getInstance().getSampleCollectionManager());
  rsp.setObjectActionListener(new CollectionEditAction());
  rsp.setTitle("Collection");
  appPanel.add( rsp );

  
  try{
  CollectionViewPanel rp = new CollectionViewPanel(DataManager.getInstance().getSampleCollectionManager());
  rp.setTitle("Collection View");
  rp.setClosable(false);
  appPanel.add( rp );
  }
  catch (Exception e) {
   e.printStackTrace();
  }
  
  
  DataImportPanel dip = new DataImportPanel();
  dip.setTitle("Metadata Import");
  appPanel.add(dip);

  
  appPanel.doLayout();

  
  
//  appPanel.addListener(new TabPanelListenerAdapter(){
//
//   public void onTabChange(TabPanel source, Panel tab)
//   {
//    tab.doLayout();
//   }
//  });
  
  new Viewport(appPanel);
 }



 
 static class ReportAction implements MakeReportListener
 {

  public void makeReport(final ReportRequest req)
  {
   
   BackendServiceAsync async = BackendService.Util.getInstance();
   async.getReport2( req , new AsyncCallback<Summary>(){

    public void onFailure(Throwable arg0)
    {
     arg0.printStackTrace();
     MessageBox.alert("Error!", "Error: "+arg0.getMessage(), null);
    }

    public void onSuccess(Summary res)
    {
     String repId = "Report "+(reportCounter++);

     Panel reportItem = new Panel();
     reportItem.setClosable(true);
     reportItem.setTitle(repId);
     reportItem.setCls("report-tab");
     reportItem.setIconCls("reportIcon");
     reportItem.setId(repId);
     reportItem.setAutoScroll(true);
     reportItem.setLayout(new TableLayout(1));
//     reportItem.setLayout(new FitLayout());

//     item.setScrollMode(Scroll.AUTO);
     
     ReportTable2 rt = new ReportTable2(req,res);
     reportItem.setTopToolbar( rt.getToolBar() );
     
     reportItem.add( rt );

     appPanel.add(reportItem);

     appPanel.setActiveTab(repId);
//     reportItem.layout();
     
//     MessageBox.alert("Success", "Success: "+arg0.toString(), null);
    }});

  }

 /*
  @SuppressWarnings("unchecked")
  public void makeReport1(List<ReportRequestItem> req)
  {
   String[][] reqArray = new String[ req.size() ][];
   int i=0;
   for( ReportRequestItem rri : req )
   {
    Object ti = rri.getObject();
    
    if( ti instanceof Parameter )
    {
     reqArray[i] = new String[3];
     
     reqArray[i][0] = "P";
     reqArray[i][1] = ((Parameter)ti).getCode();
     reqArray[i][2] = null;
    }
    else if( ti instanceof Qualifier )
    {
     reqArray[i] = new String[3];
     
     reqArray[i][0] = "Q";
     reqArray[i][1] = ((Qualifier)ti).getParameter().getCode();
     reqArray[i][2] = ((Qualifier)ti).getName();
    }
    else if( ti instanceof Variable )
    {
     reqArray[i] = new String[3];
     
     reqArray[i][0] = "V";
     reqArray[i][1] = ((Variable)ti).getParameter().getCode();
     reqArray[i][2] = ((Variable)ti).getName();
    }
    else
    {
     List<Parameter> li = (List<Parameter>)ti;
     
     reqArray[i] = new String[li.size()+2];
     
     reqArray[i][0]="G";
     reqArray[i][1]=rri.getObjectName();
     
     int l=2;
     for( Parameter lp : li )
      reqArray[i][l++]=lp.getCode();
    }
    
    i++;
   }
   
   BackendServiceAsync async = BackendService.Util.getInstance();
   async.getReport(null , new AsyncCallback<Report>(){

    public void onFailure(Throwable arg0)
    {
     MessageBox.alert("Error!", "Error: "+arg0.getMessage(), null);
     
    }

    public void onSuccess(Report arg0)
    {
     String repId = "Report "+(reportCounter++);

     Panel reportItem = new Panel();
     reportItem.setClosable(true);
     reportItem.setTitle(repId);
     reportItem.setIconCls("tab-icon");
     reportItem.setId(repId);

//     item.setScrollMode(Scroll.AUTO);
     appPanel.add(reportItem);

     reportItem.add( 
       new ReportTable(arg0) );
     
     appPanel.setActiveTab(repId);
//     reportItem.layout();
     
//     MessageBox.alert("Success", "Success: "+arg0.toString(), null);
    }});

  }
 */
}

 class ParameterEditAction implements ObjectAction<Parameter>
 {

  public void doAction(final String actName, Parameter p)
  {
   if(  actName.equals("genTemplate") )
   {
    generateTemplate( new int[] {p.getId()} );
    return;
   }
   
   if( ! actName.equals("addParam") && ! actName.equals("editParam")  )
    return;
   
   final Panel parameterEditTab = new Panel();
   parameterEditTab.setAutoScroll(true);
   parameterEditTab.setTitle(actName.equals("addParam") ? "Add parameter" : "Edit parameter");
   parameterEditTab.setIconCls("tab-icon");
   parameterEditTab.setClosable(true);
   parameterEditTab.setAutoWidth(true);

   ParameterEditPanel prmEditTab = new ParameterEditPanel();
   prmEditTab.setTitle(actName.equals("addParam") ? "Add parameter" : "Edit parameter");

   prmEditTab.setObjectActionListener(new ObjectAction<Parameter>()
   {

    public void doAction(String done, Parameter p)
    {
//     System.out.println("Parameter action: " + actName);
     appPanel.remove(parameterEditTab.getId());

     if(p != null)
     {
      //printParameter(p);

      if("cancel".equals(done))
       return;

      if("addParam".equals(actName))
      {

       DataManager.getInstance().addParameter(p, new AsyncCallback<Integer>()
       {

        public void onFailure(Throwable caught)
        {
         ErrorBox.showError("System error:<br />" + caught.getMessage());
        }

        public void onSuccess(Integer result)
        {
         if(result == null)
          ErrorBox.showError("Parameter add fail:<br />" + result);
        }
       });
      }
      else if("delete".equals(done))
      {
       DataManager.getInstance().deleteParameter(p, new AsyncCallback<Void>()
       {

        public void onFailure(Throwable caught)
        {
         ErrorBox.showError("System error:<br />" + caught.getMessage());
        }

        public void onSuccess(Void result)
        {
         if(result == null)
          ErrorBox.showError("Parameter delete failed:<br />" + result);
        }
       });       
      }
      else
      {

       DataManager.getInstance().updateParameter(p, new AsyncCallback<ParameterShadow>()
       {

        public void onFailure(Throwable caught)
        {
         ErrorBox.showError("System error:<br />" + caught.getMessage());
        }

        public void onSuccess(ParameterShadow result)
        {
         if( result == null )
         {
          ErrorBox.showError("Parameter was not updated");
          return;
         }
        }
       });
      }
     }
    }

    public void doMultyAction(String actName, List<Parameter> lp)
    {
    }
   });

   parameterEditTab.add(prmEditTab);
   parameterEditTab.setPaddings(15);

   if("editParam".equals(actName))
   {
    prmEditTab.setParameter( new Parameter(p) );
   }
   else
    prmEditTab.setParameter(new Parameter());

   appPanel.add(parameterEditTab);
   appPanel.setActiveTab(parameterEditTab.getId());

  }

  public void doMultyAction(String actName, List<Parameter> lp)
  {
   if( actName.equals("genTemplate") )
   {
    int[] ids = new int[lp.size()];
    
    int n=0;
    for( Parameter p : lp )
    {
     ids[n++]=p.getId();
//     System.out.println("Selected["+(n++)+"]: "+p.getName()+" Action: "+actName);
    }
    
    generateTemplate(ids);
   }
  }
  
  private void generateTemplate( int[] pids )
  {
   StringBuilder sb = new StringBuilder();
   
   sb.append(GWT.getModuleBaseURL());
   sb.append("DataTemplate?format=csv&request=");
   
   for( int id : pids )
    sb.append(id).append(',');
   
   sb.setLength(sb.length()-1);
   sb.append("&time=");
   sb.append(System.currentTimeMillis());
   
   FileDownloader.downloadFile(sb.toString());
  }
 } 

 class ClassifierEditAction implements ObjectAction<Classifier>
 {

  public void doAction(final String actName, Classifier p)
  {
   // System.out.println("Selected: "+(p!=null?p.getName():"null")+" Action: "+
   // actName);

   final Panel classifierEditTab = new Panel();
   classifierEditTab.setAutoScroll(true);
   classifierEditTab.setTitle(actName.equals("add") ? "Add classifier" : "Edit classifier");
   classifierEditTab.setIconCls("tab-icon");
   classifierEditTab.setClosable(true);
   classifierEditTab.setAutoWidth(true);

   ClassifierEditPanel clsEditTab = new ClassifierEditPanel();
   clsEditTab.setTitle(actName.equals("add") ? "Add classifier" : "Edit classifier");

   clsEditTab.setObjectActionListener(new ObjectAction<Classifier>()
   {

    public void doAction(String done, Classifier lp)
    {
     appPanel.remove(classifierEditTab.getId());

     if(lp != null)
     {
      if("cancel".equals(done))
       return;

      if("add".equals(actName))
      {

       DataManager.getInstance().addClassifier(lp, new AsyncCallback<ClassifierShadow>()
       {

        public void onFailure(Throwable caught)
        {
         ErrorBox.showError("System error:<br />" + caught.getMessage());
        }

        public void onSuccess(ClassifierShadow result)
        {
         if(result == null)
          ErrorBox.showError("Classifier add fail");
        }
       });
      }
      else
      {

       DataManager.getInstance().updateClassifier(lp, new AsyncCallback<ClassifierShadow>()
       {

        public void onFailure(Throwable caught)
        {
         ErrorBox.showError("System error:<br />" + caught.getMessage());
        }

        public void onSuccess(ClassifierShadow result)
        {
         if( result == null )
         {
          ErrorBox.showError("Classifier was not updated");
          return;
         }
        }
       });
      }
     }
    }

    public void doMultyAction(String actName, List<Classifier> llp)
    {
    }
   });

   classifierEditTab.setPaddings(15);
   classifierEditTab.add(clsEditTab);

   appPanel.add(classifierEditTab);

   if("edit".equals(actName))
   {
    clsEditTab.setClassifier( new Classifier(p) );
   }
   else
    clsEditTab.setClassifier(new Classifier());

   appPanel.setActiveTab(classifierEditTab.getId());

  }

  public void doMultyAction(String actName, List<Classifier> lp)
  {
   int n=0;
   for( Classifier p : lp )
   {
    System.out.println("Selected["+(n++)+"]: "+p.getName()+" Action: "+actName);
   }
   
  }
 } 

 class ProjectionEditAction implements ObjectAction<Projection>
 {

  public void doAction(final String actName, Projection p)
  {
   // System.out.println("Selected: "+(p!=null?p.getName():"null")+" Action: "+
   // actName);

   final Panel projectionEditTab = new Panel();
   projectionEditTab.setAutoScroll(true);
   projectionEditTab.setTitle(actName.equals("add") ? "Add projection" : "Edit projection");
   projectionEditTab.setIconCls("tab-icon");
   projectionEditTab.setClosable(true);
   projectionEditTab.setAutoWidth(true);

   ProjectionEditPanel prjEditPanel = new ProjectionEditPanel();
   prjEditPanel.setTitle(actName.equals("add") ? "Add projection" : "Edit projection");

   prjEditPanel.setObjectActionListener(new ObjectAction<Projection>()
   {

    public void doAction(String done, Projection lp)
    {
     appPanel.remove(projectionEditTab.getId());

     if(lp != null)
     {
      if("cancel".equals(done))
       return;

      if("add".equals(actName))
      {

       DataManager.getInstance().addProjection(lp, new AsyncCallback<Integer>()
       {

        public void onFailure(Throwable caught)
        {
         ErrorBox.showError("System error:<br />" + caught.getMessage());
        }

        public void onSuccess(Integer result)
        {
         if(result == null)
          ErrorBox.showError("Projection add failed");
        }
       });
      }
      else
      {

       DataManager.getInstance().updateProjection(lp, new AsyncCallback<Void>()
       {

        public void onFailure(Throwable caught)
        {
         ErrorBox.showError("System error:<br />" + caught.getMessage());
        }

        public void onSuccess(Void result)
        {
        }
       });
      }
     }
    }

    public void doMultyAction(String actName, List<Projection> llp)
    {
    }
   });

   projectionEditTab.setPaddings(15);
   projectionEditTab.add(prjEditPanel);

   appPanel.add(projectionEditTab);

   if("edit".equals(actName))
   {
    prjEditPanel.setProjection( new Projection(p) );
   }
   else
    prjEditPanel.setProjection(new Projection());

   appPanel.setActiveTab(projectionEditTab.getId());

  }

  public void doMultyAction(String actName, List<Projection> lp)
  {
   int n=0;
   for( Projection p : lp )
   {
    System.out.println("Selected["+(n++)+"]: "+p.getName()+" Action: "+actName);
   }
   
  }
 } 

 
 
 class CollectionEditAction implements ObjectAction<SampleCollection>
 {

  public void doAction(final String actName, SampleCollection p)
  {
   // System.out.println("Selected: "+(p!=null?p.getName():"null")+" Action: "+
   // actName);

   if( "import".equals(actName) )
   {
    UploadDialog dialog = new UploadDialog();
    dialog.setUrl("sail/UploadSvc");
    dialog.setPermittedExtensions(new String[]{"csv", "txt"});
    
    UrlParam param[] = new UrlParam[2];
    param[0] = new UrlParam("CollectionID", p.getId());
    param[1] = new UrlParam("UploadType", "AvailabilityData");
    dialog.setBaseParams(param);
    
    dialog.show();
    
    return;
   }
   
   final Panel collectionEditTab = new Panel();
   collectionEditTab.setAutoScroll(true);
   collectionEditTab.setTitle(actName.equals("add") ? "Add collection" : "Edit collection");
   collectionEditTab.setIconCls("tab-icon");
   collectionEditTab.setClosable(true);
   collectionEditTab.setAutoWidth(true);

   CollectionEditPanel repEditPanel = new CollectionEditPanel();
   repEditPanel.setTitle(actName.equals("add") ? "Add collection" : "Edit collection");

   repEditPanel.setObjectActionListener(new ObjectAction<SampleCollection>()
   {

    public void doAction(String done, SampleCollection lp)
    {
     appPanel.remove(collectionEditTab.getId());

     if(lp != null)
     {
      if("cancel".equals(done))
       return;

      if("add".equals(actName))
      {

       DataManager.getInstance().addCollection(lp, new AsyncCallback<Integer>()
       {

        public void onFailure(Throwable caught)
        {
         ErrorBox.showError("System error:<br />" + caught.getMessage());
        }

        public void onSuccess(Integer result)
        {
         if(result == null)
          ErrorBox.showError("Projection add failed");
        }
       });
      }
      else
      {

       DataManager.getInstance().updateCollection(lp, new AsyncCallback<Object>()
       {

        public void onFailure(Throwable caught)
        {
         ErrorBox.showError("System error:<br />" + caught.getMessage());
        }

        public void onSuccess(Object result)
        {

        }
       });
      }
     }
    }

    public void doMultyAction(String actName, List<SampleCollection> llp)
    {
    }
   });

   collectionEditTab.setPaddings(15);
   collectionEditTab.add(repEditPanel);

   appPanel.add(collectionEditTab);

   if("edit".equals(actName))
   {
    repEditPanel.setCollection( new SampleCollection(p) );
   }
   else
    repEditPanel.setCollection(new SampleCollection());

   appPanel.setActiveTab(collectionEditTab.getId());

  }

  public void doMultyAction(String actName, List<SampleCollection> lp)
  {
   int n=0;
   for( SampleCollection p : lp )
   {
    System.out.println("Selected["+(n++)+"]: "+p.getName()+" Action: "+actName);
   }
   
  }
 } 
 
 class CollectionInfoAction implements ObjectAction<SampleCollection>, LinkClickListener
 {
  CollectionInfoAction()
  {
   LinkManager.getInstance().addLinkClickListener("collectionInfo", this);
  }
  
  @Override
  public void doAction(String actName, SampleCollection kh)
  {
   final Panel collectionEditTab = new Panel();
   collectionEditTab.setAutoScroll(true);
   collectionEditTab.setTitle("Collection information");
   collectionEditTab.setIconCls("tab-icon");
   collectionEditTab.setClosable(true);
   collectionEditTab.setAutoWidth(true);
   
   collectionEditTab.add( new CollectionInfoPanel2( kh ) );
   
   appPanel.add(collectionEditTab);
   appPanel.setActiveTab(collectionEditTab.getId());
  }

  @Override
  public void doMultyAction(String actName, List<SampleCollection> lp)
  {
  }

  @Override
  public void linkClicked(String param)
  {
   int khId = Integer.parseInt(param);
   
   doAction(null, DataManager.getInstance().getCollection(khId) );
   
  }
  
 }

 class StudyInfoAction implements ObjectAction<Study>
 {

  @Override
  public void doAction(String actName, Study st)
  {
   final Panel studyEditTab = new Panel();
   studyEditTab.setAutoScroll(true);
   studyEditTab.setTitle("Study information");
   studyEditTab.setIconCls("tab-icon");
   studyEditTab.setClosable(true);
   studyEditTab.setAutoWidth(true);
   studyEditTab.setAutoScroll(true);
  
   studyEditTab.add( new StudyInfoPanel2( st ) );
   
   appPanel.add(studyEditTab);
   appPanel.setActiveTab(studyEditTab.getId());
  }

  @Override
  public void doMultyAction(String actName, List<Study> lp)
  {
  }
  
 }

 
 class StudyEditAction implements ObjectAction<Study>
 {

  public void doAction(final String actName, Study p)
  {
   if( "import".equals(actName) )
   {
    importRelations(p);
    return;
   }
   
   final Panel studyEditTab = new Panel();
   studyEditTab.setAutoScroll(true);
   studyEditTab.setTitle(actName.equals("add") ? "Add study" : "Edit study");
   studyEditTab.setIconCls("tab-icon");
   studyEditTab.setClosable(true);
   studyEditTab.setAutoWidth(true);

   StudyEditPanel studyEditPanel = new StudyEditPanel();
   studyEditPanel.setTitle(actName.equals("add") ? "Add study" : "Edit study");

   studyEditPanel.setObjectActionListener(new ObjectAction<Study>()
   {

    public void doAction(String done, Study lp)
    {
     appPanel.remove(studyEditTab.getId());

     if(lp != null)
     {
      if("cancel".equals(done))
       return;

      if("add".equals(actName))
      {

       DataManager.getInstance().addStudy(lp, new AsyncCallback<Integer>()
       {

        public void onFailure(Throwable caught)
        {
         ErrorBox.showError("System error:<br />" + caught.getMessage());
        }

        public void onSuccess(Integer result)
        {
         if(result == null)
          ErrorBox.showError("Projection add failed");
        }
       });
      }
      else
      {

       DataManager.getInstance().updateStudy(lp, new AsyncCallback<Object>()
       {

        public void onFailure(Throwable caught)
        {
         ErrorBox.showError("System error:<br />" + caught.getMessage());
        }

        public void onSuccess(Object result)
        {

        }
       });
      }
     }
    }

    public void doMultyAction(String actName, List<Study> llp)
    {
    }
   });

   studyEditTab.setPaddings(15);
   studyEditTab.add(studyEditPanel);

   appPanel.add(studyEditTab);

   if("edit".equals(actName))
   {
    studyEditPanel.setStudy( new Study(p) );
   }
   else
    studyEditPanel.setStudy(new Study());

   appPanel.setActiveTab(studyEditTab.getId());

  }

  public void doMultyAction(String actName, List<Study> lp)
  {
   int n=0;
   for( Study p : lp )
   {
    System.out.println("Selected["+(n++)+"]: "+p.getName()+" Action: "+actName);
   }
  }
  
  private void importRelations( final Study st )
  {
   SelectCollectionDialog dlg = new SelectCollectionDialog( new CollectionManager()
   {
    @Override
    public Collection<SampleCollection> getCollections()
    {
     return st.getCollections();
    }
    
    @Override
    public void removeDataChangeListener(DataChangeListener collectionList)
    {
    }
    
    @Override
    public void addDataChangeListener(DataChangeListener collectionList)
    {
    }
   });
   
   dlg.setActionListener( new ObjectAction<SampleCollection>()
   {
    @Override
    public void doAction(String actName, SampleCollection p)
    {
     if( "cancel".equals(actName) )
      return;
     
     UploadDialog dialog = new UploadDialog();
     dialog.setUrl("sail/UploadSvc");
     
     UrlParam param[] = new UrlParam[3];
     param[0] = new UrlParam("StudyID", st.getId());
     param[1] = new UrlParam("CollectionID", p.getId());
     param[2] = new UrlParam("UploadType", "Study2SampleRelation");
     dialog.setBaseParams(param);
     
     dialog.show();
    }

    @Override
    public void doMultyAction(String actName, List<SampleCollection> lp)
    {
    }
   });
   
   dlg.show();
  }
 } 

 
 
}
