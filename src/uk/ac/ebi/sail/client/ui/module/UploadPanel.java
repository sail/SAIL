package uk.ac.ebi.sail.client.ui.module;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtextux.client.widgets.upload.UploadDialog;


import com.gwtext.client.widgets.event.ButtonListenerAdapter;

import com.gwtext.client.widgets.layout.FitLayout;

public class UploadPanel extends Panel {
	public UploadPanel(){
		setLayout(new FitLayout());
		setPaddings(20);
		layout();
	}
	
	private void layout(){
		Panel upload= new Panel();
		Button button1 = new Button("Upload Vocabulary", new ButtonListenerAdapter(){
		    public void onClick(final Button button, EventObject e){
		        UploadDialog dialog = new UploadDialog();
		        dialog.setUrl("sail/UploadSvc");
		        //dialog.setPermittedExtensions(new String[]{"jpg", "gif"});
		        UrlParam param[] = new UrlParam[1];
		        param[0] = new UrlParam("UploadType", "parameters");
		        dialog.setBaseParams(param);
		        dialog.setPostVarName("myvar");
		        dialog.setPermittedExtensions(new String[]{"txt","TXT"});
		        dialog.show();
		        
		    }
		});

		upload.addButton(button1);
		//  doLayout();
		Button button2 = new Button("Upload Realtions", new ButtonListenerAdapter(){
		    public void onClick(final Button button, EventObject e){
		        UploadDialog dialog = new UploadDialog();
		        dialog.setUrl("sail/UploadSvc");
		        // dialog.setPermittedExtensions(new String[]{"jpg", "gif"});
		        UrlParam param[] = new UrlParam[1];
		        param[0] = new UrlParam("UploadType", "RelationMap");
		        dialog.setBaseParams(param);
		        dialog.setPostVarName("myvar");
		        dialog.show();
		        
		       // reload();
		       // AdminPanel.getInstance().doAdminInit();
		        
		    }
		});
		upload.addButton(button2);
		
		add(upload);
	}
}
	

