package ie.gmit.sw;

import java.util.ArrayList;
import java.util.HashMap;

public class Results 
{
	private int jobNumber;
	private String title;
	private ArrayList<String> docs;
	private HashMap<String, String> docsResults;
	
	public Results(int jobNumber, String documentName) {
		super();
		this.jobNumber = jobNumber;
		this.title = documentName;
		docs = new ArrayList<>();
		docsResults = new HashMap<>();
	}
	
	public int GetJobNumber()
	{
		return jobNumber;
	}
	
	public String GetTitle()
	{
		return title;
	}
	
	public void AddResult(String document, String result)
	{
		docsResults.put(document, result);
	}
	
	public int GetResultsCount()
	{
		return docsResults.size();
	}
	
	public void AddDoc(String title)
	{
		docs.add(title);
	}
	
	public ArrayList<String> GetDocs()
	{
		return docs;
	}
	
	public String GetResult(String title)
	{
		return docsResults.get(title);
	}
}
