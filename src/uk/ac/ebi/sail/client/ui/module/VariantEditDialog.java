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

import java.util.Collection;

import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.Variant;

import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.FitLayout;

public class VariantEditDialog extends Window
{
 private static VariantEditDialog instance;
 
 private VariantEditPanel vPanel;
 
 public VariantEditDialog()
 {
  setSize(350, 140);
  setCloseAction(Window.HIDE);
  setModal(true);
  setTitle("Edit Variant");
  setClosable(true);
  setPlain(true);
  setLayout(new FitLayout());
  
  add( vPanel = new VariantEditPanel() );
 }
 
 public void setListener(ObjectAction<Variant> listener)
 {
  vPanel.setListener(listener);
 }

 public void setVariant(Variant v, Collection<Variant> othv)
 {
  vPanel.setVariant(v, othv);
 }

 public static VariantEditDialog getInstance()
 {
  if( instance == null )
   instance = new VariantEditDialog();
  
  return instance;
 }
}
