package edu.usfca.cs272;

import java.io.IOException;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Server class for the Engine
 * 
 * @author kyle
 *
 */
public class EngineServer {
	/**
	 * a multithreaded InvertedIndex
	 */
	public final MultiThreadInvertedIndex index;

	/**
	 * Constructor method
	 * 
	 * @param index
	 *            a multithreaded InvertedIndex
	 */
	public EngineServer(MultiThreadInvertedIndex index) {
		this.index = index;

	}
	/**
	 * @param port
	 *            number of the server's port
	 * @param seed
	 *            seed URL
	 * @param crawler
	 *            webcrawler
	 * @throws Exception exception
	 */
	public void SearchServer(int port, URL seed, WebCrawler crawler)
			throws Exception {
		Server server = new Server(port);
		ServletHandler handler = new ServletHandler();
		try {
			handler.addServletWithMapping(
					new ServletHolder(new SearchServlet(this.index)), "/");
		} catch (IOException e) {
			System.out.println("Input Output Exception in Search Server");
		}
		server.setHandler(handler);
		server.start();
		server.join();
	}
}
