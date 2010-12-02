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
import uk.ac.ebi.sail.client.common.IntRange;

import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.FitLayout;

public class IntRangeDialog extends Window
{
 private static IntRangeDialog instance;
 
 private IntRangePanel nrPanel;
 
 public IntRangeDialog()
 {
  setSize(350, 140);
  setCloseAction(Window.HIDE);
  setModal(true);
  setTitle("Edit Range");
  setClosable(true);
  setPlain(true);
  setLayout(new FitLayout());
  
  add( nrPanel = new IntRangePanel() );
 }
 
 public void setListener(ObjectAction<IntRange> listener)
 {
  nrPanel.setListener(listener);
 }

 public void setRange(IntRange v)
 {
  nrPanel.setRange(v);
 }

 public static IntRangeDialog getInstance()
 {
  if( instance == null )
   instance = new IntRangeDialog();
  
  return instance;
 }
}
