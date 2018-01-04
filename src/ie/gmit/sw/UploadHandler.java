package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class UploadHandler. Process POST request from 'index.jsp' and 
 * reads in initial variables from 'web.xml'
 * 
 * @author Martin Repicky g00328337
 */
@WebServlet(asyncSupported = true, description = "Handles new document file upload", urlPatterns = { "/upload" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB. The file size in bytes after which the file will be
						      // temporarily stored on disk. The default size is 0 bytes.
	maxFileSize = 1024 * 1024 * 50, // 50MB. The maximum size allowed for uploaded files, in bytes
	maxRequestSize = 1024 * 1024 * 51) // 51MB. he maximum size allowed for a multipart/form-data request, in bytes.
public class UploadHandler extends HttpServlet {
    private static final long serialVersionUID = 465419841991L;
    private static ArrayBlockingQueue<Job> inQueue;

    /**
     * This method is triggered when first instance of Servlet is loaded.
     * Initial parameter: logFile String - The absolute path and filename for Logging service file.
     * Initial parameter: dbFile String -  The absolute path and filename for documents persistence DAO.
     * Initial parameter: password String - The password for documents persistence DAO.
     * Initial parameter: shingles String - The Set size of hash functions used for getting minHash values. Recommended size 300.
     * Initial parameter: loggingOn Boolean - The switch for Logging service. This will log to console and logFile specified. 
     * @param config This is a Servlets object from which parameters are read from web.xml.
     * 
     * @exception ServletException 
     * @see Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
	int numOfWorkers = Integer.parseInt(config.getInitParameter("workers"));
	String logFile = config.getInitParameter("logFile");
	String dbFile = config.getInitParameter("dbFile");
	String password = config.getInitParameter("password");
	int shingles = Integer.parseInt(config.getInitParameter("shingles"));
	boolean loggingOn = Boolean.parseBoolean(config.getInitParameter("logging"));
	// Util.class Singleton class for variables share between Servlet and . Has to be First thing to init.
	Util.init();
	Util.setLoggingON(loggingOn);
	Util.initThreadPool(numOfWorkers);
	// This will allow to change dataBase via web.xml config file eventually.
	DocumentDao db = (DocumentDao) new Db4oController(dbFile, password);
	Util.setDb(db);
	Util.setShingles(shingles);
	// Initialing Logging service to listen on servLog ArrayBlockingQueue messages
	// and write to file logFile.
	if (Util.isLoggingOn()) {
	    LogService.init(Util.getServLog(), logFile);
	}
	inQueue = Util.getInQueue();
	// Servlet first log entry
	Util.logMessage("Upload Servlet initialized");
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	request.setAttribute("message", "There was an error processing the file");
	request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    /**
     * Process POST request initialized by 'index.jsp'.
     * Creates Job object with document title as 'txtTitle' String and document stream as 'txtDocument' BufferedReader
     * and gets jobNumeber integer from Util.class static method. Job object is then handed to ArrayBlockingQueue inQueue
     * to be processed by ThreadPool of Worker.class. And finally it redirects to PollHandler Servlet, where results will be displayed once processed
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	int jobNumber = Util.getJobNumber();
	String title = request.getParameter("txtTitle");
	Part part = request.getPart("txtDocument");
	BufferedReader document = new BufferedReader(new InputStreamReader(part.getInputStream()));
	Job job = new Job(jobNumber, title, document);
	try {
	    inQueue.put(job);
	} catch (InterruptedException e) {
	    Util.logMessage(
		    String.format("Servlet error inserting document: %s Error: %s", job.getDocument(), e.getMessage()));
	}
	// Changes browser URL as well, so refresh will remember parameters, lazy way.
	// Instead of hidden form
	// request.setAttribute("jobNumber", jobNumber);
	// request.setAttribute("title", title);
	// request.getRequestDispatcher("/poll").forward(request, response);
	response.sendRedirect("poll?title=" + title + "&jobNumber=" + jobNumber);
    }

    /**
     * This method will trigger Util.shutdown() methosd to orderly finish the ThreadPool executor.
     * @see Servlet#destroy()
     */
    public void destroy() {
	Util.shutdown();
    }

    @Override
    protected void finalize() throws Throwable {
	destroy();
	super.finalize();
    }
}
