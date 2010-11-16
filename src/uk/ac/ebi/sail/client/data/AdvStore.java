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

package uk.ac.ebi.sail.client.data;

import com.gwtext.client.core.RegExp;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StoreTraversalCallback;
import com.gwtext.client.data.Record.Operation;
import com.gwtext.client.data.event.StoreListener;

public class AdvStore extends Store implements StoreListener
{
 public AdvStore(RecordDef recordDef)
 {
  super(recordDef);
  addStoreListener(this);
  
  getJsObj();
  
  init( );
 }
 
 native void init()
 /*-{
     var store = this.@com.gwtext.client.core.JsObject::getJsObj()();
     
     store.aso = this;   
     
     store.filterBy1=store.filterBy;
     
     store.filterBy=function(obj, scope)
     {
      var cbObj = new Object();
      cbObj.cb=obj;
      cbObj.scope=scope;
      var cb = @uk.ac.ebi.sail.client.data.StoreTraversalCallbackWrapper::instance(Lcom/google/gwt/core/client/JavaScriptObject;)(cbObj); 
      store.aso.@uk.ac.ebi.sail.client.data.AdvStore::filterBy(Lcom/gwtext/client/data/StoreTraversalCallback;)( cb );
     };
     
     store.clearFilter1=store.clearFilter;
     
     store.clearFilter = function(){ store.aso.@uk.ac.ebi.sail.client.data.AdvStore::clearFilter()(); };
}-*/;
 
 public native void filterBy1(StoreTraversalCallback cb)/*-{
 var store = this.@com.gwtext.client.core.JsObject::getJsObj()();
 store.filterBy1(function(r) {
     var rj = @com.gwtext.client.data.Record::instance(Lcom/google/gwt/core/client/JavaScriptObject;)(r);
     return cb.@com.gwtext.client.data.StoreTraversalCallback::execute(Lcom/gwtext/client/data/Record;)(rj);
 });
}-*/;

 
 public native void clearFilter1()/*-{
 var store = this.@com.gwtext.client.core.JsObject::getJsObj()();
 store.clearFilter1();
}-*/;

 
 public void clearFilter()
 {
  System.out.println("Filter cleared");
  clearFilter1();
 }
 
 public void filterBy( StoreTraversalCallback cb) 
 {
  System.out.println("Filter by. "+cb.getClass().getName());
  filterBy1(cb);
  System.out.println("Rec: "+getRecords().length);
 }

 public void filter(String field, RegExp regexp)
 {
  System.out.println("Filter");
 }

 public void onDataChanged(Store store)
 {
  System.out.println("onDataChanged");
 }

 public boolean doBeforeLoad(Store store)
 {
  // TODO Auto-generated method stub
  return true;
 }

 public void onAdd(Store store, Record[] records, int index)
 {
  // TODO Auto-generated method stub
  
 }

 public void onClear(Store store)
 {
  // TODO Auto-generated method stub
  
 }

 public void onLoad(Store store, Record[] records)
 {
  // TODO Auto-generated method stub
  
 }

 public void onLoadException(Throwable error)
 {
  // TODO Auto-generated method stub
  
 }

 public void onRemove(Store store, Record record, int index)
 {
  // TODO Auto-generated method stub
  
 }

 public void onUpdate(Store store, Record record, Operation operation)
 {
  // TODO Auto-generated method stub
  
 }

}
