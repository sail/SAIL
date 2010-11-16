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

import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.Range;

import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.FitLayout;

public class RealRangeDialog extends Window
{
 private static RealRangeDialog instance;
 
 private RealRangePanel nrPanel;
 
 public RealRangeDialog()
 {
  setSize(350, 140);
  setCloseAction(Window.HIDE);
  setModal(true);
  setTitle("Edit Range");
  setClosable(true);
  setPlain(true);
  setLayout(new FitLayout());
  
  add( nrPanel = new RealRangePanel() );
 }
 
 public void setListener(ObjectAction<Range> listener)
 {
  nrPanel.setListener(listener);
 }

 public void setRange(Range v)
 {
  nrPanel.setRange(v);
 }

 public static RealRangeDialog getInstance()
 {
  if( instance == null )
   instance = new RealRangeDialog();
  
  return instance;
 }
}
