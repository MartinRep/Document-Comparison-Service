package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * Process the document submitted by UploadServer. Step by step from BufferedReader to individual words, their hashes.
 * Generates HashFunctions and compare document against Set of documents.
 * 
 * @author Martin Repicky g00328337@gmit.ie
 *
 */

public class ProcessDocument {
    private int minHash = Integer.MAX_VALUE;
    private boolean alreadyExist = false;

    /**
     * Process BufferedReader document into separate words
     * @param document File submitted to UploadHandler servlet
     * @return Set of document words separated by space (" ") 
     * @throws IOException
     */
    
    public Set<String> getWords(BufferedReader document) throws IOException {
	String line;
	Set<String> allWords = new TreeSet<>();
	while ((line = document.readLine()) != null) {
	    allWords.addAll(new ArrayList<String>(Arrays.asList(line.split(" "))));
	}
	return allWords;
    }

    /**
     * Process Set of Words into Set of Hash Codes.
     * @param words Set of document words
     * @return Set of hash codes for every word in the document
     */
    
    public Set<Integer> getHashes(Set<String> words) {
	minHash = Integer.MAX_VALUE;
	Set<Integer> wordsHashes = new TreeSet<>();
	for (String word : words) {
	    int hash = word.hashCode();
	    wordsHashes.add(hash);
	    if (minHash < hash)
		minHash = hash;
	}
	return wordsHashes;
    }

    /**
     * Creates a Set of random integers used later for XOR hashFunctions
     * @param numOfHashes Shingles. Size of minHash values, minus 1 that is generated by .hashCode function inside getHashes()
     * @return Set of random Integers.
     */
    
    public Set<Integer> getHashFunctions(int numOfHashes) {
	Set<Integer> hashFunctions = new TreeSet<Integer>();
	Random random = new Random();
	for (int i = 1; i < numOfHashes; i++) // Shingles size minus nimHash from .hashCode function.
	{
	    hashFunctions.add(random.nextInt());
	}
	return hashFunctions;
    }

    /**
     * When creating hash for every word in document, hash with minimum value is stored in separate variable.
     * This way resources are saved getting minHash from already looped over Set of integers. This is done by getHashes()
     * It is later on added to Set of document MinHashes. 
     * @return Value of smallest hashCode for every word in the document.
     */
    
    public int getMinHash() {
	return minHash;
    }
    
    /**
     * Method to compare document (Set of hashes) against the rest of the documents already stored in the database.
     * @param document Uploaded Document object in UploadHandler servlet
     * @param documents Set of Document objects, retrieved from persistent storage.  
     * @return Results object populated with HashMap of document to value of percentage similarity between uploaded document and document form Set of already stored documents
     */

    public HashMap<String, String> compareDocument(Document document, List<Document> documents) {
	alreadyExist = false;
	HashMap<String, String> results = new HashMap<>();
	int shingles = document.getHashFunctionsSize();
	for (Document tmpDocument : documents) {
	    Set<Integer> retainAll = new TreeSet<>(document.getMinHashes());
	    retainAll.retainAll(tmpDocument.getMinHashes());
	    double similarity = (double) retainAll.size() / shingles * 100;
	    // Prevent duplicate saving
	    if (similarity == 100.0 && document.getTitle().equals(tmpDocument.getTitle()))
		alreadyExist = true;
	    results.put(tmpDocument.getTitle(), String.valueOf(similarity));
	}
	return results;
    }

    /**
     * Generate Set of Integers representing hash code with minimum value for every XOR operation,
     * stores only one minimum value hash code for every hashFunction. This is set by shingles value in web.xml
     * @param hashes Set of integers/hash codes for every word in the document
     * @param hashFunctions Randomly generated integers. MUST be same for every document for the comparing to work.
     * @return Set of Integers, the hash code representation of the document. This is how document content is stored.
     */
    
    public Set<Integer> getMinHashes(Set<Integer> hashes, Set<Integer> hashFunctions) {
	Set<Integer> minHashes = new TreeSet<>();
	for (int hashFunction : hashFunctions) {
	    int min = Integer.MAX_VALUE;
	    for (Integer hash : hashes) {
		int minHash = hash ^ hashFunction; // Bitwise XOR the word hashCode with the hashFunction
		if (minHash < min)
		    min = minHash;
	    }
	    minHashes.add(min); // Only store the word with the minimum hash value for each hash function
	}
	return minHashes;
    }

    /**
     * When comparing document against the rest of documents, boolean is turned true, if document has a counterpart
     * with the same name and 100 % similarity already stored inside database. This is to prevent storing multiple documents
     * with exactly the same properties. 
     * @return Boolean that indicates if uploaded document with the same name and 100% similarity is already stored in database.
     */
    
    public boolean isAlreadySaved() {
	return alreadyExist;
    }
}
