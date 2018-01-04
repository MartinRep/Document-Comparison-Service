package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Facade from MinHash comparing function called by Worker.class. Facade pattern hides the complexities of the system and 
 * provides an interface to the client using which the client can access the system.
 * Manages processing of document from BufferedReader through Set of words and hashes to finally Set of minHashes.
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

    public Set<Integer> getHashes(Set<String> words) {
	return pd.getHashes(words);
    }

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
