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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.form.TextField;

public abstract class TwinTriggerField extends TextField {

 static
 {
  reg();
 }
 

 private static native void reg()
 /*-{ $wnd.Ext.reg('twintrigger', $wnd.Ext.form.TwinTriggerField); }-*/;
 
 public TwinTriggerField() {
 }

 public TwinTriggerField(JavaScriptObject jsObj) {
     super(jsObj);
 }

 protected void initComponent() {
     super.initComponent();
     setup(this, getJsObj());
 }

 protected native Element getElement(JavaScriptObject jsObj) /*-{
     //for trigger fields, we want the text area as well as the trigger button to be treated as the element
     //unit
     var extEl = jsObj.wrap;
     if(extEl == null || extEl === undefined) {
         return null;
     }
     var el = extEl.dom;
     if(el == null || el === undefined) {
         return null;
     } else {
         //There's an inconsistency in Ext where most elements have the property 'el' set to Ext's Element
         //with the exception of Menu->Item, Menu->Separator, Menu->TextItem,  Toolbar.Item and subclasses
         //(Toolbar.Separator, Toolbar.Spacer, Toolbar.TextItem) where the 'el' property is set to
         //the DOM element itself. Therefore retruning 'el' if 'el' is not Ext's Element. See details in issue 39.
          return el.dom || el ;
     }
 }-*/;

 private native void setup(TwinTriggerField triggerField, JavaScriptObject jsObj) /*-{
     jsObj.onTrigger1Click = function(event) {
         var e = @com.gwtext.client.core.EventObject::instance(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
         triggerField.@uk.ac.ebi.sail.client.ui.widget.TwinTriggerField::onTrigger1Click(Lcom/gwtext/client/core/EventObject;)(e);
         }
     
     jsObj.onTrigger2Click = function(event) {
         var e = @com.gwtext.client.core.EventObject::instance(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
         triggerField.@uk.ac.ebi.sail.client.ui.widget.TwinTriggerField::onTrigger2Click(Lcom/gwtext/client/core/EventObject;)(e);
     }
 }-*/;
 
 protected native JavaScriptObject create(JavaScriptObject jsObj) /*-{
     return new $wnd.Ext.form.TwinTriggerField(jsObj);
 }-*/;

 /**
  * Abstract method that must be implmented for custom trigger field behavior.
  *
  * @param event the event object
  */
 protected abstract void onTrigger1Click(EventObject event);
 protected abstract void onTrigger2Click(EventObject event);

 // config properties ---
 public String getXType() {
     return "twintrigger";
 }

 /**
  * True to hide the trigger element and display only the base text field (defaults to false).
  *
  * @param hideTrigger true to hide trigger
  * @throws IllegalStateException this property cannot be changed after the Component has been rendered
  */
 public void setHideTrigger(boolean hideTrigger) throws IllegalStateException {
     setAttribute("hideTrigger", hideTrigger, false);
 }

 /**
  * A CSS class to apply to the trigger.
  *
  * @param triggerClass the trigger CSS class.
  * @throws IllegalStateException this property cannot be changed after the Component has been rendered
  */
 public void setTrigger1Class(String triggerClass) throws IllegalStateException {
     setAttribute("trigger1Class", triggerClass, true);
 }

 public void setTrigger2Class(String triggerClass) throws IllegalStateException {
  setAttribute("trigger2Class", triggerClass, true);
 }
}
