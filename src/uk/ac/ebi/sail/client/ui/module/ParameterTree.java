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

import uk.ac.ebi.sail.client.common.ClassifiableManager;
import uk.ac.ebi.sail.client.common.ClientParameterAuxInfo;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.data.TraversalCallback;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.StudyCollectionStateListener;

public class ParameterTree extends ClassifiableTree<Parameter> implements StudyCollectionStateListener
{

 public ParameterTree(boolean single, Action[] buttons, ClassifiableManager<Parameter> m)
 {
  super(single, buttons, m);
  
  setTitle("Parameter tree");
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
  
  if( stID==0 && khID == 0 )
  {
   removeFilter("_CollectionFilter");
   return;
  }
  
  AsyncCallback<Summary> asCB = new AsyncCallback<Summary>()
  {
   @Override
   public void onSuccess(final Summary arg0)
   {
    ParameterSelector ps = ParameterSelector.getInstance();
    
    if( khID != 0 )
     ps.setPattern(arg0);
    else
     ps.setPattern(arg0.getTagCounters()[0]);
    
    addFilter("_CollectionFilter", ps );
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

}
