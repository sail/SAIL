package uk.ac.ebi.sail.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;


public class MyRemoteServiceServlet extends RemoteServiceServlet{
	@Override
	protected SerializationPolicy doGetSerializationPolicy(
		HttpServletRequest request, String moduleBaseURL, String strongName) {
		String modulePath=null;
		try {
	         modulePath = new URL(moduleBaseURL).getPath();			  
	      } catch (MalformedURLException ex) {
	        // log the information, we will default
	        log("Malformed moduleBaseURL: " + moduleBaseURL, ex);
	      }
	    
	    String contextPath = request.getContextPath();
	    String[] rest=modulePath.split("/");
	    String temp=rest[rest.length-1];
		String repl=contextPath.concat("/").concat(temp).concat("/");
		moduleBaseURL=moduleBaseURL.replace(modulePath,repl);
		
		
		return super.doGetSerializationPolicy(request, moduleBaseURL, strongName);
	}

	
	  static SerializationPolicy loadSerializationPolicy(HttpServlet servlet,
		      HttpServletRequest request, String moduleBaseURL, String strongName) {
		    // The request can tell you the path of the web app relative to the
		    // container root.
		    String contextPath = request.getContextPath();

		    String modulePath = null;
		    //moduleBaseURL="http://www.simbioms.org:8080/SUMMIT2/sail_user/";
		    if (moduleBaseURL != null) {
		      try {
		        modulePath = new URL(moduleBaseURL).getPath();
				   
		    	  
		      } catch (MalformedURLException ex) {
		        // log the information, we will default
		        servlet.log("Malformed moduleBaseURL: " + moduleBaseURL, ex);
		      }
		    }

		    SerializationPolicy serializationPolicy = null;

		    /*
		     * Check that the module path must be in the same web app as the servlet
		     * itself. If you need to implement a scheme different than this, override
		     * this method.
		     */
		    if (modulePath == null || !modulePath.startsWith(contextPath)) {
		      String message = "ERROR: The module path requested, "
		          + modulePath
		          + ", is not in the same web application as this servlet, "
		          + contextPath
		          + ".  Your module may not be properly configured or your client and server code maybe out of date.";
		      servlet.log(message, null);
		    } else {
		      // Strip off the context path from the module base URL. It should be a
		      // strict prefix.
		      String contextRelativePath = modulePath.substring(contextPath.length());

		      String serializationPolicyFilePath = SerializationPolicyLoader.getSerializationPolicyFileName(contextRelativePath
		          + strongName);

		      // Open the RPC resource file and read its contents.
		      InputStream is = servlet.getServletContext().getResourceAsStream(
		          serializationPolicyFilePath);
		      try {
		        if (is != null) {
		          try {
		            serializationPolicy = SerializationPolicyLoader.loadFromStream(is,
		                null);
		          } catch (ParseException e) {
		            servlet.log("ERROR: Failed to parse the policy file '"
		                + serializationPolicyFilePath + "'", e);
		          } catch (IOException e) {
		            servlet.log("ERROR: Could not read the policy file '"
		                + serializationPolicyFilePath + "'", e);
		          }
		        } else {
		          String message = "ERROR: The serialization policy file '"
		              + serializationPolicyFilePath
		              + "' was not found; did you forget to include it in this deployment?";
		          servlet.log(message);
		        }
		      } finally {
		        if (is != null) {
		          try {
		            is.close();
		          } catch (IOException e) {
		            // Ignore this error
		          }
		        }
		      }
		    }

		    return serializationPolicy;
		  }



}
