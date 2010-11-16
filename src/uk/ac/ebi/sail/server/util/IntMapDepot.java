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

package uk.ac.ebi.sail.server.util;

import com.pri.util.ObjectRecycler;
import com.pri.util.collection.IntMap;
import com.pri.util.collection.IntTreeMap;

public class IntMapDepot extends ObjectRecycler<IntMap<Void>>
{
 private static IntMapDepot instance = new IntMapDepot(10);
 
 private IntMapDepot(int sz)
 {
  super(sz);
 }

 public static IntMapDepot getInstance()
 {
  return instance;
 }
 
 public static IntMap<Void> getMap()
 {
  IntMap<Void> map = getInstance().getObject();
  
  if( map == null )
   return new IntTreeMap<Void>();
  
  return map;
 }
 
 public static void recycleMap( IntMap<Void> mp )
 {
  mp.clear();
  getInstance().recycleObject(mp);
 }

}
