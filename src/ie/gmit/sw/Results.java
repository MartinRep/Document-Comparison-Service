package ie.gmit.sw;

import java.util.ArrayList;
import java.util.HashMap;

public class Results 
{
	private int jobNumber;
	private String title;
	private ArrayList<String> docTitles;
	private HashMap<String, String> docsResults;
	
	public Results(int jobNumber, String documentName) {
		super();
		this.jobNumber = jobNumber;
		this.title = documentName;
		docTitles = new ArrayList<>();
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
	
	public void AddResult(String title, String result)
	{
		docsResults.put(title, result);
	}
	
	public int GetResultsCount()
	{
		return docsResults.size();
	}
	
	public void AddDocTitle(String title)
	{
		docTitles.add(title);
	}
	
	public ArrayList<String> GetDocs()
	{
		return docTitles;
	}
	
	public String GetResult(String title)
	{
		return docsResults.get(title);
	}
}
