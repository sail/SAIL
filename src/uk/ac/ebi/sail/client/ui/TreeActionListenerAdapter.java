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

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.ui.widget.ErrorBox;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.event.BaseItemListener;
import com.gwtext.client.widgets.tree.DefaultSelectionModel;
import com.gwtext.client.widgets.tree.MultiSelectionModel;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.TreeSelectionModel;

public class TreeActionListenerAdapter<T> extends ButtonListenerAdapter implements BaseItemListener
{
 private ObjectAction<T> lsnr;
 private TreePanel       tree;

 public TreeActionListenerAdapter(TreePanel g)
 {
  tree = g;
 }

 public void setListener(ObjectAction<T> l)
 {
  lsnr = l;
 }

 public void onActivate(BaseItem item)
 {}

 public void onClick(BaseItem item, EventObject e)
 {
  onAction(item.getStateId());  
 }

 public void onDeactivate(BaseItem item)
 {}

 public void onClick(Button button, EventObject e)
 {
  onAction(button.getStateId());
 }
 
 public void onAction(String acState )
 {
//  System.out.println("TreeButtonListenerAdapter click");

  String flags = ActionFlags.defaultFlags;

  int pos = acState.indexOf(ActionFlags.separator);

  if(pos != -1)
  {
   flags = acState.substring(pos + 1);
   acState = acState.substring(0, pos);
  }

  if(lsnr != null)
  {
   if(flags.indexOf(ActionFlags.EMPTY) != -1)
   {
    lsnr.doAction(acState, null);
    return;
   }

   TreeSelectionModel tsm = tree.getSelectionModel();

   if(tsm instanceof DefaultSelectionModel)
   {
    TreeNode chck = ((DefaultSelectionModel) tsm).getSelectedNode();

    if(chck == null)
    {
     if(flags.indexOf(ActionFlags.ALLOW_EMPTY) != -1)
      lsnr.doAction(acState, null);

     return;
    }
    else if(flags.indexOf(ActionFlags.REQUIRE_MULTIPLE) != -1)
    {
     ErrorBox.showError("This button reqire multiple choise");
     return;
    }
    else
    {
     lsnr.doAction(acState, (T) chck.getUserObject());
     return;
    }
   }
   else if(tsm instanceof MultiSelectionModel)
   {
    TreeNode[] chcks = ((MultiSelectionModel) tsm).getSelectedNodes();

    if(chcks == null || chcks.length == 0)
    {
     if(flags.indexOf(ActionFlags.ALLOW_EMPTY) != -1)
     {
      if(/*flags.indexOf(ButtonFlags.ALLOW_MULTIPLE) == -1 && */flags.indexOf(ActionFlags.REQUIRE_MULTIPLE) == -1)
       lsnr.doAction(acState, null);
      else
       lsnr.doMultyAction(acState, null);
     }
     return;
    }
    else if(chcks.length == 1)
    {
     if(flags.indexOf(ActionFlags.REQUIRE_MULTIPLE) != -1)
      return;

     lsnr.doAction(acState, (T) chcks[0].getUserObject());
/*
     if(flags.indexOf(ButtonFlags.ALLOW_MULTIPLE) == -1)
      lsnr.doAction(btState, (T) chcks[0].getAttributeAsObject("obj"));
     else
     {
      List<T> lst = new ArrayList<T>(chcks.length);
      lst.add((T) chcks[0].getAttributeAsObject("obj"));

      lsnr.doMultyAction(btState, lst);
     }
*/
    }
    else
    {
     if(flags.indexOf(ActionFlags.ALLOW_MULTIPLE) == -1 && flags.indexOf(ActionFlags.REQUIRE_MULTIPLE) == -1)
      return;
     else
     {
      List<T> lst = new ArrayList<T>(chcks.length);
      for(TreeNode tn : chcks)
       lst.add((T) tn.getUserObject() );

      lsnr.doMultyAction(acState, lst);
     }
    }
   }
  }
 }

 public void setTree(TreePanel tree2)
 {
  tree=tree2;
 }
}
