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
import uk.ac.ebi.sail.client.common.ClassifierShadow;
import uk.ac.ebi.sail.client.common.CollectionShadow;
import uk.ac.ebi.sail.client.common.ExpressionRequestItem;
import uk.ac.ebi.sail.client.common.IDBunch;
import uk.ac.ebi.sail.client.common.ParameterShadow;
import uk.ac.ebi.sail.client.common.ProjectionShadow;
import uk.ac.ebi.sail.client.common.Report;
import uk.ac.ebi.sail.client.common.ReportRequest;
import uk.ac.ebi.sail.client.common.StudyShadow;
import uk.ac.ebi.sail.client.common.Summary;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BackendServiceAsync
{

 public void getReport( ReportRequest req, AsyncCallback<Report> callback );
 public void getReport2(ReportRequest req, AsyncCallback<Summary> asyncCallback);

 public void getParameters(AsyncCallback<Collection<ParameterShadow>> callback);
 public void getClassifiers(AsyncCallback< Collection<Classifier> > callback);
 public void getProjections(AsyncCallback<List<ProjectionShadow>> callback);
 public void getConfiguration(AsyncCallback<Map<String,String>> callback);
 
 public void addParameter( ParameterShadow p, AsyncCallback<ParameterShadow> callback );
 public void updateParameter( ParameterShadow p, AsyncCallback<ParameterShadow> callback );
 public void deleteParameter(int id, AsyncCallback<Void> callback);

 public void addClassifier( ClassifierShadow p, AsyncCallback<ClassifierShadow> callback );
 public void updateClassifier( ClassifierShadow p, AsyncCallback<ClassifierShadow> callback );
 
 public void addProjection( ProjectionShadow p, AsyncCallback<Integer> callback );
 public void updateProjection( ProjectionShadow p, AsyncCallback<Void> callback );
 
 public void getCollections(AsyncCallback<Collection<CollectionShadow>> callback);
 public void addCollection( CollectionShadow rs, AsyncCallback<Integer> callback );
 public void updateCollection( CollectionShadow p, AsyncCallback<Void> callback );
 
 public void getIDs( ReportRequest req, AsyncCallback<IDBunch[]> callback );
 
 public void addStudy(StudyShadow studyShadow, AsyncCallback<Integer> asyncCallback);
 public void updateStudy(StudyShadow studyShadow, AsyncCallback<Void> asyncCallback);
 void getStudies(AsyncCallback<Collection<StudyShadow>> callback);
 
 void getCollectionSummary(int khID, AsyncCallback<Summary> callback);
 void getStudySummary(int stID, AsyncCallback<Summary> cb);
 
 void getExpressions(AsyncCallback<Collection<ExpressionRequestItem>> asyncCallback);

}
