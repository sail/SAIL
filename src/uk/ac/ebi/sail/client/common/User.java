package uk.ac.ebi.sail.client.common;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class User implements IsSerializable,Serializable {
	
	
	// fields to store data
	public int id ;
	public String role=null	;
	public ArrayList<String> collectionId;
	public String username;
	public String pwd;

	//	public 
	
//    public String username;
//	public String pwd;
	public String name;
	public String surname;
	public String email;
	public int phone;
	public String description;
	
	/**
     * constructor
     */
    public User() {
            // nothing to do when transporting
    }
   
    public User(int id, String username,String role,String pwd,ArrayList<String> collectionId) {
    	this.id=id;
    	this.username=username;
    	  //*
    	 this.pwd=pwd;
    	 this.role=role;
    	 this.collectionId=collectionId;
    	
        // nothing to do when transporting
    }
    
    public User(User u) {
    	id=u.getId();
    	username=u.getUsername();
        pwd=u.getPwd();
    	role=u.getRole();
    	collectionId=u.getCollectionId();
    	
        // nothing to do when transporting
    }
    
    public int getId(){return id;}
    public String getUsername(){return username;}
    public String getRole(){return role;}
    public String getPwd(){return pwd;}
    public ArrayList<String> getCollectionId(){return collectionId;}
	
    public void setId(int id){this.id = id;}
    public void setUsername(String username){this.username = username;}
    public void setRole(String role)
    {
    	this.role = role;
    }
    public void setPwd(String pwd)
    {
    	this.pwd = pwd;
    }
    public void setCollectionId(ArrayList<String> collectionId)
    {
    	this.collectionId = collectionId;
    }
    
    public void addCollection(String c)
    {
     if( collectionId == null )
    	 collectionId=new ArrayList<String>();
     
     collectionId.add(c);
    }
    
    
//    public String getName(){return name;}
//    public String getSurname(){return surname;}
//	public String getEmail(){return email;}
//	public int getPhone(){return phone;}
//	public String getDescription(){return description;}
//	
}
