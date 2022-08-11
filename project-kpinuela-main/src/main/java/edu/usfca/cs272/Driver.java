package edu.usfca.cs272;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Kyle Pinuela
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class Driver {
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search
	 * an inverted index.
	 *
	 * @param args
	 *            flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		ArgumentParser parser = new ArgumentParser(args);
		InvertedIndex index;
		QueryParserInterface queryparser;
		MultiThreadInvertedIndex threadIndex = null;
		WorkQueue queue = null;
		URL seed;
		if (parser.hasFlag("-threads") || parser.hasFlag("-html")||parser.hasFlag("-server")) {
			int defaultThreads = 5;
			int threads = parser.getInteger("-threads", defaultThreads);
			if (threads <= 0) {
				threads = defaultThreads;
			}
			queue = new WorkQueue(threads);
			threadIndex = new MultiThreadInvertedIndex();
			index = threadIndex;
			queryparser = new MultiThreadQueryParser(threadIndex, queue);
		} else {
			index = new InvertedIndex();
			queryparser = new QueryParser(index);
		}
		if(parser.hasFlag("-server")) {
			int port =parser.getInteger("-server",8080);
			EngineServer server = new EngineServer(threadIndex);
			try {
				seed = new URL(parser.getString("-html"));
				if (parser.hasFlag("-max")) {
					int max = parser.getInteger("-max", 1);
					WebCrawler crawler = new WebCrawler(threadIndex, queue,
							max);
					crawler.webCrawl(seed, max);
					server.SearchServer(port, seed, crawler);
				}
			} catch (MalformedURLException e) {
				System.out.println("Invalid URL");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (parser.hasFlag("-html")) {
			try {
				seed = new URL(parser.getString("-html"));
				if (parser.hasFlag("-max")) {
					int max = parser.getInteger("-max", 1);
					WebCrawler crawler = new WebCrawler(threadIndex, queue,
							max);
					crawler.webCrawl(seed, max);
				}
			} catch (MalformedURLException e) {
				System.out.println("Invalid URL");
			}
		}
		if (parser.hasFlag("-text")) {
			Path path = parser.getPath("-text");
			try {
				if (path != null) {
					if (threadIndex != null) {
						MultiThreadInvertedIndexBuilder.build(path, threadIndex,
								queue);
					} else {
						InvertedIndexBuilder.build(path, index);
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("File not Found at " + path.toString());
			} catch (IOException e) {
				System.out.println("Warning: Input/Output Invalid at:  "
						+ path.toString());
			} catch (NullPointerException e) {
				System.out
						.println("Warning: Null Value at: " + path.toString());
			}
		}

		if (parser.hasFlag("-index")) {
			Path path = parser.getPath("-index", Path.of("index.json"));
			try {
				index.indexWrite(path);
			} catch (IOException e) {
				System.out.println("Invalid Input for query");
			}
		}

		if (parser.hasFlag("-query")) {
			Path path = parser.getPath("-query", Path.of("results.json"));
			try {
				queryparser.parseQuery(path, parser.hasFlag("-exact"));
			} catch (IOException e) {
				System.out.println("Invalid Input for query");
			}
		}

		if (parser.hasFlag("-counts")) {
			Path path = parser.getPath("-counts", Path.of("counts.json"));
			try {
				index.countWrite(path);
			} catch (IOException e) {
				System.out.println("Invalid Input output");
			}
		}

		if (parser.hasFlag("-results")) {
			Path path = parser.getPath("-results", Path.of("results.json"));
			try {
				queryparser.queryWrite(path);
			} catch (IOException e) {
				System.out.println("Invalid Results Input");
			}
		}
		if (queue != null) {
			queue.shutdown();
		}
	}
}