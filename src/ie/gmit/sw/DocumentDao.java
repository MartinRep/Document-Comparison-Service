package ie.gmit.sw;

import java.util.List;

/**
 * Data Access Object Interface.
 * This interface defines the standard operations to be performed on a model class Document
 * when interacting with persistence storage. Instance is created by UploadHandler servlet. 
 * This can benefit future development as storage type can be easily defined in web.xml
 * @author Martin Repicky g00328337@gmit.ie
 *
 */

public interface DocumentDao {
    public List<Document> getDocuments();

    public void storeDocument(Document document);
}
