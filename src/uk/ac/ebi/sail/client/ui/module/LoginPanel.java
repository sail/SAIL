package uk.ac.ebi.sail.client.ui.module;



import java.util.ArrayList;
//import static org.apache.commons.lang.StringUtils.leftPad;
import java.util.HashMap;
import java.util.Map;
import com.google.gwt.user.client.rpc.AsyncCallback;


import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.KeyListener;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.ValidationException;
import com.gwtext.client.widgets.form.Validator;
import com.gwtext.client.widgets.layout.FitLayout;

import uk.ac.ebi.sail.client.BackendService;
import uk.ac.ebi.sail.client.common.User;
import uk.ac.ebi.sail.client.ui.ConfigPanel;

public class LoginPanel extends Panel  {
	
	boolean LogOff=true;
	boolean LogIN=false;
	String Role=null;
	public Window window;
	private FormPanel formPanel = new FormPanel();
	private TextField fName = null;
	private TextField passWord = null;
	static Button loginButton = null;
	static Button registerButton = null;
	private Button submitBtn = null;

	public LoginPanel(){
		setLayout(new FitLayout());
		setAutoHeight(true);
		setPaddings(20);
	}
	
	public void closeWindow(){
		window.hide();
	}
	
	
	public boolean popUp()	 {
		
		window = new Window();
		window.setTitle("Login");
		window.setCloseAction(Window.HIDE);
	    this.setLoginPanel();
	    if(LogOff!=false){}
	    else{
	         formPanel.remove(loginButton);
	    }
	    loginButton = new Button( "Login" );
	     loginButton.addListener( new ButtonListenerAdapter() {
	        public void onClick( Button button, EventObject e ) {
	            login();	 
	        }
	    });
	   
	    formPanel.addButton(loginButton); 
	    
	    registerButton = new Button( "Registration" );
	    formPanel.addButton(registerButton);
		
	    registerButton.addListener(new ButtonListenerAdapter(){
	    	@Override
	    	public void onClick(Button button, EventObject e) {
	    		if(AdminPanel.host.contentEquals(" ")){
	    			MessageBox.alert("Registration", "For registration please contact: "+AdminPanel.outgoingMail);
	    		}
	    		else{
		    		setRegPanel();
	    		}

	    	}
	    });
	    
	  formPanel.addButton(new Button("Password forgotten?", new ButtonListenerAdapter(){
	    	public void onClick(Button button, EventObject e) {
	    		if(AdminPanel.host.contentEquals(" ")){
	    			MessageBox.alert("Password forgotten!", "Please contact: "+AdminPanel.outgoingMail);
	    		}
	    		else{
	    			forgottenPanel();
		    	    closeWindow();
	    		}
	    	}
	    }));
	    LogOff=false;
	    window.add(formPanel);
	    doLayout();
	    window.show();
	    return LogIN;
		
	}
	
    private void login() {
		Map<String,String> loginData = getUserData(formPanel.getForm() );
		User user=new User();
		BackendService.Util.getInstance().userMng(user, "isValid", loginData, new AsyncCallback<User>() {
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert( "Invalid", "Wrong username or password");
			}

			@Override
			public void onSuccess(User result) {
				if(result.getId()==-1){
	       			MessageBox.alert( "Invalid", "Wrong username or password");
	       		}
	       		else{
	       	   		AdminPanel.user=result;
	       	   	    AdminPanel.getInstance().doFinalInit();
       	        	Component[] items = AdminPanel.appPanel.getItems();
       		    	AdminPanel.appPanel.remove(items[1]);
   	       		}
			}
		});
    }
	
	
	private void setLoginPanel(){
		formPanel.setFrame(true);
	    formPanel.setAutoHeight(true);
		formPanel.setWidth(600);
	    formPanel.setLabelWidth(300);
	  
	    fName = new TextField( "Username", "userName", 230 );
	    fName.setAllowBlank( false );
	    
	    formPanel.add( fName );  
	 
	    passWord = new TextField( "Password", "pswd", 230 );
	    passWord.setInputType( "password" );
	    passWord.setAllowBlank(false);
	    passWord.addKeyListener(13, new KeyListener() {
			@Override
			public void onKey(int key, EventObject e) {
				login();
			}
		});
	    
	    formPanel.add( passWord );
	    doLayout();
	}
	
    private Map<String,String> getUserData( Form form ){
        String formValues = form.getValues();
	
        Map<String,String> loginData = new HashMap<String,String>();
        String[] nameValuePairs = formValues.split( "&" );
	
        for (int i = 0; i < nameValuePairs.length; i++) {
	        String[] oneItem = nameValuePairs[i].split( "=" );
	        loginData.put( oneItem[0], oneItem[1] );
        }
        return loginData;
    }
	
    
	private void forgottenPanel(){
		final Window fgWindow = new Window();
		fgWindow.setTitle("Password forgotten?");
		fgWindow.setPaddings(5);
		fgWindow.setClosable(true);
		
	    final Panel panel = new Panel();  
		panel.setAutoHeight(true);
		panel.setPaddings(5);
		panel.setHtml("Please enter your mail address. You will recieve a message with your new password.");

		fgWindow.add(panel);
		
		final FormPanel form=new FormPanel();
		form.setPaddings(5);
	    form.add(new TextField("Your email"));
	    
	    
	    
	    
	    form.addButton(new Button("Send", new ButtonListenerAdapter(){
	    	@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
	    	public void onClick(Button button, EventObject e) {
	     		int index=AdminPanel.usermail.indexOf(ConfigPanel
						.transform(form.getForm().getValues()).split("=")[1]);
	    		
	    		if(index==-1){MessageBox.alert("Error","Mail address doesn`t exist");}
	    		else {
	    			String pwd="forgotten";	
	    			String umail=ConfigPanel.transform(form.getForm().getValues()).split("=")[1];
	    			
	    			BackendService.Util.getInstance().sendMail("Your password is: ",pwd,umail, new AsyncCallback() {
	                    @Override
	             		public void onFailure(Throwable caught) {
	                		 MessageBox.alert( "Error", "Error while sending"+ caught );
	               		 }	
	                  	 @Override
	               		 public void onSuccess(Object result) {
	                  		window.show();
	               	     	fgWindow.close();
	               	        MessageBox.alert( "Success", "You request will be processed!");
	              	  	 }
	                  });
	    			}
		    		
	    	}
	    }));
	    
	    
	    
	    form.addButton(new Button("Chancel", new ButtonListenerAdapter(){
	    	@Override
	    	public void onClick(Button button, EventObject e) {
	    		fgWindow.close();
	    		window.show();
	    	}
	    }));
	  
	    fgWindow.add(form);
	    fgWindow.show();
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setRegPanel(){
 
		final Window regWin = new Window();
		regWin.setTitle("Registration");  
		regWin.setWidth(600);  
		regWin.setHeight(350);  
		regWin.setPlain(true);

		final Panel panel = new Panel();  
		panel.setBorder(false); 
		panel.setAutoHeight(true);
		panel.setPaddings(15);
		
		final FormPanel formPanel = new FormPanel(Position.LEFT);  
		formPanel.setFrame(true);  
		formPanel.setWidth(600);  
		formPanel.setLabelWidth(350);  
		
		FieldSet fieldSet = new FieldSet("Contact Information"); 
		fieldSet.add(new TextField("First Name", "first", 190));  
		fieldSet.add(new TextField("Last Name", "last", 190));
		TextField phone = new TextField("Phone", "phone", 190);
		fieldSet.add(phone);
		    
		fieldSet.add(new TextField("Company", "company", 190));  
		  
		final TextField username=new TextField("Username*", "username", 190);
	    username.setValidator(new Validator() {
			@Override
			public boolean validate(String value) throws ValidationException {
				if (!AdminPanel.username.contains(value)) {
					return true;
				}
				else 
					throw new ValidationException("Username still exist!");
			}
		});
		    
	    fieldSet.add(username);

	    final TextField password=new TextField("Password*", "password", 190);
	    password.setPassword(true);

		final TextField cpassword=new TextField("confirm Password*", "cpassword", 190);
	    cpassword.setPassword(true);
	    cpassword.setValidator(new Validator(){
	    	public boolean validate(String value) throws ValidationException {
	    		if(cpassword.getText().contentEquals(password.getText())) {
					return true;
				} else {
					throw new ValidationException("Password or username dont match. Please try again.");
				}
	    	}
	    });
			
		TextField email = new TextField("Email*", "email", 190);  
		email.setValidator(new Validator() {
			@Override
			public boolean validate(String value) throws ValidationException {
				ArrayList<String> email=new ArrayList<String>();
	    		String [] split=value.split("");
	    		for (int i = 1; i < split.length; i++) email.add(split[i]);
				if(email.contains("@")&email.contains(".")){System.out.println("@");
				    submitBtn.enable();
				    return true;
			    } else throw new ValidationException("This field should be an e-mail address in the format \"user@domain.com\".");
			}
		});
			
	    fieldSet.add(password);
	    fieldSet.add(cpassword);
	    fieldSet.add(email);  
		   		    
	     formPanel.add(fieldSet);
		   
	     submitBtn = new Button("Submit", new ButtonListenerAdapter() {  
             public void onClick(Button submitBtn, EventObject e) {  
	        	 String [] nameValuePairs=ConfigPanel.transform(formPanel.getForm().getValues()).split( "&" );
	             String msg=null;
	             for (int i = 0; i < nameValuePairs.length; i++) {
	           	     String[] firstValue=nameValuePairs[i].split("=");
	             	 if(firstValue[0].equalsIgnoreCase("cpassword")){}
	             	 else{msg+=nameValuePairs[i]+"\n";}
		             	   
	             }
	             String pwd=null;String umail=null;
		         BackendService.Util.getInstance().sendMail(msg,pwd,umail, new AsyncCallback() {
                    @Override
             		public void onFailure(Throwable caught) {
                		 MessageBox.alert( "Error", "Error while sending"+ caught );
               		 }	
                  	 @Override
               		 public void onSuccess(Object result) {
              	     	MessageBox.alert( "Success", "Your request will be processed!");
              	     	regWin.close();
                  	 }
                  });
       	 
		      }  
		  });  
		  submitBtn.disable();
		  formPanel.addButton(submitBtn);  
		  regWin.add(formPanel);  
		  add(panel);
		  regWin.show();
	}
	
}

