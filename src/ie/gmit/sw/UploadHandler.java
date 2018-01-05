package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import ie.gmit.sw.Util.Config;


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
    private boolean fatalError = false;

    /**
     * This method is triggered when first instance of Servlet is loaded.
     * Initial parameter: logFile String - The absolute path and filename for Logging service file.
     * Initial parameter: dbFile String -  The absolute path and filename for documents persistence DAO.
     * Initial parameter: password String - The password for documents persistence DAO.
     * Initial parameter: hashFunctionCount String - The Set size of hash functions used for getting minHash values. Recommended size 300.
     * Initial parameter: shingleSize - The number of words to make shingle from hash code is calculated. Preserve context.
     * Initial parameter: loggingOn Boolean - The switch for Logging service. This will log to console and logFile specified.
     * Initial parameter: refreshRate String - The size of the page refresh delay set to browser when pooling for results.
     * Initial parameter: workers Integer - The size of the Thread pool, iQueue and servLog.
     *   
     * @param config This is a Servlets object from which parameters are read from web.xml.
     * 
     * @exception ServletException 
     * @see HttpServlet#init
     */
    public void init(ServletConfig config) throws ServletException {
	Config.setLoggingOn(Boolean.parseBoolean(config.getInitParameter("logging")));
	Config.setLogFile(config.getInitParameter("logFile"));
	Config.setDb(new Db4oController(config.getInitParameter("dbFile"), config.getInitParameter("password")));
	Config.setHashFunctions(Integer.parseInt(config.getInitParameter("HashFunctionCount")));
	Config.setShingleSize(Integer.parseInt(config.getInitParameter("shingleSize")));
	Config.setRefreshRate(Integer.parseInt(config.getInitParameter("refreshRate")));
	Config.setNumOfWorkers(Integer.parseInt(config.getInitParameter("workers")));
	if(!Util.init()) fatalError = true;
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	request.setAttribute("message", "First submit the file.");
	request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    /**
     * Process POST request initialized by 'index.jsp'.
     * Creates Job object with document title as 'txtTitle' String and document stream as 'txtDocument' BufferedReader
     * and gets jobNumeber integer from Util class static method. Job object is then handed to Util class Facade
     * to be processed by ThreadPool of HeavyWorker class. And finally it redirects to PollHandler Servlet, where results will be displayed once processed.
     * If any Exception is thrown in Util class an error message page is displayed.
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	if(fatalError) response.sendRedirect("error.jsp");
	else {
	    int jobNumber = Util.getJobNumber();
	    String title = request.getParameter("txtTitle");
	    Part part = request.getPart("txtDocument");
	    BufferedReader document = new BufferedReader(new InputStreamReader(part.getInputStream()));
	    Job job = new Job(jobNumber, title, document);
	    // Changes browser URL as well, so refresh will remember parameters, Instead of hidden form.    
	    if(Util.processJob(job)) response.sendRedirect("poll?title=" + title + "&jobNumber=" + jobNumber);
	    else response.sendRedirect("error.jsp");		
	}
    }
    
    /**
     * Triggers Util.shutdown() method to orderly finish the ThreadPool executor. Avoids memory leaks.
     * @see HttpServlet#destroy()
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
