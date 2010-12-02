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

package uk.ac.ebi.sail.client.ui.widget;

import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.sail.client.common.Classifiable;
import uk.ac.ebi.sail.client.common.ClassifiableManager;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.data.Attributed;
import uk.ac.ebi.sail.client.data.Filterable;
import uk.ac.ebi.sail.client.data.TraversalCallback;

import com.gwtext.client.widgets.Toolbar;

public class ClassifiableSearchToolbar<T extends Classifiable&Attributed> extends Toolbar implements Filterable<T>, TraversalCallback<T>
{
 private static final String FILTER_ID="_ClsTB";
 
 private Filterable<T> filterable;
 private Map<String, TraversalCallback<T>> filterMap = new TreeMap<String, TraversalCallback<T>>();
 private ClassiableSearchComponent<T> tagComponent;
 
 public ClassifiableSearchToolbar(Filterable<T> fltrb, ClassifiableManager<Classifier> cm, String[][] si )
 {
  filterable = fltrb;
  
  tagComponent = new ClassiableSearchComponent<T>(this,this,cm);
  addFill();
  new TextSearchComponent<T>(this,this,si);
 }

 public void addFilter(String fid, TraversalCallback<T> cb)
 {
  filterMap.put(fid, cb);
  filterable.addFilter(FILTER_ID, this);
 }

 public void removeFilter(String fid)
 {
  filterMap.remove(fid);
  
  if( filterMap.size() == 0 )
   filterable.removeFilter(FILTER_ID);
  else
   filterable.addFilter(FILTER_ID, this);
   
 }

 public boolean execute(T obj)
 {
  for( TraversalCallback<T> cb : filterMap.values() )
  {
   if(  !cb.execute(obj)  )
    return false;
  }
  
  return true;
 }

 public void setTagFilter(Classifier cl, Tag tag)
 {
  tagComponent.setFilter(cl, tag);
 }
 
}
