package ie.gmit.sw;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Runnable and Cloneable Worker class extends Abstract HeavyWorker class. ThreadPoolService uses this class through HeavyWorker abstract class.
 * Process an Job object from inQueue through MinHash class Facade and stores results into Results object and output it to outQueue hashMap,
 * where it will be displayed via PoolHander Servlet. 
 * 
 * @author Martin Repicky g00328337@gmit.ie
 */

public class Worker extends HeavyWorker {

    private static ArrayBlockingQueue<Job> inQueue;
    private static ConcurrentHashMap<Integer, Results> outQueue;
    private int workerNumber;
    private Job job;
    private Document document;
    private List<Document> documents;
    private Results results;

    /**
     * Worker runnable method. Via Minhash class Facade gets words from BufferedReaded, hashes from words,
     * hashFunctions from previously stored documents, as these has to be the same for all the documents
     * for comparison to be accurate! 
     * Finally calculate minHashes for the document by XOR bitwise function applied to every word hash code.
     * This is repeated by number of shingles defined. 
     * Only compare and store the smallest hash code from every HashFunction, one shingle for every HashFunction. 
     * @exception IOException, InterruptedException
     * 
     */
    
    @Override
    public void run() {
	workerNumber = Util.getWorkerNumber();
	inQueue = Util.getInQueue();
	outQueue = Util.getOutQueue();
	try {
	    job = inQueue.take();
	    Util.logMessage(
		    String.format("Worker number %d start work on job number: %d", workerNumber, job.getJobNumber()));
	    MinHash minHash = new MinHash(Util.getDb());
	    try {
		documents = minHash.retreiveDocuments();
		document = new Document(job.getTitle());
		results = new Results(job.getJobNumber(), job.getTitle());
		Set<String> words = minHash.getWords(job.getDocument());
		Set<Integer> hashes = minHash.getHashes(words);
		if (documents.isEmpty()) {
		    document.setHashFunctions(minHash.getHashFunctions(Util.getShingles()));
		} else {
		    document.setHashFunctions(documents.get(0).getHashFunctions());
		}
		document.setMinHashes(minHash.getMinHashes(hashes, document.getHashFunctions()));
		// Compare document against documents
		results.addResults(minHash.compareDocument(document, documents));
		// add Result into outQueue
		outQueue.put(job.getJobNumber(), results);
		if (!minHash.isAlreadySaved()) // Prevent duplicate saving
		{
		    minHash.storeDocument(document);
		}
	    } catch (IOException e) {
		Util.logMessage(String.format("MinHash caused exception processing %s Error: %s", job.getTitle(),
			e.getMessage()));
	    }
	} catch (InterruptedException e) {
	    Util.logMessage(String.format("Worker number: %d processing job number: %d, Document: %s caused error: %s",
		    workerNumber, job.getJobNumber(), job.getTitle(), e.getMessage()));
	}
    }
}
