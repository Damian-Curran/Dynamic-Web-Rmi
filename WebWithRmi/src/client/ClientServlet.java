package client;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
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
/**
 * Servlet implementation class ClientServlet
 */
@WebServlet("/ClientServlet")
public class ClientServlet extends HttpServlet implements Servlet{
	private static final long serialVersionUID = 1L;
	private final static String QUEUE_NAME = "input";
	private final static String OUTPUT_NAME = "output";
	private static String definition = "";
	private static String UUI = "";
	private static String job = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClientServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
	public void init(ServletConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			//response.sendRedirect("member.jsp");
		
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
		
		if(request.getParameter("wordDefinition") != null)
		{
			String wordName = request.getParameter("word");
			Word wordDef = new Word(wordName);
			job = "getDef";
			words(job, wordDef);
			
			request.setAttribute("wordDef", definition);
			definition = "";
			request.getRequestDispatcher("/result.jsp").forward(request,response);
			
			return;
		}
		else if(request.getParameter("addWord") != null)
		{
			String wordName = request.getParameter("word");
			String wordDef = request.getParameter("def");
			
			Word wordAdd = new Word(wordName, wordDef);
			job = "addWord";
			words(job, wordAdd);
			
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
			String wordName = request.getParameter("word");
			
			Word wordModify = new Word(wordName);
			job = "modifyWord";
			words(job, wordModify);
			
			request.setAttribute("word", wordName);
			request.setAttribute("def", definition);
			request.setAttribute("modify", "Modify");
			definition = "";
			request.getRequestDispatcher("/modifyWord.jsp").forward(request,response);
			
			return;
		}
		else if(request.getParameter("addModifiedWord") != null)
		{
			String wordName = request.getParameter("word");
			String wordDef = request.getParameter("def");
			
			Word wordAdd = new Word(wordName, wordDef);
			job = "addModifiedWord";
			words(job, wordAdd);
			
			request.setAttribute("wordDef", "Word Modified");
			definition = "";
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
		try {
			stuff(jobs, word);
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = null;
		try {
			connection = factory.newConnection();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
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
		
		//name = wordName;
	    
	    while(definition == "")
	    {
		    try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
}
