package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import common.RemoteInterface;

public class Server extends UnicastRemoteObject implements  RemoteInterface{
	private static final long serialVersionUID = 1L;
	private Map<String, String> dictionary = new HashMap<String, String>();
	
	protected Server() throws RemoteException {
		super();
	}
	
	@Override
	public String getDefinition(String name) throws RemoteException, FileNotFoundException {
		String def = "";
		dictionary = readDic();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(dictionary.containsKey(name))
		{
			def = dictionary.get(name);
		}
		
		return def;
	}

	@Override
	public String addWord(String name, String def) throws RemoteException, FileNotFoundException {
		String oldDef = "";
		dictionary = readDic();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(dictionary.containsKey(name))
		{
			oldDef = dictionary.get(name);
			oldDef += " " + def;
			dictionary.put(name, oldDef);
		}
		else if(!dictionary.containsKey(name))
		{
			dictionary.put(name, def);
		}
		
		return oldDef;
	}
	
	public Map<String, String> readDic()
	{
		String line = "";
		String wordName = "";
		String wordDef = "";
		String[] words = null;
		boolean entered = true;
		String dic = "";
		
		BufferedReader  reader = new BufferedReader(new InputStreamReader(Server.class.getResourceAsStream("Dictionary.txt")));
		
		try {
			while ((line = reader.readLine()) != null)
			{
				//if(line.equals(wordName+"."))
				//continue;

				if(line != "")
				{
					words = line.split("\\s+");		
				}
				
				if(words.length == 1 && !line.contains(".") && words[0] != "" && words[0] != null && line != "" && line.matches(".*[a-zA-Z]+.*"))
				{
					//for(String e : words)
					//{
					//	System.out.println(e);
					//}
					
					if(entered == false)
					{
						if(dictionary.containsKey(wordName))
						{
							dic = dictionary.get(wordName);
							dic += " " + wordDef;
							dictionary.put(wordName, dic);
							//System.out.println(wordName);
							wordDef = "";
						}
						else
						{
							dictionary.put(wordName, wordDef);
							wordDef = "";
						}
					}
					
					wordName = line;
					entered = true;
				}
				else
				{
					wordDef += " " + line;
					entered = false;
				}
			}
			reader.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return dictionary;
	}

	@Override
	public String deleteWord(String wordName) throws RemoteException, FileNotFoundException {
		String oldDef = "";
		dictionary = readDic();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(dictionary.containsKey(wordName))
		{
			dictionary.remove(wordName);
		}
		else if(!dictionary.containsKey(wordName))
		{
			
		}
		
		return oldDef;
	}

	@Override
	public String modifyWord(String word) throws RemoteException, FileNotFoundException {
		
		String oldDef = "";
		dictionary = readDic();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(dictionary.containsKey(word))
		{
			oldDef = dictionary.get(word);
		}
		
		return oldDef;
	}

	@Override
	public String addModifiedWord(String name, String def) throws RemoteException, FileNotFoundException {
		dictionary = readDic();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(dictionary.containsKey(name))
		{
			dictionary.put(name, def);
		}
		else if(!dictionary.containsKey(name))
		{
			dictionary.put(name, def);
		}
		
		return def;
	}
}
