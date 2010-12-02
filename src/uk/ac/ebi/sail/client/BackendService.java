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

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ReportRequest")
public interface BackendService extends RemoteService
{

// public static final String SERVICE_URI = "ReportRequest";
 
 Report getReport( ReportRequest req );
 Summary getReport2(ReportRequest req);
 
 Collection<ParameterShadow> getParameters();
 Collection<Classifier> getClassifiers();
 List<ProjectionShadow> getProjections();
 Map<String,String> getConfiguration();
 
 ParameterShadow addParameter( ParameterShadow p ) throws ParameterManagementException;
 ParameterShadow updateParameter( ParameterShadow p ) throws ParameterManagementException;
 void deleteParameter(int id) throws ParameterManagementException;

 ClassifierShadow addClassifier( ClassifierShadow p ) throws ClassifierManagementException;
 ClassifierShadow updateClassifier( ClassifierShadow p ) throws ClassifierManagementException;
 
 Integer addProjection( ProjectionShadow p ) throws ProjectionManagementException;
 void updateProjection( ProjectionShadow p ) throws ProjectionManagementException;
 
 Collection<CollectionShadow> getCollections();
 Integer addCollection( CollectionShadow rs ) throws CollectionManagementException;
 void updateCollection( CollectionShadow p ) throws CollectionManagementException;
 
 IDBunch[] getIDs( ReportRequest req );



 Collection<StudyShadow> getStudies();
 int addStudy(StudyShadow studyShadow) throws StudyManagementException;
 void updateStudy(StudyShadow studyShadow) throws StudyManagementException;
 
 Summary getCollectionSummary(int khID);
 Summary getStudySummary(int stID);
 
 
 public static class Util
 {
  static BackendServiceAsync instance;
  
  public static BackendServiceAsync getInstance()
  {
   if( instance != null )
    return instance;
   
   
   instance = (BackendServiceAsync) GWT.create(BackendService.class);
//   ServiceDefTarget target = (ServiceDefTarget) instance;
//   target.setServiceEntryPoint(GWT.getModuleBaseURL() + SERVICE_URI);
   return instance;
  }
 }


 Collection<ExpressionRequestItem> getExpressions();


}
