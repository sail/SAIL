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
import uk.ac.ebi.sail.client.common.ClassifiableManager;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.ActionFlags;

import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.FitLayout;

public class ParameterSelectDialog extends Window
{
 private static ParameterSelectDialog instance;

 private ParameterSelectPanel selPanel;
 
 public ParameterSelectDialog(ClassifiableManager<Parameter> mngr)
 {
  setSize(700, 500);
  setCloseAction(Window.HIDE);
  setModal(true);
  setTitle("Select Parameter");
  setClosable(true);
  setPlain(true);
  setLayout(new FitLayout());

  add( selPanel = new ParameterSelectPanel(true, new Action[]{ 
    new Action("Select","select"+ActionFlags.separator,null,null),
    new Action("Cancel","cancel"+ActionFlags.separator+ActionFlags.EMPTY,null,null)}, mngr) );

 }

 public static ParameterSelectDialog getDialog(ClassifiableManager<Parameter> mngr)
 {
  if(instance == null)
   instance = new ParameterSelectDialog(mngr);

  return instance;
 }
 
 public void setObjectActionListener( ObjectAction<Parameter> l )
 {
  selPanel.setObjectActionListener(l);
 }
 
 public void dispose()
 {
  super.hide();
 }
 
 @Deprecated
 public void close()
 {
  super.close();
 }

 @Deprecated
 public void hide()
 {
  super.hide();
 }
}
