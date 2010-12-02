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

import java.util.HashMap;
import java.util.Map;

import uk.ac.ebi.sail.client.common.Classifiable;
import uk.ac.ebi.sail.client.common.ClassifiableManager;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.SAILObject;
import uk.ac.ebi.sail.client.common.Tag;
import uk.ac.ebi.sail.client.data.Attributed;
import uk.ac.ebi.sail.client.data.Filterable;
import uk.ac.ebi.sail.client.data.TraversalCallback;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.widget.ClassifiableSearchToolbar;

import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.StoreTraversalCallback;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.grid.ColumnModel;


public class ClassifiableList<T extends SAILObject & Classifiable & Attributed> extends ObjectList<T> implements Filterable<T>, StoreTraversalCallback
{
 
 private ClassifiableManager<Classifier> classifiableManager;
 private Map<String,TraversalCallback<T>> filters=new HashMap<String,TraversalCallback<T>>();
 private ClassifiableSearchToolbar<T> toolbar;

 public ClassifiableList(Selection selMod, Action[] bttns, RecordDef recordDef,
   ColumnModel columnModel, String expand, ClassifiableManager<Classifier> clMngr)
 {
  super();
  classifiableManager = clMngr;
  
//  List<Integer> colNums = new ArrayList<Integer>(5);
//  
//  int n = columnModel.getColumnConfigs().length;
//  
//  for(int i=0; i < n; i++)
//  {
//   String dx = columnModel.getDataIndex(i);
//   
//   for( FieldDef fd : recordDef.getFields() )
//   {
//    if( fd.getName().equals(dx) )
//    {
//     if( fd instanceof StringFieldDef )
//      colNums.add(i);
//     
//     break;
//    }
//   }
//
//  }
//
//  n = colNums.size();
//  
//  fields = new String[ n ][];
//  
//  for(int i=0; i < n; i++)
//  {
//   fields[i] = new String[2];
//   fields[i][0]=columnModel.getColumnHeader(colNums.get(i));
//   fields[i][1]=columnModel.getDataIndex(colNums.get(i));
//  }
  
  build(selMod, bttns, recordDef, columnModel, expand);
 }

 protected Toolbar getToolbar()
 {
  if( toolbar == null )
   toolbar=new ClassifiableSearchToolbar<T>(this, classifiableManager, getSearchFields());
  
  return toolbar;
 }

 public void removeFilter(String fName)
 {
  filters.remove(fName);
  refilter();
 }

 @SuppressWarnings("unchecked")
 private void refilter()
 {
  getStore().clearFilter();
  
  if( filters.size() > 0 )
   getStore().filterBy(this);
  else
   for( Record r : getStore().getRecords() )
    updateRecord(r, (T)r.getAsObject("obj"));
 }

 @SuppressWarnings("unchecked")
 public boolean execute(Record record)
 {
  T obj = (T)record.getAsObject("obj");
//  T origObj = obj;
 
//  updateRecord(record, obj, obj);
 
  for(TraversalCallback<T> cb : filters.values())
   if( !cb.execute(obj) )
    return false;

//  if( obj != origObj )
  updateRecord(record, obj);
  
  return true;
 }

 protected void updateRecord(Record record, T obj)
 {}
 
 public void addFilter(String fid, TraversalCallback<T> cb)
 {
  filters.put(fid,cb);
  refilter(); 
 }

 
 public void setTagFilter(Classifier c, Tag t)
 {
  toolbar.setTagFilter(c, t);
 }
 
/*
 protected Toolbar createToolbar()
 {
  Toolbar tb = new Toolbar();
  
  classCBox=new ComboBox();
  classCBox.setDisplayField("name");
  classCBox.setTypeAhead(true);
  classCBox.setMode(ComboBox.LOCAL);
  classCBox.setEmptyText("Select classifier");
  classCBox.setTriggerAction(ComboBox.ALL);
  classCBox.setSelectOnFocus(true);
  
  classCBoxTT = new ToolTip();
  classCBoxTT.setHtml("Select classifier");
  classCBoxTT.applyTo(classCBox);

  tagCBox=new ComboBox();
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
  
  clStore.add(classCBRecordDef.createRecord(new Object[]{" [NO FILTER] ",null}));

  for( Classifier cl : classifiableManager.getClassifiable() )
   clStore.add(classCBRecordDef.createRecord(new Object[]{cl.getName(),cl}));
  
  classCBox.setStore(clStore);
  tagCBox.setStore(tgStore);

  classCBox.setValue(clStore.getAt(0).getAsString("name"));

  
  classCBox.addListener(new ComboBoxListenerAdapter(){
   public void onSelect(ComboBox comboBox, Record record, int index)
   {
    Store tst = tagCBox.getStore();
    
    tst.removeAll();
    
    Classifier cl = (Classifier)record.getAsObject("obj");
    
    
    if( cl == null )
    {
     classCBoxTT.setHtml( "Select classifier" );
     tagCBox.reset();
     tagCBoxTT.setDisabled(true);
     filterList(null,null);
     return;
    }

    classCBoxTT.setHtml(cl.getDescription() != null? "<b>"+cl.getName()+"</b><br>"+cl.getDescription() : "Classifier");
    
    tst.add(classCBRecordDef.createRecord(new Object[]{" [ANY] ",cl}));
    
    if( cl.getTags() != null )
    {
     for( Tag t : cl.getTags() )
      tst.add(classCBRecordDef.createRecord(new Object[]{t.getName(),t}));
    }
    
    tagCBox.setValue(tst.getAt(0).getAsString("name"));
    tagCBoxTT.setDisabled(false);
    tagCBoxTT.setHtml("Any tags of the classifier");
    
    filterList(cl,null);
   }
  } );
 
  tagCBox.addListener(new ComboBoxListenerAdapter(){
   public void onSelect(ComboBox comboBox, Record record, int index)
   {
    System.out.println("Tag: "+record.getAsString("name"));
    
    Object o = record.getAsObject("obj");
    
    Tag t = null;
    Classifier cl=null;
    
    if( o instanceof Tag )
    {
     t = (Tag)o;
     cl=t.getClassifier();
     tagCBoxTT.setHtml(t.getDescription() != null? "<b>"+t.getName()+"</b><br>"+t.getDescription() : t.getName());
    }
    else
    {
     t=null;
     cl=(Classifier)o;
     tagCBoxTT.setHtml("Any tags of the classifier");
    }
    
    filterList(cl, t);
   }
  } );

  
  tb.addField(classCBox);
  tb.addField(tagCBox);
  
  return tb;
 }
 
 
 private void filterList(final Classifier cl, final Tag tag)
 {
  final String FILTER_ID="_TagFilter";
  
  MultiFilterStore<T> store = getStore();
  
  if( tag == null && cl == null )
   store.removeFilter(FILTER_ID);
  else if( tag == null )
  {
   store.addFilter(FILTER_ID, new TraversalCallback<T>(){

//    public boolean execute(Record record)
//    {
//     Classifiable p = (Classifiable)record.getAsObject("obj");
//     
//     Collection<Tag> tags = cl.getTags();
//     
//     if( p.getClassificationTags() == null )
//      return false;
//     
//     for( Tag t : p.getClassificationTags() )
//     {
//      for( Tag ct : tags )
//       if( t.getClassifier() == ct.getClassifier() )
//        return true;
//     }
//     
//     return false;
//    }

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
 
 
*/

 protected void setData( Record[] data )
 {
  super.setData(data);
  
  refilter();
 }


}
