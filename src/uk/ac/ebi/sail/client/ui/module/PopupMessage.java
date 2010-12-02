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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class PopupMessage
{
 private static PopupPanel messagePanel = new PopupPanel(true);
 
 public static void showMessage(String title, String message, int[] xy) {
  messagePanel.setPopupPosition(xy[0], xy[1]);
  messagePanel.setWidget(new HTML(getMessageHtml(title, message)));
  messagePanel.setWidth("250px");
  messagePanel.show();
}

private static native String getMessageHtml(String title, String message) /*-{
                                                    return ['<div class="msg">',
                                                            '<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
                                                            '<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><h3>', title, '</h3>', message, '</div></div></div>',
                                                            '<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
                                                            '</div>'].join('');
                                                }-*/;

}
