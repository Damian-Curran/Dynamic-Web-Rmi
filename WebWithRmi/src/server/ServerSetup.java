package server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import common.RemoteInterface;

public class ServerSetup {
	public static void main(String[] args) throws Exception{
		//Create an instance of a Server. As Server implements the RemoteInterface
		//interface, it can be referred to as a RemoteInterface type.
		RemoteInterface fs = new Server();

		//Start the RMI registry on port 1099
		LocateRegistry.createRegistry(1099);

		//Bind our remote object to the registry with the human-readable name "dictionary"
		Naming.rebind("dictionary", fs);

		//Print a message to standard output
		System.out.println("Server ready.");
	}
}
