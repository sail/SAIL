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

package uk.ac.ebi.sail.server;

import java.util.List;

public class Row
{
 private int counter;
 private List<String> sampleIDs;
 private byte[] row;
 
 public Row(int size)
 {
 }

 public void setCount(int c)
 {
  counter=c;
 }
 
 public int getCount()
 {
  return counter;
 }

 public void setSampleIDs(String ro, char sidSep)
 {
  // TODO Auto-generated method stub
  
 }

 public void set(int idx, byte val)
 {
  // TODO Auto-generated method stub
  
 }
 
 
}