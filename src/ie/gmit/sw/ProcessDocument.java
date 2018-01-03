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

public class ProcessDocument {
    private int minHash = Integer.MAX_VALUE;
    private boolean alreadyExist = false;

    public Set<String> getWords(BufferedReader document) throws IOException {
	String line;
	Set<String> allWords = new TreeSet<>();
	while ((line = document.readLine()) != null) {
	    allWords.addAll(new ArrayList<String>(Arrays.asList(line.split(" "))));
	}
	return allWords;
    }

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

    // Create nimOfHashes amount of random integers used for XOR hashFunctions
    public Set<Integer> getHashFunctions(int numOfHashes) {
	Set<Integer> hashFunctions = new TreeSet<Integer>();
	Random random = new Random();
	for (int i = 1; i < numOfHashes; i++) // Shingles size minus nimHash from .hashCode function.
	{
	    hashFunctions.add(random.nextInt());
	}
	return hashFunctions;
    }

    public int getMinHash() {
	return minHash;
    }

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

    public boolean isAlreadySaved() {
	return alreadyExist;
    }
}
