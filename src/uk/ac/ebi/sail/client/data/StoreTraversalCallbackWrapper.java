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

import com.google.gwt.core.client.JavaScriptObject;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.StoreTraversalCallback;
import com.gwtext.client.core.JsObject;

public class StoreTraversalCallbackWrapper extends JsObject implements StoreTraversalCallback
{
 
 public StoreTraversalCallbackWrapper(JavaScriptObject jso)
 {
  super(jso);
 }

 public static StoreTraversalCallbackWrapper instance(JavaScriptObject jso)
 {
  return new StoreTraversalCallbackWrapper(jso);
 }

 public native boolean execute(Record record)
 /*-{
 var func = this.@com.gwtext.client.core.JsObject::getJsObj()();
 return func.cb.call( func.scope, record.@com.gwtext.client.core.JsObject::getJsObj()() );
}-*/;
}
