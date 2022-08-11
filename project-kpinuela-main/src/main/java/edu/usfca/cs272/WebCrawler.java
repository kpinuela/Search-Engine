package edu.usfca.cs272;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * WebCrawler to store in to the InvertedIndex
 * 
 * @author kyle
 *
 */
public class WebCrawler {
	/**
	 * A Multithreaded InvertedIndex index
	 */
	private final MultiThreadInvertedIndex index;
	/**
	 * the Workqueue for Multithreading
	 */
	private WorkQueue queue;
	/**
	 * urls after they have been processed
	 */
	private Set<URL> urls;

	/**
	 * max amoubnt of URLs
	 */
	private int max;

	/**
	 * Constructor Method
	 * 
	 * @param index
	 *            The Inverted Index
	 * 
	 * @param queue
	 *            the WorkQueue
	 * 
	 * @param max
	 *            max amount
	 * 
	 */
	public WebCrawler(MultiThreadInvertedIndex index, WorkQueue queue,
			int max) {
		this.index = index;
		this.queue = queue;
		this.max = max;
		this.urls = new HashSet<URL>();
	}
	/**
	 * @param url
	 *            the url
	 * @param max
	 *            max amount of URLS
	 */
	public void webCrawl(URL url, int max) {
		urls.add(url);
		queue.execute(new Task(url));
		queue.finish();
	}

	/**
	 * Task class to clean and parse the URL
	 *
	 */
	private class Task implements Runnable {
		/**
		 * url to run through
		 */
		private final URL url;

		/**
		 * Constructor Method
		 * 
		 * @param url
		 *            url
		 */
		public Task(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
			String html = HtmlFetcher.fetch(url, 3);
			html = HtmlCleaner.stripBlockElements(html);
			synchronized (urls) {
				for (URL temp : LinkParser.getValidLinks(url, html)) {
					if (urls.size() >= max) {
						break;
					}
					if (!urls.contains(temp)) {
						urls.add(temp);
						queue.execute(new Task(temp));
					}
				}
			}
			String cleanedhtml = HtmlCleaner.stripHtml(html);
			InvertedIndex local = new InvertedIndex();
			int count = 1;
			for (String stem : TextFileStemmer.listStems(cleanedhtml)) {
				try {
					local.add(stem, url.toString(), count);
				} catch (IOException e) {
					System.out.println("Input Output Exception");
				}
				count++;
			}
			index.addAll(local);
		}
	}
}
