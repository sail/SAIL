package uk.ac.ebi.sail.client.ui.module;

import uk.ac.ebi.sail.client.BackendService;
import uk.ac.ebi.sail.client.BackendServiceAsync;
import uk.ac.ebi.sail.client.ui.ObjectGrid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.widgets.Panel;

public class ObjectPanel extends Panel {
    
	public Panel panel =new Panel();
	
	public ObjectPanel(){
		getUsersObject();
	}
	
	public void getUsersObject(){
		
		
		BackendServiceAsync as = (BackendServiceAsync) GWT .create(BackendService.class);
	    as.getUsersObject(new AsyncCallback<String[][]>() {
			@Override
			public void onSuccess(String[][] result) {
				System.out.println(" Ergebnis "+result[0][0]+" "+result[0][1]+" "+result[0][2]+" "+result[0][3]+" "+result[0][8]);
	//			System.out.println(" Ergebnis "+result[1][0]+" "+result[1][1]+" "+result[0][2]+" "+result[0][3]+" "+result[0][8]);
				System.out.println("Resultlänge "+result.length);
			//	if()
//				
				String[][] urst=new String[1][10];
				for (int i = 0; i < result.length; i++) {
					if(result[i][3].equalsIgnoreCase(AdminPanel.user.getUsername())){
						for (int j = 0; j < 10; j++) {
							if (j!=4) urst[0][j]=result[i][j];
						}
					}
				}
//			
				
//				ObjectGrid grid = new ObjectGrid(result);
//				add(grid);	
////				System.out.println("Grid    wrd sa "+AdminPanel.user.getRole()+AdminPanel.user.getUsername());

				if(AdminPanel.user.getRole().equalsIgnoreCase("ADMIN")){
					ObjectGrid grid = new ObjectGrid(result);
					add(grid);	
				}
				else if(AdminPanel.user.getRole().equalsIgnoreCase("USER")){
//					ObjectGrid grid = new ObjectGrid(urst);
//					add(grid);
//					
					ChangePwdPanel grid = new ChangePwdPanel(urst);
					add(grid);	
				}
			
			}
			
			@Override
			public void onFailure(Throwable caught) {
				 GWT.log("Can't access database", caught);
			}
		});

	}
	

}  
	
	
	


