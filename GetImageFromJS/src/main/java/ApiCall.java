import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
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
	        static public String ResponseString;
	        static public double Conf_Level;
	        static public String quer;
	        private CountDownLatch latch;

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
	            httpPost.setHeader("Authorization","Bearer "+"1000.d1dc54a31977823064c8f0048a877351.8f3193156087d971ca5d9683e36176e5\n"
	            		+ "");
	            FileBody query_image = new FileBody(new File(quer));
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
//	                System.out.println(ap);
	            } catch (IOException e) {
	                throw new RuntimeException(e);
	            }

	            JsonResponseGenerator obj=new JsonResponseGenerator();
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
	    public static void main(String[] args) throws InterruptedException {
	         ApiCall ob=new ApiCall();
	         ob.callmethod(null,null);
	    }
	    public String callmethod(List<String> sourcepath,String query_image) throws InterruptedException {
	    	List<String> arr ;
//	        String path="/home/ashwanth/Desktop/";
	        arr=sourcepath;

	        WorkerThread.quer=query_image;
//	      x`  arr = Arrays.asList("/home/ashwanth/Desktop/rajsan.jpeg","/home/ashwanth/Desktop/rock.jpg","/home/ashwanth/Desktop/test.jpg",path+"emma.jpg",path+"elizabet.jpg",path+"sabrina.jpg",path+"test1.jpg",path+"f1.jpg",path+"f2.jpg",path+"f3.jpg",path+"f4.jpg",path+"f5.jpg",path+"f6.jpg");
	       
//	        WorkerThread.quer= "/home/ashwanth/Desktop/rajsan.jpeg";
	        CountDownLatch latch = new CountDownLatch(arr.size());
	        WorkerThread.ResponseLevel= arr.size();
	        for (int i = 0; i < arr.size(); i++) {
	            new WorkerThread(latch,arr.get(i)).start();
	        }
	        // Wait for all worker threads to finish
	        latch.await();
//	        WorkerThread obj= new WorkerThread();
//	        String po=obj.source;
	        String ap=WorkerThread.ResponseString;
//	        System.out.println("All worker threads have finished.");
	        System.out.println(ap);
	        JSONObject main = new JSONObject();
	        main.put("Confidence Level", WorkerThread.Conf_Level);
	        main.put("File_Path", ap);
//	        JSONObject user = new JSONObject();
//	        user.put("FirstName", "John");
//	        user.put("LastName", "Reese");
//	        main.put("User", user);
//	        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//	        for(Thread th: threadSet){
//	            System.out.println(th.getName());
//	            th.stop();
//	        }
	        return main.toString();
	    }

}
class JsonResponseGenerator {
    String respone;

    public double getConfidence(String json) {
        JSONObject jsonObject = new JSONObject(json);
        JSONObject arr = jsonObject.getJSONObject("data");
        JSONArray arr2 = arr.getJSONArray("pairs");
        JSONObject arr3 = arr2.getJSONObject(0);
        return Double.parseDouble(arr3.getString("confidence"));
    }
    public void print(String message){
        System.out.println(message);
    }
}
