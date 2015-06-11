
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
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
	private final String FILE_NAME="/Users/Joker/GUIDData.csv";
	private List<Message> mssgList;
    volatile int numOfAvailMessages = 1000;
	
	public SQSClient(){
	System.out.println("HEllo world");
		mssgList = new ArrayList<Message>();
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
	
	private List<GuidMessage> fetchMessages(){
		return CSVFileReader.readCsvFile(FILE_NAME);
	}
	public void sendMessageToQueue(){
		 
		List<GuidMessage> qmssgs = fetchMessages();
		//TODO this needs to be multithreaded 
		for(GuidMessage qm: qmssgs){
			String qmString = qm.toString();
			sqsClient.sendMessage(new SendMessageRequest(QueueUrl,qmString));
		}
	}
	
	/*public void getQueueAttributes(){

		//GetQueueAttributesResult res = sqsClient.getQueueAttributes(new GetQueueAttributesRequest());
		//Map<String, String> hasAtt = res.getAttributes();
		//Iterator<Entry<String, String>> it = hasAtt.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String,String> k = (Map.Entry<String, String>)it.next();
			System.out.println("The Key is: "+k.getKey());
			System.out.println("The value is: "+k.getValue());
		}
	}*/
	
	public void receiveMessage(){
		ExecutorService service = Executors.newFixedThreadPool(10);
		List<String> strArr = new ArrayList<String>();
		strArr.add("ApproximateNumberOfMessagesNotVisible");
		strArr.add("ApproximateNumberOfMessages");
		GetQueueAttributesRequest greq = new GetQueueAttributesRequest(QueueUrl).withAttributeNames(strArr);
		GetQueueAttributesResult res = sqsClient.getQueueAttributes(QueueUrl,strArr);
		Map<String,String> attrs = res.getAttributes();
		System.out.println(attrs.toString());

		//Integer nums = Integer.parseInt(attrs.get(1));
		//System.out.println(nums);
		ReceiveMessageRequest req = new ReceiveMessageRequest(QueueUrl);
        //sets to number of messages in the queue
		System.out.println("Number of available messages"+numOfAvailMessages);
		service.execute(new Runnable(){
			public void run(){
				ReceiveMessageRequest req = new ReceiveMessageRequest(QueueUrl);



				req.setVisibilityTimeout(10);//set the visibility of messages received to 10 secs
				                             //del thread should del the message within 10 secs
				req.setMaxNumberOfMessages(10);
                while(numOfAvailMessages != 0)
                try{
					List<Message> messages = sqsClient.receiveMessage(req).getMessages();
					int size = messages.size();
                    System.out.println("the size of messages is "+size);
					synchronized(this) {
                        numOfAvailMessages -= size;
                    }
					//System.out.println(messages.get(0).getBody());
					//synchronized (mssgList){
					//	mssgList.add();
					//}
					numberOfMessagesConsumed.addAndGet(size);

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

		service.shutdown();

		
	}
	
	public static void main(String args[]){
		SQSClient sqsClient = new SQSClient();
		sqsClient.initClient();
		sqsClient.getAwsCredentials();
		//sqsClient.getQueueAttributes();
		//sqsClient.sendMessageToQueue();
		sqsClient.receiveMessage();
		try{
			Thread.sleep(15000);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
       // System.out.println(numberOfMessagesConsumed);
        System.out.println("Number of messages consumed "+numberOfMessagesConsumed);
		//sqsClient.listAllQueues();
	}
	
	
	
	
	

}
