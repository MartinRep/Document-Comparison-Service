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
 * Servlet implementation class UploadHandler
 */
@WebServlet(asyncSupported = true, description = "Handles new document file upload", urlPatterns = { "/upload" })
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB. The file size in bytes after which the file will be temporarily stored on disk. The default size is 0 bytes.
maxFileSize=1024*1024*50,      // 50MB. The maximum size allowed for uploaded files, in bytes
maxRequestSize=1024*1024*51)   // 51MB. he maximum size allowed for a multipart/form-data request, in bytes.
public class UploadHandler extends HttpServlet 
{
	private static final long serialVersionUID = 465419841991L;
	private static ArrayBlockingQueue<Job> inQueue;
	private static ArrayBlockingQueue<String> servLog;
	private static boolean loggingON;
       
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException 
	{
		// gets Initial parameters from web.xml
		int numOfWorkers = Integer.parseInt(config.getInitParameter("workers"));
		String logFile = config.getInitParameter("logFile");
		String dbFile = config.getInitParameter("dbFile");
		String password = config.getInitParameter("password");
		int shingles = Integer.parseInt(config.getInitParameter("shingles"));
		loggingON = Boolean.parseBoolean(config.getInitParameter("logging"));
		// Singleton variables share initialization. Has to be First thing to init.
		Util.init(numOfWorkers);
		Util.setDb((DocumentDAO) new Db4oController(dbFile, password));
		Util.setShingles(shingles);
		// Initialing Logging service to listen on servLog ArrayBlockingQueue messages and write to file logFile.
		if(loggingON) LogService.init(Util.getServLog() ,logFile);
		// Getting ArrayBlockingQueue for log entries
		servLog = Util.getServLog();
		inQueue = Util.getInQueue();
		// Servlet first log entry
		logMessage("Upload Servlet initialized with workers amount of "+ numOfWorkers);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		request.setAttribute("message", "There was an error processing the file");
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		int jobNumber = Util.getJobNumber();
		String title = request.getParameter("txtTitle");
		Part part = request.getPart("txtDocument");
		BufferedReader document = new BufferedReader(new InputStreamReader(part.getInputStream()));
		Job job = new Job(jobNumber, title, document);
		try {
			inQueue.put(job);
		} catch (InterruptedException e) {
			logMessage(String.format("Servlet error inserting document: %s Error: %s", job.getDocument(), e.getMessage()));
		}
		// Changes browser URL as well, so refresh will remember parameters, lazy way. Instead of hidden form
//		request.setAttribute("jobNumber", jobNumber);
//		request.setAttribute("title", title);
//		request.getRequestDispatcher("/poll").forward(request, response);
		response.sendRedirect("poll?title=" + title + "&jobNumber="+ jobNumber);
	}
	
	private void logMessage(String message)
	{
		if(loggingON)servLog.offer(message);
	}
	
	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() 
	{
		Util.shutdown();
		LogService.shutdown();
	}

	@Override
	protected void finalize() throws Throwable {
		destroy();
		super.finalize();
	}
	
	

}
