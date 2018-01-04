package ie.gmit.sw;

import java.util.HashMap;
import java.util.Set;

/**
 * Model class. Used by Worker to store results from comparing document against already stored documents.
 * PoolHandler Servlet takes these Results and display them in the table.
 * 
 * @author Martin Repicky g00328337@gmit.ie
 *
 */

public class Results {
    private int jobNumber;
    private String title;
    private HashMap<String, String> docsResults;

    public Results(int jobNumber, String documentName) {
	super();
	this.jobNumber = jobNumber;
	this.title = documentName;
	docsResults = new HashMap<>();
    }

    public int getJobNumber() {
	return jobNumber;
    }

    public String getTitle() {
	return title;
    }

    public void addResults(HashMap<String, String> results) {
	docsResults.putAll(results);
	;
    }

    public Set<String> getDocuments() {
	return docsResults.keySet();
    }

    public int getResultsCount() {
	return docsResults.size();
    }

    public String getResult(String title) {
	return docsResults.get(title);
    }
}
