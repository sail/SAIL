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

package uk.ac.ebi.sail.client.ui.widget;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.ebi.sail.client.data.Attributed;
import uk.ac.ebi.sail.client.data.Filterable;

import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.menu.CheckItem;
import com.gwtext.client.widgets.menu.Menu;

public class TextSearchComponent<T extends Attributed>
{

 
 public TextSearchComponent(Toolbar tb, Filterable<T> flt, String[][] searchItems)
 {
  Collection<CheckItem> items = new ArrayList<CheckItem>(10);

  Menu colMenu = new Menu();
  for(String[] itm : searchItems )
  {
   CheckItem ci = new CheckItem();
   ci.setChecked(true);
   ci.setText(itm[0]);
   ci.setStateId(itm[1]);
   colMenu.addItem(ci);
   items.add(ci);
  }

  ToolbarButton menuButton = new ToolbarButton("Columns");
  menuButton.setMenu(colMenu);

  tb.addButton(menuButton);

  SearchField<T> sf = new SearchField<T>(flt, items);

  tb.addField(sf);
 }

}
