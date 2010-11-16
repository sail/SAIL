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

package uk.ac.ebi.sail.client.common;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AnnotationShadow implements IsSerializable,Serializable
{
 
 private String text;
 private int tag;

 public AnnotationShadow()
 {
 }

 public AnnotationShadow(Annotation an)
 {
  text=an.getText();
  tag=an.getTag().getId();
 }

 public String getText()
 {
  return text;
 }

 public void setText(String text)
 {
  this.text = text;
 }

 public void setTag(int tag)
 {
  this.tag = tag;
 }

 public Annotation createAnnotation()
 {
  Annotation an = new Annotation();
  
  an.setText( text );
  
  return an;
 }

 public int getTag()
 {
  return tag;
 }

}
