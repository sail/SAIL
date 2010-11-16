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

import java.util.Collection;

import uk.ac.ebi.sail.client.ConfigManager;
import uk.ac.ebi.sail.client.common.Classifiable;
import uk.ac.ebi.sail.client.common.ClassifiableManager;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.data.Filterable;
import uk.ac.ebi.sail.client.data.TraversalCallback;

import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;

public class ClassiableSearchComponent<T extends Classifiable>
{
 private static RecordDef classCBRecordDef = new RecordDef(new FieldDef[] { 
   new StringFieldDef("name"),
   new ObjectFieldDef("obj")
 });

 private ComboBox classCBox;
 private ComboBox tagCBox;
 
 private ToolTip classCBoxTT;
 private ToolTip tagCBoxTT;

 private ClassifiableManager<Classifier> clMngr;
 private Filterable<T> store;
 
 private Toolbar toolbar;
 
 public ClassiableSearchComponent(Toolbar tb, Filterable<T> flt, ClassifiableManager<Classifier> cm)
 {
  toolbar=tb;
  store=flt;
  clMngr=cm;
  
  classCBox = new ComboBox();
  classCBox.setDisplayField("name");
  classCBox.setTypeAhead(true);
  classCBox.setMode(ComboBox.LOCAL);
  classCBox.setEmptyText("Select classifier");
  classCBox.setTriggerAction(ComboBox.ALL);
  classCBox.setSelectOnFocus(true);

  classCBoxTT = new ToolTip();
  classCBoxTT.setHtml("Select classifier");
  classCBoxTT.applyTo(classCBox);

  tagCBox = new ComboBox();
  tagCBox.setDisplayField("name");
  tagCBox.setEmptyText("Select tag");
  tagCBox.setTypeAhead(true);
  tagCBox.setMode(ComboBox.LOCAL);
  tagCBox.setTriggerAction(ComboBox.ALL);
  tagCBox.setSelectOnFocus(true);

  tagCBoxTT = new ToolTip();
  tagCBoxTT.setDisabled(true);
  tagCBoxTT.applyTo(tagCBox);

  Store clStore = new Store(classCBRecordDef);
  final Store tgStore = new Store(classCBRecordDef);

  clStore.add(classCBRecordDef.createRecord(new Object[] { " [NO FILTER] ", null }));

  for(Classifier cl : clMngr.getClassifiable())
   clStore.add(classCBRecordDef.createRecord(new Object[] { cl.getName(), cl }));

  classCBox.setStore(clStore);
  tagCBox.setStore(tgStore);

  classCBox.setValue(clStore.getAt(0).getAsString("name"));

  classCBox.addListener(new ComboBoxListenerAdapter()
  {
   public void onSelect(ComboBox comboBox, Record record, int index)
   {
    Classifier cl = (Classifier) record.getAsObject("obj");
    
    selectClassifier(cl, null);
    
    filterList(cl, null);
   }
  });

  tagCBox.addListener(new ComboBoxListenerAdapter()
  {
   public void onSelect(ComboBox comboBox, Record record, int index)
   {
//    System.out.println("Tag: " + record.getAsString("name"));

    Object o = record.getAsObject("obj");

    Tag t = null;
    Classifier cl = null;

    if(o instanceof Tag)
    {
     t = (Tag) o;
     cl = t.getClassifier();
     tagCBoxTT.setHtml(t.getDescription() != null ? "<b>" + t.getName() + "</b><br>" + t.getDescription() : t
       .getName());
    }
    else
    {
     t = null;
     cl = (Classifier) o;
     tagCBoxTT.setHtml("Any tags of the classifier");
    }

    filterList(cl, t);
   }
  });

  toolbar.addField(classCBox);
  toolbar.addField(tagCBox);
 }
 
 
 private boolean selectClassifier( Classifier cl, Tag tag )
 {
  if( cl != null )
  {
   Store cst = classCBox.getStore();

   Record clRecord = null;

   for(Record r : cst.getRecords())
   {
    if(cl.equals(r.getAsObject("obj")))
    {
     clRecord = r;
     break;
    }
   }

   if(clRecord == null)
    cl = null;
  }
  
  Store tst = tagCBox.getStore();
  tst.removeAll();


  if(cl == null)
  {
   classCBoxTT.setHtml("Select classifier");
   tagCBox.reset();
   tagCBox.setValue(null);
   tagCBoxTT.setDisabled(true);
//   filterList(null, null);
   return false;
  }

  classCBoxTT.setHtml(cl.getDescription() != null ? "<b>" + cl.getName() + "</b><br>" + cl.getDescription()
    : "Classifier");

  int tagIdx=0;
  
  tst.add(classCBRecordDef.createRecord(new Object[] { " [ANY] ", cl }));

  
  int n=0;
  if(cl.getTags() != null)
  {
   for(Tag t : cl.getTags())
   {
    n++;
    tst.add(classCBRecordDef.createRecord(new Object[] { t.getName(), t }));
    
    if( t.equals(tag))
     tagIdx=n;
   }
  }

  tagCBox.setValue(tst.getAt(tagIdx).getAsString("name"));
  tagCBoxTT.setDisabled(false);
  tagCBoxTT.setHtml("Any tags of the classifier");
  
  return true;
 }
 
 public void setFilter( Classifier cl, Tag tag )
 {
  if( selectClassifier(cl, tag) )
  {
   for( Record r : classCBox.getStore().getRecords() )
   {
    if( (cl == null && r.getAsObject("obj") == null ) || cl.equals(r.getAsObject("obj")) )
    {
     classCBox.setValue(r.getAsString("name"));
     break;
    }
   }
  }
  
  filterList(cl, tag);
 }
 
 private void filterList(final Classifier cl, final Tag tag)
 {
  ConfigManager.setParameterTagFilter( (cl!=null?cl.getName():"")+(tag!=null?(":"+tag.getName()):"") );
  
  final String FILTER_ID="_TagFilter";
  
  if( tag == null && cl == null )
   store.removeFilter(FILTER_ID);
  else if( tag == null )
  {
   store.addFilter(FILTER_ID, new TraversalCallback<T>(){

    public boolean execute(T p)
    {
     Collection<Tag> tags = cl.getTags();
     
     if( p.getClassificationTags() == null )
      return false;
     
     for( Tag t : p.getClassificationTags() )
     {
      for( Tag ct : tags )
       if( t.getClassifier() == ct.getClassifier() )
        return true;
     }
     
     return false;
    }});
  }
  else
  {
   store.addFilter(FILTER_ID, new TraversalCallback<T>(){

    public boolean execute(T p)
    {
     if( p.getClassificationTags() == null )
      return false;
     
     for( Tag t : p.getClassificationTags() )
     {
      if( t == tag )
        return true;
     }
     
     return false;
    }});
  }
  
  
 }

}
