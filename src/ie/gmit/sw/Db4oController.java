package ie.gmit.sw;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import com.db4o.ObjectSet;
import com.db4o.ext.Db4oException;

public class Db4oController implements DocumentDAO
{
	String fileName;
	String password;
	ArrayBlockingQueue<String> servLog;

	public Db4oController(String fileName, String password)
	{
		super();
		this.fileName = fileName;
		this.password = password; 
	}
	
	@Override
	public void setLogging(ArrayBlockingQueue<String> servLog)
	{
		this.servLog = servLog;
	}
	
	private void logMessage(String message)
	{
		if(Util.isLoggingON()) servLog.offer(message);
	}

	@Override
	public List<Document> getDocuments()
	{
		List<Document> documents = new ArrayList<>();
		try 
		{
			Db4oService db4o = new Db4oService(fileName, password);
			ObjectSet<Object> results = db4o.getObjects(Document.class);			
			for (Object document : results)
			{
				documents.add((Document) document);
			}
			db4o.closeDb();
			logMessage(documents.size() + " documents retrieved");
		} catch (Db4oException db4oExp)
		{
			if(db4oExp.getMessage().contains("File format incompatible")) logMessage("ERROR reading from database. Possible cause: Wrong password");
			else logMessage("Db4o Error: " + db4oExp.getMessage());
		}
		return documents;
	}

	@Override
	public void storeDocument(Document document)
	{
		try
		{
			Db4oService db4o = new Db4oService(fileName, password);
			db4o.storeObject(document);
			db4o.closeDb();			
			logMessage("Document saved.");
		} catch (Db4oException db4oExp)
		{
			if(db4oExp.getMessage().contains("File format incompatible")) logMessage("ERROR reading from database. Possible cause: Wrong password");
			else logMessage("Db4o Error: " + db4oExp.getMessage());
		}
			
	}

}
