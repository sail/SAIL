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

import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.Annotation;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.Classifier.Target;

import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.FitLayout;

public class AnnotationEditDialog extends Window
{
 private static AnnotationEditDialog instance;
 private AnnotationEditPanel aep;
 
 public AnnotationEditDialog(Classifier.Target antClass)
 {
  setSize(500, 400);
  setLayout(new FitLayout());
  
  add( aep = new AnnotationEditPanel(antClass) );
 }

 public static AnnotationEditDialog getDialog(Classifier.Target antClass)
 {
  if( instance == null )
   instance = new AnnotationEditDialog(antClass);
  else
   instance.setAnnotationTarget(antClass);
  
  return instance;
 }
 
 private void setAnnotationTarget(Target antClass)
 {
  aep.setAnnotationTarget(antClass);
 }

 public void setAnnotation( Annotation ant )
 {
  aep.setAnnotaton(ant);
 }

 public void setObjectActionListener(ObjectAction<Annotation> objectAction)
 {
  aep.setObjectActionListener(objectAction);
 }
 
 public void dispose()
 {
  super.close();
  instance=null;
 }

 @Deprecated
 public void close()
 {
  super.close();
 }

 @Deprecated
 public void hide()
 {
  super.hide();
 }
}
