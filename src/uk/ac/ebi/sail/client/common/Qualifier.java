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
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Qualifier extends ParameterPart implements Serializable, IsSerializable
{

 public Qualifier()
 {
//  id=Parameter.getNewId();
 }
 
 public Qualifier( Qualifier q )
 {
  setName(q.getName());
  setDescription(q.getDescription());
  setPredefined(q.isPredefined());
  setMandatory(q.isMandatory());
  setId(q.getId());
  
  if( q.getVariants() != null )
  {
   List<Variant> variants = new ArrayList<Variant>( q.getVariants().size() );
   
   for( Variant vr : q.getVariants() )
    variants.add(new Variant(vr));
   
   setVariants(variants);
  }
 }


 public boolean isEnum()
 {
  return true;
 }

 public void setMandatory(boolean mandatory)
 {
  this.mandatory = mandatory;
 }

 public void update(Qualifier q)
 {
  setName(q.getName());
  setDescription(q.getDescription());
  setPredefined(q.isPredefined());
  setMandatory(q.isMandatory());
  setVariants( q.getVariants() );
 }
}
