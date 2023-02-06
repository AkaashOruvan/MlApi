import java.io.File;
import java.io.IOException;
import java.util.Collection;
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
            if(ResponseString!=null){
                Thread.interrupted();
            }
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://dl.zoho.com/cv/face/compare");
            httpPost.setHeader("Authorization","Bearer "+"1000.03efc0a5f767ae5d4fa9e80e81a05b6d.7ba8417aea12d6791ee555e47adc2155"
            		+ "");
            FileBody query_image = new FileBody(new File(queryImage));
            FileBody source_image =new FileBody(new File(source));
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("query_image", query_image);
            reqEntity.addPart("source_image",source_image);
            httpPost.setEntity(reqEntity);
            HttpResponse response = null;
            try {
                response = httpclient.execute(httpPost);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String ap= null;
            try {
                ap = EntityUtils.toString(response.getEntity());
//                System.out.println(ap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            JsonResponseGenerator obj=new JsonResponseGenerator();
            if(faceNotFound) {
            	if(obj.faceNotFound(ap)) {
            		while(ResponseLevel!=0){
	                    latch.countDown();
	                    ResponseLevel--;
	                  }
            	}
            	else {
            		faceNotFound=false;
            	}
            }
            double conf_level=(obj.getConfidence(ap));
            if(conf_level>0.3){
                ResponseString=this.source;
                Conf_Level=conf_level;
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
	
	
	
	
	
	
	
	
	
	
	
	public String responseMethod(Collection<Part> parts) {
		CountDownLatch latch = new CountDownLatch(parts.size());
		for (Part part : parts) {
		    if ((!part.getName().equals("file"))) {
		        continue;
		    }
		    if(part.getName().equals("file") && part.getContentType().startsWith("image/") ) {
			    String fileName = "/home/ashwanth/picsave/"+ File.separator+getFileName(part);
			    if((fileName.endsWith(".png"))|| (fileName.endsWith(".jpg")) || (fileName.endsWith(".jpeg"))){
			    System.out.println(fileName);
			    try {
					part.write(fileName);
					new WorkerThread(latch,fileName).run();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Query File not saved");
					return "{\"Message\": \"Error\"}";
//					e.printStackTrace();
				}	
			    }
		    }
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			System.out.println("Latch issue InterruptedException");
			return WorkerThread.errorMessage;
		}
		String ap=WorkerThread.ResponseString;
//        System.out.println("All worker threads have finished.");
        System.out.println(ap);
//        JSONObject main = new JSONObject();
        try {
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
        return main.toString();
        }
        catch (Exception e) {
        	return WorkerThread.errorMessage;
        }
        
		
		
	}
	private static String getFileName(Part part) {
	    for (String contentDisposition : part.getHeader("content-disposition").split(";")) {
	        if (contentDisposition.trim().startsWith("filename")) {
	            return contentDisposition.substring(contentDisposition.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}

}
class JsonResponseGenerator {
//	JSONObject jsonObject; 
    public double getConfidence(String json) {
    	JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        JsonObject data = jsonObject.getAsJsonObject("data");
        JsonObject pairs = data.getAsJsonArray("pairs").get(0).getAsJsonObject();

        double confidence = pairs.get("confidence").getAsDouble();
       return confidence;
    }
    public boolean faceNotFound(String json) {
    	String jsonString = "{\"data\": {\"query_image\": {\"0\": {\"faceCoordinate\": [\"0.315\", \"0.241\", \"0.715\", \"0.796\"]}}, \"source_image\": {\"0\": {\"faceCoordinate\": [\"0.333\", \"0.132\", \"0.778\", \"0.750\"]}}, \"pairs\": [{\"query_image\": \"0\", \"source_image\": \"0\", \"confidence\": \"-0.0759\"}]}, \"numFacesFoundQuery\": 1, \"numFacesFoundSource\": 1, \"executionMessage\": \"Faces found\", \"message\": \"success\"}";
        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(json).getAsJsonObject();
        int numFacesFoundQuery = root.get("numFacesFoundQuery").getAsInt();
        if(numFacesFoundQuery==0) {
        	return true;
        }
//        System.out.println("numFacesFoundQuery: " + numFacesFoundQuery);
    	
    	return false;
    	
    	
    }
    
}

