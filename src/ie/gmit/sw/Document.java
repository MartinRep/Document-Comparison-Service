package ie.gmit.sw;

import java.util.Set;
import java.util.TreeSet;

public class Document
{

	// Document name
	private String title;
	// Set of minHash from every XOR hashFunction
	private Set<Integer> minHashes;
	// These random hashes set has to be the same for every document compared
	private Set<Integer> hashFunctions;
	
	public Document(String title)
	{
		super();
		this.title = title;
		minHashes = new TreeSet<>();
		hashFunctions = new TreeSet<>();
	}
	
	public Document(String title, Set<Integer> minHashes, Set<Integer> hashFunctions)
	{
		super();
		this.title = title;
		this.minHashes = minHashes;
		this.hashFunctions = hashFunctions;
	}

	public String getTitle()
	{
		return title;
	}

	public Set<Integer> getMinHashes()
	{
		return minHashes;
	}

	public void setMinHashes(Set<Integer> minHashes)
	{
		this.minHashes = minHashes;
	}

	public Set<Integer> getHashFunctions()
	{
		return hashFunctions;
	}

	public void setHashFunctions(Set<Integer> hashFunctions)
	{
		this.hashFunctions = hashFunctions;
	}
	
	public void addMinHash(int minHash)
	{
		minHashes.add(minHash);
	}
	
	public void addHashFunction(int hashFunction)
	{
		this.hashFunctions.add(hashFunction);
	}
	
	public int getHashFunctionsSize()
	{
		return this.hashFunctions.size();
	}
	
	public int getMinHashesSize()
	{
		return this.minHashes.size();
	}

}
