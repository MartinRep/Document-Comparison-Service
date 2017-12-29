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
@MultipartConfig(fileSizeThreshold=1024*1024*5, // 2MB. The file size in bytes after which the file will be temporarily stored on disk. The default size is 0 bytes.
maxFileSize=1024*1024*50,      // 50MB. The maximum size allowed for uploaded files, in bytes
maxRequestSize=1024*1024*51)   // 51MB. he maximum size allowed for a multipart/form-data request, in bytes.
public class UploadHandler extends HttpServlet 
{
	private static final long serialVersionUID = 465419841991L;
	private ArrayBlockingQueue<Job> inQueue;
	JobHandler jobHandler;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadHandler() 
    {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException 
	{
		int numOfWorkers = Integer.parseInt(config.getInitParameter("workers"));
		System.out.println("Num of Workers:" + numOfWorkers);
		jobHandler = JobHandler.Init(numOfWorkers);
		inQueue = jobHandler.GetInQueue();
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() 
	{
		// TODO Auto-generated method stub
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
		int jobNumber = jobHandler.GetJobNumber();
		String title = request.getParameter("txtTitle");
		Part part = request.getPart("txtDocument");
		BufferedReader document = new BufferedReader(new InputStreamReader(part.getInputStream()));
		Job job = new Job(jobNumber, title, document);
		try {
			inQueue.put(job);
		} catch (InterruptedException e) {
			System.out.printf("Servlet error inserting document: %s Error: %s", job.getDocument(), e.getMessage());
		}
		request.setAttribute("jobNumber", jobNumber);
		request.setAttribute("title", title);
		request.getRequestDispatcher("/poll.jsp").forward(request, response);
	}

	@Override
	protected void finalize() throws Throwable {
		jobHandler.Shutdown();
		super.finalize();
	}
	
	

}
