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
 * Servlet implementation class PollHandler
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

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		outQueue = WorkersHandler.getOutQueue();
		
	}



	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String title = request.getParameter("title");
		int jobNumber = Integer.parseInt(request.getParameter("jobNumber"));
		if(outQueue.containsKey(jobNumber))
		{
			// Getting result value as well removing it from shared Hashmap
			Results results = outQueue.remove(jobNumber);
			//Display results
			//out.printf("<p  align=\"center\"><b>%s</b>: <div style=\"white-space: pre-wrap;\">%s</div></p>",word, result);
			out.print("<head>\n" + 
					"<style>\n" + 
					"table {\n" + 
					"    font-family: arial, sans-serif;\n" + 
					"    border-collapse: collapse;\n" + 
					"    width: 100%;\n" + 
					"}\n" + 
					"\n" + 
					"td, th {\n" + 
					"    border: 1px solid #dddddd;\n" + 
					"    text-align: left;\n" + 
					"    padding: 8px;\n" + 
					"}\n" + 
					"\n" + 
					"tr:nth-child(even) {\n" + 
					"    background-color: #dddddd;\n" + 
					"}\n" + 
					"</style>\n" + 
					"</head>\n" + 
					"<body>\n" + 
					"\n" + 
					"<table>");
			out.printf("<h1 align=\"center\"><b>%s</b></h1>" , title);
			out.print("<tr><th>Document title</th><th>Similarity</th></tr>");
			for(String docTitle : results.getDocuments())
			{
				out.print("<tr><td>");
				out.print(docTitle);
				out.print("</td><td>");
				Double resDouble = Double.valueOf(results.getResult(docTitle));
				int resInt = resDouble.intValue();
				out.print(resInt + " %");
				out.print("</td></tr>");
			}
			out.println();
			out.print("</table>");
			//Home button
			out.printf("<p  align=\"center\"><button onclick=\"window.location.href=' /Document-Comparison-Service/'\">Home</button></p>");
			
		} else
		{
			response.setIntHeader("Refresh", 10);
			out.printf("<p  align=\"center\">Processing document: <b>%s</b>, please wait...</p>", title);
			out.println();
			out.printf("<p  align=\"center\">Job Number: <b>%d</b></p>",jobNumber);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
