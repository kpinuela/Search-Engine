package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;

import edu.usfca.cs272.InvertedIndex.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * An alternative implemention of the {@MessageServlet} class but using the
 * Bulma CSS framework.
 *
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class SearchServlet extends HttpServlet {
	/** Class version for serialization, in [YEAR][TERM] format (unused). */
	private static final long serialVersionUID = 202210;

	/** The title to use for this webpage. */
	private static final String TITLE = "Search Engine";
	/** Template for HTML. **/
	private final String htmlTemplate;

	/**
	 * MultiThreadInvertedIndex index
	 * 
	 */
	private final MultiThreadInvertedIndex index;

	/** Base path with HTML templates. */
	private static final Path BASE = Path.of("src", "main", "resources",
			"html");

	/**
	 * Initializes this message board. Each message board has its own collection
	 * of messages.
	 * 
	 * @param index
	 *            Multi Thread Inverted Index
	 *
	 * @throws IOException
	 *             if unable to read templates
	 */
	public SearchServlet(MultiThreadInvertedIndex index) throws IOException {
		htmlTemplate = Files.readString(BASE.resolve("index.html"), UTF_8);
		this.index = index;
	}
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String queryvalue = request.getParameter("query");
		queryvalue = queryvalue == null ? "" : queryvalue;
		queryvalue = StringEscapeUtils.escapeHtml4(queryvalue);
		ArrayList<Result> results = this.index
				.search(TextFileStemmer.uniqueStems(queryvalue), false);
		Map<String, String> values = new HashMap<>();
		values.put("title", TITLE);
		values.put("thread", Thread.currentThread().getName());
		// setup form
		values.put("method", "GET");
		values.put("action", request.getServletPath());
		// generate html from template
		StringBuilder builder = new StringBuilder();
		for (Result result : results) {
			builder.append(String.format(
					"<li><a href = %s  target=_blank>%s</a></li>\n",
					result.getLocation(), result.getLocation()));
		}
		values.put("output", builder.toString());
		StringSubstitutor replacer = new StringSubstitutor(values);
		String html = replacer.replace(htmlTemplate);
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(html);
		out.flush();
		response.sendRedirect(request.getServletPath());
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
