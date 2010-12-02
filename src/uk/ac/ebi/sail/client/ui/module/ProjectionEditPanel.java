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
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.Projection;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.layout.FitLayout;

public class ProjectionEditPanel extends FormPanel
{
  private TextField                nameField;
  private TextArea                 descriptionArea;

  private Projection               proj;

  private GridPanel clsGrid;
  private Store                    clsStore;

  private BtnListener buttonListener = new BtnListener();

  private static RecordDef         clsRecordDef    = new RecordDef(new FieldDef[] { 
    new StringFieldDef("name"),
    new StringFieldDef("desc"),
    new ObjectFieldDef("obj") });

  private static ColumnModel  clsColumnModel = new ColumnModel(new ColumnConfig[] { 
    new ColumnConfig("Classifier", "name", 130, true, null, "name"),
    new ColumnConfig("Description", "desc", 130, true, null, "desc")
    });
  
  public ProjectionEditPanel()
  {
   setFrame(true);
   setPaddings(5, 5, 5, 0);
   setWidth(450);

   add(nameField = new TextField("Name", "name", 300));

   descriptionArea = new TextArea("Description");
   descriptionArea.setWidth(300);
   add(descriptionArea);


   clsGrid = new GridPanel();
   clsGrid.setTitle("Classifiers");
   clsGrid.setLayout(new FitLayout());
   clsGrid.setSelectionModel(new RowSelectionModel(true));

   clsGrid.setColumnModel(clsColumnModel);

   clsGrid.setFrame(true);
   clsGrid.setStripeRows(true);
   clsGrid.setAutoExpandColumn("desc");
   clsGrid.setAutoHeight(true);

   ClsListEditor te = new ClsListEditor();
   
   Button bt = new Button("Add",te);
   bt.setStateId("add");
   clsGrid.addButton(bt);
   
   bt = new Button("Remove",te);
   bt.setStateId("remove");
   clsGrid.addButton(bt);

   bt = new Button("Up",te);
   bt.setStateId("up");
   clsGrid.addButton(bt);

   bt = new Button("Down",te);
   bt.setStateId("down");
   clsGrid.addButton(bt);

   clsStore = new Store(clsRecordDef);

   clsGrid.setStore(clsStore);

   add(clsGrid);

   bt = new Button("Save",buttonListener);
   bt.setStateId("ok");
   addButton(bt);

   bt = new Button("Cancel",buttonListener);
   bt.setStateId("cancel");
   addButton(bt);
  }

  public void setProjection(Projection pj)
  {
   if(pj == null)
    pj = new Projection();

   proj = pj;

   nameField.setValue(pj.getName() != null ? pj.getName() : "");
   descriptionArea.setValue(pj.getDescription() != null ? pj.getDescription() : "");

   // tagsMap.clear();

   if(pj.getClassifiers() != null)
   {
    clsStore.removeAll();

    for(Classifier t : pj.getClassifiers())
    {
     Record r = clsRecordDef.createRecord(new Object[] { t.getName(), t.getDescription(), t });
     clsStore.add(r);
    }
   }

   proj = pj;
   doLayout();
  }

  private boolean validate()
  {
   proj.setName(nameField.getValueAsString());
   proj.setDescription(descriptionArea.getValueAsString());
   
   if( proj.getClassifiers() != null && proj.getClassifiers().size() > 0 )
   {
    Classifier.Target tg = proj.getClassifiers().get(0).getTarget();
    
    for( Classifier cl : proj.getClassifiers() )
    {
     if( cl.getTarget() != tg )
     {
      ErrorBox.showError("Classifiers with different types can't be mixed on one projection");
      return false;
     }
    }
   }
   else
   {
    ErrorBox.showError("Projection must have at least one classifier");
    return false;
   }
   
   return true;
  }

  
  public Projection getProjection()
  {
   return proj;
  }


  public void setObjectActionListener(ObjectAction<Projection> ls)
  {
   buttonListener.setListener(ls);
  }

  class BtnListener extends ButtonListenerAdapter
  {
   private ObjectAction<Projection> lsnr;
   
   public BtnListener()
   {
    super();
   }
   
   public void setListener( ObjectAction<Projection> l )
   {
    lsnr=l;
   }

   public void onClick(Button button, EventObject e)
   {
    String state = button.getStateId();
    
    if( lsnr != null )
    {
     if( "ok".equals(state) )
     {
      try
      {
      if( validate() )
       lsnr.doAction( state, proj );
      }
      catch (Exception ex) {
       ex.printStackTrace();
      }
     }
     else
      lsnr.doAction( state, null );
    }
   }

  }
  
  class ClsListEditor extends ButtonListenerAdapter // implements ObjectAction<Projection>
  {

   public void onClick(Button button, EventObject e)
   {
   String state = button.getStateId();

   if("add".equals(state))
   {
    final ClassifierSelectDialog clsSelDialog = ClassifierSelectDialog.getDialog(DataManager.getInstance().getClassifierManager(null));
    clsSelDialog.setObjectActionListener(new ObjectAction<Classifier>()
    {

     public void doAction(String actName, Classifier p)
     {
      clsSelDialog.dispose();

      if("cancel".equals(actName))
       return;

      clsStore.add(clsRecordDef.createRecord(new Object[] { p.getName(), p.getDescription(), p }));
      proj.addClassifier(p);

     }

     public void doMultyAction(String actName, List<Classifier> lp)
     {

     }
    });

    clsSelDialog.show();
   }
   else if("remove".equals(state)) 
   {
    Record r = clsGrid.getSelectionModel().getSelected();

    if(r == null)
     return;

    Classifier cl = (Classifier) r.getAsObject("obj");

    clsStore.remove(r);
    proj.removeClassifier(cl);
    
    return;
   }
   else if( "down".equals(state) )
   {
    Record r = clsGrid.getSelectionModel().getSelected();

    if(r == null)
     return;

    Classifier cl = (Classifier) r.getAsObject("obj");

    int i=0;
    for(Record rd : clsStore.getRecords())
    {
     i++;

     if( cl == (Classifier) rd.getAsObject("obj") )
      break;
     
    }
    
    
    if( i == clsStore.getCount()-2 )
     return;

    
    clsStore.remove(r);
    clsStore.insert(i, r);

    clsGrid.getSelectionModel().selectRow(i);
    proj.moveClassifier(i,cl);    
   }
   else if( "up".equals(state) )
   {
    Record r = clsGrid.getSelectionModel().getSelected();

    if(r == null)
     return;

    Classifier cl = (Classifier) r.getAsObject("obj");

    int i=0;
    for(Record rd : clsStore.getRecords())
    {
     if( cl == (Classifier) rd.getAsObject("obj") )
      break;
     
     i++;
    }
    
    if( i == 0 )
     return;

    i--;
    
    clsStore.remove(r);
    clsStore.insert(i, r);

    clsGrid.getSelectionModel().selectRow(i);
    proj.moveClassifier(i,cl);    
   }

  }
   
  }
  
  
}
