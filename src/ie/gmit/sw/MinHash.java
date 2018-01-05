package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Facade class for document processing and database functions. Called by Worker class.
 * Facade pattern hides the complexities of the system and provides an interface to the client
 *  using which the client can access the system.
 * Manages processing of document from BufferedReader through Set of words and Set of hash codes
 * ro finally Set of minHashes integers.
 * Also manages persistent storage via DAO interface DocumentDao.  
 * 
 * @author Martin Repicky g00328337@gmit.ie
 * @see ProcessDocument
 * @see DocumentDao
 */

public class MinHash {

    private DocumentDao db;
    private ProcessDocument pd;

    public MinHash(DocumentDao db) {
	this.db = db;
	pd = new ProcessDocument();
    }

    public List<Document> retreiveDocuments() {
	return db.getDocuments();
    }

    public void storeDocument(Document document) {
	db.storeDocument(document);
    }

    public Set<String> getWords(BufferedReader document) throws IOException {
	return pd.getWords(document);
    }

    public Set<Integer> getHashes(Set<String> words, int shingleSize) {
	return pd.getHashes(words, shingleSize);
    }

    /**
     * When Set of hash code is generated from words hash code with minimum value is stored separately.
     * This saves O(n) for looping over Set again. 
     * @param numOfHashes
     * @return
     */
    
    public Set<Integer> getHashFunctions(int numOfHashes) {
	Set<Integer> hashFunctions = pd.getHashFunctions(numOfHashes);
	hashFunctions.add(pd.getMinHash());
	return hashFunctions;
    }

    public Set<Integer> getMinHashes(Set<Integer> hashes, Set<Integer> hashFunctions) {
	return pd.getMinHashes(hashes, hashFunctions);
    }

    public HashMap<String, String> compareDocument(Document document, List<Document> documents) {
	return pd.compareDocument(document, documents);
    }

    public boolean isAlreadySaved() {
	return pd.isAlreadySaved();
    }

}
