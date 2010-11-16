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

import java.util.List;

import uk.ac.ebi.sail.client.DataManager;
import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.SampleCollection;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.ActionFlags;
import uk.ac.ebi.sail.client.ui.module.ObjectList.Selection;

import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

public class AddCollectionDialog extends Window implements ObjectAction<SampleCollection>
{
 private CollectionList rsp;
 private ObjectAction<SampleCollection> actionListener;
 

 private static AddCollectionDialog instance;
 
 
 public static AddCollectionDialog getInstance()
 {
  if( instance == null )
   instance = new AddCollectionDialog();
  
  return instance;
 }
 
 public AddCollectionDialog()
 {
  super("Choose collections");
  
  setLayout(new BorderLayout()); 
  setModal( true );
  setWidth(700);  
  setHeight(500);
//  setPlain(true);  
  
  rsp = new CollectionList(Selection.CHECKBOX, new Action[]{
    new Action("OK","ok"+ActionFlags.separator+ActionFlags.ALLOW_MULTIPLE),
    new Action("Cancel","cancel"+ActionFlags.separator+ActionFlags.EMPTY)},
    DataManager.getInstance().getSampleCollectionManager());
  

  BorderLayoutData centerData = new BorderLayoutData(RegionPosition.CENTER);  
  centerData.setMargins(3, 0, 3, 3); 

  add( rsp, centerData );
  
  rsp.setObjectActionListener( this );
  
 }


 public void doAction(String actName, SampleCollection p)
 {
  hide();
  
  if(actionListener != null )
   actionListener.doAction(actName, p);
 }

 public void doMultyAction(String actName, List<SampleCollection> lp)
 {
  hide();

  if(actionListener != null )
   actionListener.doMultyAction(actName, lp);
 }

 public void setActionListener(ObjectAction<SampleCollection> actionListener)
 {
  this.actionListener = actionListener;
 }

}
