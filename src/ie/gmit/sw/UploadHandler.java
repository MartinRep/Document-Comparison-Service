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

/**
 * Servlet implementation class UploadHandler
 */
@WebServlet(asyncSupported = true, description = "Handles new document file upload", urlPatterns = { "/UploadHandler" })
@MultipartConfig(fileSizeThreshold=1024*1024*5, // 2MB. The file size in bytes after which the file will be temporarily stored on disk. The default size is 0 bytes.
maxFileSize=1024*1024*50,      // 10MB. The maximum size allowed for uploaded files, in bytes
maxRequestSize=1024*1024*51)   // 50MB. he maximum size allowed for a multipart/form-data request, in bytes.
public class UploadHandler extends HttpServlet {
	private static final long serialVersionUID = 465419841991L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadHandler() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		request.setAttribute("message", "There was an error processing the file");
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("POST request");
		Part part = request.getPart("txtDocument");
		BufferedReader brTextFile = new BufferedReader(new InputStreamReader(part.getInputStream()));
		String line = null;
		while ((line = brTextFile.readLine()) != null) 
		{
			System.out.println(line);
		}
		//doGet(request, response);
		
	}

}
