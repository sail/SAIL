package uk.ac.ebi.sail.client;

import uk.ac.ebi.sail.client.common.IDGenerator;


class ReportRequestItem
{
 public enum Type
 {
  PARAMETER("paramIcon"),
  PART("splitIcon"),
  FILTERED_PART("filteredSplitIcon"),
  GROUP("groupIcon"),
  FILTERED_SINGLE("filteredIcon"),
  FILTERED_COMPLEX("filteredIcon"),
  COLLECTION(""),
  PREDEF("predefinedQueryIcon");
  
  private String icon;
  
  Type(String ic)
  {
   icon=ic;
  }
  
  public String getIcon()
  {
   return icon;
  }
 }
 
 
 private String objectName;
 private Object subject;
 private Type type;
 private ReportRequestItem groupItem;
 private int reqID;
 
 
 public ReportRequestItem( Type t, String on, Object sbj )
 {
  type=t;
  objectName=on;
  subject=sbj;
  reqID = IDGenerator.getID();
 }
 
 public Object getObject()
 {
  return subject;
 }
 
 public void setObject( Object obj )
 {
  subject=obj;
 }

 public String getObjectName()
 {
  return objectName;
 }

 public void setObjectName(String objectName)
 {
  this.objectName = objectName;
 }

 public Type getType()
 {
  return type;
 }

 public void setType(Type type)
 {
  this.type = type;
 }
 
 public String getIconClass()
 {
  if( type == null )
   return null;
  
  return type.getIcon();
 }

 public ReportRequestItem getGroupItem()
 {
  return groupItem;
 }

 public void setGroupItem(ReportRequestItem groupItem)
 {
  this.groupItem = groupItem;
 }

 public int getRequestId()
 {
  return reqID;
 }
}
