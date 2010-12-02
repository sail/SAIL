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

package uk.ac.ebi.sail.client;

  /* 
   * GWT-Ext Widget Library 
   * Copyright 2007 - 2008, GWT-Ext LLC., and individual contributors as indicated 
   * by the @authors tag. See the copyright.txt in the distribution for a 
   * full listing of individual contributors. 
   * 
   * This is free software; you can redistribute it and/or modify it 
   * under the terms of the GNU Lesser General Public License as 
   * published by the Free Software Foundation; either version 3 of 
   * the License, or (at your option) any later version. 
   * 
   * This software is distributed in the hope that it will be useful, 
   * but WITHOUT ANY WARRANTY; without even the implied warranty of 
   * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
   * Lesser General Public License for more details. 
   * 
   * You should have received a copy of the GNU Lesser General Public 
   * License along with this software; if not, write to the Free 
   * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 
   * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
   */  
     
    

    
  import com.google.gwt.core.client.EntryPoint;  
  import com.google.gwt.user.client.ui.RootPanel;  
  import com.gwtext.client.widgets.Panel;  
  import com.gwtext.client.widgets.layout.HorizontalLayout;  
  import com.gwtext.client.widgets.layout.VerticalLayout;  
  import com.gwtext.client.widgets.tree.TreeNode;  
  import com.gwtext.client.widgets.tree.TreePanel;  
    
  public class SAIL_Tree_Test implements EntryPoint {  
    
      public void onModuleLoad() {  
          Panel panel = new Panel();  
          panel.setBorder(false);  
          panel.setPaddings(15);  
    
          final TreePanel treePanel = new SampleTree();  
          treePanel.setTitle("Parameters");  
          treePanel.setWidth(190);  
          treePanel.setHeight(400);  
    
          final TreePanel treePanelNoLines = new SampleTree();  
          treePanelNoLines.setTitle("No Lines");  
          treePanelNoLines.setWidth(190);  
          treePanelNoLines.setHeight(400);  
          treePanelNoLines.setLines(false);  
    
          final TreePanel treePanelVistaArrows = new SampleTree();  
          treePanelVistaArrows.setTitle("Vista Arrows");  
          treePanelVistaArrows.setWidth(190);  
          treePanelVistaArrows.setHeight(400);  
          treePanelVistaArrows.setUseArrows(true);  
    
          Panel horizontalPanel = new Panel();  
          horizontalPanel.setLayout(new HorizontalLayout(20));  
          horizontalPanel.add(treePanel);  
          horizontalPanel.add(treePanelNoLines);  
          horizontalPanel.add(treePanelVistaArrows);  
    
          Panel verticalPanel = new Panel();  
          verticalPanel.setLayout(new VerticalLayout(15));  
    
          verticalPanel.add(horizontalPanel);  
    
          panel.add(verticalPanel);  
    
          RootPanel.get().add(panel);  
      }  
    
      class SampleTree extends TreePanel {  
    
          public SampleTree() {  
    
              TreeNode root = new TreeNode("Company Heirarchy");  
    
    
              TreeNode p3g = new TreeNode("P3G");  
              p3g.setExpanded(true);  
              TreeNode engage = new TreeNode("ENGAGE");  
              engage.setExpanded(true);  

              TreeNode p3g1 = new TreeNode("P3G");  
              p3g1.setExpanded(true);  
              TreeNode engage1 = new TreeNode("ENGAGE");  
              engage1.setExpanded(true);  
              TreeNode engage2 = new TreeNode("ENGAGE");  
              engage2.setExpanded(true);  

              TreeNode liver = new TreeNode("System biology");  
              liver.setExpanded(true);  
              TreeNode blood = new TreeNode("Clinical trials");  
              blood.setExpanded(true);  
              TreeNode undef = new TreeNode("Undefined");  
              undef.setExpanded(true);  

              TreeNode liver1 = new TreeNode("System biology");  
              liver1.setExpanded(true);  
              TreeNode blood1 = new TreeNode("Clinical trials");  
              blood1.setExpanded(true);  
              TreeNode undef1 = new TreeNode("Undefined");  
              undef1.setExpanded(true);  

              TreeNode p1 = new TreeNode("P1");  
              TreeNode p11 = new TreeNode("P1");  
              TreeNode p2 = new TreeNode("P2");  
              TreeNode p3 = new TreeNode("P3");  
              TreeNode p4 = new TreeNode("P4");  
              TreeNode p5 = new TreeNode("P5");  

//              p3g.appendChild(p1);
//              engage.appendChild(p11);
//              engage.appendChild(p2);
//
//              p3g1.appendChild(p4);
//              engage1.appendChild(p3);
//
//              engage2.appendChild(p5);

              liver.appendChild(p1);
              liver1.appendChild(p11);
              
              liver1.appendChild(p2);
              blood1.appendChild(p3);
              blood.appendChild(p4);
              undef1.appendChild(p5);
              
              p3g.appendChild(liver);
              p3g.appendChild(blood);
//              p3g.appendChild(undef);

              engage.appendChild(liver1);
              engage.appendChild(blood1);
              engage.appendChild(undef1);
              
//              liver.appendChild(p3g);
//              liver.appendChild(engage);
//
//              blood.appendChild(p3g1);
//              blood.appendChild(engage1);
//
//              undef.appendChild(engage2);
              
//              root.appendChild(liver);
//              root.appendChild(blood);
//              root.appendChild(undef);

            root.appendChild(p3g);
            root.appendChild(engage);
              
              /*
              ceo.appendChild(manager1);  
              ceo.appendChild(manager2);  
              ceo.appendChild(manager3);  
    
              TreeNode director1 = new TreeNode("Robert L. Carbaugh");  
              director1.setExpanded(true);  
    
              TreeNode director2 = new TreeNode("Agnes H. Keene");  
              director2.setExpanded(true);  
    
              manager1.appendChild(director1);  
              manager1.appendChild(director2);  
    
              TreeNode director3 = new TreeNode("Erin T. Marks");  
              manager2.appendChild(director3);  
    
              manager3.appendChild(new TreeNode("Harry L. Krieger"));  
    
              director1.appendChild(new TreeNode("Jim H. Baker"));  
              director1.appendChild(new TreeNode("Randy M. Smith"));  
              director1.appendChild(new TreeNode("Annie P. Burke"));  
              director2.appendChild(new TreeNode("Shirley P. Tanaka"));  
              director2.appendChild(new TreeNode("Anthony C. Decarlo"));  
              director2.appendChild(new TreeNode("Katherine D. Saenz"));  
              director3.appendChild(new TreeNode("Carolyn M. Gauna"));  
              director3.appendChild(new TreeNode("Johanna E. Armistead"));  
              director3.appendChild(new TreeNode("Duane E. Ashe"));  
              director3.appendChild(new TreeNode("Norman N. Gardner"));  
              root.appendChild(ceo);  
    */
              setRootVisible(false);  
    
              setTitle("Company");  
              setWidth(200);  
              setHeight(400);  
              setRootNode(root);  
              root.setExpanded(true);  
          }  
      }  
  } 

