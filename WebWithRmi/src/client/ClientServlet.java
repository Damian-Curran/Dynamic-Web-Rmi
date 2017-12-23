package client;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.SerializationUtils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import server.Word;

//web servlet
@WebServlet("/ClientServlet")
public class ClientServlet extends HttpServlet implements Servlet{
	private static final long serialVersionUID = 1L;
	private final static String QUEUE_NAME = "input";
	private final static String OUTPUT_NAME = "output";
	private static String definition = "";
	private static String UUI = "";
	private static String job = "";
       
    public ClientServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//gets result of which button pressed, if pressed it wont equal null
		//page is routed to a new jsp page depending on button clicked
		if(request.getParameter("btn1") != null)
		{
			request.getRequestDispatcher("/enterWord.jsp").forward(request,response);
			return;
		}
		else if(request.getParameter("btn2") != null)
		{
			request.getRequestDispatcher("/add.jsp").forward(request,response);
			return;
		}
		else if(request.getParameter("btn3") != null)
		{
			request.getRequestDispatcher("/delete.jsp").forward(request,response);
			return;
		}
		else if(request.getParameter("btn4") != null)
		{
			request.getRequestDispatcher("/modify.jsp").forward(request,response);
			return;
		}
		
		if(request.getParameter("makeQuery") != null)
		{
			request.getRequestDispatcher("/Menu.jsp").forward(request,response);
			return;
		}
		
		//checks buttons to see which was pressed
		if(request.getParameter("wordDefinition") != null)
		{
			//gets word entered into input field 
			String wordName = request.getParameter("word");
			//creates word object with name of word entered
			Word wordDef = new Word(wordName);
			//sets job variable for later use
			job = "getDef";
			//calls words method 
			words(job, wordDef);
			
			//sets page attributes to show result, shows definition of word entered
			request.setAttribute("wordDef", definition);
			definition = "";
			request.getRequestDispatcher("/result.jsp").forward(request,response);
			
			return;
		}
		else if(request.getParameter("addWord") != null)
		{
			//method for taking word details from add.jsp
			String wordName = request.getParameter("word");
			String wordDef = request.getParameter("def");
			
			//creates word object with word name and word definition
			Word wordAdd = new Word(wordName, wordDef);
			//adds job associated with form submitted
			job = "addWord";
			words(job, wordAdd);
			
			//returns word added to let user know word has been added
			request.setAttribute("wordDef", "Word Added");
			definition = "";
			request.getRequestDispatcher("/result.jsp").forward(request,response);
			
			return;
		}
		else if(request.getParameter("wordToDelete") != null)
		{
			String wordName = request.getParameter("wordDelete");
			
			Word wordDelete = new Word(wordName);
			job = "deleteWord";
			words(job, wordDelete);
			
			request.setAttribute("wordDef", "Word Deleted");
			definition = "";
			request.getRequestDispatcher("/result.jsp").forward(request,response);
			
			return;
		}
		else if(request.getParameter("modifyWord") != null)
		{
			//takes value from input field of "word"
			String wordName = request.getParameter("word");
			
			//creates object with word input field value
			Word wordModify = new Word(wordName);
			//assigns job
			job = "modifyWord";
			//calls method 
			words(job, wordModify);
			
			//sets attributes to show word searched for and word definition associated with
			request.setAttribute("word", wordName);
			request.setAttribute("def", definition);
			definition = "";
			//loads jsp page 
			request.getRequestDispatcher("/modifyWord.jsp").forward(request,response);
			
			return;
		}
		else if(request.getParameter("addModifiedWord") != null)
		{
			//takes values from input fields
			String wordName = request.getParameter("word");
			String wordDef = request.getParameter("def");
			
			Word wordAdd = new Word(wordName, wordDef);
			job = "addModifiedWord";
			words(job, wordAdd);
			
			//lets user know word has been modified successfully 
			request.setAttribute("wordDef", "Word Modified");
			definition = "";
			//returns result page with Word modified displayed
			request.getRequestDispatcher("/result.jsp").forward(request,response);
			
			return;
		}
	}
	
	public void stuff(String jobs, Word word) throws IOException, TimeoutException
	{
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();

	    
	    UUI = UUID.randomUUID().toString();
	    System.out.println(UUI);
	    
	    AMQP.BasicProperties props = new AMQP.BasicProperties
	            .Builder()
	            .correlationId(UUI)
	            .messageId(jobs)
	            .build();
	    
	    byte[] data = SerializationUtils.serialize(word);
	    
	    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	    channel.basicPublish("", QUEUE_NAME, props, data);
	    System.out.println(" [x] Sent '" + word + "'");
	}
	
	public void words(String jobs, Word word) throws IOException
	{
		//try and catch for calling method to add task to queue
		try {
			stuff(jobs, word);
		} catch (TimeoutException e1) {
			e1.printStackTrace();
		}
		
		//creates connection to output queue
		
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = null;
		try {
			connection = factory.newConnection();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	    Channel channel = connection.createChannel();

	    channel.queueDeclare(OUTPUT_NAME, false, false, false, null);
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	    
	    Consumer consumer = new DefaultConsumer(channel) {
	      @Override
	      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
	          throws IOException {
	    	  AMQP.BasicProperties replyProps = new AMQP.BasicProperties
	                  .Builder()
	                  .correlationId(properties.getCorrelationId())
	                  .build();
	    	  
	    	  
	    	  	String rawr = replyProps.getCorrelationId().toString();
				//checks if queue correlationID string returned is showing same UUID as the one sent to input queue
				if(!UUI.equals(rawr))
				{
					System.out.println("should not be here :(");
					return;
				}
				else
				{
					System.out.println("printed from rawr");
				}
	    	  
	    	  
	    	  definition = new String(body, "UTF-8");
	        System.out.println(" [x] Received '" + definition + "'");
	      }
	    };
	    channel.basicConsume(OUTPUT_NAME, true, consumer);
	    
	    while(definition == "")
	    {
		    try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	}
}
