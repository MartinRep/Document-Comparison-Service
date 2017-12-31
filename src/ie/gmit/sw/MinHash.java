package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;

public class MinHash implements JobProcessor {

	Document document;
	int shingles = 299;
	
	public MinHash() 
	{
	}

	@Override
	public Results processJob(Job job) 
	{
		document = new Document(job.getTitle());
		try {
			Set<String> words = getWords(job.getDocument());
			Set<Integer> hashes = getHashes(words);
			Set<Integer> hashFunctions = getHashFunctions();
			generateMinHashes(hashes, hashFunctions);
			// Retrieve documents, HashMap<String name, Set<Integer> minHashes> from db40
			// compare minHashes of every document and add each Result to Results object.
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private Set<String> getWords(BufferedReader document) throws IOException
	{
		String line;
		List<String> tmp;
		Set<String> allWords = new TreeSet<>();
		while((line = document.readLine()) != null)
		{
			String[] words = line.split(" ");
			System.out.println("Line Words count: " + words.length);
			tmp = new ArrayList<String>(Arrays.asList(words));
			allWords.addAll(tmp);
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
	    Random r = new Random();
	    for (int i = 0; i < shingles; i++)
	    { //Create k random integers
	    	hashes.add(r.nextInt());
	    }
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
