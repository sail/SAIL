package uk.ac.ebi.sail.client.ui;

import uk.ac.ebi.sail.client.BackendService;
import uk.ac.ebi.sail.client.ui.module.AdminPanel;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.KeyListener;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.VType;
import com.gwtext.client.widgets.form.ValidationException;
import com.gwtext.client.widgets.form.Validator;

public class ConfigPanel {
	Window confwindow = new Window();
	
	public void show(){
		confwindow.setAutoHeight(true);
		confwindow.setTitle("Administration Configuration");
    	confwindow.setBorder(false);  
		confwindow.setPaddings(15);  
		
	    final FormPanel formPanel = new FormPanel();  
		formPanel.setFrame(true);  
		formPanel.setPaddings(5, 5, 5, 0);  
		formPanel.setWidth(400);  
		formPanel.setLabelWidth(75);  

		FieldSet userFS = new FieldSet();  
        userFS.setTitle("User Information");  
        userFS.setAutoHeight(true);
        userFS.setCollapsible(true);
		
        TextField first = new TextField("First Name", "fname", 210);  
        userFS.add(first);  
  
        TextField last = new TextField("Last Name", "lname", 210);  
        userFS.add(last);  
           
        userFS.add(new TextField("User Name*", "uname", 210));
     
        final TextField password=new TextField("Password*", "pwd", 210);
	    password.setPassword(true);

	    final TextField cpassword=new TextField("confirm Password*", "cpassword", 210);
	    cpassword.setPassword(true);
	    cpassword.setValidator(new Validator(){
	    	public boolean validate(String value) throws ValidationException {
	    		if(cpassword.getText().contentEquals(password.getText())) {
					return true;
				} else {
					throw new ValidationException("Passwords dont match. Please try again.");
				}
	    	}
	    });
        
	    userFS.add(password);
	    userFS.add(cpassword);
	    
	    TextField company = new TextField("Company", "Description", 210);  
        userFS.add(company);  
         
        TextField email = new TextField("Email", "mail", 210);  
        email.setVtype(VType.EMAIL);  
        userFS.add(email);  
        
        FieldSet detailsFS = new FieldSet("Mail Configuration");  
        detailsFS.setCollapsible(true);  
        detailsFS.setAutoHeight(true);  
           
        TextField smail = new TextField("Outgoing Mail*", "smail", 210);  
        ToolTip tip1 = new ToolTip();  
        tip1.setHtml("Mail address which will be used to send the registration form if configured. Otherwise will be shown as contact for registration");  
        tip1.applyTo(smail);  
        smail.addKeyListener(13, new KeyListener() {
			@Override
			public void onKey(int key, EventObject e) {
				save(formPanel);
			}
		});
        
        detailsFS.add(smail);  
        
        TextField host = new TextField("Outgoing Host", "Host", 210);
        ToolTip tip2= new ToolTip();
        tip2.setHtml("Outgoing mail server (SMTP): smtp.everyone.net");  
        tip2.applyTo(host);
        detailsFS.add(host);  
        
        TextField rmail = new TextField("Incoming Mail", "rmail", 210);
        ToolTip tip3= new ToolTip();
        tip3.setHtml("Address which should recieve the registration. Could be the same like outgoing mail or another one.");  
        tip3.applyTo(rmail);
        rmail.addKeyListener(13, new KeyListener() {
			@Override
			public void onKey(int key, EventObject e) {
				save(formPanel);
			}
		});
        
        detailsFS.add(rmail);  
           
        formPanel.add(userFS); 
        formPanel.add(detailsFS);  
         
        
        
        Button save=new Button("Save", new ButtonListenerAdapter(){
        	@Override
        	public void onClick(Button button, EventObject e) {
        		save(formPanel);
        	}
        });
        formPanel.addButton(save);
        confwindow.add(formPanel);   
		confwindow.show();
	}
	
	private void save(FormPanel formPanel){
	    String [] allValue=transform(formPanel.getForm().getValues()).split("&");
	    String uvalue=allValue[0]+"&"+allValue[1]+"&"+allValue[2]+"&"+allValue[3]+"&"+allValue[5]+"&"+allValue[6]+"&role=ADMIN";
	    String mvalue=allValue[7]+"&"+allValue[8]+"&"+allValue[9];
	    try {
	    	AdminPanel.outgoingMail=allValue[7].split("=")[1];
		} catch (Exception e2) {
			AdminPanel.outgoingMail=null;
		}
		try {
	    	AdminPanel.host=allValue[8].split("=")[1];
		} catch (Exception e2) {
			AdminPanel.host=null;
		}
		try {
	    	AdminPanel.ingoingMail=allValue[9].split("=")[1];
		} catch (Exception e2) {
			AdminPanel.ingoingMail=null;
		}
	    
	    
	    BackendService.Util.getInstance().addMail(mvalue,"add", new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert( "Error", "Error while sending"+ caught );
			}
			@Override
			public void onSuccess(Void result) {}
		});
	    BackendService.Util.getInstance().addUser(uvalue, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert( "Error", "Error while sending"+ caught );
			}
			@Override
			public void onSuccess(Void result) {
			    confwindow.close();
				AdminPanel.getInstance().doFinalInit();
			}
		});
   	}
		
	

	public static String transform(String input){
		CharSequence ad="%40";
	    CharSequence nad="@";
	    CharSequence sp="%20";
	    CharSequence nsp=" ";
	    CharSequence d="%24";
	    CharSequence nd="$";
	    CharSequence b="%2F";
	    CharSequence nb="/";
	    String valueUtf8=input.replace(ad,nad).replace(sp, nsp).replace(d, nd).replace(b, nb);
		return valueUtf8;
	}
}
