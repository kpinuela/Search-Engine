package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import edu.usfca.cs272.InvertedIndex.Result;

/**
 * Parses through queries and adds them to a TreeMap
 * 
 * @author kyle
 */
public class QueryParser implements QueryParserInterface {

	/**
	 * Initialize index and map with results
	 */
	private final TreeMap<String, ArrayList<Result>> resultMap;
	/**
	 * Inverted Index instance
	 */
	private final InvertedIndex index;

	/**
	 * constructor method
	 * 
	 * @param index the inverted index constructor method
	 */
	public QueryParser(InvertedIndex index) {
		resultMap = new TreeMap<>();
		this.index = index;
	}
	/**
	 * parses the line from a buffered reader and puts
	 * 
	 * @param line   line from the buffered
	 * @param search type of search
	 */
	@Override
	public void parseQuery(String line, boolean search) {
		Set<String> queryStems = TextFileStemmer.uniqueStems(line);
		if (!queryStems.isEmpty()) {
			String joined = String.join(" ", queryStems);
			if (!resultMap.containsKey(joined)) {
				resultMap.put(joined, index.search(queryStems, search));
			}
		}
	}

	/**
	 * Writes the queries to the JSON
	 * 
	 * @param path path to be written on
	 * @throws IOException input outputException
	 */
	@Override
	public void queryWrite(Path path) throws IOException {
		ResultsWriter.write(resultMap, path);
	}
}
