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
	private final static String QUEUE_NAME = "input";
	private final static String OUTPUT_NAME = "output";
	private static String UUI = "";
	
	public static void main(String[] args) throws NotBoundException, IOException, TimeoutException {
		
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();

	    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

	    Consumer consumer = new DefaultConsumer(channel) {
	      @Override
	      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
	          throws IOException {
	    	  AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
						.correlationId(properties.getCorrelationId()).messageId(properties.getMessageId()).build();
	    	  
	    	  	UUI = replyProps.getCorrelationId().toString();
				String re = replyProps.getMessageId().toString();	    	  
				
				Word wordObj = (Word) SerializationUtils.deserialize(body);
	        System.out.println(" [x] Received '" + wordObj + "'");
	        try {
				getRmi(re, wordObj);
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        System.out.println("hello welcome");
	        try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }
	    };
	    channel.basicConsume(QUEUE_NAME, true, consumer);
	}
	
	public static void inQueue(String response) throws IOException, TimeoutException
	{
		ConnectionFactory factory1 = new ConnectionFactory();
	    factory1.setHost("localhost");
	    Connection connection1 = factory1.newConnection();
	    Channel channel1 = connection1.createChannel();

	    AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().correlationId(UUI)
				.build();
	    
	    channel1.queueDeclare(OUTPUT_NAME, false, false, false, null);
	    channel1.basicPublish("", OUTPUT_NAME, props, response.getBytes("UTF-8"));
	    System.out.println(props.getCorrelationId());
	    System.out.println(" [x] Sent output'" + response + "'");

	    channel1.close();
	    connection1.close();
	}
	
	public static void getRmi(String job, Word wordObj) throws NotBoundException, IOException, TimeoutException
	{
		RemoteInterface fs = (RemoteInterface) Naming.lookup("rmi://127.0.0.1:1099/dictionary");
		
		String response = "";
		
		if(job.equalsIgnoreCase("getDef"))
		{
			response = fs.getDefinition(wordObj.getWordName().toUpperCase());
		}
		else if(job.equalsIgnoreCase("addWord"))
		{
			response = fs.addWord(wordObj.getWordName().toUpperCase(), wordObj.getWordDef());
		}
		else if(job.equalsIgnoreCase("deleteWord"))
		{
			response = fs.deleteWord(wordObj.getWordName().toUpperCase());
		}
		else if(job.equalsIgnoreCase("modifyWord"))
		{
			response = fs.modifyWord(wordObj.getWordName().toUpperCase());
		}
		else if(job.equalsIgnoreCase("addModifiedWord"))
		{
			response = fs.addModifiedWord(wordObj.getWordName().toUpperCase(), wordObj.getWordDef());
		}
		
		System.out.println(response);
		inQueue(response);
	}
}
