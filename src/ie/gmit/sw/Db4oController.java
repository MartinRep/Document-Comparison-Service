package ie.gmit.sw;

import java.util.ArrayList;
import java.util.List;

import com.db4o.ObjectSet;
import com.db4o.ext.Db4oException;

/**
 * Data Access Object concrete class - This class implements DocumentDao interface.
 * Creates instance of Db4oService with parameters(filename, password) define in web.xml
 * then process the generic Object class retreived by D4oService and parse it do Document object.
 * It is called by Worker via MinHash Facade. 
 * @author Martin Repicky g00328337@gmit.ie
 *
 */

public class Db4oController implements DocumentDao {
    String fileName;
    String password;

    public Db4oController(String fileName, String password) {
	super();
	this.fileName = fileName;
	this.password = password;
    }

    /**
     * Get all the Document.class objects from database and process them into List.
     * @return List of Document objects retrieved from database via D4oService.
     */
    @Override
    public List<Document> getDocuments() {
	List<Document> documents = new ArrayList<>();
	try {
	    Db4oService db4o = new Db4oService(fileName, password);
	    ObjectSet<Object> results = db4o.getObjects(Document.class);
	    for (Object document : results) {
		documents.add((Document) document);
	    }
	    db4o.closeDb();
	    Util.logMessage(documents.size() + " documents retrieved");
	} catch (Db4oException db4oExp) {
	    handleException(db4oExp);
	}
	return documents;
    }
    
    /**
     * Store a Document object to database via Db4Service class
     * @param document Document object to be saved. Uploaded and processed document from UploadHandler servlet
     */
    @Override
    public void storeDocument(Document document) {
	try {
	    Db4oService db4o = new Db4oService(fileName, password);
	    db4o.storeObject(document);
	    db4o.closeDb();
	    Util.logMessage("Document saved.");
	} catch (Db4oException db4oExp) {
	    handleException(db4oExp);
	}
    }

    private void handleException(Db4oException db4oExp) {
	if (db4oExp.getMessage().contains("File format incompatible"))
	    Util.logMessage("ERROR reading from database. Possible cause: Wrong password");
	else
	    Util.logMessage("Db4o Error: " + db4oExp.getMessage());
    }

}
