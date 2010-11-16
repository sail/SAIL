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

import java.util.Date;

import uk.ac.ebi.sail.client.common.Annotation;
import uk.ac.ebi.sail.client.common.SampleCollection;

import com.gwtext.client.widgets.Panel;

public class CollectionView extends Panel
{

 public CollectionView(SampleCollection rp)
 {
  Date dt = new Date(rp.getUpdateTime());
  
  setTitle(rp.getName()+" (Records: "+rp.getSampleCount()+" Last update: "+dt.toLocaleString()+")");
  setPaddings(5);
  setCollapsible(true);
  setCollapsed(true);
  
  String content = ""; //"<p><h2>"+rp.getName()+"</h2></p>";
  
  int n=0;
  
  if(rp.getAnnotations() != null)
  {
   for(Annotation ant : rp.getAnnotations())
   {
    content += "<p><b>" + ant.getTag().getClassifier().getName() + " : " + ant.getTag().getName() + "</b><br />";
    String text = ant.getText().replaceAll("\n", "<br />");
    content += text + "</p>";
    n++;

    if(n != rp.getAnnotations().size())
     content += "<hr />";
   }
  }
  
  setHtml(content);
 }

}
