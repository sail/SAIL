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

package uk.ac.ebi.sail.client.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class IDBunch implements IsSerializable,Serializable
{
 private int collectionID;
 private List<String> ids = new ArrayList<String>();

 public IDBunch()
 {}
 
 public IDBunch(int i)
 {
  collectionID=i;
 }

 public void setCollectionID(int collectionID)
 {
  this.collectionID = collectionID;
 }

 public int getCollectionID()
 {
  return collectionID;
 }

 public void addID(String recID )
 {
  ids.add(recID);
 }
 
 public Collection<String> getIds()
 {
  return ids;
 }

}
