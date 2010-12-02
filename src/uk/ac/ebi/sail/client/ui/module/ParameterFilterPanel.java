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

import java.util.List;

import uk.ac.ebi.sail.client.ObjectAction;
import uk.ac.ebi.sail.client.common.ClientParameterAuxInfo;
import uk.ac.ebi.sail.client.common.ComplexFilter;
import uk.ac.ebi.sail.client.common.Date2Str;
import uk.ac.ebi.sail.client.common.IntRange;
import uk.ac.ebi.sail.client.common.Parameter;
import uk.ac.ebi.sail.client.common.ParameterPart;
import uk.ac.ebi.sail.client.common.Qualifier;
import uk.ac.ebi.sail.client.common.Range;
import uk.ac.ebi.sail.client.common.Summary;
import uk.ac.ebi.sail.client.common.Variable;
import uk.ac.ebi.sail.client.common.Variant;
import uk.ac.ebi.sail.client.common.Variable.Type;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.Node;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreeNodeListenerAdapter;

public class ParameterFilterPanel extends TreePanel
{
 private ComplexFilter filter;
 
 public ParameterFilterPanel( ComplexFilter flt )
 {
  Parameter p = flt.getParameter();
  
  filter = flt;
  
  final TreeNode rn = new TreeNode(p.getCode() + " (" + p.getName() + ")");
  rn.setIconCls("parameterIcon");
  setRootNode(rn);

  if(p.getAllVariables() != null)
  {
   for(Variable v : p.getAllVariables())
   {
    if(v.isEnum())
    {
     TreeNode tn = new TreeNode(v.getName()+" (ENUM)");
     tn.setIconCls("variableIcon");
     tn.setUserObject(v);
     rn.appendChild(tn);

     Summary cntx = ((ClientParameterAuxInfo)p.getAuxInfo()).getCountContext();
     
     for(Variant vr : v.getVariants())
     {
      int count = vr.getCount();
      
      if( cntx != null )
      {}
      
      TreeNode vn = new TreeNode(vr.getName() + " (" + count + ")");
      vn.setIconCls("bulletIcon");
      vn.setChecked( hasVariant(v.getId(), vr.getId()) );
      vn.setUserObject(vr);
      tn.appendChild(vn);
     }
    }
    else if(v.getType() == Type.REAL)
    {
     TreeNode tn = new TreeNode(v.getName()+" ("+v.getType().name()+")");
     tn.setIconCls("variableIcon");
     tn.setUserObject(v);
     rn.appendChild(tn);

     Range rg = getRealRange(v.getId());
     
     boolean chk = false;
     if( rg == null )
      rg = new Range(v.getId(), Float.NaN, Float.NaN);
     else
      chk=true;
     
   
     TreeNode vn = new TreeNode(rg.toString());
     vn.setIconCls("rangeIcon");
     vn.setChecked(chk);
     vn.addListener(RealNodeListener.getInstance());
     vn.setUserObject(rg);
     tn.appendChild(vn);
    }
    else if(v.getType() == Type.INTEGER)
    {
     TreeNode tn = new TreeNode(v.getName()+" ("+v.getType().name()+")");
     tn.setIconCls("variableIcon");
     tn.setUserObject(v);
     rn.appendChild(tn);

     IntRange rg = getIntRange(v.getId());
     
     boolean chk = false;
     if( rg == null )
      rg = new IntRange(v.getId(), Integer.MIN_VALUE, Integer.MAX_VALUE);
     else
      chk=true;
     
     TreeNode vn = new TreeNode(rg.toString());
     vn.setIconCls("rangeIcon");
     vn.setChecked(chk);
     vn.addListener(IntNodeListener.getInstance());
     vn.setUserObject(rg);
     tn.appendChild(vn);
    }
    else if(v.getType() == Type.DATE)
    {
     TreeNode tn = new TreeNode(v.getName()+" ("+v.getType().name()+")");
     tn.setIconCls("variableIcon");
     tn.setUserObject(v);
     rn.appendChild(tn);

     IntRange rg = getIntRange(v.getId());
     
     boolean chk = false;
     if( rg == null )
      rg = new IntRange(v.getId(), Integer.MIN_VALUE, Integer.MAX_VALUE);
     else
      chk=true;
     
     TreeNode vn = new TreeNode("From "+Date2Str.int2DateStr(rg.getLimitLow())+" till "+Date2Str.int2DateStr(rg.getLimitHigh()));
     vn.setIconCls("datesIcon");
     vn.setChecked(chk);
     vn.addListener(DateNodeListener.getInstance());
     vn.setUserObject(rg);
     tn.appendChild(vn);
    }
    else if(v.getType() == Type.BOOLEAN )
    {
     TreeNode tn = new TreeNode(v.getName()+" ("+v.getType().name()+")");
     tn.setIconCls("variableIcon");
     tn.setUserObject(v);
     rn.appendChild(tn);

     IntRange rg = getIntRange(v.getId());
     
     int state = 2;
     
     if( rg == null )
      rg = new IntRange(v.getId(), 2, 2);
     else
      state = rg.getLimitHigh();

     TreeNode vn = new TreeNode(state==0?"False":(state==1?"True":"Doesn't care"));
     vn.setIconCls(state==0?"falseIcon":(state==1?"trueIcon":"dontcareIcon"));
     
//     vn.setId( String.valueOf(state) );
     vn.addListener(BooleanNodeListener.getInstance());
     vn.setUserObject(rg);
     tn.appendChild(vn);
    }
    else
    {
     TreeNode tn = new TreeNode(v.getName()+" ("+v.getType().name()+")");
     tn.setIconCls("variableIcon");
     tn.setUserObject(v);
     rn.appendChild(tn);
    }
   }
   if(p.getAllQualifiers() != null)
   {
    for(Qualifier v : p.getAllQualifiers())
    {
     if(v.isEnum())
     {
      TreeNode tn = new TreeNode(v.getName());
      tn.setIconCls("qualifierIcon");
      tn.setUserObject(v);
      rn.appendChild(tn);

      Summary cntx = ((ClientParameterAuxInfo)p.getAuxInfo()).getCountContext();
      
      for(Variant vr : v.getVariants())
      {
       String cntStr="";
       
      
       if( cntx != null && cntx.getRelatedCounters() != null )
       {
        for( Summary vrs : cntx.getRelatedCounters() )
        {
         if( vrs.getId() == vr.getId() || (vr.getId() == 0 && vrs.getId() == -v.getId() ) )
         {
          cntStr = " (" + vrs.getCount() + ")";
          break;
         }
        }
       }
       else
        cntStr = " (" + vr.getCount() + ")";

       TreeNode vn = new TreeNode(vr.getName() + cntStr);
       vn.setIconCls("bulletIcon");
       vn.setChecked( hasVariant(v.getId(), vr.getId()) );
       vn.setUserObject(vr);
       tn.appendChild(vn);
      }
     }
    }
   }

   expandAll();

  }
 }

 public ComplexFilter getFilter()
 {
  filter.clear();
  
  for( Node n : getRootNode().getChildNodes() )
  {
   ParameterPart pp = (ParameterPart)((TreeNode)n).getUserObject();
   
   for( Node sn : n.getChildNodes() )
   {
    if( pp instanceof Variable && ((Variable)pp).getType() == Type.BOOLEAN )
    {
     IntRange rg = (IntRange)sn.getUserObject();
     
     if( rg.getLimitHigh() != 2 )
      filter.addIntRange( rg );
    }
    else if( ((TreeNode)sn).getUI().isChecked() )
    {
     if( sn.getUserObject() instanceof Range )
     {
      Range rg = (Range)sn.getUserObject();
      if( (!Float.isNaN(rg.getLimitLow())) || (!Float.isNaN(rg.getLimitHigh())) )
       filter.addRealRange( rg );
     }
     else if( sn.getUserObject() instanceof IntRange )
     {
      IntRange rg = (IntRange)sn.getUserObject();
      
      if( rg.getLimitLow() > Integer.MIN_VALUE || rg.getLimitHigh() < Integer.MAX_VALUE )
       filter.addIntRange( (IntRange)sn.getUserObject() );
     }
     else if( sn.getUserObject() instanceof Variant )
      filter.addVariant(((ParameterPart)n.getUserObject()).getId(), ((Variant)sn.getUserObject()).getId());
    }

   }
  }
  
  return filter;
 }
 
 private boolean hasVariant( int partId, int variId )
 {
  if( filter.getVariants() == null )
   return false;
   
   for( List<Integer> pv : filter.getVariants() )
   {
    if( partId == pv.get(0) )
    {
     for( int i=1; i < pv.size(); i++)
     {
      if( pv.get(i) == variId )
       return true;
     }
    }
   }
  
  return false;
 }
 
 private Range getRealRange( int partId )
 {
  if( filter.getRealRanges() == null )
   return null;
 
  for( Range rg : filter.getRealRanges() )
   if( rg.getPartID() == partId )
    return rg;
  
  return null;
 }
 
 private IntRange getIntRange( int partId )
 {
  if( filter.getIntRanges() == null )
   return null;
 
  for( IntRange rg : filter.getIntRanges() )
   if( rg.getPartID() == partId )
    return rg;
  
  return null;
 }

 
 private static class RealNodeListener extends TreeNodeListenerAdapter
 {
  private static RealNodeListener instance;
  
  public static RealNodeListener getInstance()
  {
   if( instance == null )
    instance = new RealNodeListener();
   
   return instance;
  }
  
  public void onDblClick(Node node, EventObject e) 
  {
//   System.out.println("Dbl");
   e.stopPropagation();
  }

  public void onClick(final Node node, EventObject e) 
  {
   final RealRangeDialog nrd = RealRangeDialog.getInstance();
   nrd.setRange(new Range( (Range)node.getUserObject() ));

   nrd.setListener(new ObjectAction<Range>()
   {

    public void doAction(String actName, Range p)
    {
     nrd.hide();
     
     if( ! "ok".equals(actName) )
      return;
     
     Range or = (Range)node.getUserObject();
     
     or.setLimitHigh( p.getLimitHigh() );
     or.setLimitLow( p.getLimitLow() );
     
     ((TreeNode)node).setText(p.toString());
     ((TreeNode)node).getUI().toggleCheck(true);
    }

    public void doMultyAction(String actName, List<Range> lp)
    {
    }
   });
   
   nrd.show();
  }
 }
 
 private static class IntNodeListener extends TreeNodeListenerAdapter
 {
  private static IntNodeListener instance;
  
  public static IntNodeListener getInstance()
  {
   if( instance == null )
    instance = new IntNodeListener();
   
   return instance;
  }

  
  public void onDblClick(Node node, EventObject e) 
  {
//   System.out.println("Dbl");
   e.stopPropagation();
  }

  public void onClick(final Node node, EventObject e) 
  {
   final IntRangeDialog nrd = IntRangeDialog.getInstance();
   nrd.setRange(new IntRange( (IntRange)node.getUserObject() ));

   nrd.setListener(new ObjectAction<IntRange>()
   {

    public void doAction(String actName, IntRange p)
    {
     nrd.hide();
     
     if( ! "ok".equals(actName) )
      return;
     
     IntRange or = (IntRange)node.getUserObject();
     
     or.setLimitHigh( p.getLimitHigh() );
     or.setLimitLow( p.getLimitLow() );
     
     ((TreeNode)node).setText(p.toString());
     ((TreeNode)node).getUI().toggleCheck(true);
    }

    public void doMultyAction(String actName, List<IntRange> lp)
    {
    }
   });
   
   nrd.show();
  }
 }

 private static class DateNodeListener extends TreeNodeListenerAdapter
 {
  private static DateNodeListener instance;
  
  public static DateNodeListener getInstance()
  {
   if( instance == null )
    instance = new DateNodeListener();
   
   return instance;
  }

  
  public void onDblClick(Node node, EventObject e) 
  {
//   System.out.println("Dbl");
   e.stopPropagation();
  }

  public void onClick(final Node node, EventObject e) 
  {
   final DateRangeDialog nrd = DateRangeDialog.getInstance();
   nrd.setRange(new IntRange( (IntRange)node.getUserObject() ));

   nrd.setListener(new ObjectAction<IntRange>()
   {

    public void doAction(String actName, IntRange p)
    {
     nrd.hide();
     
     if( ! "ok".equals(actName) )
      return;
     
     IntRange or = (IntRange)node.getUserObject();
     
     or.setLimitHigh( p.getLimitHigh() );
     or.setLimitLow( p.getLimitLow() );
     
     ((TreeNode)node).setText("From "+Date2Str.int2DateStr(p.getLimitLow())+" till "+Date2Str.int2DateStr(p.getLimitHigh()));
     ((TreeNode)node).getUI().toggleCheck(true);
    }

    public void doMultyAction(String actName, List<IntRange> lp)
    {
    }
   });
   
   nrd.show();
  }
 }
 
 private static class BooleanNodeListener extends TreeNodeListenerAdapter
 {
  private static BooleanNodeListener instance;
  
  public static BooleanNodeListener getInstance()
  {
   if( instance == null )
    instance = new BooleanNodeListener();
   
   return instance;
  }
  
  public void onClick(final Node node, EventObject e) 
  {
   TreeNode tn = (TreeNode)node;
   
   IntRange rg = (IntRange)node.getUserObject();
   
   int state = rg.getLimitHigh() ;
   
   if( state == 0 )
   {
    rg.setLimitLow(1);
    rg.setLimitHigh(1);
    tn.setIconCls("trueIcon");
    tn.setText("True");
   }
   else if( state == 1 )
   {
    rg.setLimitLow(2);
    rg.setLimitHigh(2);
    tn.setIconCls("dontcareIcon");
    tn.setText("Doesn't care");
   }
   else
   {
    rg.setLimitLow(0);
    rg.setLimitHigh(0);
    tn.setIconCls("falseIcon");
    tn.setText("False");
   }

  }
 }
 
}
