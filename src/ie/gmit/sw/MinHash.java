package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class MinHash implements JobProcessor {

	private Document document;
	private Results results;
	private static String dbFile = "MinHashEncrypt.Xtea";
	private int shingles = 300;
	private static ArrayBlockingQueue<String> servLog;
	private List<Document> documents;
	private static final String password = "Top secret";
	private DocumentDAO db;
	private ProcessDocument pd;
	
	public MinHash() 
	{
		servLog = WorkersHandler.getServLog();
		db = (DocumentDAO) new Db4oImp(dbFile, password);
		db.setLogging(servLog);
		pd = new ProcessDocument();
	}

	@Override
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

	private List<Document> retreiveDocuments()
	{
		return db.getDocuments();
	}
	
	private Set<String> getWords(BufferedReader document) throws IOException
	{
		return pd.getWords(document);
	}
	
	private Set<Integer> getHashes(Set<String> words)
	{	
		return pd.getHashes(words);
	}
	
	private Set<Integer> getHashFunctions(int numOfHashes) 
	{
	    return pd.getHashFunctions(numOfHashes);
	}
	
	private Set<Integer> getMinHashes(Set<Integer> hashes, Set<Integer> hashFunctions)
	{
		return pd.getMinHashes(hashes, hashFunctions); 
	}

}
