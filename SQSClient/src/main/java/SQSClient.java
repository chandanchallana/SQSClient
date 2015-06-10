
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.metrics.RequestMetricCollector;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;



/**
 * This is multithreaded sample of receiving messages from Simple Queue Service
 * using AWS java sdk.
 * 
 * @author Joker
 * 
 */
public class SQSClient {
	
	public static AtomicInteger numberOfMessagesConsumed = new AtomicInteger();
	private AWSCredentials credentials;
	private AmazonSQS sqsClient;
	private final String QueueUrl="https://sqs.us-west-2.amazonaws.com/671732872341/MyTestingQueue";
	private final String FILE_NAME="/Users/Joker/state_table.csv";
	public static CountDownLatch latch = new CountDownLatch(1);
	
	public SQSClient(){
	System.out.println("HEllo world");
	numberOfMessagesConsumed.set(0);
	
	
	//init a sqs client 
	  //getAwsCredentials();
	}
	/**
	 * The function throws a client exception if the credentials are not met
	 * @throws AmazonClientException
	 */
	public void getAwsCredentials() throws AmazonClientException{
		try{ 
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		}catch(Exception e){
			System.out.println(e.getMessage());
			throw new AmazonClientException("Cannot load credentials from credentials profile");
		}
	}
	
	public void initClient(){
		sqsClient = new AmazonSQSClient(credentials);
		Region uswest2 = Region.getRegion(Regions.US_WEST_2);
		sqsClient.setRegion(uswest2);
	}
	
	public void listAllQueues(){
		try{
			for(String url : sqsClient.listQueues().getQueueUrls()){
				System.out.println(" QueueUrl: "+url);
			}
		}catch(AmazonServiceException se){
			System.out.println("There was a service exception: "+se.getMessage());
		}catch(AmazonClientException ce){
			System.out.println("there was a client exception: "+ce.getMessage());		
		}
	}
	
	private List<QueueMessage> fetchMessages(){
		return CSVFileReader.readCsvFile(FILE_NAME);
	}
	public void sendMessageToQueue(){
		 
		List<QueueMessage> qmssgs = fetchMessages();
		//TODO this needs to be multithreaded 
		for(QueueMessage qm: qmssgs){
			String qmString = qm.toString();
			sqsClient.sendMessage(new SendMessageRequest(QueueUrl,qmString));
		}
	}
	
	public void getQueueAttributes(){

		GetQueueAttributesResult res = sqsClient.getQueueAttributes(new GetQueueAttributesRequest());
		Map<String, String> hasAtt = res.getAttributes();
		Iterator<Entry<String, String>> it = hasAtt.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String,String> k = (Map.Entry<String, String>)it.next();
			System.out.println("The Key is: "+k.getKey());
			System.out.println("The value is: "+k.getValue());
		}
	}
	
	public void receiveMessage(){
		ExecutorService service = Executors.newFixedThreadPool(10);
		
		
		for(int i=0;i <10;i++){
		service.execute(new Runnable(){
			public void run(){
				try{
					List<Message> messages = sqsClient.receiveMessage(new ReceiveMessageRequest(QueueUrl)).getMessages();
					System.out.println(messages.get(0).getBody());
					numberOfMessagesConsumed.getAndIncrement();
				}catch(AmazonServiceException ase){
					System.out.println("Caught an AmazonServiceException, which means your request made it " +
		                    "to Amazon SQS, but was rejected with an error response for some reason.");
		            System.out.println("Error Message:    " + ase.getMessage());
		            System.out.println("HTTP Status Code: " + ase.getStatusCode());
		            System.out.println("AWS Error Code:   " + ase.getErrorCode());
		            System.out.println("Error Type:       " + ase.getErrorType());
		            System.out.println("Request ID:       " + ase.getRequestId());
				}catch(AmazonClientException ace){
					System.out.println("Caught an AmazonClientException, which means the client encountered " +
		                    "a serious internal problem while trying to communicate with SQS, such as not " +
		                    "being able to access the network.");
		            System.out.println("Error Message: " + ace.getMessage());
				}
			}
		});
		}
		service.shutdown();
		
	}
	
	public static void main(String args[]){
		SQSClient sqsClient = new SQSClient();
		sqsClient.initClient();
		sqsClient.getAwsCredentials();
		sqsClient.getQueueAttributes();
		//sqsClient.sendMessageToQueue();
		//sqsClient.receiveMessage();
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		System.out.println("Number of messages consumed "+numberOfMessagesConsumed);
		//sqsClient.listAllQueues();
	}
	
	
	
	
	

}
