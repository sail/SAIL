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

package uk.ac.ebi.sail.client.ui;

public interface ActionFlags
{
 String defaultFlags="";
 char separator=':';
 
 char EMPTY ='E';
 char ALLOW_EMPTY ='e';
// char REQUIRE_SINGLE='s';
 char ALLOW_MULTIPLE='m';
 char REQUIRE_MULTIPLE='M';
 
// char REQUIRE_SELECTION='s';
// char REQUIRE_MULTY_SELECTION='S';

}
