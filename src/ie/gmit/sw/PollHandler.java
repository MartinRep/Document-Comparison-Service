package ie.gmit.sw;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PollHandler. Display pooling message for jobs in progress and
 * table of comparison results returned by Worker class.
 * Set to refresh page every predefined seconds to check Hashmap against job number received
 * as parameter from UploadHandler servlet.
 * 
 * @author Martin Repicky g00328337@gmit.ie
 */
@WebServlet(description = "Handles Pooling and displaying Results", urlPatterns = { "/poll" })
public class PollHandler extends HttpServlet {
    private static final long serialVersionUID = 42423443566L;
    private static ConcurrentHashMap<Integer, Results> outQueue;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public PollHandler() {
	super();
    }

    /**
     * This method is triggered when first instance of this Servlet is loaded.
     * Gets ConcurrentHashMap instance from Utility.class. This is where
     * comparison results are put by workers. Checks against jobNumber. 
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	if (Util.getInQueue() != null)
	    outQueue = Util.getOutQueue();
    }

    /**
     * Handles results requests. UploadHandler servlet redirects here after document
     * is submitted for comparison. Servlet checks outQueue hashmap for jobNumber.
     * If the results are not found in HashMap pooling message with job number is displayed instead.
     * If the results are found they are displayed in formatted table.
     * @throws ServletException, IOException   
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	PrintWriter out = response.getWriter();
	String title = request.getParameter("title");
	int jobNumber = Integer.parseInt(request.getParameter("jobNumber"));
	if (outQueue.size() > 0) {
	    if (outQueue.containsKey(jobNumber)) {
		// Getting comparison results, as well removing it from ConcurrentHashmap
		Results results = outQueue.remove(jobNumber);
		// Display results
		String cssLocation = request.getContextPath() + "/css/results.css";
		String cssTag = "<link rel='stylesheet' type='text/css' href='" + cssLocation + "'>";
		out.printf("<html><head>%s</head><body>", cssTag);
		out.print("<div class='centered'><table>");
		out.printf("<h1 align=\"center\"><b>%s</b></h1>", title);
		out.print("<tr><th>Document title</th><th>Similarity</th></tr>");
		for (String docTitle : results.getDocuments()) {
		    out.print("<tr><td>");
		    out.print(docTitle);
		    out.print("</td><td>");
		    out.printf("%.0f %%", Double.valueOf(results.getResult(docTitle)));
		    out.print("</td></tr>");
		}
		out.println();
		out.print("</table></div>");
		// Home button
		out.printf("<p align=\"center\">"
			+ "<button onclick=\"window.location.href=' /Document-Comparison-Service/'\">Home</button>"
			+ "</p>");
		out.print("</body></html>");
	    }

	} else {
	    response.setIntHeader("Refresh", Util.getRefreshRate());
	    out.printf("<p align=\"center\">Processing document: <b>%s</b>, please wait...</p>", title);
	    out.println();
	    out.printf("<p align=\"center\">Job Number: <b>%d</b></p>", jobNumber);
	}

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	doGet(request, response);
    }

}
