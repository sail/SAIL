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

import java.util.Map;
import java.util.TreeMap;

import uk.ac.ebi.sail.client.common.Parameter;

import com.gwtext.client.widgets.tree.TreeNode;

public class TreeModelNodeO
{
 private Map<String,TreeModelNodeO> children;
 private TreeNode node;

 public TreeModelNodeO(String name)
 {
  node = new TreeNode(name);
 }
 
 
 public TreeModelNodeO getSubNode(String nodeName)
 {
  TreeModelNodeO tmn=null;
  
  if( children == null )
   children = new TreeMap<String, TreeModelNodeO>();
  else
   tmn = children.get(nodeName);
  
  if( tmn == null )
  {
   tmn = new TreeModelNodeO(nodeName);
   children.put(nodeName, tmn);
   node.appendChild(tmn.getTreeNode());
  }
  
  return tmn;
 }
 
 
 public TreeNode getTreeNode()
 {
  return node;
 }
 
 public void addLeaf(Parameter p)
 {
  TreeNode nd=new TreeNode(p.getName());
  nd.setAttribute("parameter", p);
  node.appendChild(nd);
 }
}
