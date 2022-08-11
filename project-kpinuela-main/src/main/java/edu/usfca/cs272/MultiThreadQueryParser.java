package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import edu.usfca.cs272.InvertedIndex.Result;

/**
 * Multi Threaded version of Query Parser
 * 
 * @author kyle
 *
 */
public class MultiThreadQueryParser implements QueryParserInterface {

	/**
	 * MultiThreaded InvertedIndex
	 */
	private final MultiThreadInvertedIndex index; 
	/**
	 * Initialize index and map with results
	 */
	private final TreeMap<String, ArrayList<Result>> resultMap; 

	/**
	 * work queue instance
	 */
	private final WorkQueue queue;

	/**
	 * Constructor
	 * 
	 * @param index invertedIndex
	 * @param queue workQueue
	 */
	public MultiThreadQueryParser(MultiThreadInvertedIndex index, WorkQueue queue) {
		this.index = index;
		this.queue = queue;
		resultMap = new TreeMap<String, ArrayList<Result>>();
	}

	/**
	 * parse through query
	 * 
	 * @param path   path to be parsed
	 * @param search type of search
	 * @throws IOException Input output exception
	 */
	@Override
	public void parseQuery(Path path, boolean search) throws IOException {
		QueryParserInterface.super.parseQuery(path, search);
		queue.finish();
	}

	@Override
	public void parseQuery(String line, boolean search) {
		queue.execute(new Task(line, search));
	}

	/**
	 * Writes the queries to the JSON
	 * 
	 * @param path path to be written on
	 * @throws IOException input outputException
	 */
	@Override
	public void queryWrite(Path path) throws IOException {
		synchronized (resultMap) {
			ResultsWriter.write(resultMap, path);
		}
	}

	/**
	 * Task Class for parsing the Queries
	 * @author kyle static inner class to run
	 */
	public class Task implements Runnable {

		/**
		 * line by line
		 */
		private String line;

		/**
		 * to determine search to use
		 */
		private Boolean search;

		/**
		 * Constructor Method
		 * 
		 * @param line   line by line parse
		 * @param search type of search to use
		 */
		public Task(String line, boolean search) {
			this.line = line;
			this.search = search;
		}

		@Override
		public void run() {
			Set<String> queryStems = TextFileStemmer.uniqueStems(line);
			String joined = String.join(" ", queryStems);
			synchronized (resultMap) {
				if (resultMap.containsKey(joined)) {
					return;
				}
			}
			var local = index.search(queryStems, search);
			synchronized (resultMap) {
				resultMap.put(joined, local);
			}
		}
	}
}