package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MinHash{

	private DocumentDAO db;
	private ProcessDocument pd;
	
	public MinHash(DocumentDAO db) 
	{
		this.db = db;
		pd = new ProcessDocument();
	}

	public List<Document> retreiveDocuments()
	{
		return db.getDocuments();
	}
	
	public void storeDocument(Document document)
	{
		db.storeDocument(document);
	}
	
	public Set<String> getWords(BufferedReader document) throws IOException
	{
		return pd.getWords(document);
	}
	
	public Set<Integer> getHashes(Set<String> words)
	{	
		return pd.getHashes(words);
	}
	
	public Set<Integer> getHashFunctions(int numOfHashes) 
	{
		Set<Integer> hashFunctions = pd.getHashFunctions(numOfHashes);
		hashFunctions.add(pd.getMinHash());
	    return hashFunctions;
	}
	
	public Set<Integer> getMinHashes(Set<Integer> hashes, Set<Integer> hashFunctions)
	{
		return pd.getMinHashes(hashes, hashFunctions); 
	}
	
	public HashMap<String, String> compareDocument(Document document, List<Document> documents)
	{
		return pd.compareDocument(document, documents);
	}
	
	public boolean isAlreadySaved()
	{
		return pd.isAlreadySaved();
	}

}
