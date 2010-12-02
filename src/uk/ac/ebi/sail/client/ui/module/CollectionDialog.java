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
import uk.ac.ebi.sail.client.ui.CollectionProvider;
import uk.ac.ebi.sail.client.ui.module.ObjectList.Selection;

import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

public class CollectionDialog extends Window implements CollectionProvider, ObjectAction<SampleCollection>
{
 private CollectionList rsp;
 
 private static CollectionDialog instance;
 
 
 public static CollectionDialog getInstance()
 {
  if( instance == null )
   instance = new CollectionDialog();
  
  return instance;
 }
 
 public CollectionDialog()
 {
  super("Choose collections");
  
  setLayout(new BorderLayout()); 
  setModal( true );
  setWidth(700);  
  setHeight(500);
//  setPlain(true);  
  
  rsp = new CollectionList(Selection.CHECKBOX, new Action[]{
    new Action("OK","ok"+ActionFlags.separator+ActionFlags.EMPTY)},
    DataManager.getInstance().getSampleCollectionManager());
  

  BorderLayoutData centerData = new BorderLayoutData(RegionPosition.CENTER);  
  centerData.setMargins(3, 0, 3, 3); 

  add( rsp, centerData );
  
  rsp.setObjectActionListener( this );
  
 }

 public int[] getCollections()
 {
  return rsp.getCollections();
 }

 public void doAction(String actName, SampleCollection p)
 {
  hide();
 }

 public void doMultyAction(String actName, List<SampleCollection> lp)
 {
  hide();
 }

}
