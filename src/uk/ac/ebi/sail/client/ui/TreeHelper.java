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

import java.util.Collection;
import java.util.List;

import uk.ac.ebi.sail.client.common.Classifiable;
import uk.ac.ebi.sail.client.common.Classifier;
import uk.ac.ebi.sail.client.common.Projection;
import uk.ac.ebi.sail.client.common.Tag;

import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.tree.TreeNode;

public class TreeHelper
{
 public static final String undefinedNodeName="[Undefined]";

 
 public static void printTree(TreeNode nd, int level)
 {
  Node[] child = nd.getChildNodes();
  
  
  for(Node n : child)
  {
   TreeNode tn = (TreeNode)n;

   for(int i=0; i < level*5; i++)
    System.out.print(' ');
   System.out.println(tn.getText()+" UO: "+tn.getUserObject());
   
   printTree(tn, level+1);
  }
 }
 
 public static TreeNode findNodeByUserObject( TreeNode nd, Object obj )
 {
  
  Node[] child = nd.getChildNodes();
  
  
  for(Node n : child)
  {
   TreeNode tn = (TreeNode)n;

   if( obj == tn.getUserObject() )
    return tn;
   
   if( ! tn.isLeaf() )
   {
    TreeNode found = findNodeByUserObject( tn, obj );
    
    if( found != null )
     return found;
   }
  }
  
  return null;
 }
 
 
 public static void createTree(TreeModelNode mn, TreeNode tn)
 {
  List<TreeModelNode> sub = mn.subNodes();

  if(sub != null)
  {
   if( sub.size() == 1 && sub.get(0).getName() == undefinedNodeName )
   {
    createTree(sub.get(0), tn);
   }
   else
   {
    for(TreeModelNode n : sub)
    {
     TreeNode ntn = new TreeNode(n.getName());
     ntn.setExpandable(n.subNodes() != null );
     
     if( n.getIconClass() != null)
      ntn.setIconCls(n.getIconClass());
  
     ntn.setUserObject(n.getUserObject());

     tn.appendChild(ntn);
     createTree(n, ntn);
    }
   }
  }
 
//  List<Classifiable> lvs = mn.getLeaves();
//
//  if(lvs == null)
//   return;
//
//  for(Classifiable p : lvs)
//  {
//   TreeNode ntn = new TreeNode(p.toString());
//   ntn.setId(String.valueOf(p.getId()));
//   ntn.setUserObject(p);
//   tn.appendChild(ntn);
//  }

 }
 
 public static void classify(TreeModelNode mn, Classifiable p, Projection clsfs, int level )
 {
  if( level == clsfs.getClassifiers().size() )
  {
   mn.addSubNode(p.getStructure());
   
//   TreeModelNode nn = new TreeModelNode(p.getName(),p.getStructure().getIconClass());
//   nn.setSubNodes(p.getStructure().subNodes());
//   mn.setUserObject(p);
//   
//   mn.setName( p.getName() );
//   mn.setIconClass( p.getStructure().getIconClass() );
//   mn.setSubNodes( p.getStructure().subNodes() );
////   mn.addLeaf(p);
   return;
  }
  
  Collection<Tag> tags = p.getClassificationTags();
  
  if( tags == null )
  {
   classify(mn.getSubNode(undefinedNodeName), p, clsfs, level+1);
   return;
  }
  
  Classifier cl = clsfs.getClassifiers().get(level);
  boolean hasTag=false;
  for( Tag t : tags )
  {
   if( t.getClassifier() == cl )
   {
    classify(mn.getSubNode(t.getName()), p, clsfs, level+1);
    hasTag=true;
   }
  }
  
  if( !hasTag )
   classify(mn.getSubNode(undefinedNodeName), p, clsfs, level+1);
   
 }
 
}
