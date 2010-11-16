package uk.ac.ebi.sail.client.ui;

import uk.ac.ebi.sail.client.FilteredEnumeration;
import uk.ac.ebi.sail.client.ReportRequestItem;
import uk.ac.ebi.sail.client.common.AlternativeRequestItem;
import uk.ac.ebi.sail.client.common.CollectionRequestItem;
import uk.ac.ebi.sail.client.common.ComplexFilter;
import uk.ac.ebi.sail.client.common.ComplexFilteredRequestItem;
import uk.ac.ebi.sail.client.common.FilteredRequestItem;
import uk.ac.ebi.sail.client.common.NetworkReportRequest;
import uk.ac.ebi.sail.client.common.PartRequestItem;
import uk.ac.ebi.sail.client.common.RequestItem;
import uk.ac.ebi.sail.client.common.SimpleCounter;
import uk.ac.ebi.sail.client.common.RequestItem.Type;

public class ReportCell
{
 private int count;
 private String label;
 private String html;
 private NetworkReportRequest nRequest;
 
 
 public ReportCell(int cnt)
 {
  count=cnt;
 }

 public ReportCell(SimpleCounter cnt, String key, ReportRequestItem rr, NetworkReportRequest  preq )
 {
  count=cnt.getCount();
  label=key;
  
  nRequest = new NetworkReportRequest();
  
  RequestItem[] upReq = preq!=null?preq.getItems():null;
  RequestItem[] request = new RequestItem[preq!=null?(upReq.length+1):1];
  
  for( int i=0; i < request.length-1 ; i++ )
   request[i]=upReq[i];
  
  if( cnt.getPartID() == SimpleCounter.NOT_USED )
  {
   if( cnt.getVariantID() != SimpleCounter.NOT_USED )
   {
    CollectionRequestItem rpri = new CollectionRequestItem();
    rpri.setRepIDs(new int[]{cnt.getVariantID()});
    
    request[request.length - 1] = rpri;
   }
   else
   {
    if( cnt.getParamIDs() != null )
    {
     AlternativeRequestItem ri = new AlternativeRequestItem();
     request[request.length - 1] = ri;

     ri.setAlternativeParameters(cnt.getParamIDs());
     ri.setParamID(cnt.getParameterID());
    }
    else
    {
     if( rr.getType() == ReportRequestItem.Type.FILTERED_SINGLE )
     {
      FilteredRequestItem fri = new FilteredRequestItem();
      request[request.length - 1] = fri;
      fri.setParamID(cnt.getParameterID());
      
      FilteredEnumeration fe = (FilteredEnumeration)rr.getObject();
      
      fri.setPartID(fe.getEnumeration().getId());
      fri.setVariants( fe.getVariants() );
     }
//     else if( rr.getType() == ReportRequestItem.Type.FILTERED_COMPLEX )
//     {
//      ComplexFilteredRequestItem cfri = new ComplexFilteredRequestItem();
//      request[request.length - 1] = cfri;
//      cfri.setParamID(cnt.getParameterID());
//      
//      List<FilteredEnumeration> fel = (List<FilteredEnumeration>)rr.getObject();
//      
//      int[][] vrs = new int[fel.size()][];
//      
//      int i=0;
//      for( FilteredEnumeration fe : fel )
//      {
//       int[] ovs = fe.getVariants();
//       
//       vrs[i] = new int[ovs.length+1];
//       vrs[i][0]=fe.getEnumeration().getId();
//       
//       for( int k=0; k < ovs.length; k++ )
//        vrs[i][k+1]=ovs[k];
//
//       i++;
//      }
//      
//      cfri.setVariants( vrs );     
//     }
     else if( rr.getType() == ReportRequestItem.Type.FILTERED_COMPLEX )
     {
      ComplexFilteredRequestItem cfri = new ComplexFilteredRequestItem();
      request[request.length - 1] = cfri;
      cfri.setParamID(cnt.getParameterID());
      
      cfri.setFilter((ComplexFilter)rr.getObject());
     }
     else
     {
      RequestItem ri = new RequestItem();
      request[request.length - 1] = ri;
      
      rr.getType();
      
      ri.setType(Type.PARAM);
      ri.setParamID(cnt.getParameterID());
     }
     
    }
   }
  }
  else if( cnt.getVariantID() == SimpleCounter.NOT_USED )
  {
   PartRequestItem ri = new PartRequestItem();
   request[request.length-1] = ri;

   ri.setType(Type.PART);
   ri.setParamID(cnt.getParameterID());
   ri.setPartID(cnt.getPartID());
  }
  else
  {
   FilteredRequestItem fri = new FilteredRequestItem();

   fri.setParamID(cnt.getParameterID());
   fri.setPartID(cnt.getPartID());
   fri.setVariants( new int[]{ cnt.getVariantID() } );

   request[request.length-1] = fri;
  }
  
  nRequest.setItems(request);
  
 }

 public String getHTML()
 {
  if( html != null )
   return html;
  
  if( label !=null)
   html=label+"<br>"+count;
  else
   html=String.valueOf(count);
  
  return html;
 }
 
 public NetworkReportRequest getRequest()
 {
  return nRequest;
 }

 public void setHTML(String o)
 {
  html=o;
 }

 public int getCount()
 {
  return count;
 }
 
 public String showRequest()
 {
  StringBuilder sb = new StringBuilder();
  
  RequestItem[] request = nRequest.getItems();
  
  for( int i=0; i < request.length; i++ )
  {
   if( request[i].getType() == Type.PARAM )
    sb.append("Parameter: ").append(request[i].getParamID()).append("<br>");
   else if( request[i].getType() == Type.PART )
    sb.append("Part: ").append(request[i].getParamID()).append(":").append(((PartRequestItem)request[i]).getPartID()).append("<br>");
   else if( request[i].getType() == Type.FILTERED )
    sb.append("Variant: ").append(request[i].getParamID()).append(":").append(((FilteredRequestItem)request[i]).getPartID())
    .append(":").append(((FilteredRequestItem)request[i]).getVariants()[0]).append("<br>");
   else if( request[i].getType() == Type.COLLECTION )
    sb.append("Collection: ").append(((CollectionRequestItem)request[i]).getCollectionIDs()[0]).append("<br>");
  }
  
  return sb.toString();
 }

}
