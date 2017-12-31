package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

public class MinHash implements JobProcessor {

	private Document document;
	private Results results;
	private static String dbFile = "MinHash.d4o";
	private int shingles = 300;
	private static ArrayBlockingQueue<String> servLog;
	private List<Document> documents = new ArrayList<>();
	private ObjectContainer db;
	private boolean alreadyExist = false; 
	
	public MinHash() 
	{
		servLog = WorkersHandler.getServLog();
	}

	@Override
	public Results processJob(Job job) 
	{
		document = new Document(job.getTitle());
		results = new Results(job.getJobNumber(), job.getTitle());
		try {
			retreiveDocuments();
			Set<String> words = getWords(job.getDocument());
			Set<Integer> hashes = getHashes(words);
			document.setHashFunctions(getHashFunctions());
			generateMinHashes(hashes, document.getHashFunctions());
			// Compare document against documents
			for(Document tmpDocument : documents)
			{
				Set<Integer> retainAll = new TreeSet<>(document.getMinHashes());
				retainAll.retainAll(tmpDocument.getMinHashes());
				double similarity = (double) retainAll.size() / shingles * 100;
				// Prevent duplicate saving
				if(similarity == 100.0 && job.getTitle().equals(tmpDocument.getTitle())) alreadyExist = true;
				results.addResult(tmpDocument.getTitle(), String.valueOf(similarity));
			}
			if(!alreadyExist)	//Prevent duplicate saving
			{
				db = Db4oEmbedded.openFile(dbFile);
				db.store(document);
				servLog.offer("Document saved.");
				db.close();				
			}
			// compare minHashes of every document and add each Result to Results object.
		} catch (IOException e) {
			servLog.offer(String.format("MinHash caused exception processing %s Error: %s", job.getTitle(), e.getMessage()));
		}
		
		return results;
	}

	private void retreiveDocuments()
	{
		db = Db4oEmbedded.openFile(dbFile);
		Query query = db.query();
		query.constrain(Document.class);
		ObjectSet<Document> result = query.execute();
		for (Object document : result)
		{
			documents.add((Document) document);
		}
		db.close();
	}
	
	private Set<String> getWords(BufferedReader document) throws IOException
	{
		String line;
		Set<String> allWords = new TreeSet<>();
		while((line = document.readLine()) != null)
		{
			allWords.addAll(new ArrayList<String>(Arrays.asList(line.split(" "))));
		}
		return allWords;
	}
	
	private Set<Integer> getHashes(Set<String> words)
	{
		int minHash = Integer.MAX_VALUE;
		Set<Integer> wordsHashes = new TreeSet<>();
		for (String word : words)
		{
			int hash = word.hashCode();
			wordsHashes.add(hash);
			if(minHash < hash) minHash = hash;
		}
		document.addMinHash(minHash);
		return wordsHashes;
	}
	
	private Set<Integer> getHashFunctions() 
	{
	    // get 299 numbers from db40 or generate random 299 numbers
	    // Check db40 for existing set of hashFunctions. All documents has to be hashed with same HashFunctions!
		Set<Integer> hashes = new TreeSet<Integer>();
		if(documents.isEmpty())
		{
			Random random = new Random();
			for (int i = 1; i < shingles; i++)	//First one is there already from .hashCode function
			{ //Create k random integers
				hashes.add(random.nextInt());
			}			
		} else hashes = documents.get(0).getHashFunctions();
	    return hashes;
	}
	
	private void generateMinHashes(Set<Integer> hashes, Set<Integer> hashFunctions)
	{
		//generate 299 minHashes. One from every Hash function.
	    for (int hashFunction : hashFunctions)
	    {	
			int min = Integer.MAX_VALUE;
			for (Integer hash : hashes)
			{
			    int minHash = hash ^ hashFunction; //Bitwise XOR the word hashCode with the hashFunction
			    if (minHash < min) min = minHash;
			}
			document.addMinHash(min); //Only store the word with the minimum hash value for each hash function
	    } 
	}

}
