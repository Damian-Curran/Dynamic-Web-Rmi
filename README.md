# Dynamic-Web-Rmi

In this repository we will be taking a look at making a dynamic web application in eclipse.
The end result of this project will for a user to be able to get the definition of a word using a dictionary service.
The features we aim to add are: Adding new word, Deleting word and Modifying a word.

The main technologies used to achieve our wanted outcome are: Eclipse, Apache Tomcat and lastly RabbitMQ.

We will be focusing on using Java RMI(Remote Method Invocation), a Java API that lets us perform remote method invoaction.

![](Images/rmi-2.png)

Rmi can be seen in Client, Server and ServerSetup in this project.

RabbitMQ is an open source message broker. We use it for adding tasks to a queue.
Hopefully you will have a better understanding of RabbitMQ with the use of the image below.

![](Images/rabbitmq.png)

## Dictionary
The dictionary we use is the Webster's Unabridged Dictionary by Various and can be found at http://www.gutenberg.org/ebooks/29765.

