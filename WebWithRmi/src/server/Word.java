package server;

import java.io.Serializable;

public class Word implements Serializable{
	private static final long serialVersionUID = 1L;
	private String wordName = "";
	private String wordDef = "";

	public Word(String name, String def)
	{
		wordName = name;
		wordDef = def;
	}

	public Word(String name)
	{
		wordName = name;
	}

	public String getWordName() {
		return wordName;
	}

	public void setWordName(String wordName) {
		this.wordName = wordName;
	}

	public String getWordDef() {
		return wordDef;
	}

	public void setWordDef(String wordDef) {
		this.wordDef = wordDef;
	}
}