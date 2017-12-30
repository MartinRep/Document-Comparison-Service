package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MinHash implements JobProcessor {

	Set<Integer> minHashes;
	
	public MinHash() 
	{
	}

	@Override
	public Results processJob(Job job) 
	{
		try {
			Set<String> words = getWords(job.getDocument());
			Set<Integer> hashes = getHashes(words);
			Set<Integer> hashFunctions = new TreeSet<>();
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
		Set<Integer> wordsHashes = new TreeSet<>();
		for (String word : words)
		{
			wordsHashes.add(word.hashCode());
		}
		return wordsHashes;
	}

}
