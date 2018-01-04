package ie.gmit.sw;

import java.util.Set;
import java.util.TreeSet;

/**
 * Model class. Used by DocumentDao class for storage and retrieval 
 * and by ProcessDocument class for comparison.
 * 
 * !! Set<Integer> MinHashFunctions MUST BE IDENTICAL FOR EVERY DOCUMENT COMPARED !!
 * 
 * @author Martin Repicky g00328337@gmit.ie
 *
 */

public class Document {

    // Document name
    private String title;
    // Set of minHash from every XOR hashFunction
    private Set<Integer> minHashes;
    // Set of random hashes. Must be identical in all documents compared!
    private Set<Integer> hashFunctions;

    public Document(String title) {
	super();
	this.title = title;
	minHashes = new TreeSet<>();
	hashFunctions = new TreeSet<>();
    }

    public Document(String title, Set<Integer> minHashes, Set<Integer> hashFunctions) {
	super();
	this.title = title;
	this.minHashes = minHashes;
	this.hashFunctions = hashFunctions;
    }

    public String getTitle() {
	return title;
    }

    public Set<Integer> getMinHashes() {
	return minHashes;
    }

    public void setMinHashes(Set<Integer> minHashes) {
	this.minHashes = minHashes;
    }

    public Set<Integer> getHashFunctions() {
	return hashFunctions;
    }

    public void setHashFunctions(Set<Integer> hashFunctions) {
	this.hashFunctions = hashFunctions;
    }

    public int getHashFunctionsSize() {
	return this.hashFunctions.size();
    }

    public int getMinHashesSize() {
	return this.minHashes.size();
    }

    @Override
    public String toString() {
	return String.format("Document title: %s MinHashes: %d hashFunctions: %d", this.getTitle(),
		this.getMinHashesSize(), this.getHashFunctionsSize());
    }
}
