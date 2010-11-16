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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TreeModelNode
{
 public static final String         undefinedNodeName = "[Undefined]";

 private LinkedHashMap<String, TreeModelNode> children;
 private String                     iconClass;
 // private List<Classifiable> leaves;
 private String                     name;
 private Object                     userObject;

 public TreeModelNode(String nm)
 {
  name = nm;
  iconClass = null;
 }

 public TreeModelNode(String nm, String icCls)
 {
  name = nm;
  iconClass = icCls;
 }

 public String getIconClass()
 {
  return iconClass;
 }

 public void addSubNode( TreeModelNode nn )
 {
  if(children == null)
   children = new LinkedHashMap<String, TreeModelNode>();
  
  children.put(nn.getName(), nn);
 }
 
 public TreeModelNode getSubNode(String nodeName)
 {
  TreeModelNode tmn = null;

  if(children == null)
   children = new LinkedHashMap<String, TreeModelNode>();
  else
   tmn = children.get(nodeName);

  if(tmn == null)
  {
   tmn = new TreeModelNode(nodeName);
   children.put(nodeName, tmn);
  }

  return tmn;
 }

 public List<TreeModelNode> subNodes()
 {
  if(children == null)
   return null;

  TreeModelNode undef = null;

  List<TreeModelNode> res = new ArrayList<TreeModelNode>(children.size());

  for(Map.Entry<String, TreeModelNode> me : children.entrySet())
  {
   if(undefinedNodeName == me.getKey())
    undef = me.getValue();
   else
    res.add(me.getValue());
  }

  if(undef != null)
   res.add(undef);

  return res;
 }

 // public void addLeaf(Classifiable p)
 // {
 // if( leaves == null )
 // leaves = new ArrayList<Classifiable>(20);
 //  
 // leaves.add(p);
 // }
 // 
 // public List<Classifiable> getLeaves()
 // {
 // if( leaves == null )
 // return null;
 //  
 // Collections.sort(leaves, new Comparator<Classifiable>(){
 //
 // public int compare(Classifiable o1, Classifiable o2)
 // {
 // return o1.getName().compareTo(o2.getName());
 // }});
 //  
 // return leaves;
 // }

 public String getName()
 {
  return name;
 }

 public void setName(String name2)
 {
  name = name2;
 }

 public void setIconClass(String iconClass2)
 {
  iconClass = iconClass2;
 }

 public void setSubNodes(List<TreeModelNode> subNodes)
 {
  if(children == null)
   children = new LinkedHashMap<String, TreeModelNode>();
  else
   children.clear();

  if(subNodes != null)
  {
   for(TreeModelNode tn : subNodes)
    children.put(tn.getName(), tn);
  }
 }

 public Object getUserObject()
 {
  return userObject;
 }

 public void setUserObject(Object userObject)
 {
  this.userObject = userObject;
 }

}
