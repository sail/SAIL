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

package uk.ac.ebi.sail.server.data;

import java.util.Collection;

public class ParameterRelationRowProcessor extends ParameterAlternativeRowProcessor
{
 private RowProcessor mainPattern;
 
 protected ParameterRelationRowProcessor(int mainP, int rid, RowProcessor mainPt, int[] pids, Collection<RowProcessor> pats)
 {
  super(mainP, rid, pids, pats);
  mainPattern=mainPt;
 }

 public RowProcessor getMainPattern()
 {
  return mainPattern;
 }

 @Override
 public boolean matchRecord(Record row)
 {
  if( mainPattern.matchRecord(row) )
   return true;
  
  return super.matchRecord(row);
 }
}
