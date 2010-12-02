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
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.ui.Action;
import uk.ac.ebi.sail.client.ui.ActionFlags;

import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.FitLayout;

public class ClassifierSelectDialog extends Window
{
 private static ClassifierSelectDialog instance;

 private ClassifierSelectPanel selPanel;
 
 public ClassifierSelectDialog(ClassifiableManager<Classifier> mngr)
 {
  setSize(700, 500);
  setCloseAction(Window.HIDE);
  setModal(true);
  setTitle("Select classifier");
  setClosable(true);
  setPlain(true);
  setLayout(new FitLayout());

  add( selPanel = new ClassifierSelectPanel(true, new Action[]{ 
    new Action("Select","select"+ActionFlags.separator,null,null),
    new Action("Cancel","cancel"+ActionFlags.separator+ActionFlags.EMPTY,null,null)
    }, mngr) );
 }

 public static ClassifierSelectDialog getDialog(ClassifiableManager<Classifier> mngr)
 {
  if(instance == null)
   instance = new ClassifierSelectDialog(mngr);

  return instance;
 }
 
 public void setObjectActionListener( ObjectAction<Classifier> l )
 {
  selPanel.setObjectActionListener(l);
 }

 public void dispose()
 {
  super.close();
  instance=null;
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
