import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;

//import com.api.JsonResGenerator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;



import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.io.IOException;
public class ApiCall {
	 static class WorkerThread extends Thread {
	        public String source;
	        static public int ResponseLevel;
	        static public String ResponseString=null;
	        static public double Conf_Level;
	        static public boolean faceNotFound=true;
	        static public String quer;
	        private CountDownLatch latch;
	        public static String token;
	        static public boolean faceFoundInDb=false;

	        public WorkerThread(){
	        }

	        public WorkerThread(CountDownLatch latch,String source) {
	            this.latch = latch;
	            this.source=source;
	            
	        }

	        @Override
	        public void run() {
	        	
	        	//PLan b starts
	        	CloseableHttpClient httpclient = HttpClients.createDefault();
	        	HttpPost httpPost = new HttpPost("https://dl.zoho.com/cv/face/compare");
	        	httpPost.addHeader("Authorization", "Bearer " + "1000.c61d5cd78656910e344c93cbdcc58ec8.d718023e1f6cf37eaf9a61b1db8a6456");
	  //      	  FileBody query_image = new FileBody(new File("/home/ashwanth/" + "temp.png"));	
	        	File queryImageFile = new File(quer);
//	        	File queryImageFile = new File("/home/ashwanth/Desktop/rajsan.jpeg");
	        	File sourceImageFile = new File(this.source);

	        	HttpEntity entity = MultipartEntityBuilder.create()
	        	        .addBinaryBody("query_image", queryImageFile, ContentType.DEFAULT_BINARY, queryImageFile.getName())
	        	        .addBinaryBody("source_image", sourceImageFile, ContentType.DEFAULT_BINARY, sourceImageFile.getName())
	        	        .build();

             	httpPost.setEntity(entity);
	        	CloseableHttpResponse response = null;
				try {
					response = httpclient.execute(httpPost);
					 HttpEntity responseEntity = response.getEntity();
		        	 String ap=null;
		        	 ap = EntityUtils.toString(responseEntity);
						System.out.println(ap);
 			
	        	JsonResponseGenerator  obj=new JsonResponseGenerator();
	        
	                if(faceNotFound==true) {
	                	
	                	if(JsonResponseGenerator.faceNotFound(ap)) {
	                		while(ResponseLevel!=0){
	                            latch.countDown();
	                            ResponseLevel--;
	                        }           		
	                	}
	                	else {
//	                		faceFoundInDb=true;
	                		faceNotFound=false;
	                	}
	                }
	                if(faceNotFound==false) {
	                double conf_level=(obj.getConfidence(ap));
	                if(conf_level>0.5){
	                    ResponseString=this.source;
	                    WorkerThread.Conf_Level=conf_level;
	                    while(ResponseLevel!=0){
	                        latch.countDown();
	                        ResponseLevel--;
	                    }
	                }
	                // Do the work here
//	                // Decrement the count of the latch
	                latch.countDown();
	                ResponseLevel--;
//	        	    
	        	} }
				catch (Exception e) {
					// TODO: handle exception
				}
				finally {
	        	    try {
						response.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						
						e.printStackTrace();
					}
	        	}
//

//Plan b ends
	            
	            //Plan a starts
//	            HttpClient httpclient = new DefaultHttpClient();
//	            HttpPost httpPost = new HttpPost("https://dl.zoho.com/cv/face/compare");
//	            httpPost.setHeader("Authorization","Bearer "+"1000.05fc1a60d180c8e6fee4b73c4bfbb9eb.0eb847aa0b98b4a611ad3e77e5a30d19"
//	            		+ "");
////	            FileBody query_image = new FileBody(new File(quer));
//	            FileBody query_image = new FileBody(new File("/home/ashwanth/Desktop/test.jpg"));
//	            FileBody source_image =new FileBody(new File(source));
//	            MultipartEntity reqEntity = new MultipartEntity();
//	            reqEntity.addPart("query_image", query_image);
//	            reqEntity.addPart("source_image",source_image);
//	            httpPost.setEntity(reqEntity);
//	            HttpResponse response = null;
//	            try {
//	                response = httpclient.execute(httpPost);
//	            } catch (IOException e) {
//	                throw new RuntimeException(e);
//	            }
//
//	            String ap= null;
//	            try {
//	                ap = EntityUtils.toString(response.getEntity());
////	                System.out.println(ap);
//	            } catch (IOException e) {
//	                throw new RuntimeException(e);
//	            }
//	            JsonResponseGenerator obj=new JsonResponseGenerator();
//	            if(faceNotFound) {
//	            	if(obj.faceNotFound(ap)) {
//	            		while(ResponseLevel!=0){
//		                    latch.countDown();
//		                    ResponseLevel--;
//		                  }
//	            	}
//	            	else {
//	            		faceNotFound=false;
//
//	            	}
//	            }
//	            double conf_level=(obj.getConfidence(ap));
//	            if(conf_level>0.3){
//	                ResponseString=this.source;
//	                Conf_Level=conf_level;
//	                while(ResponseLevel!=0){
//	                    latch.countDown();
//	                    ResponseLevel--;
//	                }
//	            }
//	            // Do the work here
//	            // Decrement the count of the latch
//	            latch.countDown();
	        }
	        //Plan a ends
	 }
	    public static void main(String[] args) throws InterruptedException {
	         ApiCall ob=new ApiCall();
	          ob.callmethod(null);
	    }
	    public String callmethod(List<String> sourcepath) throws InterruptedException {
	    	WorkerThread.ResponseString=null;
	    	List<String> arr ;
	        String path="/home/ashwanth/Desktop/";
	        arr=sourcepath;
//	       arr = Arrays.asList("/home/ashwanth/Desktop/rajsan.jpeg","/home/ashwanth/Desktop/rock.jpg","/home/ashwanth/Desktop/test.jpg",path+"emma.jpg",path+"elizabet.jpg",path+"sabrina.jpg",path+"test1.jpg",path+"f1.jpg",path+"f2.jpg",path+"f3.jpg",path+"f4.jpg",path+"f5.jpg",path+"f6.jpg"); 
//	        arr = Arrays.asList("/home/ashwanth/Desktop/rock.jpg");
//	        	        WorkerThread.quer= "/home/ashwanth/Desktop/rock.jpg";
//	        	        WorkerThread.quer= "/home/ashwanth/Downloads/test2.jpeg";
//	        	        WorkerThread.quer= "/home/ashwanth/Desktop/rajsan.jpeg";
	        CountDownLatch latch = new CountDownLatch(arr.size());
	        WorkerThread.ResponseLevel= arr.size();
	        JSONObject main = new JSONObject();
	        try {
	        for (int i = 0; i < arr.size(); i++) {
	            new WorkerThread(latch,arr.get(i)).start();
	        }
	        // Wait for all worker threads to finish
	        latch.await();

	        String ap=WorkerThread.ResponseString;
	        JSONObject details=new JSONObject(); 		
	        if(ap!=null ) {
	        details.put("Face Found?", "YES");
	        details.put("File_Path", ap);
	        details.put("Confidence Level", WorkerThread.Conf_Level);
	        
	        }
	        else if(ap==null && WorkerThread.faceNotFound==true){ 
	        	details.put("Face Found?", "NotFoundInQuery");
	        }
	        else {
	        	details.put("Face Found?","NotFoundInSource"); 
	        }
	        main.put("Info", details);
	        main.put("Message","Success");
	        }
	        catch (Exception e) {
	        	main.put("Message", "error");
	        	
	        }
	        finally {
	        	System.out.println(main.toString());
	        	return main.toString();
	        	}
	    }	

}

class JsonResponseGenerator {
	JSONObject jsonObject; 
    public double getConfidence(String json) {
        jsonObject = new JSONObject(json);
        JSONObject arr = jsonObject.getJSONObject("data");
        JSONArray arr2 = arr.getJSONArray("pairs");
        JSONObject arr3 = arr2.getJSONObject(0);
        return Double.parseDouble(arr3.getString("confidence"));
    }
    public static boolean faceNotFound(String json) {
    	
    	JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(json).getAsJsonObject();
        int numFacesFoundQuery = root.get("numFacesFoundQuery").getAsInt();
        if(numFacesFoundQuery==0 || numFacesFoundQuery>1 ) {
        	System.out.println(numFacesFoundQuery);
        	
        	return true;
        }
//        System.out.println("numFacesFoundQuery: " + numFacesFoundQuery);
    	
    	return false;
    	
    	
    }
    
}
