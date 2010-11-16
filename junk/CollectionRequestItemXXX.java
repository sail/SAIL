package uk.ac.ebi.sail.client.common;

public class CollectionRequestItemXXX extends RequestItem
{
 private int[] repIDs;

 public CollectionRequestItemXXX( String name, int[] cids )
 {
  setType(Type.COLLECTION);
  setName(name);
  repIDs = cids;
 }
 
 public CollectionRequestItemXXX()
 {
  setType(Type.COLLECTION);
 }
 
 public int[] getCollectionIDs()
 {
  return repIDs;
 }

 public void setRepIDs(int[] repIDs)
 {
  this.repIDs = repIDs;
 }
 
 public String toSerialString()
 {
  StringBuilder sb = new StringBuilder(200);
  
  sb.append(getType().name());
  sb.append(':');
  
  if( repIDs != null && repIDs.length > 0)
  {
   for( int v : repIDs )
   {
    sb.append(v);
    sb.append(',');
   }
   
   sb.setLength( sb.length()-1 );
  }
  
  return sb.toString();
 }

 @Override
 public String getIconClass()
 {
  return "";
 }

}
