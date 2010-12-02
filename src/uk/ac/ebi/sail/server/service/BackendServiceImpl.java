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

package uk.ac.ebi.sail.server.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.sail.client.BackendService;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.ClassifierManagementException;
import uk.ac.ebi.sail.client.common.ClassifierShadow;
import uk.ac.ebi.sail.client.common.CollectionManagementException;
import uk.ac.ebi.sail.client.common.CollectionShadow;
import uk.ac.ebi.sail.client.common.ExpressionRequestItem;
import uk.ac.ebi.sail.client.common.IDBunch;
import uk.ac.ebi.sail.client.common.ParameterManagementException;
import uk.ac.ebi.sail.client.common.ParameterShadow;
import uk.ac.ebi.sail.client.common.ProjectionManagementException;
import uk.ac.ebi.sail.client.common.ProjectionShadow;
import uk.ac.ebi.sail.client.common.Report;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.StudyManagementException;
import uk.ac.ebi.sail.client.common.StudyShadow;
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.server.BackendConfigurationManager;
import uk.ac.ebi.sail.server.data.DataManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class BackendServiceImpl extends RemoteServiceServlet implements BackendService 
{
// SampleData data = new SampleData();

 public ParameterShadow updateParameter(ParameterShadow p) throws ParameterManagementException
 {
  return DataManager.getInstance().updateParameter(p, true);
 }
 
 public void deleteParameter(int pID) throws ParameterManagementException
 {
  DataManager.getInstance().deleteParameter(pID);
 }

 public Report getReport( ReportRequest req )
 {
  try
  {
   return null; //Not implemented
//   return DataManager.getInstance().report( req );
  }
  catch (Exception e) {
   e.printStackTrace();
  }
  
  return null;
 }
 
 @Override
 public Summary getReport2( ReportRequest req )
 {
  try
  {
   return DataManager.getInstance().report2( req );
  }
  catch (Exception e) {
   e.printStackTrace();
  }
  
  return null;
 }
 
 public Collection<ParameterShadow> getParameters()
 {
  return DataManager.getInstance().getParameters();
 }

 public Collection<Classifier> getClassifiers()
 {
  return DataManager.getInstance().getClassifiers();
 }
 
 public ParameterShadow addParameter(ParameterShadow p) throws ParameterManagementException
 {
  return DataManager.getInstance().addParameter(p);
 }

 public List<ProjectionShadow> getProjections()
 {
  return DataManager.getInstance().getProjections();
 }

 public ClassifierShadow addClassifier(ClassifierShadow p)
 {
  return DataManager.getInstance().addClassifier(p);
 }

 public ClassifierShadow updateClassifier(ClassifierShadow p) throws ClassifierManagementException
 {
  return DataManager.getInstance().updateClassifier(p);
 }

 public Integer addProjection(ProjectionShadow p) throws ProjectionManagementException
 {
  return DataManager.getInstance().addProjection(p);
 }

 public void updateProjection(ProjectionShadow p) throws ProjectionManagementException
 {
  DataManager.getInstance().updateProjection(p);
 }

 public Collection<CollectionShadow> getCollections()
 {
  return DataManager.getInstance().getCollections();
 }

 public Integer addCollection(CollectionShadow rs) throws CollectionManagementException
 {
  return DataManager.getInstance().addCollection(rs);
 }

 public void updateCollection(CollectionShadow p) throws CollectionManagementException
 {
  DataManager.getInstance().updateCollection(p);
 }

 public IDBunch[] getIDs( ReportRequest req )
 {
  {
   try
   {
    return DataManager.getInstance().getIDs( req );
   }
   catch (Exception e) {
    e.printStackTrace();
   }
   
   return null;
  }
 }

 public Map<String, String> getConfiguration()
 {
  return BackendConfigurationManager.getInstance().getClientConfiguration();
 }

 @Override
 public int addStudy(StudyShadow studyShadow) throws StudyManagementException
 {
  return DataManager.getInstance().addStudy(studyShadow);
 }

 @Override
 public void updateStudy(StudyShadow studyShadow) throws StudyManagementException
 {
  DataManager.getInstance().updateStudy(studyShadow);
 }

 @Override
 public Collection<StudyShadow> getStudies()
 {
  return DataManager.getInstance().getStudies();
 }

 @Override
 public Summary getCollectionSummary(int khID)
 {
  System.out.println("Collection summary request: "+System.currentTimeMillis());
  return DataManager.getInstance().getCollectionSummary(khID);
 }
 
 @Override
 public Summary getStudySummary(int khID)
 {
  return DataManager.getInstance().getStudySummary(khID);
 }

 @Override
 public Collection<ExpressionRequestItem> getExpressions()
 {
  return DataManager.getInstance().getExpressions();
 }

}
