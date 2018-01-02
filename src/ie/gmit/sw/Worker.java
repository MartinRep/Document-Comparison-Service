package ie.gmit.sw;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Worker implements Runnable 
{

	private static ArrayBlockingQueue<Job> inQueue;
	private static ConcurrentHashMap<Integer, Results> outQueue;
	private static ArrayBlockingQueue<String> servLog;
	private int workerNumber;
	private Job job;
	private Document document;
	private List<Document> documents;
	private Results results;

	@Override
	public void run() 
	{
		workerNumber = Util.getWorkerNumber();
		inQueue = Util.getInQueue();
		outQueue = Util.getOutQueue();
		servLog = Util.getServLog();
		try {
			job = inQueue.take();
			logMessage(String.format("Worker number %d start work on job number: %d", workerNumber, job.getJobNumber()));
			MinHash minHash = new MinHash(Util.getDb());
			try {
				documents = minHash.retreiveDocuments();
				document = new Document(job.getTitle());
				results = new Results(job.getJobNumber(), job.getTitle());
				Set<String> words = minHash.getWords(job.getDocument());
				Set<Integer> hashes = minHash.getHashes(words);
				if(documents.isEmpty())
				{
					document.setHashFunctions(minHash.getHashFunctions(Util.getShingles()));					
				} else
				{
					document.setHashFunctions(documents.get(0).getHashFunctions());
				}
				document.setMinHashes(minHash.getMinHashes(hashes, document.getHashFunctions()));
				// Compare document against documents
				results.addResults(minHash.compareDocument(document, documents));
				//add Result into outQueue
				outQueue.put(job.getJobNumber(), results);
				if(!minHash.isAlreadySaved())	//Prevent duplicate saving
				{
					minHash.storeDocument(document);	
				}
			} catch (IOException e) {
				logMessage(String.format("MinHash caused exception processing %s Error: %s", job.getTitle(), e.getMessage()));
			}
		} catch (InterruptedException e) {
			logMessage(String.format("Worker number: %d processing job number: %d, Document: %s caused error: %s", workerNumber,
					job.getJobNumber(), job.getTitle(), e.getMessage()));
		}
	}
	
	private void logMessage(String message)
	{
		if(Util.isLoggingON())servLog.offer(message);
	}

}
