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
 * Servlet implementation class PollHandler. Display pooling message if document comparing haven't returned
 * results yet. Set to refresh page every 10 second to check Hashmap against job number received
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
     * This method is triggered when first instance of Servlet is loaded.
     * Gets ConcurrentHashMap instance from Utility.class. This is where
     * Results of the comparison are put by workers. Check them against jobNumber. 
     */
    
    @Override
    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	if (Util.getInQueue() != null)
	    outQueue = Util.getOutQueue();
    }

    /**
     * Handles results requests. UploadHandler servlet redirects here after document
     * is submitted for comparison. Servlet checks outQueue hashmap for jobNumeber value.
     * If the results are not in HashMap yet pooling message with job number is displayed instead.
     * Once the results are found they are displayed in the table.
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

		// Getting result value as well removing it from shared Hashmap
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
		    Double resDouble = Double.valueOf(results.getResult(docTitle));
		    int resInt = resDouble.intValue();
		    out.print(resInt + " %");
		    out.print("</td></tr>");
		}
		out.println();
		out.print("</table></div>");
		// Home button
		out.printf(
			"<p  align=\"center\"><button onclick=\"window.location.href=' /Document-Comparison-Service/'\">Home</button></p>");
		out.print("</body></html>");
	    }

	} else {
	    response.setIntHeader("Refresh", 10);
	    out.printf("<p  align=\"center\">Processing document: <b>%s</b>, please wait...</p>", title);
	    out.println();
	    out.printf("<p  align=\"center\">Job Number: <b>%d</b></p>", jobNumber);
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
