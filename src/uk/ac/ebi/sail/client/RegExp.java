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

package uk.ac.ebi.sail.client;

 import com.google.gwt.core.client.JavaScriptObject;

 import java.util.Iterator;
 import java.util.NoSuchElementException;

 /**
  * Wrapper for the RegExp JavaScript object. Can be passed as parameter to JSNI
  * methods. Almost all of its methods get inlined. No extra weight.
  * <p>
  * RegExp literals like /abc/ are not possible. For this we need a Generator
  * that can except parameters which is currently not possible with GWT.
  */
 public final class RegExp extends JavaScriptObject {

   /**
    * Wraps the result of any RegExp operation which is either an object or an
    * array of strings. Use the accessor methods, or use the iterator, or
    * <code>cast()</code> it to JsArrayString to access the matched strings.
    */
   public static final class Result extends JavaScriptObject {

     class IterableIterator implements Iterable<String>, Iterator<String> {
       private int index = 0;

       public boolean hasNext() {
         return index < length();
       }

       public Iterator<String> iterator() {
         return this;
       }

       public String next() {
         if (index == length()) {
           throw new NoSuchElementException();
         }
         assert index < length() : index + " > " + length();
         return get(index++);
       }

       public void remove() {
         throw new UnsupportedOperationException();
       }
     }

     protected Result() {
     }

     /**
      * Depending on the global ('g') flag this either returns a 0-indexed match
      * or 1-indexed submatch.
      * 
      * @param num
      * @return match (0-based index) or submatch (1-based index)
      */
     public native String get(int num) /*-{
       return this[num];
     }-*/;

     /**
      * @return The index of the match. Always <code>0</code> in global matching
      *         mode.
      */
     public native int index() /*-{
       return this.index;
     }-*/;

     /**
      * @return The input string. Always <code>null</code> in global matching
      *         mode.
      */
     public native String input() /*-{
       return this.input;
     }-*/;

     /**
      * Returns an Iterator which is also Iterable and can be used in foreach
      * loops. This is because JavaScriptObject cannot implement interfaces. :-(
      */
     public IterableIterator iterator() {
       return new IterableIterator();
     }

     /**
      * @return the number of matches or submatches.
      */
     public native int length() /*-{
       return this.length;
     }-*/;

     /**
      * @return the matched string.
      */
     public native String match() /*-{
       return this[0];
     }-*/;

     public String[] toArray() {
       return toArray(new String[length()]);
     }

     /**
      * Copies the matches into <code>stringArray</code>.
      * 
      * @param stringArray
      * @return stringArray
      */
     public String[] toArray(String[] stringArray) {
       for (int i = 0; i < length(); ++i) {
         stringArray[i] = this.get(i);
       }
       return stringArray;
     }
   }

   // CHECKSTYLE_OFF method names must start with '$'
   
   /**
    * @return
    */
   public static native String $1() /*-{
     return RegExp.$1;
   }-*/;

   /**
    * @return
    */
   public static native String $2() /*-{
     return RegExp.$2;
   }-*/;

   /**
    * @return
    */
   public static native String $3() /*-{
     return RegExp.$3;
   }-*/;

   /**
    * @return
    */
   public static native String $4() /*-{
     return RegExp.$4;
   }-*/;

   /**
    * @return
    */
   public static native String $5() /*-{
     return RegExp.$5;
   }-*/;

   /**
    * @return
    */
   public static native String $6() /*-{
     return RegExp.$6;
   }-*/;

   /**
    * @return
    */
   public static native String $7() /*-{
     return RegExp.$7;
   }-*/;

   /**
    * @return
    */
   public static native String $8() /*-{
     return RegExp.$8;
   }-*/;

   /**
    * @return
    */
   public static native String $9() /*-{
     return RegExp.$9;
   }-*/;

   // CHECKSTYLE_ON
   
   /**
    * Factory method for RegExp object.
    * 
    * @param pattern
    * @return the newly created RegExp object
    */
   public static native RegExp compile(String pattern) /*-{
     return new RegExp(pattern);
   }-*/;

   /**
    * Factory method for RegExp object.
    * 
    * @param pattern
    * @param flags Any combination of <code>'g'</code>, <code>'m'</code>, and
    *          <code>'i'</code>, and some browser-specific flags like
    *          <code>'y'</code>.
    * @return the newly created RegExp object
    */
   public static native RegExp compile(String pattern, String flags) /*-{
     return new RegExp(pattern, flags);
   }-*/;

   protected RegExp() {
   }

   /**
    * @param str
    * @return
    */
   public native Result exec(String str) /*-{
     return this.exec(str);
   }-*/;

   /**
    * @return the index following the end of the last match
    */
   public native int getLastIndex() /*-{
     return this.lastIndex;
   }-*/;

   /**
    * @return the pattern without surrounding quotes or slashes
    */
   public native String getSource() /*-{
     return this.source;
   }-*/;

   /**
    * @return true if 'g' flag is set.
    */
   public native boolean isGlobal() /*-{
     return this.global;
   }-*/;

   /**
    * @return true if 'i' flag is set.
    */
   public native boolean isIgnoringCase() /*-{
     return this.ignoreCase;
   }-*/;

   /**
    * @return true if 'm' flag is set.
    */
   public native boolean isMultiline() /*-{
     return this.multiline;
   }-*/;

   /**
    * @return true if 'y' flag is set. This is currently only supported by
    *         Firefox 3.
    */
   public native boolean isSticky() /*-{
     return this.sticky || false;
   }-*/;

   /**
    * @param str
    * @return
    */
   public native Result match(String str) /*-{
     return str.match(this);
   }-*/;

   /**
    * @param str
    * @param replacement
    * @return
    */
   public native String replace(String str, String replacement) /*-{
     return str.replace(this, replacement);
   }-*/;

   /**
    * @param str
    * @return the index of the match inside <code>str</code>. Otherwise, it
    *         returns -1.
    */
   public native int search(String str) /*-{
     return str.search(this);
   }-*/;

   /**
    * @param lastIndex
    */
   public native void setLastIndex(int lastIndex) /*-{
     this.lastIndex = lastIndex;
   }-*/;

   /**
    * @param str
    * @return
    */
   public native Result split(String str) /*-{
     return str.split(this);
   }-*/;

   /**
    * This split() is different from java.lang.String.split(). The limit is
    * applies after the complete string is split.
    * 
    * @param str
    * @param limit
    * @return
    */
   public native Result split(String str, int limit) /*-{
     return str.split(this, limit);
   }-*/;

   /**
    * This method tests if a string matches the specified pattern.
    * 
    * @param str the string to be tested
    * @return true if <code>text</code> matches the pattern.
    */
   public native boolean test(String str) /*-{
     return this.test(str);
   }-*/;
 }
