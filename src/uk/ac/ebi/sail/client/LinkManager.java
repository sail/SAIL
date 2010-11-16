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

import java.util.Map;
import java.util.TreeMap;

public class LinkManager
{
 static
 {
  instance = new LinkManager();
  
  init();
 }
 
 private static LinkManager instance;
 
 private Map<String, LinkClickListener> lsnrs = new TreeMap<String, LinkClickListener>();
 
 private static native void init()
 /*-{
  $wnd.linkClicked =
          @uk.ac.ebi.sail.client.LinkManager::jsLinkClicked(Ljava/lang/String;Ljava/lang/String;);
 }-*/;
 
 public static LinkManager getInstance()
 {
  return instance;
 }
 
 @SuppressWarnings("unused")
 private static void jsLinkClicked( String linkId, String param )
 {
  getInstance().linkClicked(linkId, param);
 }

 private void linkClicked(String linkId, String param)
 {
  LinkClickListener lsn = lsnrs.get(linkId);
  
  if( lsn != null )
   lsn.linkClicked(param);
 }
 
 public void addLinkClickListener( String linkId, LinkClickListener l )
 {
  lsnrs.put(linkId, l);
 }
 
 public void removeLinkClickListener( String linkId )
 {
  lsnrs.remove(linkId);
 }

}
