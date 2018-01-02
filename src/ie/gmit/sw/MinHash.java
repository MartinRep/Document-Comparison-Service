package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class MinHash{

	private Document document;
	private Results results;
	//private static String dbFile = "MinHashEncrypt.Xtea";
	private int shingles;
	private static ArrayBlockingQueue<String> servLog;
	private List<Document> documents;
	//private static final String password = "Top secret";
	private DocumentDAO db;
	private ProcessDocument pd;
	
	public MinHash(DocumentDAO db, int shingles) 
	{
		//servLog = WorkersHandler.getServLog();
		//db = (DocumentDAO) new Db4oController(dbFile, password);
		this.db = db;
		this.shingles = shingles;
		pd = new ProcessDocument();
	}
	
	public void setLogging(ArrayBlockingQueue<String> servLog)
	{
		db.setLogging(servLog);
	}

	public Results processJob(Job job) 
	{
		document = new Document(job.getTitle());
		results = new Results(job.getJobNumber(), job.getTitle());
		try {
			documents = retreiveDocuments();
			Set<String> words = getWords(job.getDocument());
			Set<Integer> hashes = getHashes(words);
			document.setHashFunctions(getHashFunctions(shingles));
			document.setMinHashes(getMinHashes(hashes, document.getHashFunctions()));
			// Compare document against documents
			results.addResults(pd.compareDocument(document, documents));
			if(!pd.isAlreadySaved())	//Prevent duplicate saving
			{
				db.storeDocument(document);	
			}
		} catch (IOException e) {
			servLog.offer(String.format("MinHash caused exception processing %s Error: %s", job.getTitle(), e.getMessage()));
		}
		return results;
	}

	public List<Document> retreiveDocuments()
	{
		return db.getDocuments();
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
	    return pd.getHashFunctions(numOfHashes);
	}
	
	public Set<Integer> getMinHashes(Set<Integer> hashes, Set<Integer> hashFunctions)
	{
		return pd.getMinHashes(hashes, hashFunctions); 
	}

}
