package uk.ac.ebi.sail.client.ui.module;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.Panel;


public class DataUploadPanel2 extends Panel{
	public DataUploadPanel2()
	 {
	  setPaddings(20);
	  
	  Button button = new Button("Login", new ButtonListenerAdapter()
	  {
	   public void onClick(final Button button, EventObject e)
	   {
	   System.out.println("here I am");
	   
	   
	   
	   
	   }
	  });

	  addButton(button);
	  }
	

}
