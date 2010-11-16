package uk.ac.ebi.sail.client.ui;

import uk.ac.ebi.sail.client.SecurityManager;
import uk.ac.ebi.sail.client.common.Permission;

import com.google.gwt.user.client.Window;
import com.gwtext.client.core.EventCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.MenuItem;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;

public class CellClickListener extends BaseItemListenerAdapter implements EventCallback
{

 private ReportCell rc;

 public CellClickListener(ReportCell rc)
 {
  this.rc = rc;
  
 }

 public void execute(EventObject e)
 {
  Menu clickMenu = new Menu();
  
  MenuItem itm = new MenuItem();
  itm.setText("Dowload IDs");
  itm.addListener(this);
  itm.setStateId("ids");
  clickMenu.addItem(itm);

  itm = new MenuItem();
  itm.setText("Dowload Records");
  itm.setStateId("records");
  itm.addListener(this);
  
  clickMenu.addItem(itm);
  
  clickMenu.showAt(e.getXY());
 }
 
 @Override
 public void onClick(BaseItem item, EventObject e)
 {
  if(item.getStateId().equals("ids"))
  {
   if(SecurityManager.getAccessController().checkPermission(Permission.GET_RECORD_ID))
   {
    MessageBox.confirm("Confirm IDs download", "Do you to download " + rc.getCount() + " IDs for this cell?",
      new MessageBox.ConfirmCallback()
      {

       public void execute(String btnID)
       {
        if(!"yes".equals(btnID))
         return;

        IDReporter.getInstance().report(rc.getRequest());

       }
      });
   }
   else
    MessageBox.alert("Permission denied", "You have no permission to download record IDs");
  }
  else
  {
   if(SecurityManager.getAccessController().checkPermission(Permission.GET_RECORD))
   {
     Window.open("dataExport?request="+rc.getRequest().toSerialString(), "_blank", "");
   }
   else
    MessageBox.alert("Permission denied", "You have no permission to download records");
  }
 }
}
