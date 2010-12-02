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


public class PartRequestItem extends ParameterRequestItem
{
 private int partID;
 
 public PartRequestItem(String string, ParameterPart v)
 {
  super(string, v.getParameter());
  
  setName(string);
  partID = v.getId();
  setType(Type.PART);

 }

 public PartRequestItem(int paramId, int partId2)
 {
  super(paramId);
  partID=partId2;
  setType(Type.PART);
 }

 public int getPartID()
 {
  return partID;
 }

 public void setPartID(int partID)
 {
  this.partID = partID;
 }
 
 protected void toSerialString( StringBuilder sb )
 {
  super.toSerialString( sb );
  sb.append(':').append(partID);
 }

 @Override
 public String getIconClass()
 {
  return "splitIcon";
 }
}
