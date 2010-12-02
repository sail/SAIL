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

package uk.ac.ebi.sail.server.data;

// TODO export visible parameters
// TODO header with only parameter names in case of single variable
// TODO Structured description panel change size
// TODO Imported parameters sanity checks (Code uniq, var/qual names uniq) 
// TODO Validate inheritance structure during parameters import
// TODO Sample IDs export
// TODO Projection not always switching (probably only new)
// TODO make [ANY] as last column
// TODO add [CODE] to data import header for overlaying parts (due to inheritance)
// TODO Hierarchy parameters representation
// TODO "Data set" object
// TODO Data set dependent relations
// TODO Fix scroll on collections panel
// TODO Discuss using of related Parameters
// TODO implement classifier type
// TODO Horizontal report tree
// TODO use set comparator in parameterUpdate
// TODO make transaction mechanism
// TODO establish concurrency control
// TODO OWL export
// TODO Think and discuss complementary relations
// TODO mass add tag
// TODO auto sized tabs

/*
 pokovyryalas' v tvoey novoi versii SAILa:
1. na BMI i na nekotoryx drugix parametrax knopka Add enumerations ne rabotat. Vidimo eto proixodit esli net enumerations, mozhet ee luchshe togda dezaktivirovat' avtomaticheski tam gde net enumerations?
2 bylo by udobnee esli by v sostavlennom uzhe fil'tre mozhno bylo by stroki mestami menyat'. 
3.Knopka "select with filter" - ne rabotaet, mozhet ee poka ubrat'? 
4. kogda popadaesh' v okno s Add enumerations, tam mozhno srazu sdelat' pervuyu stroku selcetd. shtoby odnim clickom men'she bylo.
6. est' mnogo parametrov gde chislo obrazcov - 69. Eti parametry sostavlyayut chast' MetS? ili oni prishli vmeste s MolOBB obrazcami? Esli oni ne vxodyat v collated MetS, to togda ix luchshe ne delat' chast'yu MetS. Eto ne znachit shto v SAILe shto-to nado menyat'. Eto mozhno prosto ob'yasnyat' po drugomu, govorit' shto u SAILs est' svoi slovar' kotoryi naraschivaetsa po mere ostupleniya annotirovannyx obrazcov iz samyx raznyx istochnikov.
7. zadala slozhnyi fil'tr: Collection.name ->BMI->Transcriptomics data.Available->Smoking status. Status->Smoking quantity. Poluchila report. Polnst'yu ego uvidet' ne poluchaetsa: net scrolla. i vysvetilas' nadpis' "Error on page" v nizhnem levom uglu.
9. eto v prodolzhenii zamechaniya 2. (sm vyshe), redaktirovanie query (copirovanie, soxranenie, dobavlenie fil'trov v uzhe skonstruirovannoe query, dopustim gde-to v seredine, perestanovka strok v query) i umenie soxranyat' reports k primeru v .xls bylo by och. polezno mne kazhetsa
10. po povodu classifierov, inherited parametrov i vsego ostal'nogo formalizma. Ty vvyol etot formalizm shtoby oblegchit' sebe zadachi skladirovaniya dannyx i poiska po dannym. Ne dlya togo, shtoby pol'zovateli nachali pol'zovat'sa etim formalizmom, poetomu v front end luchshe izbegat' vsego shto ne svyazano s podschotom, sostavleniem fil'trov i redaktirovaniem.
11. parameter tree - vidimo poka ni k chemu, vozmozhno budet imet' smysl esli poyavyatsa otnosheniya mezhdu parametrami,. Poka ix net, pokazyvat' tam osobo nechego. 
 * */


import java.beans.XMLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import uk.ac.ebi.sail.client.common.AlternativeRequestItem;
import uk.ac.ebi.sail.client.common.Annotation;
import uk.ac.ebi.sail.client.common.AnnotationShadow;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.ClassifierManagementException;
import uk.ac.ebi.sail.client.common.ClassifierShadow;
import uk.ac.ebi.sail.client.common.CollectionManagementException;
import uk.ac.ebi.sail.client.common.CollectionShadow;
import uk.ac.ebi.sail.client.common.ComplexFilter;
import uk.ac.ebi.sail.client.common.ComplexFilteredRequestItem;
import uk.ac.ebi.sail.client.common.EnumFilteredRequestItem;
import uk.ac.ebi.sail.client.common.ExpressionRequestItem;
import uk.ac.ebi.sail.client.common.GroupRequestItem;
import uk.ac.ebi.sail.client.common.IDBunch;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterFormat;
import uk.ac.ebi.sail.client.common.ParameterManagementException;
import uk.ac.ebi.sail.client.common.ParameterPart;
import uk.ac.ebi.sail.client.common.ParameterRequestItem;
import uk.ac.ebi.sail.client.common.ParameterShadow;
import uk.ac.ebi.sail.client.common.ParseException;
import uk.ac.ebi.sail.client.common.PartRequestItem;
import uk.ac.ebi.sail.client.common.Projection;
import uk.ac.ebi.sail.client.common.ProjectionManagementException;
import uk.ac.ebi.sail.client.common.ProjectionShadow;
import uk.ac.ebi.sail.client.common.Qualifier;
import uk.ac.ebi.sail.client.common.Relation;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.RequestItem;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.common.StudyManagementException;
import uk.ac.ebi.sail.client.common.StudyShadow;
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.client.common.Variant;
import uk.ac.ebi.sail.client.common.Variable.Type;
import uk.ac.ebi.sail.server.BackendConfigurationManager;
import uk.ac.ebi.sail.server.SSParameterInfo;
import uk.ac.ebi.sail.server.util.Counter;
import uk.ac.ebi.sail.server.util.SetComparator;
import uk.ac.ebi.sail.server.util.StringUtil;

import com.pri.log.Log;
import com.pri.log.Logger;
import com.pri.util.collection.ArrayIntList;
import com.pri.util.collection.IntList;
import com.pri.util.collection.IntMap;
import com.pri.util.collection.IntTreeMap;
import com.pri.util.stream.StringInputStream;

public class DataManager
{
 private static final String TBL_PARAMETER = "parameter";
 private static final String TBL_PART = "part";
 private static final String TBL_VARIANT = "variant";
 private static final String TBL_INHERITED = "inherited";
 private static final String TBL_PARAMETER_CLASSIFICATION = "parameter_classification";
 private static final String TBL_PARAMETER_ANNOTATION = "parameter_annotation";
 private static final String TBL_CLASSIFIER = "classifier";
 private static final String TBL_TAG = "tag";
 private static final String TBL_RELATION = "relation";
 private static final String TBL_CLASSIFIER_CLASSIFICATION = "classifier_classification";
 private static final String TBL_PROJECTION = "projection";
 private static final String TBL_PROJECTION_CONTENT = "projection_content";
 private static final String TBL_RECORD = "record";
 private static final String TBL_RECORD_CONTENT = "record_content";
 private static final String TBL_COLLECTION = "collection";
 private static final String TBL_COLLECTION_ANNOTATION = "collection_annotation";
 private static final String TBL_STUDY = "study";
 private static final String TBL_STUDY_ANNOTATION = "study_annotation";
 private static final String TBL_RECORD_IN_STUDY = "record_in_study";
 private static final String TBL_COLLECTION_IN_STUDY = "collection_in_study";
 private static final String TBL_EXPRESSION = "expression";
 private static final String TBL_EXPRESSION_CONTENT = "expression_content";

 private static final String FLD_ID="ID";
 private static final String FLD_NAME="Name";
 private static final String FLD_DESCRIPTION="Description";
 private static final String FLD_CODE="Code";
 private static final String FLD_TYPE="Type";
 private static final String FLD_PREDEFINED="Predefined";
 private static final String FLD_PARAMETER_ID="ParameterID";
 private static final String FLD_PART_ID="PartID";
 private static final String FLD_HOST_PARAM_ID="HostParameterID";
 private static final String FLD_TARGET_PARAM_ID="TargetParameterID";
 private static final String FLD_ALLOW_MULTY = "Multiple";
 private static final String FLD_MANDATORY = "Mandatory";
 private static final String FLD_CLASSIFIER_ID = "ClassifierID";
 private static final String FLD_PROJECTION_ID = "ProjectionID";
 private static final String FLD_ORDER = "ClassifierOrder";
 private static final String FLD_TAG_ID = "TagID";
 private static final String FLD_COUNT = "Count";
 private static final String FLD_COLLECTION_RECORD_ID="CollectionRecordID";
 private static final String FLD_COLLECTION_ID="CollectionID";
 private static final String FLD_STUDY_ID="StudyID";
 private static final String FLD_RECORD_ID="RecordID";
 private static final String FLD_ENUM_VALUE="EnumValue";
 private static final String FLD_INT_VALUE="ValueInt";
 private static final String FLD_REAL_VALUE="ValueReal";
 private static final String FLD_ANNOT_TEXT="AnnotationText";
 private static final String FLD_VARI_CODING="Coding";
 private static final String FLD_TARGET="Target";
 private static final String FLD_LAST_UPDATE="UpdateTime";
 private static final String FLD_POST_STUDY="PostStudy";
 private static final String FLD_EXPRESSION_ID="ExpressionID";
 private static final String FLD_SUBEXPRESSION_ID="SubexpressionID";
 private static final String FLD_EXPRESSION_DEPTH="Depth";
 private static final String FLD_EXPRESSION_FILTER="Filter";

 static final String QUALIFIER_TYPE="QUALIFIER";
 static final String SAMPLE_ID_COL="SAMPLE.ID";
 
 private static final String insertVariantSQL = "INSERT INTO "+TBL_VARIANT+" ("+FLD_PART_ID+','+FLD_NAME+','
 +FLD_VARI_CODING+','+FLD_PREDEFINED+") VALUES (?,?,?,?)";
 
 private static final String insertPartSQL = "INSERT INTO " + TBL_PART + " ("+FLD_PARAMETER_ID+','+ FLD_NAME + ',' + FLD_DESCRIPTION + ',' + FLD_TYPE
 + ',' + FLD_PREDEFINED + ',' + FLD_MANDATORY + ") VALUES (?,?,?,?,?,?)";
 
 private static final String deleteVariantSQL = "DELETE FROM " + TBL_VARIANT + " WHERE "+FLD_ID+"=?";
 
 private static final String updatePartSQL="UPDATE "+TBL_PART+" SET "+FLD_NAME+"=?,"+FLD_DESCRIPTION+"=?,"
 +FLD_PREDEFINED+"=?,"+FLD_MANDATORY+"=? WHERE "+FLD_ID+"=?";
 
 private static final String updateVariantSQL="UPDATE "+TBL_VARIANT+" SET "+FLD_NAME+"=?,"+FLD_PART_ID+"=?,"+FLD_VARI_CODING+"=?,"
 +FLD_PREDEFINED+"=? WHERE "+FLD_ID+"=?";

 private static final String insertInheritedSQL="INSERT INTO "+TBL_INHERITED+" ("+FLD_HOST_PARAM_ID+","+FLD_TARGET_PARAM_ID+") VALUES (?,?)";
 
 private static final String deleteInheritedSQL="DELETE FROM "+TBL_INHERITED+" WHERE "+FLD_HOST_PARAM_ID+"=? AND "+FLD_TARGET_PARAM_ID+"=?";
 
 private static final String insertParameterTagSQL="INSERT INTO "+TBL_PARAMETER_CLASSIFICATION+" ("+FLD_PARAMETER_ID+","+FLD_TAG_ID+") VALUES (?,?)";
 
 private static final String deleteParameterTagSQL="DELETE FROM "+TBL_PARAMETER_CLASSIFICATION+" WHERE "+FLD_PARAMETER_ID+"=? AND "+FLD_TAG_ID+"=?";
 
 private static final String insertRelationSQL="INSERT INTO "+TBL_RELATION+" ("+FLD_HOST_PARAM_ID+","+FLD_TARGET_PARAM_ID+","+FLD_TAG_ID+") VALUES (?,?,?)";
 
 private static final String deleteRelationSQL="DELETE FROM "+TBL_RELATION+" WHERE "+FLD_ID+"=?";
 
 private static final String insertClassifierSQL="INSERT INTO "+TBL_CLASSIFIER+" ("+FLD_NAME+','+FLD_DESCRIPTION+','+FLD_ALLOW_MULTY
 +','+FLD_MANDATORY+','+FLD_TARGET+") VALUES (?,?,?,?,?)";
 
 private static final String insertTagSQL="INSERT INTO "+TBL_TAG+" ("+FLD_NAME+','+FLD_DESCRIPTION+','+FLD_CLASSIFIER_ID+") VALUES (?,?,?)";
 
 private static final String insertClassifierClassificationSQL="INSERT INTO "+TBL_CLASSIFIER_CLASSIFICATION+" ("+FLD_CLASSIFIER_ID+','+FLD_TAG_ID+") VALUES (?,?)";
 
 private static final String updateClassifierSQL="UPDATE "+TBL_CLASSIFIER+" SET "+FLD_NAME+"=?,"+FLD_DESCRIPTION+"=?,"
 +FLD_ALLOW_MULTY+"=?,"+FLD_MANDATORY+"=?,"+FLD_TARGET+"=? WHERE "+FLD_ID+"=?";
 
 private static final String deleteTagSQL="DELETE FROM "+TBL_TAG+" WHERE "+FLD_ID+"=?";
 
 private static final String updateTagSQL="UPDATE "+TBL_TAG+" SET "+FLD_NAME+"=?,"+FLD_DESCRIPTION+"=? WHERE "+FLD_ID+"=?";
 
 private static final String deleteAllClassifierClassificationSQL="DELETE FROM "+TBL_CLASSIFIER_CLASSIFICATION+" WHERE "+FLD_CLASSIFIER_ID+"=?";
 
 private static final String insertProjectionSQL="INSERT INTO "+TBL_PROJECTION+" ("+FLD_NAME+','+FLD_DESCRIPTION+") VALUES (?,?)";
 
 private static final String insertProjectionContentSQL="INSERT INTO "+TBL_PROJECTION_CONTENT+" ("+FLD_PROJECTION_ID+','+FLD_CLASSIFIER_ID+','+FLD_ORDER+") VALUES (?,?,?)";
 
 private static final String updateProjectionSQL="UPDATE "+TBL_PROJECTION+" SET "+FLD_NAME+"=?, "+FLD_DESCRIPTION+"=? WHERE "+FLD_ID+"=?";
 
 private static final String insertStudySQL="INSERT INTO "+TBL_STUDY+" ("+FLD_NAME+","+FLD_LAST_UPDATE+") VALUES (?,?)";
 
 private static final String insertStudyAnnotationSQL="INSERT INTO "+TBL_STUDY_ANNOTATION+" ("+FLD_STUDY_ID+','+FLD_TAG_ID+','+FLD_ANNOT_TEXT+") VALUES (?,?,?)";
 
 private static final String insertStudyCollectionsSQL="INSERT INTO "+TBL_COLLECTION_IN_STUDY+" ("+FLD_STUDY_ID+','+FLD_COLLECTION_ID+") VALUES (?,?)";

 private static final String insertStudyRecordSQL="INSERT INTO "+TBL_RECORD_IN_STUDY+" ("+FLD_RECORD_ID+','+FLD_STUDY_ID+','+FLD_POST_STUDY+") VALUES (?,?,?)";

 private static final String deleteStudyRecordSQL="DELETE FROM "+TBL_RECORD_IN_STUDY+" WHERE "+FLD_RECORD_ID+"=? AND "+FLD_STUDY_ID+"=? AND "+FLD_POST_STUDY+"=?";
 
 private static final String updateStudySQL="UPDATE "+TBL_STUDY+" SET "+FLD_NAME+"=? WHERE "+FLD_ID+"=?";
 
 private static final String deleteStudyAnnotationsSQL="DELETE FROM "+TBL_STUDY_ANNOTATION+" WHERE "+FLD_STUDY_ID+"=?";

 private static final String deleteStudyCollectionsSQL="DELETE FROM "+TBL_COLLECTION_IN_STUDY+" WHERE "+FLD_STUDY_ID+"=?";

 private static final String insertCollectionSQL="INSERT INTO "+TBL_COLLECTION+" ("+FLD_NAME+","+FLD_LAST_UPDATE+") VALUES (?,?)";
 
 private static final String insertCollectionAnnotationSQL="INSERT INTO "+TBL_COLLECTION_ANNOTATION+" ("+FLD_COLLECTION_ID+','+FLD_TAG_ID+','+FLD_ANNOT_TEXT+") VALUES (?,?,?)";
 
 private static final String updateCollectionSQL="UPDATE "+TBL_COLLECTION+" SET "+FLD_NAME+"=? WHERE "+FLD_ID+"=?";
 
 private static final String deleteCollectionAnnotationsSQL="DELETE FROM "+TBL_COLLECTION_ANNOTATION+" WHERE "+FLD_COLLECTION_ID+"=?";
 
 private static final String deleteParameterAnnotationsSQL="DELETE FROM "+TBL_PARAMETER_ANNOTATION+" WHERE "+FLD_PARAMETER_ID+"=?";
 
 private static final String insertParameterAnnotationsSQL= "INSERT INTO "+TBL_PARAMETER_ANNOTATION +" ("+FLD_PARAMETER_ID+','+FLD_TAG_ID+','+FLD_ANNOT_TEXT+") VALUES (?,?,?)";

 
// private final String genotypeParam = "Gen:GENDT";
// private RowProcessor genotypeProcessor;
 
 private List<RowProcessor> tagParameters = new ArrayList<RowProcessor>();
 
 static DataManager instance ;
 
 static Logger logger = 
  Log.getLogger(DataManager.class);
 
 private DataSource dSrc;
 
 private IntMap<Parameter> params = new IntTreeMap<Parameter>();
 private Map<String,Parameter> paramCodeMap = new TreeMap<String,Parameter>();
 
 private IntMap<Classifier> classifiers = new IntTreeMap<Classifier>();
 private IntMap<ParameterPart> parts = new IntTreeMap<ParameterPart>();
 private IntMap<Tag> tags = new IntTreeMap<Tag>();
 private IntMap<SampleCollection> collections = new IntTreeMap<SampleCollection>();
 private List<ExpressionRequestItem> expressions = new ArrayList<ExpressionRequestItem>();
 
 private Collection<ParameterShadow> paramList;
 private Collection<Classifier> classifiersList;
 private List<ProjectionShadow> projectionList=new ArrayList<ProjectionShadow>(20);
 private List<CollectionShadow> collectionList=new ArrayList<CollectionShadow>(30);
 private List<StudyShadow> studyList=new ArrayList<StudyShadow>(20);
 private List<Record> data;
 
 private WeakHashMap<Integer, Summary> collectionSummaryCache=new WeakHashMap<Integer, Summary>();
 private WeakHashMap<Integer, Summary> studySummaryCache=new WeakHashMap<Integer, Summary>();
 
// private ObjectRecycler<IntMap<Void>> intMapDepot = new ObjectRecycler<IntMap<Void>>(4);
 
 public DataManager(BackendConfigurationManager defaultCfg)
 {
  if( logger == null )
   logger = Log.getLogger(DataManager.class);
  
  try
  {
   dSrc = setupDataSource( defaultCfg );
   
//   dSrc = setupDataSource("jdbc:mysql://darksite/sail");
//   dSrc = setupDataSource("jdbc:mysql://localhost/sail");
//     dSrc = setupDataSource("jdbc:mysql://localhost/sail_test1");
//   dSrc = setupDataSource("jdbc:mysql://mysql-sail.ebi.ac.uk:4188/sail1");
   
   
   rebuildStructure();
   loadData();
   prepareCounts();
  }
  catch(Exception e)
  {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }
 }
 
 private void updateStructure()
 {
  rebuildStructure();
  prepareCounts();
 }
 
 private void rebuildStructure()
 {
  loadStructure();
  
  paramList = new ArrayList<ParameterShadow>();

  paramCodeMap.clear();
  tagParameters.clear();
  
  for(Parameter p : params.values())
  {
   SSParameterInfo ssp = null;
   if( p.getAuxInfo() != null )
    ssp = (SSParameterInfo)p.getAuxInfo();
   else
   {
    ssp = new SSParameterInfo();
    p.setAuxInfo(ssp);
   }
   
   paramCodeMap.put(p.getCode(), p);
   
   ParameterShadow shw =new ParameterShadow(p);
   paramList.add(shw);
   ssp.setShadow(shw);
   
   if( p.getInheritedParameters() != null )
   {
    for( Parameter ip : p.getInheritedParameters() )
    {
     SSParameterInfo ihssp = null;
     if( ip.getAuxInfo() != null )
      ihssp = (SSParameterInfo)ip.getAuxInfo();
     else
     {
      ihssp = new SSParameterInfo();
      ip.setAuxInfo(ihssp);
     }

     ihssp.addChildren(p);
    }
   }

  }
  

 classifiersList = new ArrayList<Classifier>( classifiers.values() );
 }
 
 public static DataManager getInstance()
 {
  return instance;
 }
 
 private static DataSource setupDataSource(BackendConfigurationManager defaultCfg) 
 {
  BasicDataSource ds = new BasicDataSource();

  ds.setDriverClassName(defaultCfg.getDBDriverClass());
  ds.setUsername(defaultCfg.getDBUserName());
  ds.setPassword(defaultCfg.getDBPassword());
  ds.setUrl(defaultCfg.getConnectionURL());

  ds.setTimeBetweenEvictionRunsMillis(10000);
  
  return ds;
 }
 
 public Parameter getParameter( int id )
 {
  return params.get(id);
 }
 
 public Collection<ParameterShadow> getParameters()
 {
  return paramList;
 }
 
 public Collection<Classifier> getClassifiers()
 {
  return classifiersList;
 }
 
 protected boolean matchPattern(int[] cls, Record row )
 {
  if( cls == null || cls.length == 0 )
   return false;
  
  for(int j = 0; j < cls.length; j++)
  {
   if( ! row.hasPart(cls[j]) )
    return false;
  }
  
  return true;
 }
 
 
 private void prepareCounts()
 {
//  Parameter genP=null;
  
  for( Parameter p : params.values() )
  {
   
   SSParameterInfo sspi = (SSParameterInfo)p.getAuxInfo();
   
   int[] pat = sspi.getPattern();
   if( pat == null )
   {
    pat = prepareParameterPattern(p);

    sspi.setPattern( pat );
   }
   
   int n=0;
   for( Record r : data )
   {
    if( matchPattern(pat, r) )
     n++;
   }
   
   p.setRecordsCount(n);
   sspi.getShadow().setRecordsCount(n);
   
//   if( genotypeParam.equals(p.getCode()) )
//    genP=p;
   
   Collection<Variable> pvr = p.getVariables();
   
   if( pvr != null )
   {
    for( Variable v : pvr )
    {
     if( v.getType() == Type.TAG )
     {
      tagParameters.add( getParameterProcessor(p, p.getId()) );
      break;
     }
    }
   }
  }
 
/*  
  if( genP != null )
  {
   genotypeProcessor = getParameterProcessor( genP );
  }
  else
  {
   logger.warn("Parameter for genotyping availability not found: '"+genotypeParam+"'");
   genotypeProcessor = new RowProcessor( 0 )
   {
    @Override
    public SimpleCounter processPatterns(RowProcessor[] pat, int offset, SimpleCounter cpos, Record row)
    {
     return null;
    }
    
    @Override
    public boolean matchRecord(Record row)
    {
     return false;
    }
   };
  }
*/
  
  SampleCollection coh = null;
  CollectionShadow cohSh = null;
  
  for( Record rd : data )
  {
   if( coh == null || coh.getId() != rd.getCollectionId() )
   {
    coh = collections.get(rd.getCollectionId());
    
    for(CollectionShadow rs : collectionList )
    {
     if( rs.getId() == rd.getCollectionId() )
     {
      cohSh=rs;
      break;
     }
    }
    
    coh.setSampleCount(0);
    coh.setIndividualCount(0);
    cohSh.setSampleCount(0);
    cohSh.setIndividualCount(0);
   }
   
   coh.incSampleCount();
   coh.incIndividualCount();
   cohSh.incSampleCount();
   cohSh.incIndividualCount();
  
  }
  
 }
 
 
 private void loadData()
 {
  studySummaryCache.clear();
  collectionSummaryCache.clear();
  
  data = new ArrayList<Record>();
  
  Connection conn = null;
  ResultSet rst = null;
  try
  {
   conn = dSrc.getConnection();
   Statement stmt = conn.createStatement();

   IntMap<Record> recMap = new IntTreeMap<Record>();
   
   rst = stmt.executeQuery("SELECT * FROM "+TBL_RECORD);
   
   while(rst.next())
   {
    Record rc = new Record();
    
    rc.setId( rst.getInt(FLD_ID) );
    rc.setCount( rst.getInt(FLD_COUNT) );
    rc.setCollectionId( rst.getInt(FLD_COLLECTION_ID) );
    rc.setCollectionRecordIDs( rst.getString(FLD_COLLECTION_RECORD_ID) );
    
    recMap.put(rc.getId(), rc);
    data.add(rc);
   }
   
   rst.close();
   
   Record cRc=null;
   rst = stmt.executeQuery("SELECT * FROM "+TBL_RECORD_IN_STUDY+" ORDER BY "+FLD_RECORD_ID);

   IntList preStds = new ArrayIntList(20);
   IntList postStds = new ArrayIntList(20);
   
   while(rst.next())
   {
    int rcID = rst.getInt(FLD_RECORD_ID);
    int stID = rst.getInt(FLD_STUDY_ID);
    boolean postSt = rst.getBoolean(FLD_POST_STUDY);
    
    if( cRc == null )
     cRc = recMap.get(rcID);
    
    if( cRc == null )
    {
     logger.warn("Invalid recort to study mapping RecordID=" + rcID + " StudyID=" + stID);
     continue;
    }
    
    if( cRc.getId() != rcID )
    {
     if( preStds.size() > 0)
     {
      cRc.setPreStudies(preStds.toArray());
      Arrays.sort(cRc.getPreStudies());
      preStds.clear();
     }

     if( postStds.size() > 0)
     {
      cRc.setPostStudies(postStds.toArray());
      Arrays.sort(cRc.getPostStudies());
      postStds.clear();
     }
     
     cRc = recMap.get(rcID);
     if( cRc == null )
     {
      logger.warn("Invalid recort to study mapping RecordID=" + rcID + " StudyID=" + stID);
      continue;
     }
    }

    if( postSt )
     postStds.add(stID);
    else
     preStds.add(stID);
   }
   
   if( preStds.size() > 0)
   {
    cRc.setPreStudies(preStds.toArray());
    Arrays.sort(cRc.getPreStudies());
   }

   if( postStds.size() > 0)
   {
    cRc.setPostStudies(postStds.toArray());
    Arrays.sort(cRc.getPostStudies());
   }
   
   rst.close();
   
   rst = stmt.executeQuery("SELECT * FROM "+TBL_RECORD_CONTENT);

   Record rc = new Record();
   rc.setId(-1);
   
   while(rst.next())
   {
    int ptid = rst.getInt(FLD_PART_ID);
    int rcid = rst.getInt(FLD_RECORD_ID);
    
    ParameterPart pp = parts.get(ptid);
    
    if( pp == null )
    {
     logger.warn("Invalid ParameterPart reference RecordID="+rcid+" PartID="+ptid);
     continue;
    }
    
    if( rc.getId() != rcid )
     rc = recMap.get(rcid);

    if( rc == null )
    {
     logger.warn("Invalid record reference RecordID="+rcid+" PartID="+ptid);
     continue;
    }
    
    if( pp.isEnum() )
    {
     int variID = rst.getInt(FLD_INT_VALUE);

     short vidx = pp.getVariantIndexByVariantID(variID);
     
     VariantPartValue vpv = new VariantPartValue(pp);
     vpv.setVariant(vidx);
     rc.addPartValue( vpv );
     
     pp.countVariantByIndex(vidx);
    }
    else
    {
     Variable vrbl = (Variable)pp;
     
     if( vrbl.getType() == Type.INTEGER || vrbl.getType() == Type.DATE || vrbl.getType() == Type.BOOLEAN )
     {
      int intval = rst.getInt(FLD_INT_VALUE);
      
      if( rst.wasNull() )
       rc.addPartValue( new PartValue(pp) );
      else
      {
       IntPartValue ipv = new IntPartValue(pp);
       ipv.setIntValue(intval);
       rc.addPartValue( ipv );
      }
     }
     else if( vrbl.getType() == Type.REAL )
     {
      float fltval = rst.getFloat(FLD_REAL_VALUE);
      
      if( rst.wasNull() )
       rc.addPartValue( new PartValue(pp) );
      else
      {
       RealPartValue rpv = new RealPartValue(pp);
       rpv.setRealValue(fltval);
       rc.addPartValue( rpv );
      }
     }
     else
      rc.addPartValue( new PartValue(pp) );
    }
/*    
    PartValue pv = new PartValue(pp);
    
    pp.count();
    if( pp.isEnum() )
    {
     String variantStr = rst.getString(FLD_ENUM_VALUE);
     
     short varid = pp.getVariantID( variantStr );
     pv.setVariant(varid);

//     if( variantStr == null )
//      pv.setSecuredVariant();
//     else
//     {
//      short varid = pp.getVariantID( variantStr );
//      pv.setVariant(varid);
//     }
    }
    
    rc.addPartValue( pv );
*/
   }
   
   rst.close();
   
   for( Record rd : data )
    rd.completeRecord();
   
   Collections.sort(data, RecordComparator.getIntstance() );

  }
  catch(SQLException e)
  {
   Log.error("SQL error", e);
  }
  finally
  {
   if(rst != null)
    try
    {
     rst.close();
    }
    catch(SQLException e)
    {
    }

   if(conn != null)
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }

  }
 }
 
 private Record findRecord( Record key )
 {
  RecordComparator c = RecordComparator.getIntstance();
  int low = 0;
  int high = data.size() - 1;

  while(low <= high)
  {
   int mid = (low + high) >>> 1;
   Record midVal = data.get(mid);
   int cmp = c.compare(midVal, key);

   if(cmp < 0)
    low = mid + 1;
   else if(cmp > 0)
    high = mid - 1;
   else
    return midVal; // key found
  }
  
  return null; // key not found.
 }
 
 private int findRecordByCollection( int key )
 {
  int low = 0;
  int high = data.size() - 1;

  while(low <= high)
  {
   int mid = (low + high) >>> 1;
   Record midVal = data.get(mid);
   int cmp = midVal.getCollectionId()-key;

   if(cmp < 0)
    low = mid + 1;
   else if(cmp > 0)
    high = mid - 1;
   else
    return mid; // key found
  }
  
  return -1; // key not found.
 }

 
 private Collection<Parameter> loadStructure()
 {
  Connection conn = null;
  ResultSet rst = null;

  try
  {
   conn = dSrc.getConnection();
   Statement stmt = conn.createStatement();

   rst = stmt.executeQuery("SELECT * FROM "+TBL_PARAMETER);
   params.clear();
   
   while(rst.next())
   {
    Parameter p = new Parameter();
    p.setId(rst.getInt(FLD_ID));
    p.setCode( rst.getString(FLD_CODE) );
    p.setName( rst.getString(FLD_NAME) );
    p.setDescription( rst.getString(FLD_DESCRIPTION) );
    
    params.put(p.getId(), p);
   }

   rst.close();

   
   
//   IntMap<ParameterPart> parts = new IntTreeMap<ParameterPart>();
   parts.clear();
   
   rst=stmt.executeQuery("SELECT * FROM "+TBL_PART);

   while(rst.next())
   {
    String type = rst.getString(FLD_TYPE);
    
    if( QUALIFIER_TYPE.equals(type) )
    {
     Qualifier q = new Qualifier();
     q.setId( rst.getInt(FLD_ID));
     q.setName(rst.getString(FLD_NAME));
     q.setDescription( rst.getString(FLD_DESCRIPTION) );
     q.setPredefined( rst.getBoolean(FLD_PREDEFINED) );
     q.setMandatory( rst.getBoolean(FLD_MANDATORY) );
     
     Parameter p = params.get( rst.getInt(FLD_PARAMETER_ID) );
     
     if( p == null )
      logger.warn("Abandoned qualifier ID="+q.getId()+" Name: '"+q.getName()+"'");
     else
     {
      p.addQualifier(q);
      parts.put(q.getId(), q);
     }
    }
    else
    {
     Variable v = new Variable();
     v.setId( rst.getInt(FLD_ID));
     v.setName(rst.getString(FLD_NAME));
     v.setDescription(rst.getString(FLD_DESCRIPTION));
     
     try
     {
      v.setType( Type.valueOf(type) );
      
      Parameter p = params.get( rst.getInt(FLD_PARAMETER_ID) );
      
      if( p == null )
       logger.warn("Abandoned variable ID="+v.getId()+" Name: '"+v.getName()+"'");
      else
      {
       p.addVariable(v);
       parts.put(v.getId(), v);
      }
     }
     catch (Exception e)
     {
      Log.error("Invalid variable type value: '"+type+"'. Variable ID="+v.getId());
     }
    }
   }

   rst.close();

   
   rst=stmt.executeQuery("SELECT * FROM "+TBL_VARIANT);

   while(rst.next())
   {
    ParameterPart pp = parts.get(rst.getInt(FLD_PART_ID));
    
    if( pp == null )
     logger.warn("Abandoned variant ID="+rst.getInt(FLD_ID)+" Name: '"+rst.getString(FLD_NAME)+"'");
    else
     pp.addVariant( new Variant( rst.getInt(FLD_ID), rst.getString(FLD_NAME), rst.getInt(FLD_VARI_CODING), rst.getBoolean(FLD_PREDEFINED) ) );
   }

   rst.close();

   
   rst=stmt.executeQuery("SELECT * FROM "+TBL_INHERITED);

   while(rst.next())
   {
    Parameter hp = params.get(rst.getInt(FLD_HOST_PARAM_ID));
    Parameter tp = params.get(rst.getInt(FLD_TARGET_PARAM_ID));
   
    if( hp == null )
     logger.warn("Abandoned inheritance: HostID="+rst.getInt(FLD_HOST_PARAM_ID)+" TargetID="+rst.getString(rst.getInt(FLD_TARGET_PARAM_ID)));
    else if( tp == null )
     logger.warn("Inheritance of non-existent parameter: HostID="+rst.getInt(FLD_HOST_PARAM_ID)+" TargetID="+rst.getString(rst.getInt(FLD_TARGET_PARAM_ID)));
    else
     hp.addInheritedParameter(tp);
   }

   rst.close();


   classifiers.clear();
   
   rst=stmt.executeQuery("SELECT * FROM "+TBL_CLASSIFIER);

   while(rst.next())
   {
    Classifier cl = new Classifier();
    
    cl.setId( rst.getInt(FLD_ID));
    cl.setName( rst.getString(FLD_NAME));
    cl.setDescription(rst.getString(FLD_DESCRIPTION));
    cl.setAllowMulty( rst.getBoolean(FLD_ALLOW_MULTY));
    cl.setMandatory(rst.getBoolean(FLD_MANDATORY));
    
    Classifier.Target tg = null;
    try
    {
     tg = Classifier.Target.valueOf( rst.getString(FLD_TARGET) );
    }
    catch (Exception e) 
    {
    }
    
    if( tg == null )
     logger.warn("Invalid classifier (ID="+cl.getId()+") target: '"+rst.getString(FLD_TARGET)+"'");
    
    cl.setTarget(tg);
    
    classifiers.put(cl.getId(), cl);
   }

   rst.close();

   
   tags.clear();
   
   rst=stmt.executeQuery("SELECT * FROM "+TBL_TAG);

   while(rst.next())
   {
    Tag t = new Tag();
    
    t.setId( rst.getInt(FLD_ID) );
    t.setName( rst.getString(FLD_NAME));
    t.setDescription(rst.getString(FLD_DESCRIPTION));

    Classifier cl = classifiers.get(rst.getInt(FLD_CLASSIFIER_ID));
    
    if( cl == null )
     logger.warn("Abandoned tag ID="+t.getId()+" Name: '"+t.getName()+"'");
    else
    {
     cl.addTag(t);
     tags.put(t.getId(), t);
    }
   }

   rst.close();

   
   rst=stmt.executeQuery("SELECT * FROM "+TBL_PARAMETER_CLASSIFICATION);

   while(rst.next())
   {
    Parameter p = params.get( rst.getInt(FLD_PARAMETER_ID) );
    Tag t = tags.get( rst.getInt(FLD_TAG_ID) );
    
    if( p == null || t == null )
     logger.warn("Invalid parameter classification ParamID="+rst.getInt(FLD_PARAMETER_ID)+" tag ID="+rst.getInt(FLD_TAG_ID));
    else
    {
     p.addClassificationTag(t);
    }
   }

   rst.close();

   
   rst=stmt.executeQuery("SELECT * FROM "+TBL_PARAMETER_ANNOTATION);

   while(rst.next())
   {
    Parameter p = params.get( rst.getInt(FLD_PARAMETER_ID) );
    Tag t = tags.get( rst.getInt(FLD_TAG_ID) );
    
    if( p == null || t == null )
     logger.warn("Invalid parameter annotation ParamID="+rst.getInt(FLD_PARAMETER_ID)+" tag ID="+rst.getInt(FLD_TAG_ID));
    else
    {
     Annotation an = new Annotation();
     an.setTag(t);
     an.setText(rst.getString(FLD_ANNOT_TEXT));
     p.addAnnotation(an);
    }
   }

   rst.close();
   
   rst=stmt.executeQuery("SELECT * FROM "+TBL_RELATION);

   while(rst.next())
   {
    Parameter hp = params.get( rst.getInt(FLD_HOST_PARAM_ID) );
    Parameter tp = params.get( rst.getInt(FLD_TARGET_PARAM_ID) );
    Tag t = tags.get( rst.getInt(FLD_TAG_ID) );
    
    if( hp == null || tp == null || t == null )
     logger.warn("Invalid relation host parameter ID="+rst.getInt(FLD_HOST_PARAM_ID)
       +" target parameter ID="+rst.getInt(FLD_TARGET_PARAM_ID)+" tag ID="+rst.getInt(FLD_TAG_ID));
    else
    {
     Relation rel = new Relation();
     rel.setId( rst.getInt(FLD_ID));
     rel.setHostParameter(hp);
     rel.setTargetParameter(tp);
     rel.setTag(t);
     
     hp.addRelation(rel);
    }
   }

   rst.close();

   
   rst=stmt.executeQuery("SELECT * FROM "+TBL_CLASSIFIER_CLASSIFICATION);

   while(rst.next())
   {
    Classifier c = classifiers.get( rst.getInt(FLD_CLASSIFIER_ID) );
    Tag t = tags.get( rst.getInt(FLD_TAG_ID) );
    
    if( c == null || t == null )
     logger.warn("Invalid classifier classification ParamID="+rst.getInt(FLD_CLASSIFIER_ID)+" tag ID="+rst.getInt(FLD_TAG_ID));
    else
    {
     c.addClassificationTag(t);
    }
   }

   rst.close();

   
   rst=stmt.executeQuery("SELECT * FROM "+TBL_PROJECTION);

   IntMap<Projection> pjMap = new IntTreeMap<Projection>();
   Projection pj = null;
   while(rst.next())
   {
    pj = new Projection();

    pj.setId(rst.getInt(FLD_ID));
    pj.setName(rst.getString(FLD_NAME));
    pj.setDescription(rst.getString(FLD_DESCRIPTION));
    
    pjMap.put(pj.getId(), pj);
   }

   rst.close();
   

   if(pjMap.size() > 0)
   {
    rst = stmt.executeQuery("SELECT * FROM " + TBL_PROJECTION_CONTENT + " ORDER BY " + FLD_PROJECTION_ID + ','
      + FLD_ORDER);

    while(rst.next())
    {
     int id = rst.getInt(FLD_PROJECTION_ID);
     if(pj == null || pj.getId() != id)
      pj = pjMap.get(id);

     if(pj == null)
      logger.warn("Abandoned projection content reference PjID=" + id + " ClID=" + rst.getInt(FLD_CLASSIFIER_ID));
     else
     {
      Classifier cl = classifiers.get(rst.getInt(FLD_CLASSIFIER_ID));
      
      if( cl == null )
       logger.warn("Projection PjID="+id+" is referencing to non-existent classifier ClID="+rst.getInt(FLD_CLASSIFIER_ID));
      else
       pj.addClassifier(cl);
     }
    }

    rst.close();
    
    projectionList.clear();
    for( Projection pjfm : pjMap.values() )
     projectionList.add( new ProjectionShadow(pjfm));
   }
   

   collectionList.clear();
   collections.clear();
   
   rst = stmt.executeQuery("SELECT * FROM "+TBL_COLLECTION);
   
   while( rst.next() )
   {
    CollectionShadow rps = new CollectionShadow();
    
    rps.setId(rst.getInt(FLD_ID));
    rps.setName(rst.getString(FLD_NAME));
    rps.setUpdateTime(rst.getLong(FLD_LAST_UPDATE));
    
    collectionList.add(rps);
    collections.put(rps.getId(), rps.createCollection());
   }
   
   rst.close();
   
   
   rst = stmt.executeQuery("SELECT * FROM "+TBL_COLLECTION_ANNOTATION+" ORDER BY "+FLD_COLLECTION_ID);
   
   CollectionShadow rs = null;
   while( rst.next() )
   {
    int rpId = rst.getInt(FLD_COLLECTION_ID);
    
    if( rs == null || rs.getId() != rpId )
    {
     rs=null;
     for(CollectionShadow rps : collectionList)
     {
      if( rps.getId() == rpId )
      {
       rs = rps;
       break;
      }
     }
     
     if( rs == null )
     {
      logger.warn("Abandoned collection annotation CollectionID="+rpId);
      continue;
     }
    }
    
    AnnotationShadow ans = new AnnotationShadow();
    ans.setTag(rst.getInt(FLD_TAG_ID));
    ans.setText(rst.getString(FLD_ANNOT_TEXT));
    
    rs.addAnotationShadow( ans );
   }
   rst.close();
   
   
   
   loadStudies( stmt );
   
   loadExpressions( stmt );

   rst=null;
  }
  catch(SQLException e)
  {
   Log.error("SQL error", e);
  }
  finally
  {
   if(rst != null)
   {
    try
    {
     rst.close();
    }
    catch(SQLException e)
    {
    }
   }
   
   if(conn != null)
   {
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }
   }
  }
  
  return null;
 }
 
 
 private void loadExpressions( Statement stmt ) throws SQLException
 {
  IntMap<GroupRequestItem> exprMap = new IntTreeMap<GroupRequestItem>();
  
  ResultSet rst = stmt.executeQuery("SELECT * FROM "+TBL_EXPRESSION);
  
  while( rst.next() )
  {
   String name = rst.getString(FLD_NAME);

   GroupRequestItem expr = null;
   
   if( name == null )
    expr = new GroupRequestItem();
   else
    expr = new ExpressionRequestItem();
   
   expr.setId(rst.getInt(FLD_ID));
   expr.setName(rst.getString(FLD_NAME));
   expr.setDepth(rst.getInt(FLD_EXPRESSION_DEPTH));
   expr.setDescription( rst.getString(FLD_DESCRIPTION));   
   
   exprMap.put(expr.getId(), expr);
  }
  
  rst.close();
  
  int rid=1;
  
  rst = stmt.executeQuery("SELECT * FROM "+TBL_EXPRESSION_CONTENT);
  
  while( rst.next() )
  {
   int rpId = rst.getInt(FLD_EXPRESSION_ID);

   GroupRequestItem expr = exprMap.get(rpId);
   
   if( expr == null )
   {
    logger.warn("Abandoned subexpression ExpressionID="+rpId);
    continue;
   }
   
   int sid = rst.getInt(FLD_PARAMETER_ID);
   
   if( sid > 0 )
   {
    Parameter p = params.get(sid);
    
    if( p == null )
    {
     logger.warn("Invalid parameter subexpression ExpressionID="+rpId+" ParameterID="+sid);
     continue;
    }
    
    String filtTxt = rst.getString(FLD_EXPRESSION_FILTER);
    
    RequestItem ri=null;
    
    if( filtTxt != null && filtTxt.length() > 0 )
    {
     Object fo = null;
     
     try
     {
      XMLDecoder dec = new XMLDecoder(new StringInputStream(filtTxt));

      fo = dec.readObject();

      dec.close();
     }
     catch (Exception e) 
     {
     }
     
     if( fo instanceof ComplexFilter )
     {
      ComplexFilter cf = (ComplexFilter)fo;
      
      cf.setParameter(p);
      
      ri = new ComplexFilteredRequestItem( p.getName(), cf );
     }
    }
    else
     ri = new ParameterRequestItem( p.getName(), p );
    
    ri.setId(rid++);
    
    expr.addItem(ri);
   }
   else
   {
    sid = rst.getInt(FLD_SUBEXPRESSION_ID);
    
    if(sid <= 0)
    {
     logger.warn("Invalid subexpression ExpressionID=" + rpId + " ParameterID or SubexpressionID must be non-zero");
     continue;
    }
    
    GroupRequestItem sbexp = exprMap.get(sid);

    if( sbexp == null )
    {
     logger.warn("Invalid parameter subexpression ExpressionID="+rpId+" SubexpressionID="+sid);
     continue;
    }
    
    expr.addItem(sbexp);
   }
   
  }
  rst.close();
  
  expressions.clear();
  
  for( GroupRequestItem expi : exprMap.values() )
  {
   if( expi.getName() != null )
    expressions.add( (ExpressionRequestItem)expi);
  }
  
 }

 
 
 private void loadStudies( Statement stmt ) throws SQLException
 {
  studyList.clear();
  
  IntMap<StudyShadow> studies = new IntTreeMap<StudyShadow>();
  
  ResultSet rst = stmt.executeQuery("SELECT * FROM "+TBL_STUDY);
  
  while( rst.next() )
  {
   StudyShadow rps = new StudyShadow();
   
   rps.setId(rst.getInt(FLD_ID));
   rps.setName(rst.getString(FLD_NAME));
   rps.setUpdateTime(rst.getLong(FLD_LAST_UPDATE));
   
   studyList.add(rps);
   studies.put(rps.getId(), rps);
  }
  
  rst.close();
  
  
  rst = stmt.executeQuery("SELECT * FROM "+TBL_STUDY_ANNOTATION+" ORDER BY "+FLD_STUDY_ID);
  
  StudyShadow rs = null;
  while( rst.next() )
  {
   int rpId = rst.getInt(FLD_STUDY_ID);
   
   if( rs == null || rs.getId() != rpId )
   {
    rs=studies.get(rpId);
    
    if( rs == null )
    {
     logger.warn("Abandoned study annotation StudyID="+rpId);
     continue;
    }
   }
   
   AnnotationShadow ans = new AnnotationShadow();
   ans.setTag(rst.getInt(FLD_TAG_ID));
   ans.setText(rst.getString(FLD_ANNOT_TEXT));
   
   rs.addAnotationShadow( ans );
  }
  rst.close();
  
  
  rst = stmt.executeQuery("SELECT * FROM "+TBL_COLLECTION_IN_STUDY+" ORDER BY "+FLD_STUDY_ID);
  
  rs = null;
  while( rst.next() )
  {
   int rpId = rst.getInt(FLD_STUDY_ID);
   
   if( rs == null || rs.getId() != rpId )
   {
    rs=studies.get(rpId);
    
    if( rs == null )
    {
     logger.warn("Abandoned collection to study mapping StudyID="+rpId);
     continue;
    }
   }
   
   int cohID = rst.getInt(FLD_COLLECTION_ID);
   
   SampleCollection coll = collections.get(cohID);
   if( coll == null )
   {
    logger.warn("Abandoned collection to study mapping StudyID="+rpId+" CollectionID="+cohID);
    continue;
   }

   rs.setCollectionsSamples(rs.getCollectionsSamples()+coll.getSampleCount());
   rs.addCollection( cohID );
   
  }
  rst.close();

 }
 
 public ParameterShadow updateParameter( ParameterShadow p, boolean holdData ) throws ParameterManagementException
 {
  int id = p.getId();
  
  Parameter origP = params.get(id);
  
  if( origP == null )
   throw new ParameterManagementException("Parameter doesn't exist ID="+id,ParameterManagementException.INV_PARAMETER_ID);
 
  SetComparator<Variable> variablesCmp = SetComparator.compare(origP.getVariables(), p.getVariables());
  SetComparator<Qualifier> qualifiersCmp = SetComparator.compare(origP.getQualifiers(), p.getQualifiers());
 
 /*
  Collection<Variable> newVars=null;
  
  if( p.getVariables() != null )
  {
   if( origP.getVariables() == null )
    newVars = p.getVariables();
   else
   {
    newVars = new ArrayList<Variable>(5);
    
    for( Variable nv : p.getVariables() )
    {
     boolean found = false;
     
     for( Variable ev : origP.getVariables() )
     {
      if( nv.getId() == ev.getId() )
      {
       found=true;
       break;
      }
     }
     
     if( ! found )
     {
      newVars.add(nv);
     }
    }
    
   }
  }
  
  
  Collection<Variable> delVars=null;
  
  if( origP.getVariables() != null )
  {
   if( p.getVariables() == null )
    delVars = origP.getVariables();
   else
   {
    delVars = new ArrayList<Variable>(5);
    
    for( Variable ov : origP.getVariables() )
    {
     boolean found = false;
     
     for( Variable nv : p.getVariables() )
     {
      if( nv.getId() == ov.getId() )
      {
       found=true;
       break;
      }
     }
     
     if( ! found )
     {
      delVars.add(ov);
     }
    }
    
   }
  }
*/
/*  
  Collection<Qualifier> newQual=null;
  
  if( p.getQualifiers() != null )
  {
   if( origP.getQualifiers() == null )
    newQual = p.getQualifiers();
   else
   {
    newQual = new ArrayList<Qualifier>(5);
    
    for( Qualifier nv : p.getQualifiers() )
    {
     boolean found = false;
     
     for( Qualifier ev : origP.getQualifiers() )
     {
      if( nv.getId() == ev.getId() )
      {
       found=true;
       break;
      }
     }
     
     if( ! found )
     {
      newQual.add(nv);
     }
    }
    
   }
  }
  
  
  Collection<Qualifier> delQuals=null;
  
  if( origP.getQualifiers() != null )
  {
   if( p.getQualifiers() == null )
    delQuals = origP.getQualifiers();
   else
   {
    delQuals = new ArrayList<Qualifier>(5);
    
    for( Qualifier ov : origP.getQualifiers() )
    {
     boolean found = false;
     
     for( Qualifier nv : p.getQualifiers() )
     {
      if( nv.getId() == ov.getId() )
      {
       found=true;
       break;
      }
     }
     
     if( ! found )
     {
      delQuals.add(ov);
     }
    }
    
   }
  }
*/
  
  Collection<Parameter> newInh=null;
  
  if( p.getInheritedParameters() != null )
  {
   if( origP.getInheritedParameters() == null )
   {
    newInh = new ArrayList<Parameter>( p.getInheritedParameters().length );
    for( int pid  :   p.getInheritedParameters() )
    {
     Parameter ip = params.get(pid);
     
     if( ip == null )
      throw new ParameterManagementException("Invalid inherited parameter ID="+pid,ParameterManagementException.INV_INH_PARAMETER_ID);
     
     newInh.add( ip );
    }
   }
   else
   {
    newInh = new ArrayList<Parameter>(5);
    
    for( int nvid : p.getInheritedParameters() )
    {
     boolean found = false;
     
     for( Parameter ev : origP.getInheritedParameters() )
     {
      if( nvid == ev.getId() )
      {
       found=true;
       break;
      }
     }
     
     if( ! found )
     {
      Parameter ip = params.get(nvid);
      
      if( ip == null )
       throw new ParameterManagementException("Invalid inherited parameter ID="+nvid,ParameterManagementException.INV_INH_PARAMETER_ID);
      
      newInh.add(ip);
     }
    }
    
   }
  }
  
  
  Collection<Parameter> delInh=null;
  
  if( origP.getInheritedParameters() != null )
  {
   if( p.getInheritedParameters() == null )
    delInh = origP.getInheritedParameters();
   else
   {
    delInh = new ArrayList<Parameter>(5);
    
    for( Parameter ov : origP.getInheritedParameters() )
    {
     boolean found = false;
     
     for( int nvid : p.getInheritedParameters() )
     {
      if( nvid == ov.getId() )
      {
       found=true;
       break;
      }
     }
     
     if( ! found )
     {
      delInh.add(ov);
     }
    }
    
   }
  }

  
  Collection<Tag> newTag=null;
  
  if( p.getTags() != null )
  {
   if( origP.getClassificationTags() == null )
   {
    newTag = new ArrayList<Tag>(p.getTags().length);
    for(int tid : p.getTags())
    {
     Tag t = tags.get(tid);

     if(t == null)
      throw new ParameterManagementException("Invalid tag ID=" + tid, ParameterManagementException.INV_TAG_ID);

     newTag.add(t);
    }
   }
   else
   {
    newTag = new ArrayList<Tag>(5);
    
    for( int tid : p.getTags() )
    {
     boolean found = false;
     
     for( Tag ev : origP.getClassificationTags() )
     {
      if( tid == ev.getId() )
      {
       found=true;
       break;
      }
     }
     
     if( ! found )
     {
      Tag t = tags.get(tid);

      if(t == null)
       throw new ParameterManagementException("Invalid tag ID=" + tid, ParameterManagementException.INV_TAG_ID);

      newTag.add(t);
     }
    }
    
   }
  }
  
  
  Collection<Tag> delTag=null;
  
  if( origP.getClassificationTags() != null )
  {
   if( p.getTags() == null )
    delTag = origP.getClassificationTags();
   else
   {
    
    for( Tag ov : origP.getClassificationTags() )
    {
     boolean found = false;
     
     for( int tid : p.getTags() )
     {
      if( tid == ov.getId() )
      {
       found=true;
       break;
      }
     }
     
     if( ! found )
     {
      if( delTag == null )
       delTag = new ArrayList<Tag>(5);

      delTag.add(ov);
     }
    }
    
   }
  }

  
  Collection<Relation> fullRels = new ArrayList<Relation>( );

  
  Collection<Relation> newRel=null;
  
  if( p.getRelations() != null )
  {
   if( origP.getRelations() == null )
   {
    newRel = new ArrayList<Relation>(p.getRelations().length);
    
    for(int[] irel : p.getRelations())
    {
     Tag t = tags.get(irel[2]);

     if(t == null)
      throw new ParameterManagementException("Invalid relation tag ID=" + irel[2], ParameterManagementException.INV_TAG_ID);

     Parameter tp = params.get(irel[1]);
     
     if( tp == null )
      throw new ParameterManagementException("Invalid relation target parameter ID="+irel[1],ParameterManagementException.INV_RERLAGET_PARAMETER_ID);

     Relation nr = new Relation();
     nr.setHostParameter(origP);
     nr.setTargetParameter(tp);
     nr.setTag(t);
     
     newRel.add(nr);
     fullRels.add(nr);
    }
   }
   else
   {
    newRel = new ArrayList<Relation>(5);
    
    for( int[] rl : p.getRelations() )
    {
     boolean found = false;
     
     for( Relation er : origP.getRelations() )
     {
      if( rl[0] == er.getId() )
      {
       found=true;
       break;
      }
     }
     
     if( ! found )
     {
      Tag t = tags.get(rl[2]);

      if(t == null)
       throw new ParameterManagementException("Invalid relation tag ID=" + rl[2], ParameterManagementException.INV_TAG_ID);

      Parameter tp = params.get(rl[1]);
      
      if( tp == null )
       throw new ParameterManagementException("Invalid relation target parameter ID="+rl[1],ParameterManagementException.INV_RERLAGET_PARAMETER_ID);

      Relation nr = new Relation();
      nr.setHostParameter(origP);
      nr.setTargetParameter(tp);
      nr.setTag(t);
      
      newRel.add(nr);
      fullRels.add(nr);
     }
    }
    
   }
  }
  
  
  Collection<Relation> delRel=null;
  
  if( origP.getRelations() != null )
  {
   if( p.getRelations() == null )
    delRel = origP.getRelations();
   else
   {
    delRel = new ArrayList<Relation>(5);
    
    for( Relation ov : origP.getRelations() )
    {
     boolean found = false;
     
     for( int[] relid : p.getRelations() )
     {
      if( relid[0] == ov.getId() )
      {
       found=true;
       break;
      }
     }
     
     if( ! found )
     {
      delRel.add(ov);
     }
     else
      fullRels.add(ov);
    }
    
   }
  }

  Connection conn = null;
  ResultSet rst = null;

  try
  {
   conn = dSrc.getConnection();
   
  
   Statement stmt = conn.createStatement();
   StringBuilder sb = new StringBuilder(200);
   

   if( variablesCmp.getItemsToDelete() != null )
   {
    for( Variable v : variablesCmp.getItemsToDelete() )
     sb.append(v.getId()).append(',');
   }
   
   if( qualifiersCmp.getItemsToDelete() != null )
   {
    for( Qualifier q : qualifiersCmp.getItemsToDelete() )
     sb.append(q.getId()).append(',');
   }
   
   if( sb.length() > 0 )
   {
    sb.setCharAt(sb.length()-1, ')');
    
    String ids = sb.toString();
    
    if( holdData )
    {
     rst = stmt.executeQuery("SELECT COUNT(*) FROM "+TBL_RECORD_CONTENT+" WHERE "+FLD_PART_ID+" IN ("+ids);
     
     rst.next();
     
     int nRec = rst.getInt(1);
     if( nRec > 0 )
      throw new ParameterManagementException("There are "+nRec+" data records annotated by parts ID=("
        +ids,ParameterManagementException.DATA_ANNOTATED_BY_PART);
    
     rst.close();
    }

    stmt.executeUpdate("DELETE FROM  "+TBL_RECORD_CONTENT+" WHERE "+FLD_PART_ID+" IN ("+ids);
    
    stmt.executeUpdate("DELETE FROM  "+TBL_PART+" WHERE "+FLD_PARAMETER_ID+"="+p.getId()+" AND "+FLD_ID+" IN ("+ids);
    stmt.executeUpdate("DELETE FROM  "+TBL_VARIANT+" WHERE "+FLD_PART_ID+" IN ("+ids);
   }
   
   PreparedStatement pstmt = conn.prepareStatement("UPDATE "+TBL_PARAMETER+" SET "+FLD_NAME+"=?,"+FLD_DESCRIPTION+"=?,"+FLD_CODE+"=? WHERE ID="+p.getId());
   pstmt.setString(1, p.getName() );
   pstmt.setString(2, p.getDesc() );
   pstmt.setString(3, p.getCode() );
   
   pstmt.executeUpdate();
   pstmt.close();
   pstmt=null;
 
   Helper hlp = new Helper( conn );
   
   hlp.insertParts(variablesCmp.getNewItems(),p.getId());
   hlp.insertParts(qualifiersCmp.getNewItems(),p.getId());
   
   hlp.updateParts(p.getVariables(), origP.getVariables());
   hlp.updateParts(p.getQualifiers(), origP.getQualifiers());
   
   hlp.destroy();
   
  
   if( newInh != null )
   {
    PreparedStatement insertInhStmt=conn.prepareStatement(insertInheritedSQL);
    
    for( Parameter ip :  newInh )
    {
     insertInhStmt.setInt(1,  p.getId());
     insertInhStmt.setInt(2, ip.getId());
     
     insertInhStmt.executeUpdate();
     
     ((SSParameterInfo)ip.getAuxInfo()).addChildren(origP);
    }
    
    insertInhStmt.close();
   }
   
   if( delInh != null )
   {
    PreparedStatement deleteInhStmt=conn.prepareStatement(deleteInheritedSQL);
    
    for( Parameter ip :  newInh )
    {
     deleteInhStmt.setInt(1,  p.getId());
     deleteInhStmt.setInt(2, ip.getId());
     
     deleteInhStmt.executeUpdate();

     ((SSParameterInfo)ip.getAuxInfo()).removeChildren(origP);
    }
    
    deleteInhStmt.close();
   }

   
   if( newTag != null )
   {
    PreparedStatement insertParamTagStmt=conn.prepareStatement(insertParameterTagSQL);
    
    for( Tag t :  newTag )
    {
     insertParamTagStmt.setInt(1, p.getId());
     insertParamTagStmt.setInt(2, t.getId());
     
     insertParamTagStmt.executeUpdate();
    }
    
    insertParamTagStmt.close();
   }
   
   if( delTag != null )
   {
    PreparedStatement deleteParamTagStmt=conn.prepareStatement(deleteParameterTagSQL);
    
    for( Tag t :  delTag )
    {
     deleteParamTagStmt.setInt(1, p.getId());
     deleteParamTagStmt.setInt(2, t.getId());
     
     deleteParamTagStmt.executeUpdate();
    }
    
    deleteParamTagStmt.close();
   }
   
   
   if( newRel != null )
   {
    PreparedStatement insertRelationStmt=conn.prepareStatement(insertRelationSQL,Statement.RETURN_GENERATED_KEYS);
    
    for( Relation r :  newRel )
    {
     insertRelationStmt.setInt(1, p.getId());
     insertRelationStmt.setInt(2, r.getTargetParameter().getId());
     insertRelationStmt.setInt(3, r.getTag().getId());
     
     insertRelationStmt.executeUpdate();

     rst = insertRelationStmt.getGeneratedKeys();
     
     if( rst.next() )
      r.setId(rst.getInt(1));
     else
      throw new ParameterManagementException("Can't get generated IDs",ParameterManagementException.SYSTEM_ERROR);
     
     rst.close();
    }
    
    insertRelationStmt.close();
   }
   

   if( delRel != null )
   {
    PreparedStatement deleteRelationStmt=conn.prepareStatement(deleteRelationSQL);
    
    for( Relation r :  delRel )
    {
     deleteRelationStmt.setInt(1, r.getId());
     
     deleteRelationStmt.executeUpdate();
    }
    
    deleteRelationStmt.close();
   }

   pstmt = conn.prepareStatement(deleteParameterAnnotationsSQL);
   pstmt.setInt(1, origP.getId());
   pstmt.executeUpdate();
   pstmt.close();
   
   if( p.getAnnotations() != null )
   {
    pstmt = conn.prepareStatement(insertParameterAnnotationsSQL);

    for( AnnotationShadow ans : p.getAnnotations() )
    {
     pstmt.setInt(1, origP.getId());
     pstmt.setInt(2, ans.getTag());
     pstmt.setString(3, ans.getText());
     
     pstmt.executeUpdate();
    }
    
    pstmt.close();
   }
   
   origP.setName(p.getName());
   origP.setCode(p.getCode());
   origP.setDescription(p.getDesc());
   origP.setVariables( p.getVariables() );
   origP.setQualifiers(p.getQualifiers());
   origP.clearInherited();
   
   if( p.getInheritedParameters() != null )
   {
    for( int pid : p.getInheritedParameters() )
     origP.addInheritedParameter( params.get(pid) );
   }
   
   origP.clearTags();
   
   if( p.getTags() != null )
   {
    for( int tid : p.getTags() )
     origP.addClassificationTag( tags.get(tid) );
   }
 
   origP.setRelations( fullRels );
   
   ParameterShadow ps = ((SSParameterInfo)origP.getAuxInfo()).getShadow();
   
   ps.update(origP);
   
   return ps;
   
  }
  catch(SQLException e)
  {
   logger.error("SQL error", e);
  }
  finally
  {
   if(rst != null)
   {
    try
    {
     rst.close();
    }
    catch(SQLException e)
    {
    }
   }
   
   if(conn != null)
   {
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }
   }
  }
 
  return null;
 }
 
 
 public ParameterShadow addParameter( ParameterShadow sp ) throws ParameterManagementException
 {
  Connection conn = null;
  ResultSet rst = null;

  Parameter pr = sp.createParameter();
  
  if( sp.getInheritedParameters() != null )
  {
   for(int ipid : sp.getInheritedParameters() )
   {
    Parameter inhP = params.get(ipid);
    
    if( inhP == null )
     throw new ParameterManagementException("Invalid inherited parameter ID="+ipid,ParameterManagementException.INV_INH_PARAMETER_ID);
    
    pr.addInheritedParameter( inhP );
    
   }
  }
  
  if( sp.getTags() != null )
  {
   for(int tid : sp.getTags())
   {
    Tag t = tags.get(tid);
    
    if( t == null )
     throw new ParameterManagementException("Invalid tag ID="+tid,ParameterManagementException.INV_TAG_ID);
    
    pr.addClassificationTag( t );
   }
  }

  if( sp.getAnnotations() != null )
  {
   for(AnnotationShadow ans : sp.getAnnotations())
   {
    Tag t = tags.get(ans.getTag());
    
    if( t == null )
     throw new ParameterManagementException("Invalid annotation tag ID="+ans.getTag(),ParameterManagementException.INV_TAG_ID);
    
    Annotation an = ans.createAnnotation();
    an.setTag(t);
    
    pr.addAnnotation(an);
   }
  }

  
  if( sp.getRelations() != null )
  {
   int i=1;
   for( int[] rl : sp.getRelations() )
   {
    Parameter rP = params.get(rl[1]);
    
    if( rP == null )
     throw new ParameterManagementException("Invalid inherited parameter ID="+rl[1],ParameterManagementException.INV_INH_PARAMETER_ID);

    Tag t = tags.get(rl[2]);
    
    if( t == null )
     throw new ParameterManagementException("Invalid tag ID="+rl[2],ParameterManagementException.INV_TAG_ID);
    
    rl[0]=i;
    
    Relation r = new Relation();
    r.setId(i);
    r.setHostParameter(pr);
    r.setTargetParameter(rP);
    r.setTag(t);
    
    pr.addRelation(r);
    
    i++;
   }
  }
  
  try
  {
   conn = dSrc.getConnection();
   PreparedStatement pstmt = conn.prepareStatement("INSERT INTO "+TBL_PARAMETER+" ("
     +FLD_CODE+','+FLD_NAME+','+FLD_DESCRIPTION+") VALUES (?,?,?)",Statement.RETURN_GENERATED_KEYS);
   
   pstmt.setString(1, sp.getCode());
   pstmt.setString(2, sp.getName());
   pstmt.setString(3, sp.getDesc());

   try
   {
    pstmt.executeUpdate();
   }
   catch( SQLIntegrityConstraintViolationException ex )
   {
    throw new ParameterManagementException("Parameter with code: '"+sp.getCode()+"' already exists",ex,ParameterManagementException.CODE_EXISTS);
   }

   rst = pstmt.getGeneratedKeys();
   int id = -1;
   
   if( rst.next() )
    id=rst.getInt(1);
   
   pstmt.close();
   
   if( sp.getVariables() != null || sp.getQualifiers() != null )
   {
    PreparedStatement vStmt=null;
    
    pstmt = conn.prepareStatement(insertPartSQL, Statement.RETURN_GENERATED_KEYS);

    if(sp.getVariables() != null)
    {
     for(Variable v : sp.getVariables())
     {
      pstmt.setInt(1, id);
      pstmt.setString(2, v.getName());
      pstmt.setString(3, v.getDescription());
      pstmt.setString(4, v.getType().name());
      pstmt.setBoolean(5, v.isPredefined());
      pstmt.setBoolean(6, v.isMandatory());
      
      pstmt.executeUpdate();
      
      rst = pstmt.getGeneratedKeys();
      
      if( rst.next() )
       v.setId( rst.getInt(1) );
      
      rst.close();
      
      if( v.getType() == Type.ENUM && v.getVariants() != null )
      {
       if( vStmt == null )
        vStmt=conn.prepareStatement(insertVariantSQL, Statement.RETURN_GENERATED_KEYS);
       
       for( Variant vr : v.getVariants() )
       {
        if( ! vr.isPredefined() )
         continue;
        
        vStmt.setInt(1, v.getId());
        vStmt.setString(2,vr.getName());
        vStmt.setInt(3,vr.getCoding());
        vStmt.setBoolean(4, vr.isPredefined());
        
        vStmt.executeUpdate();
        
        rst = vStmt.getGeneratedKeys();
        
        if( rst.next() )
         vr.setId( rst.getInt(1) );
        
        rst.close();
       }
      }
     }
    }

    if(sp.getQualifiers() != null)
    {
     for(Qualifier q : sp.getQualifiers())
     {
      pstmt.setInt(1, id);
      pstmt.setString(2, q.getName());
      pstmt.setString(3, q.getDescription());
      pstmt.setString(4, QUALIFIER_TYPE);
      pstmt.setBoolean(5, q.isPredefined());
      pstmt.setBoolean(6, q.isMandatory());
     
      pstmt.executeUpdate();
      
      rst = pstmt.getGeneratedKeys();
      
      if( rst.next() )
       q.setId( rst.getInt(1) );
      
      rst.close();
      
      if( q.getVariants() != null )
      {
       if( vStmt == null )
        vStmt=conn.prepareStatement(insertVariantSQL, Statement.RETURN_GENERATED_KEYS);
       
       for( Variant vr : q.getVariants() )
       {
        if(!vr.isPredefined())
         continue;

        vStmt.setInt(1, q.getId());
        vStmt.setString(2, vr.getName());
        vStmt.setInt(3, vr.getCoding());
        vStmt.setBoolean(4, vr.isPredefined());

        vStmt.executeUpdate();

        rst = vStmt.getGeneratedKeys();

        if(rst.next())
         vr.setId(rst.getInt(1));

        rst.close();
       }
      }
     }
    }

    if( vStmt != null )
     vStmt.close();

    pstmt.close();
   }
   
   
   if( sp.getInheritedParameters() != null )
   {
    pstmt = conn.prepareStatement("INSERT INTO " + TBL_INHERITED + " (" + FLD_HOST_PARAM_ID + ',' + FLD_TARGET_PARAM_ID
      + ") VALUES (" + id + ",?)");

    for(int ip : sp.getInheritedParameters())
    {
     pstmt.setInt(1, ip);
     pstmt.executeUpdate();
    }

    pstmt.close();
   }
   
   if( sp.getAnnotations() != null )
   {
    pstmt = conn.prepareStatement(insertParameterAnnotationsSQL);

    for(AnnotationShadow ans : sp.getAnnotations())
    {
     pstmt.setInt(1, id);
     pstmt.setInt(2, ans.getTag());
     pstmt.setString(3, ans.getText());
     pstmt.executeUpdate();
    }

    pstmt.close();
   }
   
   if(sp.getTags() != null)
   {
    pstmt = conn.prepareStatement("INSERT INTO " + TBL_PARAMETER_CLASSIFICATION + " (" + FLD_PARAMETER_ID + ','
      + FLD_TAG_ID + ") VALUES (" + id + ",?)");

    for(int t : sp.getTags())
    {
     pstmt.setInt(1, t);
     pstmt.executeUpdate();
    }

    pstmt.close();
   }
   
   if(sp.getRelations() != null)
   {
    pstmt = conn.prepareStatement("INSERT INTO " + TBL_RELATION + " (" + FLD_HOST_PARAM_ID + ','
      +FLD_TARGET_PARAM_ID+','+FLD_TAG_ID + ") VALUES (" + id + ",?,?)", PreparedStatement.RETURN_GENERATED_KEYS);

    for(int[] r : sp.getRelations())
    {
     pstmt.setInt(1, r[1]);
     pstmt.setInt(2, r[2]);
     pstmt.executeUpdate();
     
     rst = pstmt.getGeneratedKeys();
     
     int rlid = -1;
     
     if( rst.next() )
      rlid = rst.getInt(1);
     
     for( Relation rl : pr.getRelations() )
     {
      if( rl.getId() == r[0] )
      {
       rl.setId(rlid);
       break;
      }
     }
     
     r[0]=rlid;
    }

    pstmt.close();
   }
   
   SSParameterInfo ssp  = new SSParameterInfo();
   
   ssp.setShadow(sp);
   pr.setAuxInfo(ssp);
   
   pr.setId(id);
   sp.setId(id);
   
   params.put(id, pr);
   paramCodeMap.put(pr.getCode(), pr);
   paramList.add(sp);
   
   return sp;
  }
  catch(SQLException e)
  {
   logger.error("SQL error", e);
   throw new ParameterManagementException("SQL error: "+e.getMessage(),e,ParameterManagementException.SQL_ERROR);
  }
  finally
  {
   if(rst != null)
   {
    try
    {
     rst.close();
    }
    catch(SQLException e)
    {
    }
   }
   
   if(conn != null)
   {
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }
   }
  }

 }
 
 public void deleteParameter(int pid)
 {
 }

 
 class Helper
 {
  PreparedStatement updatePartStmt=null;
  PreparedStatement delVariantStmt=null;
  PreparedStatement updateVariantStmt=null;
  PreparedStatement insVariStmt=null;
  PreparedStatement insPartStmt=null;
  PreparedStatement delValuesStmt=null;
  
  Connection conn=null;
  
  public Helper(Connection conn2)
  {
   conn=conn2;
  }

  public void destroy() throws SQLException
  {
   if( updatePartStmt != null )
    updatePartStmt.close();
   
   if( delVariantStmt != null )
    delVariantStmt.close();

   if( updateVariantStmt != null )
    updateVariantStmt.close();

   if( insVariStmt != null )
    insVariStmt.close();

   if( insPartStmt != null )
    insPartStmt.close();

   if( delValuesStmt != null )
    delValuesStmt.close();
  }

  void insertParts(Collection<? extends ParameterPart> parts, int pid) throws SQLException, ParameterManagementException
  {
   if( parts != null && parts.size() > 0 )
   {
    for( ParameterPart v : parts )
    {
     if( insPartStmt == null )
      insPartStmt = conn.prepareStatement(insertPartSQL,Statement.RETURN_GENERATED_KEYS);
     
     insPartStmt.setInt(1, pid);
     insPartStmt.setString(2, v.getName());
     insPartStmt.setString(3, v.getDescription());
     insPartStmt.setString(4, (v instanceof Variable)?((Variable)v).getType().name():QUALIFIER_TYPE);
     insPartStmt.setBoolean(5, v.isPredefined());
     insPartStmt.setBoolean(6, v.isMandatory());
     
     insPartStmt.executeUpdate();
     
     ResultSet rst = insPartStmt.getGeneratedKeys();
     
     if( rst.next() )
      v.setId(rst.getInt(1));
     else
      throw new ParameterManagementException("Can't get generated IDs",ParameterManagementException.SYSTEM_ERROR);
     
     v.setDirty(false);
     rst.close();
     
     if( v.getVariants() != null )
     {
      if( insVariStmt == null )
       insVariStmt=conn.prepareStatement(insertVariantSQL,Statement.RETURN_GENERATED_KEYS);
      
      for( Variant vari : v.getVariants() )
      {
       if( ! vari.isPredefined() )
        continue;
        
       insVariStmt.setInt(1, v.getId());
       insVariStmt.setString(2, vari.getName());
       insVariStmt.setInt(3, vari.getCoding());
       insVariStmt.setBoolean(4, vari.isPredefined());
       
       insVariStmt.executeUpdate();

       rst = insVariStmt.getGeneratedKeys();
       
       if( rst.next() )
        vari.setId(rst.getInt(1));
       else
        throw new ParameterManagementException("Can't get generated IDs",ParameterManagementException.SYSTEM_ERROR);

       rst.close();

      }
     }
    }
   }

  }
  
  void  updateParts( Collection<? extends ParameterPart> parts, Collection<? extends ParameterPart> origParts ) throws SQLException, ParameterManagementException
  {
   if( parts != null && parts.size() > 0 && origParts != null )
   {
    for( ParameterPart v : parts )
    {
     if( ! v.isDirty() )
      continue;
     
     if( updatePartStmt == null )
      updatePartStmt = conn.prepareStatement(updatePartSQL);
     
     updatePartStmt.setString(1, v.getName() );
     updatePartStmt.setString(2, v.getDescription() );
     updatePartStmt.setBoolean(3, v.isPredefined());
     updatePartStmt.setBoolean(4, v.isMandatory());
     updatePartStmt.setInt(5, v.getId() );
     
     updatePartStmt.executeUpdate();
     
     ParameterPart origV=null;
     for( ParameterPart ov : origParts )
     {
      if( ov.getId() == v.getId() )
      {
       origV=ov;
       break;
      }
     }
     
     if( origV == null ) // That means that new variable was created
      continue;
     
     SetComparator<Variant> varisCmp = SetComparator.compare(origV.getVariants(), v.getVariants() );
     
     if( varisCmp.getItemsToDelete() != null )
     {
      for( Variant dv : varisCmp.getItemsToDelete() )
      {
       if( ! dv.isPredefined() )
        continue;
       
       if( delVariantStmt == null )
        delVariantStmt=conn.prepareStatement(deleteVariantSQL);
       
       if( delValuesStmt == null )
        delValuesStmt=conn.prepareStatement("DELETE FROM "+TBL_RECORD_CONTENT+" WHERE "+FLD_PART_ID+"=? AND "+FLD_INT_VALUE+"=?");
       
       delVariantStmt.setInt(1, dv.getId());
       delVariantStmt.executeUpdate();
       
       delValuesStmt.setInt(1, v.getId());
       delValuesStmt.setInt(2, dv.getId());
       delValuesStmt.executeUpdate();
      }
     }

     if(varisCmp.getNewItems() != null)
     {
      for(Variant nv : varisCmp.getNewItems())
      {
       if( ! nv.isPredefined() )
        continue;
       
       if(insVariStmt == null)
        insVariStmt = conn.prepareStatement(insertVariantSQL, Statement.RETURN_GENERATED_KEYS);

       insVariStmt.setInt(1, v.getId());
       insVariStmt.setString(2, nv.getName());
       insVariStmt.setInt(3, nv.getCoding());
       insVariStmt.setBoolean(4, nv.isPredefined());

       insVariStmt.executeUpdate();

       ResultSet rst = insVariStmt.getGeneratedKeys();

       if(rst.next())
        nv.setId(rst.getInt(1));
       else
        throw new ParameterManagementException("Can't get generated IDs", ParameterManagementException.SYSTEM_ERROR);

       rst.close();
      }
     }
     
     if( varisCmp.getUpdateItems() != null )
     {
      for(Variant uv : varisCmp.getUpdateItems())
      {
       if( ! uv.isDirty() )
        continue;

       if( ! uv.isPredefined() )
        continue;
       
       if( updateVariantStmt == null )
        updateVariantStmt=conn.prepareStatement(updateVariantSQL,Statement.RETURN_GENERATED_KEYS);

       updateVariantStmt.setString(1, uv.getName());
       updateVariantStmt.setInt(2, v.getId());
       updateVariantStmt.setInt(3, uv.getCoding());
       updateVariantStmt.setBoolean(4, uv.isPredefined());
       updateVariantStmt.setInt(5, uv.getId());
       
       updateVariantStmt.executeUpdate();
      
      }
     
     }
     
     /*
     if( origV.getVariants() != null )
     {
      for( Variant vari : origV.getVariants() )
      {
       Variant newVari=null;
       
       if( v.getVariants() != null )
       {
        for( Variant nvr : v.getVariants() )
        {
         if( nvr.getId() == vari.getId() )
         {
          newVari=nvr;
          break;
         }
        }
       }
       
       if( newVari == null )
       {
        if( delVariantStmt == null )
         delVariantStmt=conn.prepareStatement(deleteVariantSQL);
        
        delVariantStmt.setInt(1, vari.getId());
        delVariantStmt.executeUpdate();
       }
       
      }
     }
     
     if( v.getVariants() != null && v.getVariants().size() > 0 )
     {
      for( Variant nvr : v.getVariants() )
      {
       if( nvr.getId() <= 0 )
       {
        if( insVariStmt == null )
         insVariStmt=conn.prepareStatement(insertVariantSQL,Statement.RETURN_GENERATED_KEYS);
        
        insVariStmt.setInt(1, v.getId());
        insVariStmt.setString(2, nvr.getName());
        insVariStmt.setInt(3, nvr.getCoding());
        insVariStmt.setBoolean(4, nvr.isPredefined() );

        insVariStmt.executeUpdate();
        
        ResultSet rst=insVariStmt.getGeneratedKeys();
        
        if( rst.next() )
         nvr.setId(rst.getInt(1));
        else
         throw new ParameterManagementException("Can't get generated IDs",ParameterManagementException.SYSTEM_ERROR);

        rst.close();
       }
       else if( nvr.isDirty() )
       {
        if( updateVariantStmt == null )
         updateVariantStmt=conn.prepareStatement(updateVariantSQL,Statement.RETURN_GENERATED_KEYS);

        updateVariantStmt.setString(1, nvr.getName());
        updateVariantStmt.setInt(2, v.getId());
        updateVariantStmt.setBoolean(3, nvr.isPredefined());
        updateVariantStmt.setInt(4, nvr.getId());
        
        updateVariantStmt.executeUpdate();
       }
      }
     }
     */
    }
   }
  }
 }


 public List<ProjectionShadow> getProjections()
 {
  return projectionList;
 }

 public ClassifierShadow addClassifier(ClassifierShadow cs)
 {
  Connection conn = null;
  ResultSet rst = null;

  try
  {
   conn = dSrc.getConnection();
   PreparedStatement pstmt = conn.prepareStatement(insertClassifierSQL, Statement.RETURN_GENERATED_KEYS);

   pstmt.setString(1, cs.getName());
   pstmt.setString(2, cs.getDesc());
   pstmt.setBoolean(3, cs.isAllowMulty());
   pstmt.setBoolean(4, cs.isMandatory());
   pstmt.setString(5, cs.getTarget().name());
   

   pstmt.executeUpdate();

   rst = pstmt.getGeneratedKeys();
   int id = -1;

   if(rst.next())
    id = rst.getInt(1);
   
   rst.close();
   pstmt.close();
   
   if( cs.getTags() != null )
   {
    pstmt = conn.prepareStatement(insertTagSQL, Statement.RETURN_GENERATED_KEYS);

    for( Tag t : cs.getTags() )
    {
     pstmt.setString(1, t.getName());
     pstmt.setString(2, t.getDescription());
     pstmt.setInt(3, id);

     pstmt.executeUpdate();

     rst = pstmt.getGeneratedKeys();
     if(rst.next())
      t.setId(rst.getInt(1));

     rst.close();
    }
    
    pstmt.close();
   }
   
   if( cs.getClassificationTags() != null )
   {
    pstmt = conn.prepareStatement(insertClassifierClassificationSQL);
    
    for( int tid : cs.getClassificationTags() )
    {
     Tag t = tags.get(tid);
     
     if( t == null )
     {
      logger.warn("Invalid tag ID="+tid);
      continue;
     }
     
     pstmt.setInt(1, id);
     pstmt.setInt(2, tid);
     
     pstmt.executeUpdate();
    }
   }
   
   Classifier nc = cs.createClassifier();
   nc.setId(id);
   cs.setId(id);
   nc.setTags(cs.getTags());
   
   if( nc.getTags() != null )
   {
    for( Tag t : nc.getTags() )
     tags.put(t.getId(), t);
   }
   
   classifiers.put(nc.getId(), nc);
   classifiersList.add(nc);
   
   return cs;
  }
  catch(SQLException e)
  {
   logger.error("SQL error", e);
  }
  finally
  {
   if(rst != null)
   {
    try
    {
     rst.close();
    }
    catch(SQLException e)
    {
    }
   }

   if(conn != null)
   {
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }
   }
  }

  return null;
 }

 public ClassifierShadow updateClassifier(ClassifierShadow scl) throws ClassifierManagementException
 {
  int id = scl.getId();
  Classifier orig = classifiers.get(id);

  if(orig == null)
   throw new ClassifierManagementException("Classifier doesn't exist ID=" + id,
     ClassifierManagementException.INV_CLASSIFIER_ID);

  SetComparator<Tag> res = SetComparator.compare( orig.getTags(), scl.getTags() );

  Connection conn = null;
  ResultSet rst = null;

  try
  {
   conn = dSrc.getConnection();
   PreparedStatement pstmt = conn.prepareStatement(updateClassifierSQL);

   pstmt.setString(1, scl.getName());
   pstmt.setString(2, scl.getDesc());
   pstmt.setBoolean(3, scl.isAllowMulty());
   pstmt.setBoolean(4, scl.isMandatory());
   pstmt.setString(5, scl.getTarget().name());
   pstmt.setInt(6, id);

   pstmt.executeUpdate();
   pstmt.close();

   if(res.getItemsToDelete() != null)
   {
    pstmt = conn.prepareStatement(deleteTagSQL);

    PreparedStatement delParamTag = conn.prepareStatement("DELETE FROM " + TBL_PARAMETER_CLASSIFICATION + " WHERE "
      + FLD_TAG_ID + "=?");
    PreparedStatement delClsTag = conn.prepareStatement("DELETE FROM " + TBL_CLASSIFIER_CLASSIFICATION + " WHERE "
      + FLD_TAG_ID + "=?");
    PreparedStatement delRelTag = conn.prepareStatement("DELETE FROM " + TBL_RELATION + " WHERE "
      + FLD_TAG_ID + "=?");
    PreparedStatement delRepAnntTag = conn.prepareStatement("DELETE FROM " + TBL_COLLECTION_ANNOTATION + " WHERE "
      + FLD_TAG_ID + "=?");

    PreparedStatement delParamAnntTag = conn.prepareStatement("DELETE FROM " + TBL_PARAMETER_ANNOTATION + " WHERE "
      + FLD_TAG_ID + "=?");

    for(Tag t : res.getItemsToDelete())
    {
     pstmt.setInt(1, t.getId());
     pstmt.executeUpdate();

     delParamTag.setInt(1, t.getId());
     delParamTag.executeUpdate();

     delClsTag.setInt(1, t.getId());
     delClsTag.executeUpdate();

     delRelTag.setInt(1, t.getId());
     delRelTag.executeUpdate();

     delRepAnntTag.setInt(1, t.getId());
     delRepAnntTag.executeUpdate();

     delParamAnntTag.setInt(1, t.getId());
     delParamAnntTag.executeUpdate();
    
     tags.remove(t.getId());
    }

    delParamTag.close();
    delClsTag.close();
    delRelTag.close();
    delRepAnntTag.close();
    pstmt.close();
   }

   if(res.getNewItems() != null)
   {
    pstmt = conn.prepareStatement(insertTagSQL, Statement.RETURN_GENERATED_KEYS);

    for(Tag t : res.getNewItems())
    {
     pstmt.setString(1, t.getName());
     pstmt.setString(2, t.getDescription());
     pstmt.setInt(3, id);

     pstmt.executeUpdate();

     rst = pstmt.getGeneratedKeys();
     if(rst.next())
      t.setId(rst.getInt(1));

     rst.close();
     
     tags.put(t.getId(),t);
    }

    pstmt.close();
   }

   if(res.getUpdateItems() != null && res.getUpdateItems().size() > 0)
   {
    pstmt = conn.prepareStatement(updateTagSQL);

    for(Tag t : res.getUpdateItems())
    {
     pstmt.setString(1, t.getName());
     pstmt.setString(2, t.getDescription());
     pstmt.setInt(3, t.getId());

     pstmt.executeUpdate();
    }

    pstmt.close();
   }

   pstmt = conn.prepareStatement(deleteAllClassifierClassificationSQL);
   pstmt.setInt(1, id);
   pstmt.executeUpdate();
   pstmt.close();

   if(scl.getClassificationTags() != null) // TODO check tag existance
   {
    pstmt = conn.prepareStatement(insertClassifierClassificationSQL);

    for(int tid : scl.getClassificationTags())
    {
     pstmt.setInt(1, id);
     pstmt.setInt(2, tid);

     pstmt.executeUpdate();
    }
   }

   if(res.getItemsToDelete() != null)
    updateStructure();
   else
   {
    orig.setName(scl.getName());
    orig.setDescription(scl.getDesc());

    if(orig.getTags() != null)
    {
     for(Tag ot : orig.getTags())
     {
      for(Tag nt : scl.getTags())
      {
       if(ot.getId() == nt.getId())
       {
        ot.setName(nt.getName());
        ot.setDescription(nt.getDescription());
        break;
       }
      }
     }
    }

    if(res.getNewItems() != null)
    {
     for(Tag t : res.getNewItems())
      orig.addTag(t);
    }

    orig.setClassificationTags(null);
    if(scl.getClassificationTags() != null)
    {
     for(int tid : scl.getClassificationTags())
     {
      Tag t = tags.get(tid);

      if(t == null)
      {
       logger.warn("Invalid tag ID=" + tid);
      }
      else
       orig.addTag(t);
     }
    }

   }

   return scl;
  }
  catch(SQLException e)
  {
   logger.error("SQL error", e);
  }
  finally
  {
   if(rst != null)
   {
    try
    {
     rst.close();
    }
    catch(SQLException e)
    {

    }

    if(conn != null)
    {
     try
     {
      conn.close();
     }
     catch(SQLException e)
     {
      Log.error("Connection closing error", e);
     }
    }
   }
  }
  return null;
 }

 public Integer addProjection(ProjectionShadow p) throws ProjectionManagementException
 {
  Connection conn = null;
  ResultSet rst = null;
  try
  {
   
   Projection pj = p.createProjection();
   for(int clid : p.getClassifiers() )
   {
    Classifier cl = classifiers.get(clid);
    
    if( cl == null ) 
     throw new ProjectionManagementException("Invalid classifer ID="+clid,ProjectionManagementException.INV_CLASSIFIER_ID);
    
    pj.addClassifier(cl);
   }

   conn = dSrc.getConnection();
   PreparedStatement pstmt = conn.prepareStatement( insertProjectionSQL, Statement.RETURN_GENERATED_KEYS );

   pstmt.setString(1, p.getName());
   pstmt.setString(2,p.getDescription());
   
   pstmt.executeUpdate();
   
   rst = pstmt.getGeneratedKeys();
   
   int id=-1;
   if( rst.next() )
    id=rst.getInt(1);
   
   pstmt.close();
   
   pstmt = conn.prepareStatement(insertProjectionContentSQL);
   
   
   int n=1;
   for(int clid : p.getClassifiers() )
   {
    pstmt.setInt(1, id);
    pstmt.setInt(2, clid);
    pstmt.setInt(3, n++);
    
    pstmt.executeUpdate();
   }
   
   pstmt.close();
   
   p.setId(id);
   
   projectionList.add(p);
   
   return id;
   
  }
  catch(SQLException e)
  {
   Log.error("SQL error", e);
   throw new ProjectionManagementException("SQL error", e, ProjectionManagementException.SQL_ERROR );
  }
  finally
  {
   if(rst != null)
    try
    {
     rst.close();
    }
    catch(SQLException e)
    {
    }

   if(conn != null)
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }

  }
 }
 
 public void updateProjection(ProjectionShadow p) throws ProjectionManagementException
 {

  Connection conn = null;
  ResultSet rst = null;
  try
  {
   conn = dSrc.getConnection();
   PreparedStatement pstmt = conn.prepareStatement(updateProjectionSQL);

   pstmt.setString(1, p.getName());
   pstmt.setString(2, p.getDescription());
   pstmt.setInt(3, p.getId());
   
   pstmt.executeUpdate();
   
   pstmt.close();
   
   Statement stmt = conn.createStatement();
   
   stmt.executeUpdate("DELETE FROM "+TBL_PROJECTION_CONTENT+" WHERE "+FLD_PROJECTION_ID+"="+p.getId());
   
   pstmt = conn.prepareStatement(insertProjectionContentSQL);
   
   int n=1;
   for(int clid : p.getClassifiers() )
   {
    Classifier cl = classifiers.get(clid);
    
    if( cl == null )
     throw new ProjectionManagementException("Invalid classifier ID="+clid,ProjectionManagementException.INV_CLASSIFIER_ID);
    
    pstmt.setInt(1, p.getId());
    pstmt.setInt(2, clid);
    pstmt.setInt(3, n++);
    
    pstmt.executeUpdate();
   }

  }
  catch(SQLException e)
  {
   Log.error("SQL error", e);
   throw new ProjectionManagementException("SQL error", ProjectionManagementException.SQL_ERROR);
  }
  finally
  {
   if(rst != null)
    try
    {
     rst.close();
    }
    catch(SQLException e)
    {
    }

   if(conn != null)
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }

  }
 }
 
 static class GpPat
 {
  String name;
  int[] pat;
  int pid;
 }

 /*
 public Report report1(RequestItem[] req)
 {
  Report chain = new Report();
  
  Object[] pat = new Object[req.length];
  for(int i = 0; i < req.length; i++)
  {
   if( req[i].getType() == RequestItem.Type.GROUP )
   {
    GroupRequestItem gpi = (GroupRequestItem)req[i];
    
    GpPat[] gpats = new GpPat[gpi.getItems().size()];

    int k=0;
    for(Integer pid : gpi.getItems())
    {
     Parameter p = findParameter(pid);
     GpPat gp = new GpPat();
     gp.name = p.getName();
     gp.pat = getParameterPattern(p);
     gpats[k++] = gp;
    }
    
    pat[i]=gpats;
    
    chain.addHeader(gpi.getGroupName());

   }
   else
   {
    Parameter p = findParameter(req[i].getParamID());

    if(p == null)
    {
     logger.error("Parameter not found. ID=" + req[i].getParamID());
     return null;
    }

    if(req[i].getType() == RequestItem.Type.PARAMETER )
    {
     pat[i] = getParameterPattern(p);
     chain.addHeader(p.getName());
    }
    else if( req[i].getType() == RequestItem.Type.QUALIFIER )
    {
     Qualifier q = null;

     Collection<Qualifier> qs = p.getQualifiers();

     if(qs == null)
     {
      System.out.println("Qualifier not found. pID=" + req[i].getParamID() + " qID=" + req[i].getPartID());
      return null;
     }


     for(Qualifier ql : qs)
     {
      if(ql.getId() == req[i].getPartID())
      {
       q = ql;
       break;
      }
     }

     if(q == null)
     {
      System.out.println("Qualifier not found. pID=" + req[i].getParamID() + " qID=" + req[i].getPartID());
      return null;
     }

     chain.addHeader(p.getName()+"."+q.getName());

     pat[i] = req[i].getPartID();
    }
    else if( req[i].getType() == RequestItem.Type.VARIABLE )
    {
     Variable v = null;

     Collection<Variable> qs = p.getVariables();

     if(qs == null)
     {
      System.out.println("Variable not found. pID=" + req[i].getParamID() + " qID=" + req[i].getPartID());
      return null;
     }

     for(Variable ql : qs)
     {
      if(ql.getId() == req[i].getPartID())
      {
       v = ql;
       break;
      }
     }

     if(v == null)
     {
      System.out.println("Variable not found. pID=" + req[i].getParamID() +" vID=" + req[i].getPartID());
      return null;
     }

     chain.addHeader(p.getName()+"."+v.getName());

     pat[i] = req[i].getPartID();
    }
    else if( req[i].getType() == RequestItem.Type.FILTERED )
    {
     ParameterPart pp = parts.get( req[i].getPartID() );
     
     if( pp == null )
     {
      System.out.println("Part not found. pID=" + req[i].getParamID() +" partID=" + req[i].getPartID());
      return null;
     }
     
     if( pp.getParameter().getId() != p.getId() )
     {
      System.out.println("Parameter has no part with such ID. pID=" + req[i].getParamID() +" partID=" + req[i].getPartID());
      return null;
     }
     
     Arrays.sort(((FilteredRequestItem)req[i]).getVariants());
     
     chain.addHeader(p.getName()+"."+pp.getName());
     pat[i] = req[i];
    }
   }
  }

  // Object chain=(req[0] instanceof Parameter)?new SimpleCounter():new
  // HashMap<String,SimpleCounter>() ;


  chain.setCount(data.size());

  for(Record row : data)
  {
   processPatterns( pat, 0, chain, row );
  }

  return chain;
 }
*/
 
 private RowProcessor convert( RequestItem ritm, ReportRequest req )
 {
  
  if( ritm instanceof GroupRequestItem )
  {
   GroupRequestItem gpi = (GroupRequestItem)ritm;
   
   RowProcessor[] gpats = new RowProcessor[gpi.getItems().size()];

   int k=0;
   for(RequestItem gri : gpi.getItems())
    gpats[k++] = convert(gri, req);
   
   GroupRowProcessor grp = new GroupRowProcessor(ritm.getId(), gpats, gpi.getDepth());
   
   grp.setName(gpi.getName());

   return grp;
  }
//  else if( ritm instanceof CollectionRequestItem )
//  {
//   CollectionRequestItem rri = (CollectionRequestItem)ritm;
//   
//   CollectionRowProcessor rp = new CollectionRowProcessor(ritm.getId(), rri.getCollectionIDs(),collections);
//   rp.setName("Collection");
//   
//   return rp;
//  }
  else if( ritm instanceof AlternativeRequestItem )
  {
   AlternativeRequestItem rri = (AlternativeRequestItem)ritm;
   Collection<RowProcessor> pats = new ArrayList<RowProcessor>();

   for( int apid : rri.getAlternativeParameters() )
   {
    Parameter ap = findParameter(apid);
    
    if( ap == null )
    {
     logger.error("Parameter not found. ID=" + rri.getParameterId() );
     return null;
    }
    
    pats.add(getParameterProcessor(ap, rri.getId()));
   }
   
   Parameter p = findParameter(rri.getParameterId());
   
   ParameterAlternativeRowProcessor rp = new ParameterAlternativeRowProcessor( rri.getParameterId(), rri.getId(), rri.getAlternativeParameters(), pats );
   rp.setName(p!=null?(p.getCode()+" (+)"):"Group (+)");
   
   return rp;
  }
  else if( ritm instanceof ParameterRequestItem )
  {
   ParameterRequestItem rri = (ParameterRequestItem)ritm;
   Parameter p = findParameter(rri.getParameterID());

   if(p == null)
   {
    logger.error("Parameter not found. ID=" + rri.getParameterID());
    return null;
   }

   if( rri.getType() == RequestItem.Type.PARAM )
   {
    int[] rTags=null;
    
    if( req.getRelations() != null )
    {
     rTags = new int[req.getRelations().size()];
     
     int i=0;
     for( Integer tg : req.getRelations() )
      rTags[i++]=tg;
    }
    
    if( rTags == null && ! req.isAllRelations() )
    {
     RowProcessor rp = getParameterProcessor(p, ritm.getId()); //new ParameterRowProcessor( req.getParamID(), getParameterPattern(p) );
     rp.setName(p.getCode());
     
     return rp;
    }
    else
    {
     if( rTags != null )
      Arrays.sort(rTags);
     
     Collection<Relation> rels = p.getRelations();
     
     Collection<RowProcessor> pats = null;
     
     
     if( rels != null && rels.size() > 0 )
     {
      IntList pids = null;
      
      for( Relation r : rels )
      {
       if( req.isAllRelations() || (rTags != null &&  Arrays.binarySearch(rTags, r.getTag().getId()) >= 0) )
       {
        if( pats == null )
        {
         pats = new ArrayList<RowProcessor>();
         pids = new ArrayIntList();
        }
         
        Parameter tp = r.getTargetParameter();
        pids.add(tp.getId());
        pats.add(getParameterProcessor(tp, ritm.getId()));
       }
      }
      
      if( pats == null )
      {
       RowProcessor rp = getParameterProcessor(p, ritm.getId()); //new ParameterRowProcessor( req.getParamID(), getParameterPattern(p) );
       rp.setName(p.getCode());
       
       return rp;
      }
      else
      {
//       pids.add(p.getId());
//       pats.add( getParameterProcessor(p) );
       
       ParameterRelationRowProcessor rp = new ParameterRelationRowProcessor( p.getId(), ritm.getId(), getParameterProcessor(p, ritm.getId()), pids.toArray(), pats );
       rp.setName(p.getCode()+" (+Rels)");
       
       return rp;
      }
      
     }
     else
     {
      RowProcessor rp = getParameterProcessor(p, ritm.getId()); //new ParameterRowProcessor( req.getParamID(), getParameterPattern(p) );
      rp.setName(p.getCode());
      
      return rp;
     }

    }
   }
   else if( ritm instanceof PartRequestItem )
   {
    PartRequestItem prri = (PartRequestItem)ritm;

    ParameterPart pp =p.getPart(prri.getPartID());
    
    if(pp == null )
    {
     System.out.println("Parameter part not found. pID=" + prri.getParameterID() + " ppID=" + prri.getPartID());
     return null;
    }
 
    if( ! pp.isEnum() )
    {
     System.out.println("Parameter part is not enumeration. pID=" + prri.getParameterID() +" partID=" + prri.getPartID());
     return null;
    }
    
    SplitRowProcessor rp = new SplitRowProcessor(prri.getParameterID(), prri.getId(), getParameterPattern(p), prri.getPartID());

    rp.setName(p.getCode()+"."+pp.getName());
    
    return rp;
   }
   else if( ritm instanceof EnumFilteredRequestItem )
   {
    EnumFilteredRequestItem erri = (EnumFilteredRequestItem)ritm;

    ParameterPart pp = parts.get( erri.getPartID() );
    
    if( pp == null )
    {
     System.out.println("Part not found. pID=" + erri.getParameterID() +" partID=" + erri.getPartID());
     return null;
    }
    
    if( p.getPart(erri.getPartID()) == null )
    {
     System.out.println("Parameter has no part with such ID. pID=" + erri.getParameterID() +" partID=" + erri.getPartID());
     return null;
    }
    
    if( ! pp.isEnum() )
    {
     System.out.println("Parameter part is not enumeration. pID=" + erri.getParameterID() +" partID=" + erri.getPartID());
     return null;
    }
    
    Arrays.sort(((EnumFilteredRequestItem)erri).getVariants());
    
    FilteredSplitRowProcessor rp = new FilteredSplitRowProcessor(erri.getParameterID(), erri.getId(), getParameterPattern(p), (EnumFilteredRequestItem)erri);
    rp.setName(p.getCode()+"."+pp.getName());
    
    return rp;
   }
//   else if( req.getType() == RequestItem.Type.FILTERED )
//   {
//    FilteredRequestItem rri = (FilteredRequestItem)req;
//    ParameterPart pp = parts.get( rri.getPartID() );
//    
//    if( pp == null )
//    {
//     System.out.println("Part not found. pID=" + req.getParamID() +" partID=" + rri.getPartID());
//     return null;
//    }
//    
//    if( p.getPart(rri.getPartID()) == null )
//    {
//     System.out.println("Parameter has no part with such ID. pID=" + req.getParamID() +" partID=" + rri.getPartID());
//     return null;
//    }
//
//    if( ! pp.isEnum() )
//    {
//     System.out.println("Parameter part is not enumeration. pID=" + req.getParamID() +" partID=" + rri.getPartID());
//     return null;
//    }
//    
//    Arrays.sort(((FilteredRequestItem)req).getVariants());
//    
//    FilteredRowProcessor rp = new FilteredRowProcessor(req.getParamID(), req.getId(), getParameterPattern(p), (FilteredRequestItem)req);
//    rp.setName(p.getCode()+"."+pp.getName());
//    
//    return rp;
//   }
//   else if( req.getType() == RequestItem.Type.CFILTERED )
//   {
//    ComplexFilteredRequestItem cfrq = (ComplexFilteredRequestItem)req;
//    
//    int [][] variants = cfrq.getVariants();
//    
//    for( int[] vpart : variants )
//    {
//     ParameterPart pp = parts.get( vpart[0] );
//     
//     if( pp == null )
//     {
//      System.out.println("Part not found. pID=" + req.getParamID() +" partID=" + vpart[0]);
//      return null;
//     }
//
//     if( ! pp.isEnum() )
//     {
//      System.out.println("Parameter part is not enumeration. pID=" + req.getParamID() +" partID=" + vpart[0]);
//      return null;
//     }
//     
//     if( p.getPart(vpart[0]) == null )
//     {
//      System.out.println("Parameter has no part with such ID. pID=" + req.getParamID() +" partID=" + vpart[0]);
//      return null;
//     }
//    }
//    
//    ComplexFilteredRowProcessor rp = new ComplexFilteredRowProcessor(req.getParamID(), getParameterPattern(p), variants);
//    rp.setName(p.getCode()+" (filtered)");
//    return rp;
//   }
   else if( ritm instanceof ComplexFilteredRequestItem )
   {
    ComplexFilteredRequestItem cfrq = (ComplexFilteredRequestItem)ritm;
    
    ComplexFilteredRowProcessor rp = new ComplexFilteredRowProcessor(cfrq.getParameterID(), cfrq.getId(), getParameterPattern(p), cfrq.getFilter());

    rp.setName(p.getCode()+" (filtered)");
    return rp;
   }
   else if( ritm instanceof PartRequestItem )
   {
    PartRequestItem prri = (PartRequestItem)ritm;
    ParameterPart pp = p.getPart(prri.getPartID());

    if(pp == null)
    {
     System.out.println("Parameter part not found. pID=" + prri.getParameterID() +" ppID=" + prri.getPartID());
     return null;
    }

    SplitRowProcessor rp = new SplitRowProcessor(prri.getParameterID(), prri.getId(), getParameterPattern(p), prri.getPartID());
    rp.setName(p.getCode()+"."+pp.getName());
    
    return rp;
   }
  }

  return null;
 }
 
// private RowProcessor[] preparePattern( ReportRequest nreq )
// {
//  RequestItem[] req = nreq.getItems();
//  
//  RowProcessor[] pat = new RowProcessor[req.length];
//  
//  for(int i = 0; i < req.length; i++)
//  {
//   pat[i] = convert(req[i], nreq);
//  }
//  
//  return pat;
//
// }
 

 public IDBunch[] getIDs( ReportRequest nreq )
 {
  if( nreq == null )
   return null;
  
  RowProcessor pat = convert( nreq.getRootGroup(), nreq);
  
  IntMap<IDBunch> bunchMap = new IntTreeMap<IDBunch>();
  IDBunch lastBunch = new IDBunch(-1);
  
  for(Record row : data)
  {
   if( ! pat.matchRecord(row) )
    continue;

   int repID = row.getCollectionId();
   
   
   if( lastBunch.getCollectionID() != repID )
   {
    lastBunch = bunchMap.get(repID);
    
    if( lastBunch == null )
    {
     lastBunch = new IDBunch(repID);
     bunchMap.put(repID, lastBunch);
    }
   }
   
   lastBunch.addID( row.getCollectionRecordIDs() );
  }
  
  IDBunch[] res = new IDBunch[bunchMap.size()];
  
  int i=0;
  for( IDBunch bch : bunchMap.values() )
   res[i++]=bch;
  
  return res;
 }
 
 public Summary report2( ReportRequest req )
 {
  RowProcessor pat = convert(req.getRootGroup(), req);

  Summary result = new Summary();
  result.setCount(data.size());
  
  int studyId=0;
  boolean isEligable=false;
  
  if( req.isCollectionSplit() )
  {
   
   int[] khLst = req.getCollections();
   
   if( khLst == null )
   {
    khLst=new int[collectionList.size()];
    int i=0;
    
    for( CollectionShadow ksh : collectionList )
     khLst[i++]=ksh.getId();
   }
   else if( khLst[0] < 0 )
   {
    studyId = (-khLst[0])/2;
    isEligable = (-khLst[0])%2 == 0 ;
    
    for(StudyShadow st : studyList)
    {
     if( st.getId() == studyId )
     {
      khLst = new int[st.getCollections().size()];
      
      int k=0;
      for( int kid : st.getCollections() )
       khLst[k++] = kid;
     }
    }
   }
   
   int i=0;
   Summary[] res = new Summary[khLst.length];
   for(int khId : khLst)
   {
    res[i] = processRecords(pat, khId, studyId,isEligable);
    
    i++;
   }
   
   result.setRelatedCounters(res);
  }
  else
  {
   result.setRelatedCounters( new Summary[]{ processRecords(pat, 0, 0,  false) } );
  }

  return result;
 }
 
/* 
 private boolean matchRecord2(RowProcessor[] pat,  boolean andOp, Record rec)
 {
  Class<GroupRowProcessor> grpClass = GroupRowProcessor.class;

  for( int k=0; k < pat.length; k++ )
  {
   if( pat[k] == null )
    continue;
   
   if( pat[k].getClass() == grpClass )
   {
    boolean groupRes= ! andOp;
    for( RowProcessor rp : ((GroupRowProcessor)pat[k]).getSubProcessors() )
    {
     if( rp.matchRecord( rec ) )
     {
      if( andOp )
      {
       groupRes = true;
       break;
      }
     }
     else
     {
      if( ! andOp )
      {
       groupRes = false;
       break;
      }
     }
    }
    
    if( groupRes )
    {
     if( ! andOp )
      return true;
    }
    else
     if( andOp )
      return false;
   }
   else
   {
    if( pat[k].matchRecord(rec) )
    {
     if( ! andOp )
      return true;
    }
    else
    {
     if( andOp )
      return false;
    }
   }
   
  }
  
  return andOp;
 }
*/ 
/* 
 private int countRecords(RowProcessor[] pat, int ind, int khID, int inc, boolean andOp, IntMap<IntMap<Void>> paramCMap, IntMap<Counter> paramCnt )
 {
  int datalen = data.size();
  
  int count=0;

  int i=ind;
  while( i >= 0 && i < datalen )
  {
   Record rec = data.get(i);
   
   if( rec.getCollectionId() != khID && khID != 0 )
    break;
   
   boolean res = andOp;
   
   Class<GroupRowProcessor> grpClass = GroupRowProcessor.class;
   Class<ParameterRelationRowProcessor> relClass = ParameterRelationRowProcessor.class;
   
   for( int k=0; k < pat.length; k++ )
   {
    if( pat[k] == null )
     continue;
    
    if( pat[k].getClass() == grpClass )
    {
     boolean groupRes= ! andOp;
     for( RowProcessor rp : ((GroupRowProcessor)pat[k]).getSubProcessors() )
     {
      
      if( pat[k].getClass() == relClass )
      {
       boolean localRes=false;
       for( RowProcessor srp : ((ParameterRelationRowProcessor)pat[k]).getAlternatives() )
       {
        if( rp.matchRecord( rec ) )
        {
         IntMap<Void> ils = paramCMap.get(pat[k].getId());
         
         if( ils == null )
         {
          ils = intMapDepot.getObject();
          if( ils == null )
           ils = new IntTreeMap<Void>();
          
          paramCMap.put(pat[k].getId(),ils);
         }
         
         ils.put(srp.getId(), null);
         localRes=true;
        }
       }
       
       if( localRes )
       {
        if( andOp )
         groupRes = true;
       }
       else
       {
        if( ! andOp )
         groupRes = false;
       }
      }
      else if( rp.matchRecord( rec ) )
      {
       if( andOp )
        groupRes = true;
       
       paramCMap.put(rp.getId(), null);
      }
      else
      {
       if( ! andOp )
        groupRes = false;
      }
     }
     
     if( groupRes )
     {
      if( ! andOp )
       res = true;
     }
     else
      if( andOp )
       res = false;
    }
    else if( pat[k].getClass() == relClass )
    {
     for( RowProcessor rp : ((ParameterRelationRowProcessor)pat[k]).getAlternatives() )
     {
      if( rp.matchRecord( rec ) )
      {
       IntMap<Void> ils = paramCMap.get(pat[k].getId());
       
       if( ils == null )
       {
        ils = intMapDepot.getObject();
        if( ils == null )
         ils = new IntTreeMap<Void>();
        
        paramCMap.put(pat[k].getId(),ils);
       }
       
       ils.put(rp.getId(), null);
      }

     }    
    }
    else
    {
     if( pat[k].matchRecord(rec) )
     {
      if( ! andOp )
       res = true;
      
      paramCMap.put(pat[k].getId(), null);
     }
     else
     {
      if( andOp )
       res = false;
     }
    }
    
   }
   
   if( res )
    count++;
   
   for( IntMap.Entry<IntMap<Void>> me : paramCMap.entrySet() )
   {
    Counter cn = paramCnt.get(me.getKey());
    
    if( cn == null )
     paramCnt.put(me.getKey(), cn = new Counter(1) );
    else
     cn.inc();

    if( me.getValue() != null )
    {
     IntIterator iter = me.getValue().keyIterator();
     
     while( iter.hasNext() )
     {
      cn.inc(iter.next());
     }
     
     me.getValue().clear();
     intMapDepot.recycleObject(me.getValue());
    }
   }
   
   paramCMap.clear();
   
   i+=inc;
  }
  
  Counter cn = paramCnt.get(0);
  
  if( cn == null )
   paramCnt.put(0, cn = new Counter() );

  cn.add(inc>0?i-ind:ind-i);
  
  return count;
 }
*/
 
 private Summary processRecords(final RowProcessor pat, final int khID, int studyID, boolean isEligible )
 {
  final IntMap<Counter> paramCnt = new IntTreeMap<Counter>();
  
  if( studyID != 0 )
   processCollection(khID, new PseudoCollectionRecordProcessor(pat, tagParameters, paramCnt, studyID, isEligible));
  else
   processCollection(khID, new CommonRecordProcessor(pat, tagParameters, paramCnt));
  
//  count+=countRecords(pat, ind, khID, 1, andOp, paramCMap, paramCnt);
//  count+=countRecords(pat, ind-1, khID, -1, andOp, paramCMap, paramCnt);
  
  
  Summary blkr = new Summary();
  
  blkr.setId(khID);
  Counter cn=paramCnt.get(-1);
  Summary rc = new Summary(0,cn!=null?cn.getValue():0);
//  blkr.setResultCounter( rc );
  
  IntMap<Counter> tgc =  cn!=null?cn.getTagCounters():null;
  if( tgc != null  && tgc.size() > 0 )
  {
   Summary[] trcs = new Summary[tgc.size()];
   
   int j=0;
   for( IntMap.Entry<Counter> me : tgc.entrySet() )
    trcs[j++] = new Summary(me.getKey(),me.getValue().getValue());
   
   rc.setTagCounters(trcs);
  }


  cn=paramCnt.get(0);
  blkr.setCount( cn!=null?cn.getValue():0);

  tgc =  cn!=null?cn.getTagCounters():null;
  if( tgc != null  && tgc.size() > 0 )
  {
   Summary[] trcs = new Summary[tgc.size()];
   
   int j=0;
   for( IntMap.Entry<Counter> me : tgc.entrySet() )
    trcs[j++] = new Summary(me.getKey(),me.getValue().getValue());
   
   blkr.setTagCounters(trcs);
  }
  
  paramCnt.remove(0);
  paramCnt.remove(-1);
  
  if( paramCnt.size() == 0 )
  {
   blkr.setRelatedCounters( new Summary[]{rc} );
   return blkr;
  }
  
  Summary[] paramsSet = new Summary[paramCnt.size()+1];
  int i=1;
  for( IntMap.Entry<Counter> me : paramCnt.entrySet() )
  {
   cn = me.getValue();
   paramsSet[i] = new Summary( me.getKey(),cn.getValue() );

   tgc =  cn.getTagCounters();
   if( tgc != null  && tgc.size() > 0 )
   {
    Summary[] trcs = new Summary[tgc.size()];
    
    int j=0;
    for( IntMap.Entry<Counter> tme : tgc.entrySet() )
     trcs[j++] = new Summary(tme.getKey(),tme.getValue().getValue());
    
    paramsSet[i].setTagCounters(trcs);
   }

   
   IntMap<Counter> subc = me.getValue().getSubcounters();

   if( subc != null )
   {
    Summary[] rels = new Summary[subc.size()];
    
    int j=0;
    for( IntMap.Entry<Counter> sbme : subc.entrySet() )
    {
     cn = sbme.getValue();
     rels[j] = new Summary(sbme.getKey(),cn.getValue());
     
     tgc =  cn.getTagCounters();
     if( tgc != null  && tgc.size() > 0 )
     {
      Summary[] trcs = new Summary[tgc.size()];
      
      int k=0;
      for( IntMap.Entry<Counter> tme : tgc.entrySet() )
       trcs[k++] = new Summary(tme.getKey(),tme.getValue().getValue());
      
      rels[j].setTagCounters(trcs);
     }
     
     j++;
    }
    
    paramsSet[i].setRelatedCounters(rels);
   }
   
   i++;
  }
  
  paramsSet[0]=rc;
  blkr.setRelatedCounters(paramsSet);
  
  return blkr;
 }
 
 
 /*
 public Report report( ReportRequest req)
 {
  Report chain = new Report();
  
  RowProcessor[] pat = preparePattern(req);
  
  for( int i=0; i < pat.length; i++ )
   chain.addHeader( pat[i].getName() );

  chain.setCount(data.size());

  long start = System.currentTimeMillis();
  for(Record row : data)
  {
   processPatterns( pat, chain, row );
  }
  long stop = System.currentTimeMillis();
  
  System.out.println("Time: "+(stop-start)+"ms. Or "+( (stop-start)/(double)data.size()*1000 )+"s per 1000000 recs");

  return chain;
 }
*/
 
 private void processCollection( int cohID, RecordProcessor rp )
 {
  int ind;
  
  int datasize = data.size();

  if( cohID > 0 )
   ind= findRecordByCollection(cohID);
  else
   ind = datasize/2;
  
  if( ind < 0 )
   return;
  
  
  int i=ind;
  while( i < datasize )
  {
   Record r = data.get(i);
   if( r.getCollectionId() != cohID && cohID != 0 )
    break;
   
   rp.process(r);
   
   i++;
  }
  
  i=ind-1;
  while( i >= 0)
  {
   Record r = data.get(i);
   if( r.getCollectionId() != cohID && cohID != 0 )
    break;
   
   rp.process(r);
   
   i--;
  }

 }
 
 private void processPseudoCollection( int studyID, boolean isEligible, IntMap<Counter> res )
 {
  StudyShadow ssh = null;
  
  for( StudyShadow sh : studyList )
  {
   if( sh.getId() == studyID )
   {
    ssh=sh;
    break;
   }
  }
  
  if( ssh == null )
   return;
  
  for( int khID : ssh.getCollections() )
   processCollection(khID, new StudySampleRecordProcessor(studyID,isEligible,res) );
 }


 public Summary getCollectionSummary( int cohID )
 {
  Summary summary = collectionSummaryCache.get( cohID );
  
  if( summary != null )
   return summary;

  summary = new Summary(cohID);
  summary.setComment("Collection ID="+cohID);
  
  IntMap<Counter> res = new IntTreeMap<Counter>();
  
  long start = System.currentTimeMillis();
  
  if( cohID < 0 )
   processPseudoCollection((-cohID)/2, (-cohID)%2 == 0, res);
  else
   processCollection(cohID, new CollectionSummaryRecordProcessor(res));
  
  long delta = System.currentTimeMillis()-start;
  
  Counter cn = res.get(0);
  
  summary.setCount(cn==null?0:cn.getValue());
  
  if( cn!=null && cn.getTagCounters() != null )
  {
   Summary[] rcs = new Summary[ cn.getTagCounters().size() ];
   
   int i=0;
   for( IntMap.Entry<Counter> me : cn.getTagCounters().entrySet() )
    rcs[i++]=new Summary(me.getKey(),me.getValue().getValue(),"Collection with tag ID="+me.getKey());
   
   summary.setTagCounters(rcs);

  }

//  System.out.println("Delta: "+delta+"ms. Per record: "+delta/summary.getCount()+" Per 1M records: "+delta*1000000/summary.getCount());
  
  res.remove(0);
  

  Summary[] prms = new Summary[ res.size() ];
  
  int i=0;
  for( IntMap.Entry<Counter> me : res.entrySet() )
  {
   Counter pcn = me.getValue();
   Summary rc = new Summary(me.getKey(),pcn.getValue(),"Parameter ID="+me.getKey());

   if( pcn.getSubcounters() != null )
   {
    Summary[] rcs = new Summary[ pcn.getSubcounters().size() ];
    
    int j=0;
    for( IntMap.Entry<Counter> tcme : pcn.getSubcounters().entrySet() )
    {
     Counter vcn = tcme.getValue();
     
     Summary tSm=new Summary(tcme.getKey(),vcn.getValue(),"Variant ID="+tcme.getKey());

     if( vcn.getTagCounters() != null )
     {
      Summary[] vtcs = new Summary[ vcn.getTagCounters().size() ];

      int m=0;
      for( IntMap.Entry<Counter> vtcme : vcn.getTagCounters().entrySet() )
       vtcs[m++] = new Summary(vtcme.getKey(),vtcme.getValue().getValue(),"Variant ID="+tcme.getKey()+" with tag ID="+vtcme.getKey());
      
      tSm.setTagCounters(vtcs);
     }
     
     
     rcs[j++]=tSm;
    }
    
    rc.setRelatedCounters(rcs);
   }

   if( pcn.getTagCounters() != null )
   {
    Summary[] rcs = new Summary[ pcn.getTagCounters().size() ];
    
    int j=0;
    for( IntMap.Entry<Counter> tcme : pcn.getTagCounters().entrySet() )
     rcs[j++]=new Summary(tcme.getKey(),tcme.getValue().getValue(),"Parameter ID="+me.getKey()+" with tag ID="+tcme.getKey());
    
    rc.setTagCounters(rcs);
   }
   
   prms[i++] = rc;
  }
  
  summary.setRelatedCounters(prms);
 
  collectionSummaryCache.put(cohID, summary);

  return summary;
 }
 
 public Summary getStudySummary( int stID )
 {
  Summary summary = new Summary(stID);
  summary.setComment("Study ID="+stID);

  StudyShadow ssh = null;
  
  for( StudyShadow sh : studyList )
  {
   if( sh.getId() == stID )
   {
    ssh=sh;
    break;
   }
  }
  
  if( ssh == null )
  {
   logger.error("Request for non-existent study ID="+stID);
   return null;
  }
  
  Summary[] studySummary = new Summary[3];
  Summary[] cohs = new Summary[ssh.getCollections().size()];
  
  Summary studySumm = new Summary();
  studySumm.setComment("Study summary");
  
  studySummary[0] = studySumm;
  studySummary[1] = getCollectionSummary(-stID*2);
  studySummary[2] = getCollectionSummary(-(stID*2+1));
  
  IntMap<Summary> tmap = new IntTreeMap<Summary>();
  IntMap<Summary> pmap = new IntTreeMap<Summary>();
  IntMap<IntMap<Summary>> ptmap = new IntTreeMap<IntMap<Summary>>();
  
  int i=0;
  for( int collID : ssh.getCollections() )
  {
   Summary collSumm = getCollectionSummary(collID);

   studySumm.setCount(studySumm.getCount()+collSumm.getCount());
   
   if( collSumm.getRelatedCounters() != null )
   {
    for( Summary colPrmSum: collSumm.getRelatedCounters() )
    {
     Summary stPSum = pmap.get(colPrmSum.getId());
     
     if( stPSum == null )
     {
      stPSum=new Summary(colPrmSum.getId(),0,"Parameter ID="+colPrmSum.getId());
      pmap.put(colPrmSum.getId(), stPSum);
     }
     
     stPSum.setCount( stPSum.getCount() + colPrmSum.getCount() );
     
     stPSum.setRelatedCounters(aggregateSummary(stPSum.getRelatedCounters(), colPrmSum.getRelatedCounters()));
//     stPSum.setTagCounters(aggregateSummary(stPSum.getTagCounters(), colPrmSum.getTagCounters()));

     /*     
     Summary[] relC = colPrmSum.getRelatedCounters();
     if( relC != null )
     {
      if(stPSum.getRelatedCounters() == null)
      {
       Summary[] vrnt = new Summary[relC.length];
       stPSum.setRelatedCounters(vrnt);
       
       int j=0;
       for(Summary s : relC)
        vrnt[j++]=new Summary(s.getId(),s.getCount(),"Variant ID="+s.getId());
      }
      else
      {
       for(Summary collVrSumm : relC)
       {
        boolean found = false;
        for(Summary stVrSum : stPSum.getRelatedCounters())
        {
         if( collVrSumm.getId() == stVrSum.getId() )
         {
          found=true;
          stVrSum.setCount(stVrSum.getCount()+collVrSumm.getCount());
          break;
         }
        }
        
        if( ! found )
        {
         int j=stPSum.getRelatedCounters().length+1;
         Summary[] newStVrSumm = new Summary[j];
         newStVrSumm[--j]=new Summary(collVrSumm.getId(),collVrSumm.getCount(),"Variant ID="+collVrSumm.getId());
         
         while( --j >= 0 )
          newStVrSumm[j]=stPSum.getRelatedCounters()[j];
        }
       }
      }
     }
      */    
     if( colPrmSum.getTagCounters() != null )
     {
      for( Summary tsum : colPrmSum.getTagCounters() )
      {
       Summary ptSumm=null;
       
       IntMap<Summary> ptm = ptmap.get(colPrmSum.getId());
       if( ptm == null )
       {
        ptm = new IntTreeMap<Summary>();
        ptSumm = new Summary(tsum.getId(),0,"Parameter ID="+colPrmSum.getId()+" with tag ID="+tsum.getId());
        ptm.put(tsum.getId(),ptSumm);
        ptmap.put(colPrmSum.getId(), ptm);
       }
       else
       {
        ptSumm = ptm.get(tsum.getId());
        if( ptSumm == null )
        {
         ptSumm = new Summary(tsum.getId(),0,"Parameter ID="+colPrmSum.getId()+" with tag ID="+tsum.getId());
         ptm.put(tsum.getId(),ptSumm);
        }
       }
       
       ptSumm.setCount(ptSumm.getCount()+tsum.getCount());
      }
     }
     

    }
   }
   
   if( collSumm.getTagCounters() != null )
   {
    for( Summary tagSmry : collSumm.getTagCounters() )
    {
     Summary ts = tmap.get(tagSmry.getId());

     if(ts == null)
     {
      ts = new Summary(tagSmry.getId(),0,"Study with tag ID="+tagSmry.getId());
      tmap.put(tagSmry.getId(), ts);
     }

     ts.setCount(ts.getCount() + tagSmry.getCount());
    }
   }
   
   cohs[i++]=collSumm;
  }
  
  if( tmap.size() > 0 )
  {
   Summary[] tagSmrys = new Summary[tmap.size()];
   
   i=0;
   
   for( Summary ts : tmap.values() )
    tagSmrys[i++]=ts;
   
   studySumm.setTagCounters(tagSmrys);
  }
  
  if( pmap.size() > 0 )
  {
   Summary[] paramSmrys = new Summary[pmap.size()];

   i=0;
   
   for( Summary ps : pmap.values() )
   {
    paramSmrys[i++]=ps;

    IntMap<Summary> ptsm = ptmap.get( ps.getId() );
    
    if( ptsm != null )
    {
     Summary[] pTgSums = new Summary[ ptsm.size() ];
     
     int j=0;
     for( Summary ts : ptsm.values() )
      pTgSums[j++]=ts;
     
     ps.setTagCounters(pTgSums);
    }
   }
   
   studySumm.setRelatedCounters(paramSmrys);
  }
  
  summary.setRelatedCounters(cohs);
  summary.setTagCounters(studySummary);
  
  return summary;
 }
 
 private Summary[] aggregateSummary(Summary[] source, Summary[] add)
 {
  if( add == null )
   return source;
  
  if( source == null )
  {
   Summary[] res = new Summary[add.length];
   
   for(int i=add.length-1; i >= 0; i--)
   {
    res[i]=new Summary(add[i].getId(),add[i].getCount(),add[i].getComment());
    res[i].setRelatedCounters(aggregateSummary(null, add[i].getRelatedCounters()));
    res[i].setTagCounters(aggregateSummary(null, add[i].getTagCounters()));
   }
   
   return res;
  }
   
  
  for(Summary addSum : add)
  {
   boolean found = false;
   for(Summary srcSum : source)
   {
    if( srcSum.getId() == addSum.getId() )
    {
     found=true;
     srcSum.setCount(srcSum.getCount()+addSum.getCount());
     srcSum.setRelatedCounters(aggregateSummary(srcSum.getRelatedCounters(), addSum.getRelatedCounters()));
     srcSum.setTagCounters(aggregateSummary(srcSum.getTagCounters(), addSum.getTagCounters()));
     break;
    }
   }
   
   if( ! found )
   {
    Summary[] res = new Summary[source.length+1];
    
    for(int i=source.length-1; i >= 0; i--)
     res[i]=source[i];

    res[source.length]=new Summary(addSum.getId(),addSum.getCount(),addSum.getComment());
    res[source.length].setRelatedCounters(aggregateSummary(null, addSum.getRelatedCounters()));
    res[source.length].setTagCounters(aggregateSummary(null, addSum.getTagCounters()));

    source = res;
   }
  }
  
  return source;
 }

 /*
 public Summary getStudySummaryXXX( int stID )
 {
  Summary summary = studySummaryCache.get( stID );
  
  if( summary != null )
   return summary;

  
  summary = new Summary(stID);
  
  IntMap<Counter> res = new IntTreeMap<Counter>();
  
  StudyShadow ssh = null;
  
  for( StudyShadow sh : studyList )
  {
   if( sh.getId() == stID )
   {
    ssh=sh;
    break;
   }
  }
  
  if( ssh == null )
  {
   logger.error("Request for non-existent study ID="+stID);
   return null;
  }
  
  for( int khID : ssh.getCollections() )
   processCollection(khID, new CollectionSummaryRecordProcessor(res) );

  Counter cn = res.get(0);
  
  summary.setCount(cn==null?0:cn.getValue());
  
  IntMap<Counter> tgc =  cn.getTagCounters();
  if( tgc != null  && tgc.size() > 0 )
  {
   Summary[] trcs = new Summary[tgc.size()];
   
   int k=0;
   for( IntMap.Entry<Counter> tme : tgc.entrySet() )
    trcs[k++] = new Summary(tme.getKey(),tme.getValue().getValue());
   
   summary.setTagCounters(trcs);
  }
  
  res.remove(0);
  
  Summary[] prms = new Summary[ res.size() ];
  
  int i=0;
  for( IntMap.Entry<Counter> me : res.entrySet() )
  {
   cn = me.getValue();
   prms[i] = new Summary(me.getKey(),cn.getValue());
   
   tgc =  cn.getTagCounters();
   if( tgc != null  && tgc.size() > 0 )
   {
    Summary[] trcs = new Summary[tgc.size()];
    
    int k=0;
    for( IntMap.Entry<Counter> tme : tgc.entrySet() )
     trcs[k++] = new Summary(tme.getKey(),tme.getValue().getValue());
    
    prms[i].setTagCounters(trcs);
   }
   
   i++;
  }
  
  summary.setRelatedCounters(prms);
 
  studySummaryCache.put(stID, summary);

  return summary;
 }
*/
 
 /*
 public int[] getParametersByCollection( int cohID )
 {
  if( collectionSummaryCache.containsKey(cohID) )
   return collectionSummaryCache.get(cohID);
  
  IntMap<Void> pids = new IntTreeMap<Void>();
  
  for( Record r : data )
  {
   if( r.getCollectionId() == cohID )
   {
    for( PartValue pv : r.getPartValues() )
     pids.put(pv.getPart().getParameter().getId(), null);
   }
  }
  
  int[] res = new int[pids.size()];
  
  int i=0;
  IntIterator keyIter = pids.keyIterator();
  while( keyIter.hasNext() )
   res[i++]=keyIter.next();
  
  collectionSummaryCache.put(cohID, res);
  
  return res;
 }
 */
 
 /*
 public int[] getParametersByStudy( int stID )
 {
  if( studySummaryCache.containsKey(stID) )
   return studySummaryCache.get(stID);
 
  long start = System.currentTimeMillis();
  
  int[] collections = null;
  for( StudyShadow stsh : studyList )
  {
   if( stsh.getId() == stID )
   {
    collections = new int[stsh.getCollections().size()];
    
    int i=0;
    
    for( int khID : stsh.getCollections() )
     collections[i++]=khID;
    
    break;
   }
  }
  
  if( collections == null )
   return new int[0];
  
  Arrays.sort(collections);
  
  IntMap<Void> pids = new IntTreeMap<Void>();
  
  int cKhId=-1;
  boolean applicableCollection = false;

  for( Record r : data )
  {
   if( r.getCollectionId() == cKhId )
   {
    if( ! applicableCollection )
      continue;
   }
   else
   {
    cKhId = r.getCollectionId();
    applicableCollection = Arrays.binarySearch(collections, cKhId) >= 0;
   }

   if(applicableCollection)
    for(PartValue pv : r.getPartValues())
     pids.put(pv.getPart().getParameter().getId(), null);
    
  }
  
  int[] res = new int[pids.size()];
  
  int i=0;
  IntIterator keyIter = pids.keyIterator();
  while( keyIter.hasNext() )
   res[i++]=keyIter.next();
  
  long stop = System.currentTimeMillis();
  
  System.out.println("Study 2 param evaluation: "+(stop-start)+"ms. Or "+( (stop-start)/(double)data.size()*1000 )+"s per 1000000 recs");

  studySummaryCache.put(stID, res);
  
  return res;
 }
*/
 
/* 
 private void processPatterns( RowProcessor[] pat, SimpleCounter cpos, Record row)
 {
  for(int i = 0; i < pat.length; i++)
  {
   cpos =  pat[i].processPatterns(pat, i, cpos, row);
   
   if( cpos == null )
    return;
  }
 }
*/
 
 /*
 
 @Deprecated
 private void processPatterns( Object[] pat, int offset, SimpleCounter cpos, Record row)
 {
  for(int i = offset; i < pat.length; i++)
  {
   if( pat[i] instanceof FilteredRequestItem )
   {
    PartValue pv = row.getPartValue(((FilteredRequestItem)pat[i]).getPartID());

    if(pv == null)
     break;

    Variant key = pv.getPart().getVariant(pv.getVariant());

    if(key != null && Arrays.binarySearch(((FilteredRequestItem)pat[i]).getVariants(), key.getId()) >= 0 )
    {
     if(cpos.getRef() == null)
      cpos.setRef(new SimpleCounter());

     cpos = cpos.getRef();

     SimpleCounter sc = cpos.getRef(key.getName());
     sc.inc();
     cpos = sc;
    }
    else
     break;

   }
   else if(pat[i] instanceof int[])
   {
    if( ! matchPattern((int[]) pat[i], row))
    {
//     for( int k=i; k < pat.length; k++ )
//     {
//      if(cpos.getRef() == null)
//       cpos.setRef(new SimpleCounter());
//      
//      cpos=cpos.getRef();
//     }
     return;
    }
    
    if(cpos.getRef() == null)
     cpos.setRef(new SimpleCounter());

    cpos = cpos.getRef();
    cpos.inc();
   }
   else if( pat[i] instanceof GpPat[] )
   {
    GpPat[] gpp = (GpPat[])pat[i];

    boolean matched=false;
    for( int k=0; k < gpp.length; k++ )
    {
     if( gpp[k]==null )
      break;
     
     if( matchPattern(gpp[k].pat, row) )
     {
      matched=true;
      
      if( cpos.getRef() == null )
       cpos.setRef( new SimpleCounter() );

      SimpleCounter sc = cpos.getRef().getRef(gpp[k].name);
      sc.inc();
      processPatterns(pat,i+1,sc,row);
     }
     
    }

    if( matched )
    {
     SimpleCounter sc = cpos.getRef().getRef("[ANY]");
     sc.inc();
     processPatterns(pat,i+1,sc,row);
    }
    return;
   }
   else
   {
    int ptid = (Integer) pat[i];
//    String key = row[col]==null?"[not set]":row[col];
    
    PartValue pv = row.getPartValue(ptid);
    
    if( pv == null )
     break;
    
    Variant key = pv.getPart().getVariant(pv.getVariant());

    if(key != null)
    {
     if( cpos.getRef() == null )
      cpos.setRef( new SimpleCounter() );
     
     cpos=cpos.getRef();
     
     SimpleCounter sc = cpos.getRef(key.getName());
     sc.inc();
     cpos = sc;
    }
    else
     break;

   }
  }  
 }

 */
 
 private Parameter findParameter(int pid)
 {
  return params.get(pid);
 }

 private int[] prepareParameterPattern(Parameter p)
 {
  Collection<Variable> v = p.getAllVariables();
  Collection<Qualifier> q = p.getAllQualifiers();

  IntList pt = new ArrayIntList( (v == null ? 0 : v.size()) + (q == null ? 0 : q.size()) );
  
  if(v != null)
  {
   for(Variable vr : v)
   {
    if( vr.isMandatory() )
     pt.add(vr.getId());
   }
  }

  if(q != null)
  {
   for(Qualifier ql : q)
   {
    if( ql.isMandatory() )
     pt.add(ql.getId());
   }
  }

  int[] clms = pt.toArray();
  
  Arrays.sort(clms);
  
  return clms;
 }
 
 private int[] getParameterPattern(Parameter p)
 {
  SSParameterInfo sspi  = (SSParameterInfo)p.getAuxInfo();
  
  return sspi.getPattern();
 }
 
 private RowProcessor getParameterProcessor( Parameter p, int reqID )
 {
  Collection<Variable> allV = p.getAllVariables();
  
  if( allV != null && allV.size() > 0 )
   return new ParameterRowProcessor(p.getId(), reqID, getParameterPattern(p));
  
  if( ((SSParameterInfo)p.getAuxInfo()).getChildren() == null )
   return new UnmatchedRowProcessor( p.getId(), reqID );
  
  ParameterAlternativeRowProcessor arp = new ParameterAlternativeRowProcessor(p.getId(), reqID, new int[]{p.getId()}, new ArrayList<RowProcessor>());
  
  for( Parameter cp : ((SSParameterInfo)p.getAuxInfo()).getChildren())
  {
   RowProcessor crp=getParameterProcessor(cp, reqID);
   
   if( crp instanceof ParameterAlternativeRowProcessor )
    arp.merge((ParameterAlternativeRowProcessor)crp);
   else if( crp instanceof ParameterRowProcessor )
    arp.add( crp );
  }
  
  if( arp.getAlternatives().size() == 0 )
   return new UnmatchedRowProcessor( p.getId(), reqID );
  
  return arp;
 }


 public void importParameters( String txt )  throws ParseException, ParameterManagementException
 {
  for( Parameter p : ParameterFormat.parse(txt, params.values(), classifiersList, tags.values()) )
  {
   addParameterRec(p);
  }
 }

 private int addParameterRec( Parameter p ) throws ParameterManagementException
 {
  if( p.getId() != 0 )
   return p.getId();

  if( p.getInheritedParameters() != null )
  {
   for( Parameter ip : p.getInheritedParameters() )
    addParameterRec(ip);
  }
 
  if( p.getRelations() != null )
  {
   for( Relation rl : p.getRelations() )
    addParameterRec(rl.getTargetParameter());
  }

  ParameterShadow ps = addParameter( new ParameterShadow(p) );
  
  int id = ps.getId();
  p.setId(id);
  return id;
 }
 
 
 public void importData(String txt, int collectionID ) throws ParseException
 {
  studySummaryCache.clear();
  collectionSummaryCache.clear();
  
  Collection<Record> rcs = new ArrayList<Record>();
  int cpos = 0;
  int len = txt.length();
  int  ln = 0;

  char colSeparator = '\0';
  
  if( ! txt.startsWith(SAMPLE_ID_COL) )
  {
   if( ! txt.startsWith("\""+SAMPLE_ID_COL+"\"") )
    throw new ParseException(1,"The first column must be "+SAMPLE_ID_COL);
   
   colSeparator = txt.charAt(SAMPLE_ID_COL.length()+2);
  }
  else
   colSeparator = txt.charAt(SAMPLE_ID_COL.length());
  
  if( colSeparator != '\t' && colSeparator != ',')
   throw new ParseException(1,"Column separator must be either tab or comma");
  
  String sep = ""+colSeparator;
  
  String lineSep = "\r\n";
  
  if( txt.indexOf(lineSep) == -1)
   lineSep="\n";

  if( txt.indexOf(lineSep) == -1)
   throw new ParseException(1,"File must contains at least 2 lines separated by either \\n or \\r\\n "); 
  
  Map<ParameterPart,List<Variant>> tmpVarisMap = new TreeMap<ParameterPart, List<Variant>>();
  
//  String qSep = "\""+sep;
  
  List<String> parts = new ArrayList<String>(100);
  FullPartRef[] pparts=null;
  while(cpos < len)
  {
//   ln++;
//   System.out.println("Line: "+ln);
   int pos = txt.indexOf(lineSep, cpos);

   if(pos == -1)
    break;

   parts.clear();
   StringUtil.splitExcelString(txt.substring(cpos, pos), sep, parts);
   
//   String[] parts = txt.substring(cpos, pos).split(sep,-2);

   
   if(ln==0)
   {
    ln++;
    pparts = analyzeHeader(parts);
   }
   else
   {
    ln++;
//    String[] row = new String[header.length];
    
    if( parts.size() == 0 )
     break;
    
    Record rc = new Record();
    rc.setCollectionId(collectionID);
    
    for(int i = -1; i < pparts.length; i++)
    {
     if( i == -1 )
      rc.setCollectionRecordIDs(parts.get(0));
     else
     {
      String val = "";
      
      if( i+1 < parts.size() )
       val = parts.get(i+1).trim();
      
      ParameterPart pp = pparts[i].getParameterPart();
      if( val.length() != 0 )
      {

       if( pp.isEnum() )
       {
        short vid = pp.getVariantIndexByValue(val);
        // short vid = pparts[i].getParameterPart().getVariantID(val);
        
        if( vid == -1 )
        {
         if(pp.isPredefined())
          throw new ParseException(ln,"Variant '"+val+"' is not allowed for column: '"
            +pparts[i].getParameter().getCode()+'.'+pp.getName()+"'");
         
         List<Variant> tmpVaris = tmpVarisMap.get(pp);
         
         short n=-1;
         if( tmpVaris == null )
         {
          tmpVaris=new ArrayList<Variant>(5);
          tmpVarisMap.put(pp,tmpVaris);
         }
         else
         {
          n=1;
          for( Variant v : tmpVaris )
          {
           if( v.getName().equals(val) )
           {
            v.incCount();
            break;
           }
           
           n++;
          }
          
          if( n > tmpVaris.size() )
           n=-1;
         }
         
         if( n < 0 )
         {
          Variant nV = new Variant(val,0,false);
          nV.setId(0);
          nV.incCount();
          
          tmpVaris.add(nV);
          
          n=(short)tmpVaris.size();
          
         }

         vid=(short)-n;
        }
        

        VariantPartValue pv = new VariantPartValue(pp);

        pv.setVariant(vid);
        rc.addPartValue(pv);
        
       }
       else 
       {
        if( ParameterPart.SECURED_VARIANT_SIGN.equals(val) )
        {
         PartValue pv = new PartValue(pp);
         rc.addPartValue(pv);
        }
        else
        {
         Variable vrbl = (Variable)pp;
         
         if( vrbl.getType() == Type.REAL )
         {
          float realValue;
          try
          {
           realValue = Float.parseFloat(val);
          }
          catch (Exception e) 
          {
           throw new ParseException(ln,"Invalid value for REAL type column "
             +pparts[i].getParameter().getCode()+'.'+pp.getName());
          }
          
          RealPartValue rpv = new RealPartValue(pp);
          rpv.setRealValue(realValue);
          rc.addPartValue(rpv);
         }
         else if( vrbl.getType() == Type.INTEGER || vrbl.getType() == Type.DATE )
         {
          int intValue;
          try
          {
           intValue = Integer.parseInt(val);
          }
          catch (Exception e) 
          {
           throw new ParseException(ln,"Invalid value for "+vrbl.getType().name()+" type column "
             +pparts[i].getParameter().getCode()+'.'+pp.getName());
          }
          
          IntPartValue ipv = new IntPartValue(pp);
          ipv.setIntValue(intValue);
          rc.addPartValue(ipv);         
         }
         else if( vrbl.getType() == Type.BOOLEAN )
         {
          boolean boolValue;
          try
          {
           boolValue = Boolean.parseBoolean(val);
          }
          catch (Exception e) 
          {
           throw new ParseException(ln,"Invalid value for "+vrbl.getType().name()+" type column "
             +pparts[i].getParameter().getCode()+'.'+pp.getName());
          }
          
          if( (! boolValue ) && "1".equals(val) )
           boolValue=true;
          
          IntPartValue ipv = new IntPartValue(pp);
          ipv.setIntValue(boolValue?1:0);
          rc.addPartValue(ipv);         
         }
         else
          throw new ParseException(ln,"Invalid value for "+vrbl.getType().name()+" type column "
            +pparts[i].getParameter().getCode()+'.'+pp.getName());
        }
       }
      }
      else
      {
       PartValue pv = new EmptyPartValue(pp);
       rc.addPartValue(pv);
      }
      
     }
    }

    rcs.add(rc);
   }

   cpos = pos + lineSep.length();
  }
  
  
  Connection conn = null;
  ResultSet rst = null;
  try
  {
   conn = dSrc.getConnection();

   PreparedStatement recstmt = conn.prepareStatement("INSERT INTO "+TBL_RECORD+" ("
     +FLD_COLLECTION_RECORD_ID+","+FLD_COUNT+","+FLD_COLLECTION_ID+") VALUES (?,1,?)",Statement.RETURN_GENERATED_KEYS);
   PreparedStatement contstmt = conn.prepareStatement("INSERT INTO "+TBL_RECORD_CONTENT+" ("
     +FLD_INT_VALUE+","+FLD_REAL_VALUE+","+FLD_RECORD_ID+","+FLD_PART_ID+") VALUES (?,?,?,?)");
   PreparedStatement updcontstmt = conn.prepareStatement("UPDATE "+TBL_RECORD_CONTENT+" SET "
     +FLD_INT_VALUE+"=?,"+FLD_REAL_VALUE+"=? WHERE "+FLD_RECORD_ID+"=? AND "+FLD_PART_ID+"=?");
   PreparedStatement rmcontstmt = conn.prepareStatement("DELETE FROM "+TBL_RECORD_CONTENT+" WHERE "
     +FLD_RECORD_ID+"=? AND "+FLD_PART_ID+"=?");
   PreparedStatement insvarstmt =  conn.prepareStatement("INSERT INTO "+TBL_VARIANT+" ("
     +FLD_PART_ID+','
     +FLD_NAME+','
     +FLD_VARI_CODING+','
     +FLD_PREDEFINED+
     ") VALUES (?,?,0,0)", Statement.RETURN_GENERATED_KEYS );
   
   PreparedStatement delRecStmt=null;
   PreparedStatement delRecContStmt=null;

   for( Record r : rcs )
   {
    Record exR = findRecord(r);

    int rid = 0;
    if(exR == null)
    {
     if( r.getPartValues().size() > 0)
     {
      recstmt.setString(1, r.getCollectionRecordIDs());
      recstmt.setInt(2, collectionID);
      recstmt.executeUpdate();

      rst = recstmt.getGeneratedKeys();

      if(rst.next())
       rid = rst.getInt(1);

      r.setId(rid);

      data.add(r);
     }
    }
    else
    {
     rid = exR.getId();
   
     if( r.getPartValues().size() == 0 )
     {
      if( delRecStmt == null )
      {
       delRecStmt = conn.prepareStatement("DELETE FROM "+TBL_RECORD+" WHERE "+FLD_ID+"=?");
       delRecContStmt = conn.prepareStatement("DELETE FROM "+TBL_RECORD_CONTENT+" WHERE "+FLD_RECORD_ID+"=?");
      }
      
      delRecStmt.setInt(1, rid);
      delRecStmt.executeUpdate();
      
      delRecContStmt.setInt(1, rid);
      delRecContStmt.executeUpdate();
      
      data.remove(exR);
      
      for( PartValue pv : exR.getPartValues() )
      {
       if( pv instanceof VariantPartValue )
        pv.getPart().uncountVariantByIndex(((VariantPartValue)pv).getVariant());
      }
      
      continue;
     }
    
    }
    
    for(PartValue pv : r.getPartValues())
    {

     PartValue opv = exR != null ? exR.getPartValue(pv.getPartID()) : null;

     if(pv instanceof EmptyPartValue)
     {
      if(opv != null)
      {
       exR.removePartValue(opv);

       rmcontstmt.setInt(1, rid);
       rmcontstmt.setInt(2, pv.getPartID());

       rmcontstmt.executeUpdate();
       
       if( opv instanceof VariantPartValue )
        opv.getPart().uncountVariantByIndex(((VariantPartValue)opv).getVariant());
       
       if( exR.getPartValues().size() == 0 )
       {
        if( delRecStmt == null )
        {
         delRecStmt = conn.prepareStatement("DELETE FROM "+TBL_RECORD+" WHERE "+FLD_ID+"=?");
         delRecContStmt = conn.prepareStatement("DELETE FROM "+TBL_RECORD_CONTENT+" WHERE "+FLD_RECORD_ID+"=?");
        }
        
        delRecStmt.setInt(1, exR.getId());
        delRecStmt.executeUpdate();
        
        delRecContStmt.setInt(1, exR.getId());
        delRecContStmt.executeUpdate();       
       
        data.remove(exR);
        exR=null;
       }
      }
     }
     else
     {
      PreparedStatement stmt = null;

      if(opv != null)
       stmt = updcontstmt;
      else
       stmt = contstmt;

      stmt.setInt(3, rid);
      stmt.setInt(4, pv.getPartID());

      if(pv instanceof IntPartValue)
      {
       stmt.setInt(1, ((IntPartValue) pv).getIntValue());
       stmt.setNull(2, java.sql.Types.FLOAT);
      }
      else if(pv instanceof RealPartValue)
      {
       stmt.setNull(1, java.sql.Types.INTEGER);
       stmt.setFloat(2, ((RealPartValue) pv).getRealValue());
      }
      else if(pv instanceof VariantPartValue)
      {
       short vidx = ((VariantPartValue) pv).getVariant();

       Variant tv = null;
       if(vidx < 0)
       {
        List<Variant> tmpVaris = tmpVarisMap.get(pv.getPart());

        tv = tmpVaris.get((-vidx) - 1);

        if(tv.getId() == 0)
        {
         insvarstmt.setInt(1, pv.getPartID());
         insvarstmt.setString(2, tv.getName());

         insvarstmt.executeUpdate();

         ResultSet vrst = insvarstmt.getGeneratedKeys();
         vrst.next();
         tv.setId(vrst.getInt(1));

         vrst.close();

         short nidx = pv.getPart().addVariant(tv);

         tv.setCoding(nidx);

         ((VariantPartValue) pv).setVariant(nidx);
        }
        else
         ((VariantPartValue) pv).setVariant((short) tv.getCoding());
       }
       else
       {
        tv = pv.getPart().getVariant(vidx);
        tv.incCount();
       }
       
       stmt.setInt(1, tv.getId());
       stmt.setNull(2, java.sql.Types.FLOAT);
      }
      else
      {
       stmt.setNull(1, java.sql.Types.INTEGER);
       stmt.setNull(2, java.sql.Types.FLOAT);
      }

      stmt.executeUpdate();

      if(opv != null)
       exR.removePartValue(opv);

      if(exR != null)
       exR.addPartValue(pv);
     }
    }

    if(exR != null)
     exR.completeRecord();
    else
     r.completeRecord();

   }
   
   recstmt.close();
   contstmt.close();
   updcontstmt.close();
   rmcontstmt.close();
   insvarstmt.close();
   
   for( List<Variant> tvl : tmpVarisMap.values() )
    for( Variant tv : tvl )
     tv.setCoding(0);
   
   Statement stmt = conn.createStatement();
   stmt.executeUpdate("UPDATE "+TBL_COLLECTION+" SET "+FLD_LAST_UPDATE+"="+System.currentTimeMillis()+" WHERE "+FLD_ID+"="+collectionID);
   stmt.close();
   
   Collections.sort(data, RecordComparator.getIntstance() );

   prepareCounts(); // TODO count only parameter with new data
   
  }
  catch(SQLException e)
  {
   Log.error("SQL error", e);
   throw new ParseException(0,"SQL error");
  }
  catch( Exception e1 )
  {
   logger.error("Data import error",e1);
   throw new ParseException(0,"Unknown error: "+e1.getMessage());
  }
  finally
  {
   if(rst != null)
    try
    {
     rst.close();
    }
    catch(SQLException e)
    {
    }

   if(conn != null)
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }

  }
  
 }
 
 public void importRelations(String txt) throws ParseException
 {
  String SEP = null;
  
  int cpos = 0;
  int len = txt.length();
  
  String lineSep = "\n";
  
  cpos = txt.indexOf(lineSep);
  
  if( cpos == -1 )
   throw new ParseException(1,"File must contain at least one line");
  
  if( cpos > 0 && txt.charAt(cpos-1) == '\r' )
   lineSep="\r\n";
  
  cpos = 0;
  
  List<Relation> newRels = new ArrayList<Relation>();
  
  int ln=0;
  while(cpos < len)
  {
   ln++;
   int pos = txt.indexOf(lineSep, cpos);

   if(pos == -1)
    break;

   String str = txt.substring(cpos, pos);
   cpos = pos+lineSep.length();
   
   if( str.length() == 0 )
    continue;

   if( SEP == null )
   {
    if( str.indexOf('\t') > 0 )
     SEP = "\t";
    else
     SEP = ",";
   }
   
   String[] parts = str.split(SEP);

   if( parts.length != 4 )
    throw new ParseException(ln,"Invalid syntax. Must be <Host> <Classifier> <Tag> <Target>");
   
   Parameter hp = paramCodeMap.get(parts[0]);
   
   if( hp == null )
    throw new ParseException(ln,"Invalid or unknown parameter code: '"+parts[0]+"'");
   
   Parameter tp = paramCodeMap.get(parts[3]);
   if( tp == null )
    throw new ParseException(ln,"Invalid or unknown parameter code: '"+parts[1]+"'");
   
   Classifier cl = null;
   
   for( Classifier c : classifiersList )
   {
    if( c.getName().equals(parts[1]) )
    {
     cl=c;
     break;
    }
   }
   
   if( cl == null )
    throw new ParseException(ln,"Unknown classifier: '"+parts[1]+"'");
      
   Tag tg = null;
   
   for( Tag t : cl.getTags() )
   {
    if( t.getName().equals(parts[2]) )
    {
     tg = t;
     break;
    }
   }
   
   if( tg == null )
    throw new ParseException(ln,"Unknown tag: '"+parts[1]+":"+parts[2]+"'");

   boolean has = false;
   if( hp.getRelations() != null )
   {
    for( Relation r : hp.getRelations() )
    {
     if( r.getTag() == tg && r.getTargetParameter() == tp )
     {
      has=true;
//      throw new ParseException(ln,"This relation already exists");
     }
    }
   }
   
   if( ! has )
   {
    Relation rl = new Relation();
    rl.setTargetParameter(tp);
    rl.setTag(tg);
    rl.setHostParameter(hp);
    
    newRels.add(rl);    
   }
   
  }
  
  if( newRels.size() == 0 )
   return;
  
  Connection conn = null;
  ResultSet rst = null;
  try
  {
   conn = dSrc.getConnection();
   PreparedStatement stmt = conn.prepareStatement(insertRelationSQL,PreparedStatement.RETURN_GENERATED_KEYS);

   for( Relation r: newRels )
   {
    stmt.setInt(1, r.getHostParameter().getId());
    stmt.setInt(2, r.getTargetParameter().getId());
    stmt.setInt(3, r.getTag().getId());
    
    stmt.executeUpdate();
    
    rst = stmt.getGeneratedKeys();
    
    if(rst.next())
    {
     r.setId(rst.getInt(1));
    }
    
    rst.close();
    
    r.getHostParameter().addRelation(r);
    ParameterShadow ps = ((SSParameterInfo)r.getHostParameter().getAuxInfo()).getShadow();
    
    int[][] rels = ps.getRelations();
    
    if( rels == null )
     ps.setRelations( new int[][]{ new int[]{r.getId(),r.getTargetParameter().getId(),r.getTag().getId()} });
    else
    {
     int [][] nrels = new int[rels.length+1][];
     
     for(int i=0; i < rels.length; i++)
      nrels[i]=rels[i];
     
     nrels[ rels.length ] = new int[]{r.getId(),r.getTargetParameter().getId(),r.getTag().getId()};
     
     ps.setRelations(nrels);
    }
    
   }
   

  }
  catch(SQLException e)
  {
   Log.error("SQL error", e);
  }
  finally
  {
   if(conn != null)
   {
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }
   }
  }
  
 }
 
 private FullPartRef[] analyzeHeader( List<String> nms ) throws ParseException //TODO null pointer for invalid header 
 {
  FullPartRef[] pts = new FullPartRef[nms.size()-1];
  
  boolean first = true;
  int i=0;
  cyc: for( String pn : nms )
  {
   if( first )
   {
    first=false;
    
    if( !SAMPLE_ID_COL.equals(pn) )
     throw new ParseException(1, "First column must be "+SAMPLE_ID_COL);
    
    continue;
   }
   
   if( pn.charAt(0) == '"' && pn.charAt(pn.length()-1) == '"' )
    pn=pn.substring(1,pn.length()-1);
   
   int pos=pn.indexOf('.');

   String paramCode = null;
   String subParamCode = null;
   String partName = null;

   if( pos > 0 )
   {
    int pos2 = pn.indexOf('.', pos+1);
    
    if( pos2 >= 0 )
    {
     paramCode = pn.substring(0,pos);
     subParamCode = pn.substring(pos+1,pos2);
     partName = pn.substring(pos2+1);
    }
    else
    {
     paramCode = pn.substring(0,pos);
     partName = pn.substring(pos+1);
    }
   }
   else if( pos == -1 )
   {
    paramCode=pn;
   }
   
   if( paramCode == null )
    throw new ParseException(1,"Invalid parameter reference: '"+pn+"'");
   
   for(Parameter p : params.values() )
   {
    if( p.getCode().equals(paramCode) )
    {
     Collection<Variable> vrs = p.getAllVariables();
     Collection<Qualifier> qls = p.getAllQualifiers();

     if( partName == null )
     {
      int vN = vrs!=null?vrs.size():0;
      int qN = qls!=null?qls.size():0;

      if( (vN+qN) != 1 )
       throw new ParseException(1,"Ambiguous reference. Must be qualified by part name : '"+pn+"'");
      
      if( vN == 1 )
      {
       pts[i++]=new FullPartRef(p, vrs.iterator().next());
       continue cyc;
      }

      if( qN == 1 )
      {
       pts[i++]=new FullPartRef(p, qls.iterator().next());
       continue cyc;
      }

     }
     else
     {
      if( vrs != null )
      {
       for( Variable v : vrs )
       {
        if( v.getName().equals(partName) && (subParamCode==null || v.getParameter().getCode().equals(subParamCode) ) )
        {
         pts[i++]=new FullPartRef(p,v);
         continue cyc;
        }
       }
      }
      
      if( qls != null )
      {
       for( Qualifier q : qls )
       {
        if( q.getName().equals(partName) && (subParamCode==null || q.getParameter().getCode().equals(subParamCode) ) )
        {
         pts[i++]=new FullPartRef(p,q);
         continue cyc;
        }
       }
      }
      
      throw new ParseException(1,"No part '"+(subParamCode!=null?(subParamCode+'.'):"")+partName+"' in parameter '"+paramCode+"'");     
     }
     

    }

   }
   throw new ParseException(1,"No parameter with code '"+paramCode+"'");
  }

  IntMap<Parameter> pmap = new IntTreeMap<Parameter>();
  pmap.put(pts[pts.length-1].getParameter().getId(), pts[pts.length-1].getParameter());
  
  for( int k=0; k < pts.length-1; k++)
  {
   Parameter prm = pts[k].getParameter();
   pmap.put(prm.getId(), prm);
   
   for( int m=k+1; m < pts.length; m++ )
    if( pts[k].getParameterPart().getId() == pts[m].getParameterPart().getId() )
     throw new ParseException(1,"Duplicate variable/qualifier reference. Columns "+(k+2)+" and "+(m+2));
  }
  
  for(Parameter p : pmap.values())
  {
   Collection<Variable> vrs = p.getAllVariables();

   if(vrs != null)
   {
    for(Variable v : vrs)
    {
     if( ! v.isMandatory() )
      continue;
     
     boolean found = false;

     for(int m = 0; m < pts.length; m++)
     {
      if(v == pts[m].getParameterPart())
      {
       found = true;
       break;
      }
     }

     if(!found)
      throw new ParseException(1,"Incomplete parameter '" + p.getName() + "' description. No column for variable '"
        + v.getName() + '\'');

    }
   }

   Collection<Qualifier> qls = p.getAllQualifiers();
   if(qls != null)
   {
    for(Qualifier v : qls)
    {
     if( ! v.isMandatory() )
      continue;

     boolean found = false;

     for(int m = 0; m < pts.length; m++)
     {
      if(v == pts[m].getParameterPart())
      {
       found = true;
       break;
      }
     }

     if(!found)
      throw new ParseException(1,"Incomplete parameter '" + p.getName() + "' description. No column for qualifier '"
        + v.getName() + '\'');

    }
   }

  }
  
  return pts;
 }

 public Collection<StudyShadow> getStudies()
 {
  return studyList;
 }

 public int addStudy(StudyShadow rs) throws StudyManagementException
 {
  Connection conn = null;
  ResultSet rst = null;
  try
  {
   
   Collection<Annotation> annots = null;
   if(rs.getAnnotations() != null)
   {
    annots = new ArrayList<Annotation>( rs.getAnnotations().size() );
    
    for(AnnotationShadow ant : rs.getAnnotations())
    {
     Tag t = tags.get(ant.getTag());

     if(t == null)
      throw new StudyManagementException("Invalid tag ID=" + ant.getTag(),
        StudyManagementException.INV_TAG_ID);

     Annotation a = new Annotation();
     a.setText(ant.getText());
     a.setTag(t);

     annots.add(a);
    }
   }

   conn = dSrc.getConnection();
   PreparedStatement pstmt = conn.prepareStatement( insertStudySQL, Statement.RETURN_GENERATED_KEYS );

   pstmt.setString(1, rs.getName());
   pstmt.setLong(2, System.currentTimeMillis());
   
   pstmt.executeUpdate();
   
   rst = pstmt.getGeneratedKeys();
   
   int id=-1;
   if( rst.next() )
    id=rst.getInt(1);
   
   pstmt.close();

   if(rs.getAnnotations() != null)
   {
    pstmt = conn.prepareStatement(insertStudyAnnotationSQL);

    for(AnnotationShadow ant : rs.getAnnotations())
    {
     pstmt.setInt(1, id);
     pstmt.setInt(2, ant.getTag());
     pstmt.setString(3, ant.getText());

     pstmt.executeUpdate();
    }

    pstmt.close();
   }

   if(rs.getCollections() != null )
   {
    pstmt = conn.prepareStatement(insertStudyCollectionsSQL);

    for(int cohId : rs.getCollections())
    {
     pstmt.setInt(1, id);
     pstmt.setInt(2, cohId);

     pstmt.executeUpdate();
    }

    pstmt.close();
   }
   
   rs.setId(id);   
   studyList.add(rs);
   
   return id;
   
  }
  catch(SQLException e)
  {
   Log.error("SQL error", e);
   throw new StudyManagementException("SQL error", e, StudyManagementException.SQL_ERROR );
  }
  finally
  {
   if(rst != null)
    try
    {
     rst.close();
    }
    catch(SQLException e)
    {
    }

   if(conn != null)
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }

  }
 }

 public Object updateStudy(StudyShadow stsh) throws StudyManagementException
 {
  studySummaryCache.remove(stsh.getId());
  
  StudyShadow origStSh = null;
  
  for( StudyShadow ss : studyList )
  {
   if( ss.getId() == stsh.getId() )
   {
    origStSh = ss;
    break;
   }
  }
  
  if( origStSh == null )
   throw new StudyManagementException("Study with ID="+stsh.getId()+" doesn't exist",StudyManagementException.INV_ID);
  
  Connection conn = null;
  ResultSet rst = null;
  try
  {
   conn = dSrc.getConnection();
   PreparedStatement pstmt = conn.prepareStatement( updateStudySQL );

   pstmt.setString(1, stsh.getName());
   pstmt.setInt(2, stsh.getId());
   
   pstmt.executeUpdate();
   pstmt.close();
      
   pstmt = conn.prepareStatement(deleteStudyAnnotationsSQL);
   pstmt.setInt(1, stsh.getId());
   pstmt.executeUpdate();
   pstmt.close();
   
   if(stsh.getAnnotations() != null)
   {
    pstmt = conn.prepareStatement(insertStudyAnnotationSQL);

    for(AnnotationShadow ant : stsh.getAnnotations())
    {
     Tag t = tags.get(ant.getTag());

     if(t == null)
      throw new StudyManagementException("Invalid tag ID=" + ant.getTag(),
        StudyManagementException.INV_TAG_ID);


     pstmt.setInt(1, stsh.getId());
     pstmt.setInt(2, ant.getTag());
     pstmt.setString(3, ant.getText());

     pstmt.executeUpdate();
    }
   }
   
   origStSh.setName(stsh.getName());
   origStSh.setAnnotations(stsh.getAnnotations());
   
   pstmt = conn.prepareStatement(deleteStudyCollectionsSQL);
   pstmt.setInt(1, stsh.getId());
   pstmt.executeUpdate();
   pstmt.close();
   
   if(stsh.getCollections() != null)
   {
    pstmt = conn.prepareStatement(insertStudyCollectionsSQL);

    for(int cohId : stsh.getCollections())
    {
     if( ! collections.containsKey(cohId) )
      throw new StudyManagementException("Invalid Collection reference. CollectionID=" + cohId,
        StudyManagementException.INV_COLLECTION_ID);


     pstmt.setInt(1, stsh.getId());
     pstmt.setInt(2, cohId);

     pstmt.executeUpdate();
    }
   }
   
   origStSh.setName(stsh.getName());
   origStSh.setAnnotations(stsh.getAnnotations());
   origStSh.setCollections(stsh.getCollections());
  
   return null;
   
  }
  catch(SQLException e)
  {
   logger.error("SQL error", e);
   throw new StudyManagementException("SQL error", e, StudyManagementException.SQL_ERROR );
  }
  finally
  {
   if(rst != null)
    try
    {
     rst.close();
    }
    catch(SQLException e)
    {
    }

   if(conn != null)
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }
  }
 }

 public SampleCollection getCollection( int id )
 {
  return collections.get(id);
 }
 
 public Collection<CollectionShadow> getCollections()
 {
  return collectionList;
 }

 public int addCollection(CollectionShadow rs) throws CollectionManagementException
 {
  Connection conn = null;
  ResultSet rst = null;
  try
  {
   
   Collection<Annotation> annots = null;
   if(rs.getAnnotations() != null)
   {
    annots = new ArrayList<Annotation>( rs.getAnnotations().size() );
    
    for(AnnotationShadow ant : rs.getAnnotations())
    {
     Tag t = tags.get(ant.getTag());

     if(t == null)
      throw new CollectionManagementException("Invalid tag ID=" + ant.getTag(),
        CollectionManagementException.INV_TAG_ID);

     Annotation a = new Annotation();
     a.setText(ant.getText());
     a.setTag(t);

     annots.add(a);
    }
   }

   conn = dSrc.getConnection();
   PreparedStatement pstmt = conn.prepareStatement( insertCollectionSQL, Statement.RETURN_GENERATED_KEYS );

   pstmt.setString(1, rs.getName());
   pstmt.setLong(2, System.currentTimeMillis());
   
   pstmt.executeUpdate();
   
   rst = pstmt.getGeneratedKeys();
   
   int id=-1;
   if( rst.next() )
    id=rst.getInt(1);
   
   pstmt.close();

   if(rs.getAnnotations() != null)
   {
    pstmt = conn.prepareStatement(insertCollectionAnnotationSQL);

    for(AnnotationShadow ant : rs.getAnnotations())
    {
     pstmt.setInt(1, id);
     pstmt.setInt(2, ant.getTag());
     pstmt.setString(3, ant.getText());

     pstmt.executeUpdate();
    }

    pstmt.close();
   }

   rs.setId(id);   
   collectionList.add(rs);
   
   SampleCollection r = rs.createCollection();
   r.setAnnotations(annots);
   collections.put(rs.getId(), r);
   
   return id;
   
  }
  catch(SQLException e)
  {
   Log.error("SQL error", e);
   throw new CollectionManagementException("SQL error", e, CollectionManagementException.SQL_ERROR );
  }
  finally
  {
   if(rst != null)
    try
    {
     rst.close();
    }
    catch(SQLException e)
    {
    }

   if(conn != null)
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }

  }
 }

 public Object updateCollection(CollectionShadow rs) throws CollectionManagementException
 {
  SampleCollection rpsry = collections.get(rs.getId());
  
  if( rpsry == null )
   throw new CollectionManagementException("Collection with ID="+rs.getId()+" doesn't exist",CollectionManagementException.INV_ID);
  
  Connection conn = null;
  ResultSet rst = null;
  try
  {
   Collection<Annotation> annots = null;
   
   if( rs.getAnnotations() != null && rs.getAnnotations().size() > 0 )
    annots = new ArrayList<Annotation>( rs.getAnnotations().size() );
   

   conn = dSrc.getConnection();
   PreparedStatement pstmt = conn.prepareStatement( updateCollectionSQL );

   pstmt.setString(1, rs.getName());
   pstmt.setInt(2, rs.getId());
   
   pstmt.executeUpdate();
   pstmt.close();
      
   pstmt = conn.prepareStatement(deleteCollectionAnnotationsSQL);
   pstmt.setInt(1, rs.getId());
   pstmt.executeUpdate();
   pstmt.close();
   
   if(annots != null)
   {
    pstmt = conn.prepareStatement(insertCollectionAnnotationSQL);

    for(AnnotationShadow ant : rs.getAnnotations())
    {
     Tag t = tags.get(ant.getTag());

     if(t == null)
      throw new CollectionManagementException("Invalid tag ID=" + ant.getTag(),
        CollectionManagementException.INV_TAG_ID);

     Annotation a = new Annotation();
     a.setText(ant.getText());
     a.setTag(t);

     annots.add(a);
    
     
     pstmt.setInt(1, rs.getId());
     pstmt.setInt(2, ant.getTag());
     pstmt.setString(3, ant.getText());

     pstmt.executeUpdate();
    }
   }
   
   for(CollectionShadow ors : collectionList)
   {
    if( ors.getId() == rs.getId() )
    {
     ors.setName(rs.getName());
     ors.setAnnotations( rs.getAnnotations() );
     
     break;
    }
   }
   
   rpsry.setName(rs.getName());
   rpsry.setAnnotations(annots);
   
   return null;
   
  }
  catch(SQLException e)
  {
   logger.error("SQL error", e);
   throw new CollectionManagementException("SQL error", e, CollectionManagementException.SQL_ERROR );
  }
  finally
  {
   if(rst != null)
    try
    {
     rst.close();
    }
    catch(SQLException e)
    {
    }

   if(conn != null)
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }
  }
 }


 private static class RecordComparator implements Comparator<Record>
 {
  private static RecordComparator instance = new RecordComparator();
  
  static RecordComparator getIntstance()
  {
   return instance;
  }
  
  public int compare(Record o1, Record o2)
  {
   if(o1.getCollectionId() != o2.getCollectionId())
    return o1.getCollectionId() - o2.getCollectionId();

   return o1.getCollectionRecordIDs().compareTo(o2.getCollectionRecordIDs());
  }
 }

/*
 public ExportReport getExportReport(NetworkReportRequest nreq)
 {
  RowProcessor[] pat = preparePattern(nreq);
  
  ExportReport res = new ExportReport();
  
  
  row: for(Record row : data)
  {
   for( RowProcessor rp : pat )
   {
    if( ! rp.matchRecord(row) )
     continue row;
   }
   
   res.add(row);
  }
  
  return res;
 }
*/
 
 public ExportReport2 getExportReport2( ReportRequest nreq )
 {
  RowProcessor pat = convert(nreq.getRootGroup(), nreq);
  
  ExportReport2 res = new ExportReport2();
  
  
  int datalen = data.size();

  int[] khLst = null;
  
  if( nreq.isCollectionSplit() )
   khLst = nreq.getCollections();

  if( khLst != null )
  {
   for(int khId : khLst)
   {
    int ind = findRecordByCollection(khId);

    if(ind >= 0)
    {
     int i = ind;
     while( i >= 0 && i < datalen )
     {
      Record r = data.get(i);
      
      if( r.getCollectionId() != khId )
       break;
      
      if( pat.matchRecord(r) ) // ??? || pat.length==1
       res.add(khId,r);
      
      i++;
     }
     
     i=ind-1;
     while( i >= 0 && i < datalen )
     {
      Record r = data.get(i);
      
      if( r.getCollectionId() != khId )
       break;
      
      if( pat.matchRecord(r) )
       res.add(khId,r);
      
      i--;
     }

    }
   }
  }
  else
  {
   for( Record r : data )
    if( pat.matchRecord(r) )
     res.add(r.getCollectionId(),r);
  }
  
  return res;
 }

 
 public void destroy()
 {
  // TODO Auto-generated method stub
  
 }

 public static void setInstance(DataManager dataManager)
 {
  instance = dataManager;
 }

 
 class Var
 {
  int id;
  int ptid;
  int cod;
  String name;

  public int getId()
  {
   return id;
  }
  public void setId(int id)
  {
   this.id = id;
  }
  public int getPtid()
  {
   return ptid;
  }
  public void setPtid(int ptid)
  {
   this.ptid = ptid;
  }
  public int getCod()
  {
   return cod;
  }
  public void setCod(int cod)
  {
   this.cod = cod;
  }
  public String getName()
  {
   return name;
  }
  public void setName(String name)
  {
   this.name = name;
  }
 }

 class PartVariSet extends ArrayList<Var>
 {
  int max; 
  
  public void setMax( int m )
  {
   max=m;
  }
  
  public int getNextCoding()
  {
   return ++max;
  }
  
  public int getMax()
  {
   return max;
  }
 }
 
 static final String logEOL = "<br/>";
 
 public String upgradeDB_20090720()
 {
  StringBuilder log = new StringBuilder();

  Connection conn = null;
  ResultSet rst = null;
  
  try
  {
   conn = dSrc.getConnection();
   Statement stmt = conn.createStatement();
   
//   stmt.executeUpdate("UPDATE "+TBL_VARIANT+" SET "+FLD_PREDEFINED+"=1");
//   log.append("Predefined flag is set for variants").append(logEOL);
   
   stmt.executeUpdate("DELETE FROM "+TBL_VARIANT+" WHERE "+FLD_NAME+"='@'");
   log.append("Anonymous variants have been removed").append(logEOL);
   
   rst = stmt.executeQuery("SELECT * FROM "+TBL_VARIANT + " ORDER BY "+FLD_PART_ID);

   
   IntMap<PartVariSet> variMap = new IntTreeMap<PartVariSet>();
   
   List<Var> varis = new ArrayList<Var>(5);
   PartVariSet partLst = null;
   
   while( rst.next() )
   {
    Var v = new Var();
    
    
    int ptid = rst.getInt(FLD_PART_ID);
    
    v.setId  ( rst.getInt(FLD_ID) );
    v.setPtid( ptid );
    v.setCod ( rst.getInt(FLD_VARI_CODING) );
    v.setName(rst.getString(FLD_NAME));
    
    log.append("----- Loading variant (ID,Part,Name,Code) ("+v.getId()+','+ptid+","+v.getName()+','+v.getCod()+") -----").append(logEOL);
    
    partLst = variMap.get(ptid);

    if(partLst == null)
    {
     partLst = new PartVariSet();
     variMap.put(ptid, partLst);
    }

    partLst.add(v);
    
    int cptid = 0 ;
    
    if ( varis.size() > 0 )
     cptid = varis.get(0).getPtid();
    
    if( varis.size() == 0 || cptid == ptid )
    {
     varis.add(v);
    }
    else
    {
     partLst.setMax(updateBlock(conn, varis));
     varis.clear();
     
     varis.add(v);
     
     log.append("Variants for Part="+cptid+" have been precessed. Max code="+partLst.getMax()).append(logEOL);
    }
   }
   
   rst.close();
   
   if( varis.size() > 0 )
   {
    partLst.setMax(updateBlock(conn, varis));
    log.append("Variants for Part="+varis.get(0).getPtid()+" have been precessed. Max code="+partLst.getMax()).append(logEOL);
   }
   
   rst = stmt.executeQuery("SELECT * FROM "+TBL_RECORD_CONTENT+" ORDER BY "+FLD_PART_ID);
   
   ResultSet trst = null;
   PreparedStatement selPartType = conn.prepareStatement("SELECT "+FLD_TYPE+" FROM "+TBL_PART+" WHERE "+FLD_ID+"=?");
   PreparedStatement updPartVal =  conn.prepareStatement("UPDATE "+TBL_RECORD_CONTENT+" SET "+FLD_INT_VALUE+"=? WHERE "
     +FLD_PART_ID+"=? AND "+FLD_RECORD_ID+"=?");
   PreparedStatement insVariant =  conn.prepareStatement("INSERT INTO "+TBL_VARIANT+" ("
     +FLD_PART_ID+','
     +FLD_NAME+','
     +FLD_VARI_CODING+','
     +FLD_PREDEFINED+
     ") VALUES (?,?,?,0)", Statement.RETURN_GENERATED_KEYS );
 
   int cptID = -1;
   String partType = null;
   
   while( rst.next() )
   {
    int ptID = rst.getInt(FLD_PART_ID);

    if( cptID != ptID )
    {

     selPartType.setInt(1, ptID);

     trst = selPartType.executeQuery();

     if(!trst.next())
     {
      log.append("Can't find part with ID=" + ptID+" in database").append(logEOL);
      return log.toString();
     }

     partType = trst.getString(1);
     
     trst.close();
    }
    
    if( ! ( "QUALIFIER".equals(partType) || "ENUM".equals(partType) ) )
     continue;
    

    String value = rst.getString(FLD_ENUM_VALUE);
    
    log.append("Processing value for Part="+ptID+" Value="+value).append(logEOL);
    
    if( ParameterPart.SECURED_VARIANT_SIGN.equals(value) )
    {
     updPartVal.setInt(1, 0);
     updPartVal.setInt(2, ptID);
     updPartVal.setInt(3, rst.getInt(FLD_RECORD_ID));

     updPartVal.executeUpdate();

     log.append("Part value updated. RecordID="+rst.getInt(FLD_RECORD_ID)+" Part="+ptID+" Variant=0").append(logEOL);
     continue;
    }
    
    partLst = variMap.get(ptID);
    
    if( partLst == null )
    {
     partLst = new PartVariSet();
     
     partLst.setMax(0);
     variMap.put(ptID, partLst);
    }
     
    Var matchV = null;
    for( Var v : partLst )
    {
     if( v.getName().equals(value) )
     {
      matchV = v;
      break;
     }
    }
    
    if( matchV == null )
    {
     log.append("There is no matching variant. Part="+ptID+" Value="+value).append(logEOL);

     int ncod = partLst.getNextCoding();
     
     insVariant.setInt(1, ptID);
     insVariant.setString(2, value);
     insVariant.setInt(3, ncod );
     
     insVariant.executeUpdate();
     
     ResultSet krst = insVariant.getGeneratedKeys();
     
     if(! krst.next() )
     {
      log.append("Can't get generated key. Variant name' " + value+"' PartID="+ptID).append(logEOL);
      return log.toString();
     }
     
     matchV = new Var();
     matchV.setId(krst.getInt(1));
     matchV.setCod(ncod);
     matchV.setName(value);
     matchV.setPtid(ptID);
     
     krst.close();
     
     partLst.add(matchV);
 
     log.append("New variant added. ID="+matchV.getId()+" Part="+ptID+" Value="+value+" Code="+matchV.getCod()).append(logEOL);
     
    }
    

    updPartVal.setInt(1, matchV.getId());
    updPartVal.setInt(2, ptID);
    updPartVal.setInt(3, rst.getInt(FLD_RECORD_ID));

    updPartVal.executeUpdate();

    log.append("Part value updated. RecordID="+rst.getInt(FLD_RECORD_ID)+" Part="+ptID+" Variant="+matchV.getId()).append(logEOL);
   }
   
  }
  catch(SQLException e)
  {
   log.append("SQL error" +  e).append(logEOL);
  }
  finally
  {
   if(rst != null)
    try
    {
     rst.close();
    }
    catch(SQLException e)
    {
    }

   if(conn != null)
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }

  }
  
  return log.toString();
 }

 private int updateBlock(Connection conn, List<Var> varis) throws SQLException
 {
  Collections.sort(varis, new Comparator<Var>() {
   public int compare(Var o1, Var o2)
   {
    return o1.getCod()-o2.getCod();
   }});
  
  int l = varis.size()-1;
  int max = varis.get(l).getCod();
  
  for( int i=0; i <= l-1; i++ )
  {
   for( int j=i+1; j <= l; j++ )
    if( varis.get(i).getCod() == varis.get(j).getCod() )
     varis.get(j).setCod(0);
  }
  
  for( Var vi : varis )
  {
   if( vi.getCod() == 0 )
    vi.setCod(++max);
  }
  
  PreparedStatement pstmt = conn.prepareStatement( "UPDATE "+TBL_VARIANT+" SET "+FLD_VARI_CODING+"=? WHERE "+FLD_ID+"=?");
  
  for( Var v : varis )
  {
   pstmt.setInt(1, v.getCod() );
   pstmt.setInt(2, v.getId()  );
   
   pstmt.executeUpdate();
  }
  
  return max; 
  
 }

 public void importSample2StudyRelations(String txt, int studyID, int collectionID) throws ParseException
 {
  studySummaryCache.remove(studyID);

  String SEP = null;
  
  int cpos = 0;
  int len = txt.length();
  
  String lineSep = "\n";
  
  cpos = txt.indexOf(lineSep);
  
  if( cpos == -1 )
   throw new ParseException(1,"File must contain at least one line");
  
  if( cpos > 0 && txt.charAt(cpos-1) == '\r' )
   lineSep="\r\n";
  
  cpos = 0;
  
  class RelInfo
  {
   boolean preStudy;
   boolean postStudy;
   Record rec;
  }
  
  List<RelInfo> newRels = new ArrayList<RelInfo>();
  
  Record r = new Record();
  
  int ln=0;
  while(cpos < len)
  {
   ln++;
   int pos = txt.indexOf(lineSep, cpos);

   if(pos == -1)
    break;

   String str = txt.substring(cpos, pos);
   cpos = pos+lineSep.length();
   
   if( str.length() == 0 )
    continue;

   if( SEP == null )
   {
    if( str.indexOf('\t') > 0 )
     SEP = "\t";
    else
     SEP = ",";
   }
   
   String[] parts = str.split(SEP);

   if( parts.length != 3 )
    throw new ParseException(ln,"Invalid syntax. Must be [RecordID] [Is eligible] [Is selected]");
   
   r.setCollectionRecordIDs(parts[0]);
   r.setCollectionId( collectionID );
   
   Record rr = findRecord( r );
   
   if( rr == null )
    throw new ParseException(ln,"There is no record with ID='"+parts[0]+"'");
   
   RelInfo ri = new RelInfo();
   
   ri.postStudy="1".equals(parts[2]) || "true".equalsIgnoreCase(parts[2]);
   ri.preStudy ="1".equals(parts[1]) || "true".equalsIgnoreCase(parts[1]);
   
   ri.rec = rr;

   newRels.add(ri);   
  }
  
  if( newRels.size() == 0 )
   return;
  
  Connection conn = null;
  try
  {
   conn = dSrc.getConnection();
   PreparedStatement insStmt = conn.prepareStatement(insertStudyRecordSQL);
   PreparedStatement delStmt = conn.prepareStatement(deleteStudyRecordSQL);

   int[] stLst;
   boolean isPost ;   
   boolean state; 
   for( RelInfo ri: newRels )
   {
    for(int k = 0; k < 2; k++)
    {
     if( k == 0 )
     {
      isPost = false;
      state = ri.preStudy;
      stLst = ri.rec.getPreStudies();
     }
     else
     {
      isPost = true;
      state = ri.postStudy;
      stLst = ri.rec.getPostStudies();
     }


     if(stLst != null)
     {
      int ind = Arrays.binarySearch(stLst, studyID);

      if(ind >= 0)
      {
       if(!state)
       {
        int[] newLst = new int[stLst.length - 1];

        int j = 0;
        for(int i = 0; i < newLst.length; i++, j++)
        {
         if(stLst[j] == studyID)
          j++;

         newLst[i] = stLst[j];
        }

        if(isPost)
         ri.rec.setPostStudies(newLst);
        else
         ri.rec.setPreStudies(newLst);

        delStmt.setInt(1, ri.rec.getId());
        delStmt.setInt(2, studyID);
        delStmt.setBoolean(3, isPost);
        delStmt.executeUpdate();
       }
      }
      else
      {
       if(state)
       {
        int[] newLst = new int[stLst.length + 1];

        for(int i = 0; i < newLst.length; i++)
         newLst[i] = stLst[i];

        newLst[stLst.length] = studyID;

        Arrays.sort(newLst);

        if(isPost)
         ri.rec.setPostStudies(newLst);
        else
         ri.rec.setPreStudies(newLst);

        insStmt.setInt(1, ri.rec.getId());
        insStmt.setInt(2, studyID);
        insStmt.setBoolean(3, isPost);
        insStmt.executeUpdate();
       }
      }
     }
     else if( state )
     {
      if(isPost)
       ri.rec.setPostStudies(new int[]{ studyID });
      else
       ri.rec.setPreStudies(new int[]{ studyID });

      insStmt.setInt(1, ri.rec.getId());
      insStmt.setInt(2, studyID);
      insStmt.setBoolean(3, isPost);
      insStmt.executeUpdate();
     }

    }

   }
  }
  catch(SQLException e)
  {
   Log.error("SQL error", e);
  }
  finally
  {
   if(conn != null)
   {
    try
    {
     conn.close();
    }
    catch(SQLException e)
    {
     Log.error("Connection closing error", e);
    }
   }
  }
 }

 public Collection<ExpressionRequestItem> getExpressions()
 {
  return expressions;
 }
}
