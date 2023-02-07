import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.Part;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



public class ZiaApiCall {
	
	static class WorkerThread extends Thread {
		public static String errorMessage="{\"Message\": \"Error\"}";
        public String source;
        static public int ResponseLevel;
        static public String ResponseString=null;
        static public double Conf_Level;
        static public boolean faceNotFound=true;
        
        private CountDownLatch latch;
        public static String queryImage;
        

        public WorkerThread(){
        }

        public WorkerThread(CountDownLatch latch,String source) {
            this.latch = latch;
            this.source=source;     
        }

        @Override
        public void run() {
         
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://dl.zoho.com/cv/face/compare");
            httpPost.setHeader("Authorization","Bearer "+"1000.d7e9b712f3667dc43d8a11dc29063db5.2f4ff8df9e176cc74b45267b7d8e72a2"
            		+ "");
            FileBody query_image = new FileBody(new File(queryImage));
            FileBody source_image =new FileBody(new File(source));
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("query_image", query_image);
            reqEntity.addPart("source_image",source_image);
            httpPost.setEntity(reqEntity);
            HttpResponse response = null;
            String ap= null;
            try {
                response = httpclient.execute(httpPost);
           
     
         
                ap = EntityUtils.toString(response.getEntity());
                System.out.println(ap);
            }
//                System.out.println(ap);
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            JsonResponseGenerator obj=new JsonResponseGenerator();
//            if(faceNotFound) {
//            	if(obj.faceNotFound(ap)) {
//            		while(ResponseLevel!=0){
//	                    latch.countDown();
//	                    ResponseLevel--;
//	                  }
//            	}
//            	else {
//            		faceNotFound=false;
//            	}
//            }
            double conf_level=(obj.getConfidence(ap));
            System.out.println(conf_level);
            if(conf_level>0.3){
                ResponseString=this.source;
                Conf_Level=conf_level;
                System.out.println(ResponseLevel);
                while(ResponseLevel!=0){
                    latch.countDown();
                    ResponseLevel--;
                }
            }
            // Do the work here
            // Decrement the count of the latch
            latch.countDown();
        }
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String responseMethod(Collection<Part> parts) throws InterruptedException {
		

		CountDownLatch latch = new CountDownLatch(parts.size());

		List<String> arr = new ArrayList<String>(); 
		
		for (Part part : parts) {
	
		    if ((!part.getName().equals("file"))) {
		        continue;
		    }
		    if(part.getName().equals("file") && part.getContentType().startsWith("image/") ) {
			    String fileName = "/home/ashwanth/picsave/"+ getFileName(part);

			    if((fileName.endsWith(".png"))|| (fileName.endsWith(".jpg")) || (fileName.endsWith(".jpeg"))){

			    try {
					part.write(fileName);
					System.out.println("Thread Started");
					arr.add(fileName);				
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Query File not saved");

//				}	
//			    }
		    }
		}
		    }}
		System.out.println("WOrkdone");
		for(int i=0;i<arr.size();i++) {
			new WorkerThread(latch,arr.get(i)).start();
		}
		
		
//		try {
		latch.await();
//		} catch (InterruptedException e) {
//			System.out.println("Latch issue InterruptedException");
//			return WorkerThread.errorMessage;
//		}
		String ap=WorkerThread.ResponseString;
//        System.out.println("All worker threads have finished.");
        System.out.println(ap);
//        JSONObject main = new JSONObject();
//        try {
        JSONObject details=new JSONObject(); 
        JSONObject main = new JSONObject();
        if(ap!=null) {
        details.put("Face Found?", "YES");
        details.put("File_Path", ap);
        details.put("Confidence Level", WorkerThread.Conf_Level);
        }
        else {
        	details.put("Face Found?", "NO");
        }
        main.put("Info", details);
      
        main.put("Message","Success");
        return main.toString();}
//        }
//        catch (Exception e) {
//        	return WorkerThread.errorMessage;
//        }
        
		
		
	
	private static String getFileName(Part part) {
	    for (String contentDisposition : part.getHeader("content-disposition").split(";")) {
	        if (contentDisposition.trim().startsWith("filename")) {
	            return contentDisposition.substring(contentDisposition.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}

}
//class JsonResponseGenerator {
//
//    public double getConfidence(String json) {
//    	JsonParser parser = new JsonParser();
//        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
//        JsonObject data = jsonObject.getAsJsonObject("data");
//        JsonObject pairs = data.getAsJsonArray("pairs").get(0).getAsJsonObject();
//
//        double confidence = pairs.get("confidence").getAsDouble();
//       return confidence;
//    }
//    public boolean faceNotFound(String json) {
//    	String jsonString = "{\"data\": {\"query_image\": {\"0\": {\"faceCoordinate\": [\"0.315\", \"0.241\", \"0.715\", \"0.796\"]}}, \"source_image\": {\"0\": {\"faceCoordinate\": [\"0.333\", \"0.132\", \"0.778\", \"0.750\"]}}, \"pairs\": [{\"query_image\": \"0\", \"source_image\": \"0\", \"confidence\": \"-0.0759\"}]}, \"numFacesFoundQuery\": 1, \"numFacesFoundSource\": 1, \"executionMessage\": \"Faces found\", \"message\": \"success\"}";
//        JsonParser parser = new JsonParser();
//        JsonObject root = parser.parse(json).getAsJsonObject();
//        int numFacesFoundQuery = root.get("numFacesFoundQuery").getAsInt();
//        if(numFacesFoundQuery==0) {
//        	return true;
//        }
////        System.out.println("numFacesFoundQuery: " + numFacesFoundQuery);
//    	
//    	return false;
//    	
//    	
//    }
//    
//}
//
