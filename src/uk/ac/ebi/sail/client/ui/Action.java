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

package uk.ac.ebi.sail.client.ui;

import com.gwtext.client.widgets.Component;

public class Action
{
 private String   text;
 private String   action;
 private String   cls;
 private Action[] subActions;
 private Component component;


 public Action()
 {}

 public Action(String text, String action)
 {
  this(text, action, null, null);
 }
 
 public Action(String text, String action, String cls, Action[] subActions)
 {
  super();
  this.text = text;
  this.action = action;
  this.cls = cls;
  this.subActions = subActions;
 }

 public String getText()
 {
  return text;
 }

 public void setText(String text)
 {
  this.text = text;
 }

 public String getAction()
 {
  return action;
 }

 public void setAction(String action)
 {
  this.action = action;
 }

 public String getCls()
 {
  return cls;
 }

 public void setCls(String cls)
 {
  this.cls = cls;
 }

 public Action[] getSubActions()
 {
  return subActions;
 }

 public void setSubActions(Action[] subActions)
 {
  this.subActions = subActions;
 }

 public Component getComponent()
 {
  return component;
 }

 public void setComponent(Component component)
 {
  this.component = component;
 }

 public Action copy()
 {
  return copyAction(this);
 }
 
 public static Action copyAction( Action orAc )
 {
  Action newAc = new Action();
  newAc.action=orAc.action;
  newAc.cls=orAc.cls;
  newAc.text=orAc.text;
  
  if( orAc.subActions != null )
  {
   newAc.subActions = new Action[orAc.subActions.length];
   
   for( int i=0; i < orAc.subActions.length; i++ )
    newAc.subActions[i]=copyAction(orAc.subActions[i]);
  }
  
  return newAc;
 }
}
