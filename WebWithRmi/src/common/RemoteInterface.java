package common;

import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote {
	public String getDefinition(String name) throws RemoteException, FileNotFoundException;
	public String addWord(String name, String def) throws RemoteException, FileNotFoundException;
	public String deleteWord(String wordName) throws RemoteException, FileNotFoundException;
	public String modifyWord(String word) throws RemoteException, FileNotFoundException;
	public String addModifiedWord(String name, String def) throws RemoteException, FileNotFoundException;
}
