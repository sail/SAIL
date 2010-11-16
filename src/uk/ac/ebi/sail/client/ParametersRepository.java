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

import uk.ac.ebi.sail.client.common.Parameter;

import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.StringFieldDef;

public class ParametersRepository
{
 private static RecordDef recordDef = new RecordDef(new FieldDef[] {
   new IntegerFieldDef("id"),
   new StringFieldDef("code"),
   new StringFieldDef("parameterName"),
   new StringFieldDef("parameterDescription"),
   new IntegerFieldDef("records"),
   new IntegerFieldDef("varables"),
   new IntegerFieldDef("enumerations"),
   new ObjectFieldDef("parameter") });

 public static RecordDef getRecordDefinition()
 {
  return recordDef;
 }

 public static Parameter getParameter(int asInteger)
 {
  // TODO Auto-generated method stub
  return null;
 }

}
