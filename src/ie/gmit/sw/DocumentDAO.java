package ie.gmit.sw;

import java.util.List;

public interface DocumentDAO {
    public List<Document> getDocuments();

    public void storeDocument(Document document);
}
