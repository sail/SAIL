package uk.ac.ebi.sail.client.common;

import java.io.Serializable;
import java.util.List;

import uk.ac.ebi.sail.client.FilteredEnumeration;
import uk.ac.ebi.sail.client.ReportRequestItem;
import uk.ac.ebi.sail.client.common.RequestItem.Type;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NetworkReportRequest implements Serializable,IsSerializable
{
 private RequestItem[] reqArray;
 private int[] rels;
 private boolean allRelations;
 private boolean isAndOp;
 
 public NetworkReportRequest( )
 {
  
 }

 
 public NetworkReportRequest( ReportRequest req )
 {
  reqArray = new RequestItem[ req.getReportRequestItems().size() ];
  int i=0;
  for( ReportRequestItem rri : req.getReportRequestItems() )
  {
   reqArray[i]=convert( rri );
      
   i++;
  }
  

  List<Integer> rrRels = req.getRelations();
  if( rrRels != null && rrRels.size() > 0 )
  {
   rels = new int[ rrRels.size() ];
   int j=0;
   
   for( Integer rid : rrRels )
    rels[j++]=rid;
  }
  
  allRelations = req.isAllRelations();
  isAndOp = req.isAndOperation();
 } 

 
 @SuppressWarnings("unchecked")
 public static RequestItem convert( ReportRequestItem rri )
 {
  Object ti = rri.getObject();
  
  if( rri.getType() == ReportRequestItem.Type.PARAMETER )
  {
   RequestItem ri = new RequestItem();
   
   ri.setType(Type.PARAM);
   ri.setParamID(((Parameter)ti).getId());
   ri.setId(rri.getRequestId());
   
   return ri;
  }
  else if( rri.getType() == ReportRequestItem.Type.COLLECTION )
  {
   CollectionRequestItem rpri = new CollectionRequestItem();
   rpri.setType(Type.COLLECTION);
   rpri.setRepIDs((int[])ti);
   rpri.setId(rri.getRequestId());
   
   return rpri;
   
  }
  else if( rri.getType() == ReportRequestItem.Type.PART )
  {
   PartRequestItem ri = new PartRequestItem();
   
   ri.setType(Type.SPLIT);
   ri.setParamID(((ParameterPart)ti).getParameter().getId());
   ri.setPartID(((ParameterPart)ti).getId());
   ri.setId(rri.getRequestId());
   
   return ri;
  }
  else if( rri.getType() == ReportRequestItem.Type.FILTERED_PART )
  {
   FilteredRequestItem fri = new FilteredRequestItem();
   
   fri.setType(Type.FSPLIT);

   fri.setParamID(((FilteredEnumeration)ti).getParameter().getId());
   fri.setPartID(((FilteredEnumeration)ti).getEnumeration().getId());
   fri.setVariants(((FilteredEnumeration)ti).getVariants());
   fri.setId(rri.getRequestId());
   
   return fri;
  }
  else if( rri.getType() == ReportRequestItem.Type.FILTERED_SINGLE )
  {
   FilteredRequestItem fri = new FilteredRequestItem();

   fri.setParamID(((FilteredEnumeration)ti).getParameter().getId());
   fri.setPartID(((FilteredEnumeration)ti).getEnumeration().getId());
   fri.setVariants(((FilteredEnumeration)ti).getVariants());
   fri.setId(rri.getRequestId());
   
   return fri;
  }
//  else if( rri.getType() == ReportRequestItem.Type.FILTERED_COMPLEX )
//  {
//   ComplexFilteredRequestItem fri = new ComplexFilteredRequestItem();
//   
//   List<FilteredEnumeration> list = (List<FilteredEnumeration>)ti;
//   fri.setParamID( list.get(0).getParameter().getId());
//
//   int [][] mtrx = new int[list.size()][];
//   
//   int i=0;
//   for( FilteredEnumeration fe : list )
//   {
//    mtrx[i] = new int[ fe.getVariants().length + 1 ];
//    
//    mtrx[i][0] = fe.getEnumeration().getId();
//    
//    int j=1;
//    
//    for( int vid : fe.getVariants() )
//     mtrx[i][j++]=vid;
//    
//    i++;
//   }
//   
//   fri.setVariants(mtrx);
//   
//   return fri;
//  }
  else if( rri.getType() == ReportRequestItem.Type.FILTERED_COMPLEX )
  {
   ComplexFilteredRequestItem fri = new ComplexFilteredRequestItem();
   
   ComplexFilter cf = (ComplexFilter)ti;
   fri.setParamID( cf.getParameter().getId());
   fri.setFilter( cf );
   fri.setId(rri.getRequestId());
   
   return fri;
  }
  else if( rri.getType() == ReportRequestItem.Type.GROUP )
  {
   List<ReportRequestItem> li = (List<ReportRequestItem>)ti;
   
   GroupRequestItem gri = new GroupRequestItem();

   
   gri.setGroupName(rri.getObjectName());
   
   for( ReportRequestItem lp : li )
    gri.addItem(convert(lp));

   gri.setId(rri.getRequestId());
   
   return gri;
  }
  else if( rri.getType() == ReportRequestItem.Type.PREDEF )
   return (GroupRequestItem)ti;
  
  return null;
 }
 
 public void setAndOperation( boolean andOp )
 {
  isAndOp=andOp;
 }
 
 public boolean isAndOperation()
 {
  return isAndOp;
 }
 
 public RequestItem[] getItems()
 {
  return reqArray;
 }
 
 public int[] getRelations()
 {
  return rels;
 }

 public void setRelations(int[] relIDs)
 {
  rels=relIDs;
 }
 
 
 public void setItems( RequestItem[] itm )
 {
  reqArray = itm;
 }
 
 public String toSerialString()
 {
  StringBuilder sb = new StringBuilder();
  
  sb.append("OP:");
  
  if( isAndOp )
   sb.append("AND;");
  else
   sb.append("OR;");
  
  if( isAllRelations() )
   sb.append("REL:-1;");
  else if( rels != null && rels.length > 0 )
  {
   sb.append("REL:");
   
   for( int rt : rels )
    sb.append(rt).append(',');

   sb.setCharAt(sb.length()-1, ';');
  }
  
  for(RequestItem ri : reqArray )
   sb.append(ri.toSerialString()).append(';');
  
  sb.setLength(sb.length()-1);
  
  return sb.toString();
 }


 public boolean isAllRelations()
 {
  return allRelations;
 }


 public void setAllRelation(boolean b)
 {
  allRelations=b;
 }


}
