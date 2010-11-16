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

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import uk.ac.ebi.sail.client.CollectionManager;
import uk.ac.ebi.sail.client.DataChangeListener;
import uk.ac.ebi.sail.client.common.SampleCollection;

import com.gwtext.client.widgets.PaddedPanel;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.AnchorLayout;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

public class CollectionViewPanel extends Panel implements DataChangeListener
{
 private Panel contentPanel;
 private CollectionManager manager;
 
 public CollectionViewPanel()
 {
 }
 
 public CollectionViewPanel(CollectionManager mng)
 {
  manager = mng;
  
  setLayout( new FitLayout() );
  setTitle("Collections");
  setAutoScroll(true);
//  setPaddings(2);
  
  contentPanel = new Panel();
  contentPanel.setFrame(false);
  contentPanel.setLayout(new AnchorLayout());
  contentPanel.setAutoScroll(true);
//  contentPanel.setPaddings(2);
  
  add( contentPanel );
  
//  Button bt = new Button("Add");
//  bt.setStateId("add");
//  addButton( bt );
  
  manager.addDataChangeListener(this);
  
  SampleCollection[] rps = new SampleCollection[manager.getCollections().size()];
 
  int i=0;
  for( SampleCollection rp : manager.getCollections() )
   rps[i++]=rp;

  Arrays.sort(rps, new Comparator<SampleCollection>(){

   public int compare(SampleCollection o1, SampleCollection o2)
   {
    long dif = o2.getUpdateTime()-o1.getUpdateTime();
    
    if( dif == 0 )
     return 0;
    
    return dif>0?1:-1;
   }});
  
  for( SampleCollection rp : rps )
  {
   CollectionView rv = new CollectionView( rp );
   contentPanel.add( new PaddedPanel(rv,3),  new AnchorLayoutData("95%"));
  }
  //dataChanged();
 }

 public void setCollections( Collection<SampleCollection> reps )
 {
  contentPanel.removeAll();
  
  AnchorLayoutData ald = new AnchorLayoutData("100%");
  
  for( SampleCollection rp : reps )
  {
   CollectionView rv = new CollectionView( rp );
   contentPanel.add( new PaddedPanel(rv,3),  new AnchorLayoutData("95%"));
  }
  
  contentPanel.doLayout();
  doLayout();
 }

 public void dataChanged()
 {
  setCollections( manager.getCollections() );
 }
 
 public void destroy()
 {
  manager.removeDataChangeListener( this );

  super.destroy();
 }
}
