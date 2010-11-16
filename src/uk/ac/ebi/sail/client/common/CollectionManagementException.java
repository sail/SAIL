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

import com.google.gwt.user.client.rpc.IsSerializable;

public class CollectionManagementException extends DataManagementException implements IsSerializable
{
 private static int ERROR_BEGIN = DataManagementException.ERROR_END;
 
 public static final int INV_TAG_ID = ERROR_BEGIN +1;
 public static final int INV_ID = ERROR_BEGIN +2;

 
 protected static final int ERROR_END=ERROR_BEGIN+5;



 public CollectionManagementException()
 {
  super(null,0);
 }

 
 public CollectionManagementException(String msg, int ec)
 {
  super(msg, ec);
 }

 public CollectionManagementException(String msg, Throwable t, int ec)
 {
  super(msg, t, ec);
 }

}
