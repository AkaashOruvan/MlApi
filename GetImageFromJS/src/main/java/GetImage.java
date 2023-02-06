import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Iterator;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.*;

@WebServlet("/ZsApi")
@MultipartConfig(
	fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
	  maxFileSize = 1024 * 1024 * 100,      // 100 MB
	  maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class GetImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static long folderNum = 0; 
	public static String query_Image;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Part filePart = request.getPart("query"); 
		String queryFileName = filePart.getSubmittedFileName();
		System.out.println(queryFileName);
		if((queryFileName.endsWith(".png"))|| (queryFileName.endsWith(".jpg")) || (queryFileName.endsWith(".jpeg"))) {
		filePart.write("/home/ashwanth/picsave/"+ File.separator +queryFileName);	
		query_Image="/home/ashwanth/picsave/"+queryFileName;
		ZiaApiCall.WorkerThread.queryImage="/home/ashwanth/picsave/"+queryFileName;
		}
		else {
			response.getWriter().println("Query File format not supported");
		}
		Collection<Part> parts = request.getParts();
		for (Part part : parts) {
		    if ((!part.getName().equals("file"))) {
		        continue;
		    }
		    if(part.getName().equals("file") && part.getContentType().startsWith("image/") ) {
			    String fileName = getFileName(part);
			    if((fileName.endsWith(".png"))|| (fileName.endsWith(".jpg")) || (fileName.endsWith(".jpeg"))){
			    System.out.println(fileName);
			    part.write("/home/ashwanth/picsave/"
			    + File.separator
			    + fileName);	
			    }
		    }
		}
	}
		private static String getFileName(Part part) {
		    for (String contentDisposition : part.getHeader("content-disposition").split(";")) {
		        if (contentDisposition.trim().startsWith("filename")) {
		            return contentDisposition.substring(contentDisposition.indexOf('=') + 1).trim().replace("\"", "");
		        }
		    }
		    return null;
		}}
	
//		 List<Part> fileParts = new ArrayList<Part>();
//	        for (Part part : request.getParts()) {
//	            if (part.getName().equals("file")) {
//	                fileParts.add(part);
//	            }
//	        }
//	        
//	        // save the files to disk
//	        for (Part filePart : fileParts) {
//	            String fileName = getFileName(filePart);
//	            filePart.write(fileName);
//	        }
//	    }
//	    
//	    private String getFileName(Part part) {
//	        for (String content : part.getHeader("content-disposition").split(";")) {
//	            if (content.trim().startsWith("filename")) {
//	                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
//	            }
//	        }
//	        return null;
//	    }	
//}
		
		
		
		
		
//	long start =System.currentTimeMillis();
//	ArrayList<String> arr = new ArrayList<String>();
//	boolean isMultipart = ServletFileUpload.isMultipartContent(request);
//	System.out.println(request.getInputStream());
//	System.out.println(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
//	   if (!isMultipart) {
//	   }
//	   else{
//	      FileItemFactory factory = new DiskFileItemFactory();
//	      ServletFileUpload upload = new ServletFileUpload(factory);
//	      List items = null;
//	      try {
//	          items = upload.parseRequest(request);
//	          } catch (FileUploadException e) {
//	               e.printStackTrace();
//	          }
//	      Iterator itr = items.iterator();
//	      while (itr.hasNext()) {
//	           FileItem item = (FileItem) itr.next();
//	           if (item.isFormField()) {
//	           } else {
//	           try {
//	              String itemName = item.getName();
//	              String filepath="/home/ashwanth/picsave/"+item.getName();
//	              arr.add(filepath);
//	              System.out.println("/home/ashwanth/picsave/"+itemName);
//	              
//	              
//	              File savedFile = new File(filepath);
//	              item.write(savedFile);
//	                 } catch (Exception e) {
//	                   e.printStackTrace();
//	              }
//	       }
	
	
	
	
	
	
//	  for (int i = 0;i<Integer.parseInt(request.getParameter("len"));i++) { 
//		  Part filePart = request.getPart("x"+i); String fileName =
//	  filePart.getSubmittedFileName();
//	  filePart.write("/home/ashwanth/picsave/"+fileName);
//	  System.out.println(fileName); //arr.add("/home/ashwanth/picsave/"+fileName);
//	 }

	      
	      
	      
	      
	      
	      
	      
	      
	      
	      
		
//	   System.out.println("Here");
//	   for(int i=0;i<arr.size();i++) {
//		   System.out.println(arr.get(i));
	   
	   
//	   ApiCall obj= new ApiCall();
//	   String res = null;
//	   String query=arr.get(arr.size() - 1);
//	   arr.remove(arr.size() - 1);
////		  try { res=obj.callmethod(arr,query); 
////		  } catch (InterruptedException e) { 
////			  e.printStackTrace(); 
////			  }
////		 
//		response.getWriter().println(res);
	
	/*boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		   if (!isMultipart) {
		   }
		   else{
		      FileItemFactory factory = new DiskFileItemFactory();
		      ServletFileUpload upload = new ServletFileUpload(factory);
		      List items = null;
		      try {
		          items = upload.parseRequest(request);
		          } catch (FileUploadException e) {
		               e.printStackTrace();
		          }
		      Iterator itr = items.iterator();
		      while (itr.hasNext()) {
		           FileItem item = (FileItem) itr.next();
		           if (item.isFormField()) {
		           } else {
		           try {
		              String itemName = item.getName();
		              
		              File savedFile = new File("/home/ashwanth/picsave/"+itemName);
		              item.write(savedFile);
//		              out.println("<tr><td><b>Your file has been saved at the loaction:</b></td></tr><tr><td><b>"+config.getServletContext().getRealPath("/")+"uploadedFiles"+"\\"+itemName+"</td></tr>");
		              } catch (Exception e) {
		                   e.printStackTrace();
		              }
		       }
		}
		   }
		   long end=System.currentTimeMillis();
//		   System.out.println(end-start);
	}
}*/
		/*boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		   if (!isMultipart) {
		   }
		   else{
		      FileItemFactory factory = new DiskFileItemFactory();
		      ServletFileUpload upload = new ServletFileUpload(factory);
		      List items = null;
		      try {
		          items = upload.parseRequest(request);
		          } catch (FileUploadException e) {
		               e.printStackTrace();
		          }
		      Iterator itr = items.iterator();
		      while (itr.hasNext()) {
		           FileItem item = (FileItem) itr.next();
		           if (item.isFormField()) {
		           } else {
		           try {
		              String itemName = item.getName();
		              
		              File savedFile = new File("/home/ashwanth/picsave/"+itemName);
		              item.write(savedFile);
		              out.println("<tr><td><b>Your file has been saved at the loaction:</b></td></tr><tr><td><b>"+config.getServletContext().getRealPath("/")+"uploadedFiles"+"\\"+itemName+"</td></tr>");
		              } catch (Exception e) {
		                   e.printStackTrace();
		              }
		       }
		}
		   }
		   long end=System.currentTimeMillis();
		   System.out.println(end-start);
	}*/
		
		
		
		
		
		
		
		
		
		
		
//		System.out.println("Response recived");
//		System.out.println(request.getParameter("len"));
	 /* System.out.println(request.getParameter("len"));
	  long start=System.currentTimeMillis();
	 x // Create an ArrayList object
	  
	  int noOfFile=Integer.parseInt(request.getParameter("len"));
	  Part[] Array=new Part[Integer.parseInt(request.getParameter("len"))];
	  for(int i=0;i<Array.length;i++){
		  Array[i]=request.getPart("x"+i);
	  }
	  for (Part sh:Array) {
		  String fileName = sh.getSubmittedFileName();
		  arr.add("/home/ashwanth/picsave/"+fileName);
		  for (Part part : request.getParts()) {			  
			  part.write("/home/ashwanth/picsave/"+fileName);
		  }		*/
		  
	  
	  
	  
			  
  
	/*	ApiCall obj= new ApiCall();
		String res = null;*/
//		try {   
//			res=obj.callmethod(arr);
//		} catch (InterruptedException e) {
//			 TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	 /* response.getWriter().println("The file uploaded sucessfully."+res);
	  long end=System.currentTimeMillis();*/
//	  System.out.println(end-start);
	


