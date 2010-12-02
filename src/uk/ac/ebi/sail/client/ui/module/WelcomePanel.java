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

package uk.ac.ebi.sail.client.ui.module;

import uk.ac.ebi.sail.client.ConfigManager;

import com.gwtext.client.widgets.Panel;

public class WelcomePanel extends Panel
{
 public static final String summaryLink="welcome/goSummary";
 public static final String constructorLink="welcome/goConstructor";

 public WelcomePanel()
 {
  setTitle("Welcome");
  
  setHtml(
  "<div style='width: 100%; height: 100%; background-image: url(\"images/welcome/fill2.jpg\")'>\r\n" + 
  "<table style='width: 100%; height: 100%; background-image: url(\"images/welcome/sail-bg2.jpg\"); background-color1: #5f81ae; background-repeat: no-repeat'>\r\n" + 
  "<tr style=\"height: 180px\">\r\n" + 
  "<td style=\"width: 70%; vertical-align: top; text-align: left\">" +
  "<IMG style='margin: 30px' SRC=\"images/welcome/sail-logo3.png\" WIDTH=\"444\" HEIGHT=\"145\" BORDER=\"0\"></td>\r\n" + 
  "<td style=\"width: 30%; vertical-align: top; text-align: right; background-repeat: repeat; padding: 30px\"><div style='width: 100%; height: 250px; background-image: url(\"images/welcome/sail-circle2.png\")'></div></td>\r\n" + 
  "</tr>\r\n" + 
  "<tr style=\"height: auto\">\r\n" + 
  "<td style=\"width: 70%; vertical-align: bottom; text-align: left\"><img style=\"margin: 50px\" src=\"images/welcome/ebi-logo.png\"></td>\r\n" + 
  "<td style=\"width: 30%; vertical-align: middle; text-align: left\">\r\n" + 
  "<a style=\"border: 0\" href=\"javascript:linkClicked('welcome','"+summaryLink+"')\"><img style=\"margin: 5px; border: 0\" src=\"images/welcome/summary.png\"></a>\r\n" + 
  "<a href=\"javascript:linkClicked('welcome','"+constructorLink+"')\"><img style=\"margin: 5px; border: 0\" src=\"images/welcome/report.png\"></a>\r\n" + 
  "<a href=\""+ConfigManager.getTutorialURL()+"\"><img style=\"margin: 5px; border: 0\" src=\"images/welcome/tutorial.png\"></a>\r\n" + 
  "<a href=\"http://simbioms.cvs.sourceforge.net/viewvc/simbioms/SAIL/\"><img style=\"margin: 5px; border: 0\" src=\"images/welcome/source.png\"></a>\r\n" + 
  "</td>\r\n" + 
  "</tr>\r\n" + 
  "</table>\r\n" + 
  "</div>"  
  );
 }
}
