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

package uk.ac.ebi.sail.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.sail.client.common.Annotation;
import uk.ac.ebi.sail.client.common.AnnotationShadow;
import uk.ac.ebi.sail.client.common.ClassifiableManager;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.ClassifierShadow;
import uk.ac.ebi.sail.client.common.ClientParameterAuxInfo;
import uk.ac.ebi.sail.client.common.CollectionShadow;
import uk.ac.ebi.sail.client.common.ExpressionRequestItem;
import uk.ac.ebi.sail.client.common.GroupRequestItem;
import uk.ac.ebi.sail.client.common.IDBunch;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterRequestItem;
import uk.ac.ebi.sail.client.common.ParameterShadow;
import uk.ac.ebi.sail.client.common.Projection;
import uk.ac.ebi.sail.client.common.ProjectionShadow;
import uk.ac.ebi.sail.client.common.Relation;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.RequestItem;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.Study;
import uk.ac.ebi.sail.client.common.StudyShadow;
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.common.Timer;
import uk.ac.ebi.sail.client.common.Classifier.Target;
import uk.ac.ebi.sail.client.ui.module.AdminPanel;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;

public class DataManager
{
 protected static final int COLLECTION_SUMMARY_CACHE_SIZE = 50;
 protected static final int STUDY_SUMMARY_CACHE_SIZE = 50;

 private static DataManager instance;
 
 private BackendServiceAsync async;
 private List<Parameter> parameters;
 
 private Collection<Classifier> classifierList;
 private List<Projection> projList;
 private List<SampleCollection> collectionList;
 private List<Study> studyList;
 
 private Collection<ExpressionRequestItem> expressions;
 
 final Map<Integer, Tag> tagMap = new HashMap<Integer, Tag>();
 
 private List<InitListener> lsnrs = new ArrayList<InitListener>();
 
 private List<DataChangeListener> prmLsnrs = new ArrayList<DataChangeListener>();
 private List<DataChangeListener> clsLsnrs = new ArrayList<DataChangeListener>();
 private List<DataChangeListener> prjLsnrs = new ArrayList<DataChangeListener>();
 private List<DataChangeListener> collectionLsnrs = new ArrayList<DataChangeListener>();
 private List<DataChangeListener> studyLsnrs = new ArrayList<DataChangeListener>();

 private Map<Integer,CacheItem> studySummaryCache = new TreeMap<Integer,CacheItem>();
 private Map<Integer,CacheItem> collectionSummaryCache = new TreeMap<Integer,CacheItem>();
 
 static final MessageBoxConfig wbConfig;
 
 static
 {
  wbConfig = new MessageBoxConfig();
  
  wbConfig.setButtons(false);
  wbConfig.setClosable(false);
  wbConfig.setIconCls("bigWaitIcon");
  wbConfig.setModal(true);
  wbConfig.setWidth(200);
 }
 
 static class CacheItem
 {
  Summary summary;
  List<AsyncCallback<Summary>> queue;

  public CacheItem(Summary summary)
  {
   this.summary = summary;
  }
 }
 
 public DataManager()
 {
  async = BackendService.Util.getInstance();
 }
 
 public static DataManager getInstance()
 {
  if( instance == null )
   instance = new DataManager();
  
  return instance;
 }

 public void init()
 {
  loadAll( new AsyncCallback<Object>(){

   public void onFailure(Throwable caught)
   {
    // TODO Auto-generated method stub
    
   }

   public void onSuccess(Object result)
   {
    for(InitListener il : lsnrs)
    {
     il.doInit();
    }
   }});
 }
 
 public void loadAll( final AsyncCallback<Object> clbk )
 {
  Timer.reportEvent("Start of data load");

  wbConfig.setTitle("Please wait");
  wbConfig.setMsg("Loading configuration");
  
  MessageBox.show(wbConfig);
//  MessageBox.wait("Loading...", "Loading configuration");

  loadConfiguration(new AsyncCallback<Map<String, String>>()
  {

   public void onFailure(Throwable caught)
   {
    MessageBox.hide();
    MessageBox.alert("Error!", "Error: " + caught.getMessage(), null);
    clbk.onFailure(caught);
   }

   public void onSuccess(Map<String, String> arg0)
   {

    MessageBox.hide();
    
    wbConfig.setTitle("Please wait");
    wbConfig.setMsg("Loading classifiers");
    
    MessageBox.show(wbConfig);

//    MessageBox.wait("Loading...", "Loading classifiers");

    loadClassifiers(new AsyncCallback<Collection<Classifier>>()
    {

     public void onFailure(Throwable caught)
     {
      MessageBox.hide();
      MessageBox.alert("Error!", "Error: " + caught.getMessage(), null);
      clbk.onFailure(caught);
     }

     public void onSuccess(Collection<Classifier> result)
     {
      Timer.reportEvent("Classifiers loaded");
      MessageBox.hide();
      
      wbConfig.setTitle("Please wait");
      wbConfig.setMsg("Loading projections");
      
      MessageBox.show(wbConfig);

//      MessageBox.wait("Loading...", "Loading projections");
      loadProjections(new AsyncCallback<List<ProjectionShadow>>()
      {

       public void onFailure(Throwable caught)
       {
        MessageBox.hide();
        MessageBox.alert("Error!", "Error: " + caught.getMessage(), null);
        clbk.onFailure(caught);
       }

       public void onSuccess(List<ProjectionShadow> result)
       {
        Timer.reportEvent("Projections loaded");
        MessageBox.hide();
        
        wbConfig.setTitle("Please wait");
        wbConfig.setMsg("Loading parameters");
        
        MessageBox.show(wbConfig);

//        MessageBox.wait("Loading...", "Loading parameters");
        loadParameters(new AsyncCallback<Collection<ParameterShadow>>()
        {

         public void onFailure(Throwable caught)
         {
          MessageBox.hide();
          MessageBox.alert("Error!", "Error: " + caught.getMessage(), null);
          clbk.onFailure(caught);
         }

         public void onSuccess(Collection<ParameterShadow> result)
         {
          MessageBox.hide();
          
          wbConfig.setTitle("Please wait");
          wbConfig.setMsg("Loading collections");
          
          MessageBox.show(wbConfig);

//          MessageBox.wait("Loading...", "Loading collections");
          loadCollections(new AsyncCallback<Collection<CollectionShadow>>()
          {

           public void onFailure(Throwable caught)
           {
            MessageBox.hide();
            MessageBox.alert("Error!", "Error: " + caught.getMessage(), null);
            clbk.onFailure(caught);
           }

           public void onSuccess(Collection<CollectionShadow> result)
           {
            MessageBox.hide();
            
            wbConfig.setTitle("Please wait");
            wbConfig.setMsg("Loading studies");
            
            MessageBox.show(wbConfig);

//            MessageBox.wait("Loading...", "Loading studies");
            
            loadStudies(new AsyncCallback<Collection<StudyShadow>>()
            {

             @Override
             public void onFailure(Throwable caught)
             {
              MessageBox.hide();
              MessageBox.alert("Error!", "Error: " + caught.getMessage(), null);
              clbk.onFailure(caught);
             }

             public void onSuccess(Collection<StudyShadow> result)
             {
              MessageBox.hide();
              
              wbConfig.setTitle("Please wait");
              wbConfig.setMsg("Loading studies");
              
              MessageBox.show(wbConfig);
              
              loadExpressions( new AsyncCallback<Collection<ExpressionRequestItem>>(){

               @Override
               public void onFailure(Throwable caught)
               {
                MessageBox.hide();
                MessageBox.alert("Error!", "Error: " + caught.getMessage(), null);
                clbk.onFailure(caught);
               }

               @Override
               public void onSuccess(Collection<ExpressionRequestItem> result)
               {
                MessageBox.hide();

                Timer.reportEvent("Data loaded");

                printContent();

                clbk.onSuccess(null);

               }
               } );
             }
            });
           }


          });

         }
        });
       }
      });

     }
    });
   }
  });
 }

 private void loadProjections( final AsyncCallback<List<ProjectionShadow>> clbk )
 {
  async.getProjections(new AsyncCallback<List<ProjectionShadow>>(){

   public void onFailure(Throwable caught)
   {
    clbk.onFailure(caught);
   }

   public void onSuccess(List<ProjectionShadow> result)
   {
    projList = new ArrayList<Projection>(result.size());
    
    for( ProjectionShadow pjs : result )
    {
     Projection pj = pjs.createProjection();
     
     if( pjs.getClassifiers() != null )
     {
      for( int cid : pjs.getClassifiers() )
      {
       for( Classifier cl : classifierList )
       {
        if( cl.getId() == cid )
        {
         pj.addClassifier(cl);
         break;
        }
       }
      }
      projList.add(pj);
     }
    }
    
    clbk.onSuccess(result);
   }});
 }

 private void loadCollections( final AsyncCallback<Collection<CollectionShadow>> clbk )
 {
  async.getCollections(new AsyncCallback<Collection<CollectionShadow>>(){

   public void onFailure(Throwable caught)
   {
    clbk.onFailure(caught);
   }

   public void onSuccess(Collection<CollectionShadow> result)
   {
    collectionList = new ArrayList<SampleCollection>(result.size());
    
    for( CollectionShadow rsp : result )
    {
     SampleCollection pj = rsp.createCollection();
     
     if( rsp.getAnnotations() != null )
     {
      for( AnnotationShadow ans : rsp.getAnnotations() )
      {
       Annotation an = ans.createAnnotation();
       an.setTag(tagMap.get(ans.getTag()));
       pj.addAnnotation(an);
      }
      
     }
     
     collectionList.add(pj);
    }
    
    clbk.onSuccess(result);
   }});
 }

 private void loadStudies( final AsyncCallback<Collection<StudyShadow>> clbk )
 {
  async.getStudies(new AsyncCallback<Collection<StudyShadow>>(){

   public void onFailure(Throwable caught)
   {
    clbk.onFailure(caught);
   }

   public void onSuccess(Collection<StudyShadow> result)
   {
    studyList = new ArrayList<Study>(result.size());
    
    for( StudyShadow rsp : result )
    {
     Study pj = rsp.createStudy();
     
     if( rsp.getAnnotations() != null )
     {
      for( AnnotationShadow ans : rsp.getAnnotations() )
      {
       Annotation an = ans.createAnnotation();
       an.setTag(tagMap.get(ans.getTag()));
       pj.addAnnotation(an);
      }
      
     }
     
     if( rsp.getCollections() != null )
     {
      boolean err = false;
      for( int cohId : rsp.getCollections() )
      {
       SampleCollection coh = getCollection(cohId);
       
       if( coh == null )
       {
        err = true;
        continue;
       }
       
       pj.addCollection(coh);
      }
      
      if( err )
       ErrorBox.showError("There studies that refer to not existing collections");
     }

     
     studyList.add(pj);
    }
    
    clbk.onSuccess(result);
   }});
 }
 
 private void loadConfiguration( final AsyncCallback<Map<String,String>> clbk )
 {
  async.getConfiguration(new AsyncCallback<Map<String,String>>(){

   public void onFailure(Throwable caught)
   {
    clbk.onFailure(caught);
   }

   public void onSuccess(Map<String, String> map)
   {
    ConfigManager.setDefaultConfiguration( map );
    
    clbk.onSuccess(map);
   }
  
  });
 }
 
 private void loadClassifiers( final AsyncCallback<Collection<Classifier>> clbk )
 {
  async.getClassifiers(new AsyncCallback<Collection<Classifier>>(){

   public void onFailure(Throwable caught)
   {
    clbk.onFailure(caught);
   }

   public void onSuccess(Collection<Classifier> result)
   {
    classifierList = result;

    tagMap.clear();
    for(Classifier cl : classifierList)
    {
     Collection<Tag> tgs = cl.getTags();
     if(tgs != null)
     {
      for(Tag t : tgs)
       tagMap.put(t.getId(), t);
     }
    }   
    
    clbk.onSuccess(result);
   
   }});
 }
 
 private void loadExpressions( final AsyncCallback<Collection<ExpressionRequestItem>> clbk )
 {
  async.getExpressions(new AsyncCallback<Collection<ExpressionRequestItem>>(){

   public void onFailure(Throwable caught)
   {
    clbk.onFailure(caught);
   }

   public void onSuccess(Collection<ExpressionRequestItem> result)
   {
    expressions=result;
    
    for( GroupRequestItem ge : result )
     updateExpression(ge);
    
    clbk.onSuccess(result);
   
   }});
 }

 private void updateExpression( GroupRequestItem  exprIt )
 {
  for( RequestItem ri : exprIt.getItems() )
  {
   if( ri instanceof ParameterRequestItem )
   {
    ParameterRequestItem pri = (ParameterRequestItem)ri;
    
    pri.setParameter( getParameter(pri.getParameterID()) );
   }
   else if( ri instanceof GroupRequestItem )
    updateExpression( (GroupRequestItem)ri );
  }
 }
 
 private void loadParameters( final AsyncCallback<Collection<ParameterShadow>> clbk )
 {
  async.getParameters(new AsyncCallback<Collection<ParameterShadow>>()
    {

     public void onFailure(Throwable arg0)
     {
      MessageBox.hide();
      MessageBox.alert("Error!", "Error: " + arg0.getMessage(), null);
      clbk.onFailure(arg0);
     }

     public void onSuccess(Collection<ParameterShadow> shdws)
     {
      Timer.reportEvent("Parameters get complete");

      Map<Integer, Parameter> paramMap = new HashMap<Integer, Parameter>();

      for(ParameterShadow ps : shdws)
      {
       Parameter p = ps.createParameter();
       p.setAuxInfo( new ClientParameterAuxInfo() );
       
       paramMap.put(ps.getId(), p );
      }
      
      Timer.reportEvent("Parameters put into the map");
      
      for(ParameterShadow ps : shdws)
      {
       Parameter mp = paramMap.get(ps.getId());

       if(mp == null)
       {
        MessageBox.alert("Error!", "Error: Invalid parameter structure. Parameter not found ID=" + ps.getId(), null);
        return;
       }

       if(ps.getInheritedParameters() != null)
       {
        for(int pid : ps.getInheritedParameters())
        {
         Parameter ip = paramMap.get(pid);

         if(mp == null)
         {
          MessageBox.alert("Error!", "Error: Invalid parameter structure. Parameter not found ID=" + pid, null);
          return;
         }

         mp.addInheritedParameter(ip);
         
         ((ClientParameterAuxInfo)ip.getAuxInfo()).addChild(mp);
        }
       }

       if(ps.getAnnotations() != null)
       {
        for(AnnotationShadow ans : ps.getAnnotations() )
        {
         Tag t = tagMap.get(ans.getTag());

         if(t == null)
         {
          MessageBox.alert("Error!", "Error: Invalid classifier structure. Tag not found ID=" + ans.getTag(), null);
          return;
         }

         Annotation an = ans.createAnnotation();
         an.setTag(t);
         
         mp.addAnnotation(an);
        }
       }

       if(ps.getTags() != null)
       {
        for(int tid : ps.getTags())
        {
         Tag t = tagMap.get(tid);

         if(t == null)
         {
          MessageBox.alert("Error!", "Error: Invalid parameter classification structure. Tag not found ID=" + tid, null);
          return;
         }

         mp.addClassificationTag(t);
        }
       }

       if(ps.getRelations() != null)
       {
        for(int[] rl : ps.getRelations())
        {
         Tag t = tagMap.get(rl[2]);

         if(t == null)
         {
          MessageBox.alert("Error!", "Error: Invalid classifier structure. Tag not found ID=" + rl[2], null);
          return;
         }

         Parameter tp = paramMap.get(rl[1]);
         if(tp == null)
         {
          MessageBox.alert("Error!", "Error: Invalid parameter structure. Parameter not found ID=" + rl[1], null);
          return;
         }

         Relation rel = new Relation();
         rel.setId(rl[0]);
         rel.setTargetParameter(tp);
         rel.setTag(t);

         mp.addRelation(rel);
        }
       }

      }

      Timer.reportEvent("Parameters structure complete");

      
      parameters = new ArrayList<Parameter>(paramMap.size());
      for(Parameter p : paramMap.values())
       parameters.add(p);

      Collections.sort(parameters, new Comparator<Parameter>()
      {
       @Override
       public int compare(Parameter o1, Parameter o2)
       {
        return o1.getId()-o2.getId();
       }
      });
      
      Timer.reportEvent("Parameters put into the list");

      clbk.onSuccess(shdws);
     }
    });
 }
 
 private void printContent()
 {
  System.out.println("Parameters: "+parameters.size());
  System.out.println("Classifiers: "+classifierList.size());
  System.out.println("Projections: "+projList.size());
  System.out.println("Collections: "+collectionList.size());
 }
 
 public void addInitListener( InitListener l )
 {
  lsnrs.add( l );
 }
 
 public Parameter getParameter( int id )
 {
  int low = 0;
  int high = parameters.size() - 1;

  while(low <= high)
  {
   int mid = (low + high) >>> 1;
   
   Parameter p = parameters.get(mid);
   
   int cmp = p.getId()-id;

   if(cmp < 0)
    low = mid + 1;
   else if(cmp > 0)
    high = mid - 1;
   else
    return p; // key found
  }
  
  return null; // key not found.
 }
 
 public void setCountContext( int studyId, final int clctId, final AsyncCallback<Void> cb )
 {
  AsyncCallback<Summary> asCB = new AsyncCallback<Summary>()
  {
   @Override
   public void onSuccess(final Summary arg0)
   {
    Summary[] smr=null;
    
    if( clctId != 0 )
     smr=arg0.getRelatedCounters();
    else
     smr=arg0.getTagCounters()[0].getRelatedCounters();
    
    Arrays.sort(smr);
    
    Summary tmpPC = new Summary();
    for( Parameter p : getParameters() )
    {
     tmpPC.setId(p.getId());
     
     int ind = Arrays.binarySearch(smr, tmpPC);
     if( ind >= 0 )
      ((ClientParameterAuxInfo)p.getAuxInfo()).setCountContext(smr[ind]);
     else
      ((ClientParameterAuxInfo)p.getAuxInfo()).setCountContext(null);
    }
    
    cb.onSuccess(null);
   }
   
   @Override
   public void onFailure(Throwable arg0)
   {
    cb.onFailure(arg0);
   }
  };
  
  if( clctId != 0 )
   getCollectionSummary(clctId, asCB);
  else if( studyId != 0 )
   getStudySummary(studyId, asCB);
  else
  {
   for( Parameter p : getParameters() )
    ((ClientParameterAuxInfo)p.getAuxInfo()).setCountContext(null);
   
   cb.onSuccess(null);
  }

 }
 
 public Collection<Parameter> getParameters()
 {
  return parameters;
 }
 
 public Collection<ExpressionRequestItem> getExpressions()
 {
  return expressions;
 }
 
 public Collection<Classifier> getClassifiers()
 {
  return classifierList;
 }
 
 public void addParameter( final Parameter p, final AsyncCallback<Integer> cbk )
 {
  async.addParameter( new ParameterShadow(p), new AsyncCallback<ParameterShadow>(){

   public void onFailure(Throwable caught)
   {
    cbk.onFailure(caught);
   }


   public void onSuccess(ParameterShadow result)
   {
    p.setId(result.getId());
    
    p.setVariables( result.getVariables() );
    p.setQualifiers(result.getQualifiers() );
    
    if( p.getRelations() != null )
    {
     int n=0;
     
     for( Relation rl : p.getRelations() )
     {
      for( int[] srl : result.getRelations() )
      {
       if( srl[1] == rl.getTargetParameter().getId() && srl[2] == rl.getTag().getId() )
       {
        rl.setId( srl[0] );
        n++;
        break;
       }
      }
     }

     if( p.getRelations().size() != n || result.getRelations().length != n )
     {
      ErrorBox.showError("Invalid relation structure. Parameter was not added");
      return;
     }
    }
    
    p.setAuxInfo( new ClientParameterAuxInfo() );
    parameters.add(p);

    if( prmLsnrs != null )
    {
     for( DataChangeListener lsnr  : prmLsnrs )
      lsnr.dataChanged();
    }
   }});
 }

 public void deleteParameter(Parameter p, AsyncCallback<Void> asyncCallback)
 {
  async.deleteParameter( p.getId() , new AsyncCallback<Void>(){

   public void onFailure(Throwable arg0)
   {
    // TODO Auto-generated method stub
    
   }

   @Override
   public void onSuccess(Void arg0)
   {
    // TODO Auto-generated method stub
    
   }});
  
 }
 
 public void updateParameter(final Parameter param, final AsyncCallback<ParameterShadow> asyncCallback)
 {
  async.updateParameter(new ParameterShadow(param), new AsyncCallback<ParameterShadow>(){

   public void onFailure(Throwable caught)
   {
    asyncCallback.onFailure(caught);
   }

   public void onSuccess(ParameterShadow result)
   {
    for( Parameter p : parameters )
    {
     if( p.getId() == result.getId() )
     {
      result.set(p);
      
      p.setName( param.getName() );
      p.setCode( param.getCode() );
      p.setDescription( param.getDescription() );
      
      p.setInheritedParameters( param.getInheritedParameters() );
      p.setClassificationTags( param.getClassificationTags() );
      
      p.setAnnotations( param.getAnnotations() );
      
      p.setRelations( null );
      
      if( param.getRelations() != null )
      {
       int n=0;
       
       for( Relation rl : param.getRelations() )
       {
        for( int[] srl : result.getRelations() )
        {
         if( srl[1] == rl.getTargetParameter().getId() && srl[2] == rl.getTag().getId() )
         {
          rl.setId( srl[0] );
          n++;
          
          p.addRelation(rl);
          break;
         }
        }
       }

       if( param.getRelations().size() != n || result.getRelations().length != n )
       {
        ErrorBox.showError("Invalid relation structure. Parameter was not added");
        return;
       }
      }
      
      
      if( prmLsnrs != null )
      {
       for( DataChangeListener lsnr  : prmLsnrs )
        lsnr.dataChanged();
      }
      
      asyncCallback.onSuccess(result);
      break;
     }
    }
    
   }});
 }

 public List<Projection> getProjections()
 {
  return projList;
 }

 public Projection getDefaultProjection()
 {
  if( projList != null && projList.size() > 0 )
   return projList.get(0);
   
  return null;
 }

 public void addParameterChangeListener(DataChangeListener lsnr)
 {
  prmLsnrs.add(lsnr);
 }
 
 public void addClassifierChangeListener(DataChangeListener lsnr)
 {
  clsLsnrs.add(lsnr);
 }
 
 public void addProjectionChangeListener(DataChangeListener lsnr)
 {
  prjLsnrs.add(lsnr);
 }

 public void removeParameterChangeListener(DataChangeListener lsnrt)
 {
  prmLsnrs.remove(lsnrt);
 }
 
 public void addStudyChangeListener(DataChangeListener lsnr)
 {
  studyLsnrs.add(lsnr);
 }

 public void removeStudyChangeListener(DataChangeListener lsnrt)
 {
  studyLsnrs.remove(lsnrt);
 }

 public void addCollectionChangeListener(DataChangeListener lsnr)
 {
  collectionLsnrs.add(lsnr);
 }

 public void removeCollectionChangeListener(DataChangeListener lsnrt)
 {
  collectionLsnrs.remove(lsnrt);
 }

 
 public void removeClassifierChangeListener(DataChangeListener lsnr)
 {
  clsLsnrs.remove(lsnr);
 }

 public void removeProjectionChangeListener(DataChangeListener lsnr)
 {
  prjLsnrs.remove(lsnr);
 }

 public void addClassifier(final Classifier cl, final AsyncCallback<ClassifierShadow> asyncCallback)
 {
  async.addClassifier( new ClassifierShadow(cl), new AsyncCallback<ClassifierShadow>(){

   public void onFailure(Throwable caught)
   {
    asyncCallback.onFailure(caught);
   }

   public void onSuccess(ClassifierShadow result)
   {
    if(result != null)
    {
     cl.setId(result.getId());
     
     cl.setTags(result.getTags());
     
     for( Tag t : result.getTags() )
      tagMap.put(t.getId(), t);
     
     classifierList.add(cl);

     if(clsLsnrs != null)
     {
      for(DataChangeListener lsnr : clsLsnrs)
       lsnr.dataChanged();
     }
    }
    
    asyncCallback.onSuccess(result);
   }});
 }

 
 public void updateClassifier(final Classifier clsf, final AsyncCallback<ClassifierShadow> asyncCallback)
 {
  async.updateClassifier(new ClassifierShadow(clsf), new AsyncCallback<ClassifierShadow>(){

   public void onFailure(Throwable caught)
   {
    asyncCallback.onFailure(caught);
   }

   public void onSuccess(final ClassifierShadow result)
   {
    if(result == null)
    {
     ErrorBox.showError("Classifier was not updated");
     return;
    }
    
    Classifier orig = null;

    for(Classifier p : classifierList)
    {
     if(p.getId() == result.getId())
     {
      orig = p;
      break;
     }
    }
    
    boolean wasTagsRemoved = false;
    
    if( orig.getTags() != null )
    {
     if( clsf.getTags() == null )
      wasTagsRemoved=true;
     else
     {
      for( Tag ot : orig.getTags() )
      {
       boolean found=false;
       for( Tag nt : clsf.getTags() )
       {
        if( ot.getId() == nt.getId() )
        {
         found=true;
         break;
        }
       }
       
       if( !found )
       {
        wasTagsRemoved=true;
        break;
       }
      }
     }
    }

    if( wasTagsRemoved )
    {
     loadAll(new AsyncCallback<Object>(){

      public void onFailure(Throwable caught)
      {
       asyncCallback.onFailure(caught);
      }

      public void onSuccess(Object res)
      {
       if(clsLsnrs != null)
       {
        for(DataChangeListener lsnr : clsLsnrs)
         lsnr.dataChanged();
       }

       if(prmLsnrs != null)
       {
        for(DataChangeListener lsnr : prmLsnrs)
         lsnr.dataChanged();
       }
       
       if(prjLsnrs != null)
       {
        for(DataChangeListener lsnr : prjLsnrs)
         lsnr.dataChanged();
       }
       
       asyncCallback.onSuccess(result);
      }});
     
     return;
    }
    
    orig.setName(result.getName());
    orig.setDescription(result.getDesc());
    orig.setTarget(result.getTarget());
    
    orig.setTags(null);
    
    if(result.getTags() != null)
    {
     for(Tag t : result.getTags())
     {
      Tag ot = tagMap.get(t.getId());

      if(ot == null)
      {
       ot = t;
       tagMap.put(t.getId(), t);
      }
      else
      {
       ot.setName( t.getName() );
       ot.setDescription(t.getDescription());
      }
      
      orig.addTag(ot);
     }
    }

   orig.setClassificationTags(clsf.getClassificationTags());

    if(clsLsnrs != null)
    {
     for(DataChangeListener lsnr : clsLsnrs)
      lsnr.dataChanged();
    }

    asyncCallback.onSuccess(result);

    }

   });
 }

 public void addProjection(final Projection lp, final AsyncCallback<Integer> asyncCallback)
 {
  async.addProjection(new ProjectionShadow(lp), new AsyncCallback<Integer>()
  {

   public void onFailure(Throwable caught)
   {
    asyncCallback.onFailure(caught);
   }

   public void onSuccess(Integer result)
   {
    if(result != null)
    {
     lp.setId(result);
     projList.add(lp);

     if(prjLsnrs != null)
     {
      for(DataChangeListener lsnr : prjLsnrs)
       lsnr.dataChanged();
     }
    }

    asyncCallback.onSuccess(result);

   }

  });
 }

 public void updateProjection(final Projection lp, final AsyncCallback<Void> asyncCallback)
 {
  async.updateProjection(new ProjectionShadow(lp), new AsyncCallback<Void>()
  {

   public void onFailure(Throwable caught)
   {
    asyncCallback.onFailure(caught);
   }

   public void onSuccess(Void result)
   {
    for(Projection op : projList)
    {
     if(op.getId() == lp.getId())
     {
      op.setName(lp.getName());
      op.setDescription(lp.getDescription());
      op.setClassifiers(lp.getClassifiers());

      break;
     }
    }

    if(prjLsnrs != null)
    {
     for(DataChangeListener lsnr : prjLsnrs)
      lsnr.dataChanged();
    }

    asyncCallback.onSuccess(result);
   }

  });
 }

 public SampleCollection getCollection( int id )
 {
  if( id < 0 )
  {
   int stID = (-id)/2;
   
   Study st = getStudy(stID);
   //*joern
  // return new SampleCollection(id, ((-id)%2==0)?"Eligible data for study: "+st.getName():"Data used in study "+st.getName());
   return new SampleCollection(id, ((-id)%2==0)?"Eligible data for study: "+st.getName():"Data used in study "+st.getName(),AdminPanel.role,AdminPanel.id,AdminPanel.id);
  }
  
  for(SampleCollection r : collectionList)
   if( r.getId() == id )
    return r;
  
  return null;
 }
 
 public Collection<SampleCollection> getCollections()
 {
  //System.out.println("wieso");
  
  return collectionList;
 }
 
 public Collection<Study> getStudies()
 {
  return studyList;
 }
 
 public Study getStudy( int stId )
 {
  for( Study st : studyList )
   if( st.getId() == stId )
    return st;
  
  return null;
 }


 public void addStudy(final Study lp, final AsyncCallback<Integer> asyncCallback)
 {
  async.addStudy(new StudyShadow(lp), new AsyncCallback<Integer>()
    {

     public void onFailure(Throwable caught)
     {
      asyncCallback.onFailure(caught);
     }

     public void onSuccess(Integer result)
     {
      if(result != null)
      {
       lp.setId(result);
       studyList.add(lp);

       if(studyLsnrs != null)
       {
        for(DataChangeListener lsnr : studyLsnrs)
         lsnr.dataChanged();
       }
      }

      asyncCallback.onSuccess(result);

     }

    });
 }

 public void updateStudy(final Study lp, final AsyncCallback<Object> asyncCallback)
 {
  async.updateStudy(new StudyShadow(lp), new AsyncCallback<Void>()
    {

     public void onFailure(Throwable caught)
     {
      asyncCallback.onFailure(caught);
     }

     public void onSuccess(Void result)
     {
      Study oldR=null;
      
      for(Study r :  studyList)
      {
       if( r.getId() == lp.getId() )
       {
        oldR=r;
        break;
       }
      }
      
      if( oldR == null )
       studyList.add(lp);
      else
      {
       oldR.setName( lp.getName() );
       oldR.setAnnotations( lp.getAnnotations() );
       oldR.setCollections( lp.getCollections() );
      }
      
      if(studyLsnrs != null)
      {
       for(DataChangeListener lsnr : studyLsnrs)
        lsnr.dataChanged();
      }

      asyncCallback.onSuccess(result);

     }

    });
 }

 
 public void addCollection(final SampleCollection lp, final AsyncCallback<Integer> asyncCallback)
 {
  //System.out.println("ID"+lp.getId()+"Name"+lp.getName());
  async.addCollection(new CollectionShadow(lp), new AsyncCallback<Integer>()
    {
	  

     public void onFailure(Throwable caught)
     {
        asyncCallback.onFailure(caught);
     }

     public void onSuccess(Integer result)
     {
      if(result != null)
      {
       lp.setId(result);
       collectionList.add(lp);

       if(collectionLsnrs != null)
       {
        for(DataChangeListener lsnr : collectionLsnrs)
         lsnr.dataChanged();
       }
      }

      asyncCallback.onSuccess(result);

     }

    });
 }

 public void updateCollection(final SampleCollection lp, final AsyncCallback<Object> asyncCallback)
 {
  async.updateCollection(new CollectionShadow(lp), new AsyncCallback<Void>()
    {
	  
     public void onFailure(Throwable caught)
     {
    	 
      asyncCallback.onFailure(caught);
     }

     public void onSuccess(Void result)
     {
    	  SampleCollection oldR=null;
      
      for(SampleCollection r :  collectionList)
      {
       if( r.getId() == lp.getId() )
       {
        oldR=r;
        break;
       }
      }
      
      if( oldR == null )
       collectionList.add(lp);
      else
      {
       oldR.setName( lp.getName() );
       oldR.setAnnotations( lp.getAnnotations() );
      }
      
      if(collectionLsnrs != null)
      {
       for(DataChangeListener lsnr : collectionLsnrs)
        lsnr.dataChanged();
      }

      asyncCallback.onSuccess(result);

     }

    });
 }

 public ClassifiableManager<Classifier> getClassifierManager(Target annotationClass)
 {
  return new ClassifierManager(annotationClass); // TODO May be here we need some caching ??
 }

 public ClassifiableManager<Parameter> getParameterManager()
 {
  return new ParameterManager(); // TODO May be here we need some caching ??
 }


 public ProjectionManager getProjectionManager()
 {
  return new ProjectionManagerImpl();
 }
 
 public CollectionManager getSampleCollectionManager()
 {
  return new CollectionManagerImpl();
 }
 
 public StudyManager getStudyManager()
 {
  return new StudyManagerImpl();
 }

 private class StudyManagerImpl implements StudyManager
 {

  public void addDataChangeListener(DataChangeListener lsnr)
  {
   DataManager.this.addStudyChangeListener(lsnr);
  }

  public Collection<Study> getStudies()
  {
   return DataManager.this.getStudies();
  }

  public void removeDataChangeListener(DataChangeListener lsnr)
  {
   DataManager.this.removeStudyChangeListener(lsnr);
  }
 }
 
 private class CollectionManagerImpl implements CollectionManager
 {

  public void addDataChangeListener(DataChangeListener lsnr)
  {
   DataManager.this.addCollectionChangeListener(lsnr);
  }

  public Collection<SampleCollection> getCollections()
  {
  // joernout();
   return DataManager.this.getCollections();
  }

  public void removeDataChangeListener(DataChangeListener lsnr)
  {
   DataManager.this.removeCollectionChangeListener(lsnr);
  }
 }
 
 private class ProjectionManagerImpl implements ProjectionManager
 {

  public void addDataChangeListener(DataChangeListener ls)
  {
   DataManager.this.addProjectionChangeListener(ls);
  }

  public Collection<Projection> getProjections()
  {
   return DataManager.this.getProjections();
  }

  public void removeDataChangeListener(DataChangeListener ls)
  {
   DataManager.this.removeProjectionChangeListener(ls);
  }
  
 }

 
 private class ParameterManager implements ClassifiableManager<Parameter>
 {

  public void addClassifiableChangeListener(DataChangeListener lsnr)
  {
   addParameterChangeListener(lsnr);
  }

  public void addProjectionChangeListener(DataChangeListener dataChangeListener)
  {
   DataManager.this.addProjectionChangeListener(dataChangeListener);
  }

  public Collection<Parameter> getClassifiable()
  {
   return DataManager.this.getParameters();
  }

  public Collection<Classifier> getClassifiers()
  {
   ArrayList<Classifier> clfs = new ArrayList<Classifier>( DataManager.this.getClassifiers().size() );
   
   for( Classifier c : DataManager.this.getClassifiers() )
   {
    if( c.getTarget() == Classifier.Target.PARAMETER )
     clfs.add(c);
   }
   
   return clfs;
  }

  public Projection getDefaultProjection()
  {
   // TODO Auto-generated method stub
   return null;
  }

  public List<Projection> getProjections()
  {
   ArrayList<Projection> prjs = new ArrayList<Projection>( DataManager.this.getProjections().size() );
   
   for( Projection pj: DataManager.this.getProjections() )
   {
    if( pj.getClassifiers().get(0).getTarget() == Classifier.Target.PARAMETER )
     prjs.add(pj);
   }
   
   return prjs;
  }

  public void removeClassifiableChangeListener(DataChangeListener lsnrt)
  {
   removeParameterChangeListener(lsnrt);
  }

  public void removeProjectionChangeListener(DataChangeListener projLsnr)
  {
   DataManager.this.removeProjectionChangeListener(projLsnr);
  }
  
 }

 
 private class ClassifierManager implements ClassifiableManager<Classifier>
 {
  private Target target;
  
  ClassifierManager( Target annotationClass )
  {
   target=annotationClass;
  }
  
  public void addClassifiableChangeListener(DataChangeListener lsnr)
  {
   DataManager.this.addClassifierChangeListener(lsnr);
  }

  public void addProjectionChangeListener(DataChangeListener dataChangeListener)
  {
   DataManager.this.addProjectionChangeListener(dataChangeListener);
  }

  public Collection<Classifier> getClassifiable()
  {
   if( target == null )
    return DataManager.this.getClassifiers();
   
   ArrayList<Classifier> clfs = new ArrayList<Classifier>( DataManager.this.getClassifiers().size() );
   
   for( Classifier c : DataManager.this.getClassifiers() )
   {
    if( c.getTarget() == target )
     clfs.add(c);
   }
   
   return clfs;
  }

  public Collection<Classifier> getClassifiers()
  {
   ArrayList<Classifier> clfs = new ArrayList<Classifier>( DataManager.this.getClassifiers().size() );
   
   for( Classifier c : DataManager.this.getClassifiers() )
   {
    if( c.getTarget() == Classifier.Target.CLASSIFIER )
     clfs.add(c);
   }
   
   return clfs;
  }

  public Projection getDefaultProjection()
  {
   return null;
  }

  public List<Projection> getProjections()
  {
   ArrayList<Projection> prjs = new ArrayList<Projection>( DataManager.this.getProjections().size() );
   
   for( Projection pj: DataManager.this.getProjections() )
   {
    if( pj.getClassifiers().get(0).getTarget() == Classifier.Target.CLASSIFIER )
     prjs.add(pj);
   }
   
   return prjs;
  }

  public void removeClassifiableChangeListener(DataChangeListener lsnrt)
  {
   DataManager.this.removeClassifierChangeListener(lsnrt);
  }

  public void removeProjectionChangeListener(DataChangeListener projLsnr)
  {
   DataManager.this.removeProjectionChangeListener(projLsnr);
  }
  
 }


 public void selectIDs(ReportRequest request, final AsyncCallback<IDBunch[]> asyncCallback)
 {
  async.getIDs(request, asyncCallback);
 }

 public void getCollectionSummary(final int iD, final AsyncCallback<Summary> asyncCallback)
 {
//  if( collectionSummaryLock )
//  
//  collectionSummaryLock = true;
  
  CacheItem cchItm = collectionSummaryCache.get(iD);
  

  if( cchItm != null )
  {
   if( cchItm.summary != null )
   {
    asyncCallback.onSuccess(cchItm.summary);
    return;
   }

   if( cchItm.queue == null )
    cchItm.queue = new ArrayList<AsyncCallback<Summary>>(5);
   
   cchItm.queue.add(asyncCallback);
   return;
  }
  
  collectionSummaryCache.put(iD, new CacheItem(null) );

  
  MessageBox.wait("Requesting server...", "Requesting server for collection");

  AsyncCallback<Summary> cb = new AsyncCallback<Summary>(){

   @Override
   public void onFailure(Throwable arg0)
   {
    MessageBox.hide();
    ErrorBox.showError("System error:<br>"+arg0.getLocalizedMessage());

    asyncCallback.onFailure(arg0);

    CacheItem cchItm = collectionSummaryCache.get(iD);
    if( cchItm.queue != null )
    {
     for(AsyncCallback<Summary> cb : cchItm.queue)
      cb.onFailure(arg0);

     cchItm.queue = null;
    }   
   }

   @Override
   public void onSuccess(Summary arg0)
   {
    MessageBox.hide();
    if( collectionSummaryCache.size() >= COLLECTION_SUMMARY_CACHE_SIZE )
     collectionSummaryCache.remove(collectionSummaryCache.keySet().iterator().next());
    
    CacheItem cchItm = collectionSummaryCache.get(iD);
    cchItm.summary=arg0;
    
    asyncCallback.onSuccess(arg0);
    
    if( cchItm.queue != null )
    {
     for( AsyncCallback<Summary> cb : cchItm.queue )
      cb.onSuccess(arg0);
     
     cchItm.queue=null;
    }
    
   }};
  
   async.getCollectionSummary(iD, cb);
 }

 public void getStudySummary(final int stID, final AsyncCallback<Summary> asCB)
 {
  CacheItem cchItm = studySummaryCache.get(stID);
  

  if( cchItm != null )
  {
   if( cchItm.summary != null )
   {
    asCB.onSuccess(cchItm.summary);
    return;
   }

   if( cchItm.queue == null )
    cchItm.queue = new ArrayList<AsyncCallback<Summary>>(5);
   
   cchItm.queue.add(asCB);
   return;
  }
  
  studySummaryCache.put(stID, new CacheItem(null) );

  
  MessageBox.wait("Requesting server...", "Requesting server for study");

  AsyncCallback<Summary> cb = new AsyncCallback<Summary>(){

   @Override
   public void onFailure(Throwable arg0)
   {
    MessageBox.hide();
    ErrorBox.showError("System error:<br>"+arg0.getLocalizedMessage());
    asCB.onFailure(arg0);

    CacheItem cchItm = studySummaryCache.get(stID);
    if( cchItm.queue != null )
    {
     for(AsyncCallback<Summary> cb : cchItm.queue)
      cb.onFailure(arg0);

     cchItm.queue = null;
    }   
   }

   @Override
   public void onSuccess(Summary arg0)
   {
    MessageBox.hide();
    if( studySummaryCache.size() >= STUDY_SUMMARY_CACHE_SIZE )
     studySummaryCache.remove(studySummaryCache.keySet().iterator().next());
    
    CacheItem cchItm = studySummaryCache.get(stID);
    cchItm.summary=arg0;
    
    if( arg0.getRelatedCounters() != null )
    {
     for( Summary cohsm : arg0.getRelatedCounters() )
      collectionSummaryCache.put(cohsm.getId(), new CacheItem(cohsm));
    }
    
    for( int i=1; i <=2; i++ )
    {
     Summary pseCoh = arg0.getTagCounters()[i];
     collectionSummaryCache.put(pseCoh.getId(), new CacheItem(pseCoh));
    }
    
    
    asCB.onSuccess(arg0);
    
    if( cchItm.queue != null )
    {
     for( AsyncCallback<Summary> cb : cchItm.queue )
      cb.onSuccess(arg0);
     
     cchItm.queue=null;
    }
   }};
  
   async.getStudySummary(stID, cb);
 }

/* public id getID(AdminPanel.id)
 {
  int i =1;
  return i;
 }
 */
 
}
