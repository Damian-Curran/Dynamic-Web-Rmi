package server;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.SerializationUtils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import common.RemoteInterface;

public class Client {
	// set queue names, to add and take from
	private final static String QUEUE_NAME = "input";
	private final static String OUTPUT_NAME = "output";
	// variable to hold the unique number received from clientServlet
	private static String UUI = "";

	public static void main(String[] args) throws NotBoundException, IOException, TimeoutException {
		
		//creates connection factory
		ConnectionFactory factory = new ConnectionFactory();
		//sets the host to be used
	    factory.setHost("localhost");
	    //creates connection to the factory
	    Connection connection = factory.newConnection();
	    //creates channel from connection to be used
	    Channel channel = connection.createChannel();
	    
	    //uses channel to declare a new queue
	    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

	    //creates a defaultconsumer
	    Consumer consumer = new DefaultConsumer(channel) {
	      //we override the method to manipulate variables
	      @Override
	      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
	          throws IOException {
	    	  //manipulate AMQP basic properties to get message id stored and correlation id
	    	  AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
						.correlationId(properties.getCorrelationId()).messageId(properties.getMessageId()).build();
	    	  
	    	  	UUI = replyProps.getCorrelationId().toString();
				String re = replyProps.getMessageId().toString();	    	  
				
				//deserialize object back to word to allow access to variables stored
				Word wordObj = (Word) SerializationUtils.deserialize(body);
	        System.out.println(" [x] Received '" + wordObj + "'");
	        try {
				getRmi(re, wordObj);
			} catch (NotBoundException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
	        //sleeps thread to run every 10 seconds to act as "polling" effect, for the client to check when function returns value
	        try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	      }
	    };
	    channel.basicConsume(QUEUE_NAME, true, consumer);
	}
	
	//method used for adding item to the queue
	public static void inQueue(String response) throws IOException, TimeoutException {
		ConnectionFactory factory1 = new ConnectionFactory();
		factory1.setHost("localhost");
		Connection connection1 = factory1.newConnection();
		Channel channel1 = connection1.createChannel();

		//sets props correlationId to be that of the ID received from the publisher(clientServlet)
		AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().correlationId(UUI).build();

		channel1.queueDeclare(OUTPUT_NAME, false, false, false, null);
		channel1.basicPublish("", OUTPUT_NAME, props, response.getBytes("UTF-8"));
		System.out.println(props.getCorrelationId());
		System.out.println(" [x] Sent output'" + response + "'");

		channel1.close();
		connection1.close();
	}
	
	//method which uses remote method invocation on server
	public static void getRmi(String job, Word wordObj) throws NotBoundException, IOException, TimeoutException {
		//looks up locally for a resource binded as "dictionary"
		RemoteInterface fs = (RemoteInterface) Naming.lookup("rmi://127.0.0.1:1099/dictionary");

		String response = "";

		//if/else statements to point program in right direction depending on what the user wanted to do
		if (job.equalsIgnoreCase("getDef")) {
			response = fs.getDefinition(wordObj.getWordName().toUpperCase());
		} else if (job.equalsIgnoreCase("addWord")) {
			response = fs.addWord(wordObj.getWordName().toUpperCase(), wordObj.getWordDef());
		} else if (job.equalsIgnoreCase("deleteWord")) {
			response = fs.deleteWord(wordObj.getWordName().toUpperCase());
		} else if (job.equalsIgnoreCase("modifyWord")) {
			response = fs.modifyWord(wordObj.getWordName().toUpperCase());
		} else if (job.equalsIgnoreCase("addModifiedWord")) {
			response = fs.addModifiedWord(wordObj.getWordName().toUpperCase(), wordObj.getWordDef());
		}

		System.out.println(response);
		//response returned from method call is put into the queue with this method call
		inQueue(response);
	}
}
