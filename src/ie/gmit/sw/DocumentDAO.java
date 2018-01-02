package ie.gmit.sw;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public interface DocumentDAO
{
	public List<Document> getDocuments();
	public void storeDocument(Document document);
	public void setLogging(ArrayBlockingQueue<String> servLog);
}
