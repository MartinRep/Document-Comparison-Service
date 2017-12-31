package ie.gmit.sw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Results 
{
	private int jobNumber;
	private String title;
	private HashMap<String, String> docsResults;
	
	public Results(int jobNumber, String documentName) {
		super();
		this.jobNumber = jobNumber;
		this.title = documentName;
		docsResults = new HashMap<>();
	}
	
	public int getJobNumber()
	{
		return jobNumber;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void addResult(String title, String result)
	{
		docsResults.put(title, result);
	}
	
	public Set<String> getDocuments()
	{
		return docsResults.keySet();
	}
	
	public int getResultsCount()
	{
		return docsResults.size();
	}
	
	
	public String getResult(String title)
	{
		return docsResults.get(title);
	}
}
