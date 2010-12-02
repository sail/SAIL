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
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.Relation;
import uk.ac.ebi.sail.client.common.Classifier.Target;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.ActionFlags;
import uk.ac.ebi.sail.client.ui.module.ObjectList.Selection;

import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.core.TextAlign;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

public class RelationsDialog extends Window implements ObjectAction<Parameter>
{
 private ObjectList<Parameter> rGrid;
 private ObjectAction<Parameter> listener;
 private boolean addGroupState;
 
 private static RecordDef         recordDef = new RecordDef(new FieldDef[] {
   new StringFieldDef("code"),
   new StringFieldDef("param"),
   new StringFieldDef("class"),
   new StringFieldDef("tag"),
   new IntegerFieldDef("records"),
   new ObjectFieldDef("obj") });

 public static RelationsDialog getIntance()
 {
  return new RelationsDialog();
 }

 public RelationsDialog()
 {
  super("Select parameters");
  
  setLayout(new BorderLayout()); 
  setModal( true );
  setWidth(600);  
  setHeight(400);
//  setPlain(true);  
  
  BorderLayoutData centerData = new BorderLayoutData(RegionPosition.CENTER);  
  centerData.setMargins(3, 0, 3, 3); 
  
  ColumnConfig[] columns = new ColumnConfig[] { 
    new ColumnConfig("Code", "code", 100, true, null, "code"),
    new ColumnConfig("Parameter", "param", 180, true, null, "param"),
    new ColumnConfig("Classifier", "class", 125, true, null, "class"),
    new ColumnConfig("Tag", "tag", 90),
    new ColumnConfig("Records", "records", 55)
  };
  
  columns[4].setAlign(TextAlign.RIGHT);
  ColumnModel columnModel = new ColumnModel(columns);
  
  
  
  rGrid = new ClassifiableList<Parameter>(Selection.CHECKBOX, new Action[]{ 
    new Action("Add To Selection","select"+ActionFlags.separator+ActionFlags.ALLOW_MULTIPLE,null,null),
    new Action("Add To Report","addToReport"+ActionFlags.separator+ActionFlags.ALLOW_MULTIPLE,null,null),
    new Action("Add To Group","addToGroup"+ActionFlags.separator+ActionFlags.ALLOW_MULTIPLE,null,null),
    new Action("Cancel","cancel"+ActionFlags.separator+ActionFlags.EMPTY,null,null)},
    recordDef, columnModel,"param",
    DataManager.getInstance().getClassifierManager(Target.PARAMETER));
  rGrid.setObjectActionListener(this);
  
  add( rGrid, centerData );
  
 }
 
 public void setParameter( Parameter p )
 {
  if( p.getRelations() == null )
   return;
  
  for(Relation rl : p.getRelations())
  {
   rGrid.getStore().add(recordDef.createRecord(new Object[]{
     rl.getTargetParameter().getCode(),
     rl.getTargetParameter().getName(),
     rl.getTag().getClassifier().getName(),
     rl.getTag().getName(),
     rl.getTargetParameter().getRecordsCount(),
     rl.getTargetParameter()}));
  }
  

 }

 public void setObjectActionListener(ObjectAction<Parameter> ls)
 {
  listener = ls;
 }

 public void hideDialog()
 {
  close();
 }
 
 public void doAction(String actName, Parameter p)
 {
  hideDialog();
  
  if( listener != null )
   listener.doAction(actName, p);  
 }

 public void doMultyAction(String actName, List<Parameter> lp)
 {
  hideDialog();
  
  if( listener != null )
   listener.doMultyAction(actName, lp);  
 }

 public void setGroupState(boolean agp)
 {
  rGrid.setActionEnabled("addToGroup", agp);
 }
 
}
