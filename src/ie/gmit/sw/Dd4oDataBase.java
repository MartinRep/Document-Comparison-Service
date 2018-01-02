package ie.gmit.sw;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Query;

import xtea_db4o.XTeaEncryptionStorage;

public class Dd4oDataBase implements DocumentDAO
{
	ObjectContainer db;
	EmbeddedConfiguration config;
	String fileName;
	String password;
	ArrayBlockingQueue<String> servLog;
	
	public Dd4oDataBase(String fileName)
	{
		super();
		this.fileName = fileName;
		config = Db4oEmbedded.newConfiguration();
	}

	public Dd4oDataBase(String fileName, String password)
	{
		super();
		this.fileName = fileName;
		this.password = password;
		config = Db4oEmbedded.newConfiguration();
		config.file().storage(new XTeaEncryptionStorage(password)); 
	}
	
	public void setLogging(ArrayBlockingQueue<String> servLog)
	{
		this.servLog = servLog;
	}
	
	private void logMessage(String message)
	{
		if(servLog != null) servLog.offer(message);
	}

	@Override
	public List<Document> getDocuments()
	{
		List<Document> documents = new ArrayList<>();
		db = Db4oEmbedded.openFile(config, fileName);
		Query query = db.query();
		query.constrain(Document.class);
		ObjectSet<Document> result = query.execute();
		for (Object document : result)
		{
			documents.add((Document) document);
		}
		db.close();
		logMessage(documents.size() + " documents retrieved");
		return documents;
	}

	@Override
	public void storeDocument(Document document)
	{
		db = Db4oEmbedded.openFile(config, fileName);
		db.store(document);
		logMessage("Document saved.");
		db.close();			

	}

}
