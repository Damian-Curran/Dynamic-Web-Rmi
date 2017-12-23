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
	//private map to be used inside class 
	private Map<String, String> dictionary = new HashMap<String, String>();
	
	//throws remote exception as do all methods 
	//cause of implementing remote, using rmi
	protected Server() throws RemoteException {
		super();
	}
	
	@Override
	public String getDefinition(String name) throws RemoteException, FileNotFoundException {
		String def = "";
		dictionary = readDic();
		
		//sleeps function to act as delay for pinging server
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//checks map for key value and returns value associated with
		if(dictionary.containsKey(name))
		{
			def = dictionary.get(name);
		}
		
		return def;
	}

	//add word function
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
		
		//checks if map already has keyvalue
		//if it does it adds the new definition onto the end of the old
		//if it doesnt, it creates new key value and value association
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
	
	//function which reads dictionary in, seperating key words from definitions
	public Map<String, String> readDic()
	{
		//values needed for method functionality
		String line = "";
		String wordName = "";
		String wordDef = "";
		String[] words = null;
		boolean entered = true;
		String dic = "";
		
		//create bufferreader of input stream from class folder
		BufferedReader  reader = new BufferedReader(new InputStreamReader(Server.class.getResourceAsStream("Dictionary.txt")));
		
		//reads line if there are more to be read
		try {
			while ((line = reader.readLine()) != null)
			{
				//splits words if not empty, this allows for .length to be used to count words in line
				if(line != "")
				{
					words = line.split("\\s+");		
				}
				
				//if word is of length 1, being a key word and does not contain a ., doesnt equal null and contains an alphabetic letter
				//line.matches(".*[a-zA-Z]+.*") needed to be used as dictionary would not read properly otherwise, it was getting 
				//stuck on a character
				if(words.length == 1 && !line.contains(".") && words[0] != "" && words[0] != null && line != "" && line.matches(".*[a-zA-Z]+.*"))
				{
					//checks if entered previously
					//first time does not enters, next time does
					if(entered == false)
					{
						//if dictionary contains word, enter
						if(dictionary.containsKey(wordName))
						{
							//gets value associated with key value and adds new value to end
							dic = dictionary.get(wordName);
							dic += " " + wordDef;
							dictionary.put(wordName, dic);
							//sets wordDef back to empty
							wordDef = "";
						}
						else
						{
							//if word not contained in dictionary then enter 
							dictionary.put(wordName, wordDef);
							wordDef = "";
						}
					}
					//key value = line from dictionary
					wordName = line;
					entered = true;
				}
				else
				{
					//enter if words.length is > 1, meaning not key term, implying definition
					//keeps entering here until next sentence with only 1 word
					wordDef += " " + line;
					entered = false;
				}
			}
			//closes reader
			reader.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//returns map dictionary
		return dictionary;
	}

	//method for deleting terms from dictionary
	@Override
	public String deleteWord(String wordName) throws RemoteException, FileNotFoundException {
		String oldDef = "";
		dictionary = readDic();
		
		//sleeps to act as if server pinging
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//if dictionary has wordName then delete it and its associated value
		if(dictionary.containsKey(wordName))
		{
			dictionary.remove(wordName);
		}
		
		return oldDef;
	}

	//method for modifying words
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
		
		//if dictionary contains word then return value associated with
		if(dictionary.containsKey(word))
		{
			oldDef = dictionary.get(word);
		}
		
		return oldDef;
	}

	//difference between addWord and addModifiedWord
	//this method allows the user to save the word they modified in dictionary
	//when saved, it does not add previous value, only replaces
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
