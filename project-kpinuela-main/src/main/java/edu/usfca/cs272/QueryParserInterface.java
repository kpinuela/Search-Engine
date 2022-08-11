package edu.usfca.cs272;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author kyle An Interface for the Single Threaded and Multithreaded Query
 *         Parser
 *
 */
public interface QueryParserInterface {

	/**
	 * Parses through queries
	 * 
	 * @param path   path
	 * @param search type of search
	 * @throws IOException Input Output Exception
	 */
	public default void parseQuery(Path path, boolean search) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				parseQuery(line, search);
			}
		}
	}

	/**
	 * Parses line by line
	 * 
	 * @param line   line from index
	 * @param search type of search
	 */
	public void parseQuery(String line, boolean search);

	/**
	 * writes the query to the JSOn
	 * 
	 * @param path to be written on to
	 * @throws IOException input Output Exception
	 */
	public void queryWrite(Path path) throws IOException;

}
