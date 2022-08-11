package edu.usfca.cs272;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author kyle Multithreaded version of the Inverted Index
 *
 */
public class MultiThreadInvertedIndexBuilder extends InvertedIndexBuilder {
	/**
	 * @param start First path that is either file or directory
	 * @param map   multithreaded inverted index
	 * @param queue The Work Queue
	 * @throws IOException          IOException
	 * @throws NullPointerException NullPointerException
	 */
	public static void build(Path start, MultiThreadInvertedIndex map, WorkQueue queue)
			throws IOException, NullPointerException {
		if (Files.isDirectory(start)) {
			traverseDirectory(start, map, queue);
		} else {
			queue.execute(new Task(start, map));
		}
		queue.finish();
	}

	/**
	 * Checks for each path of a directory and adds it to the inverted index
	 * 
	 * @param directory the directory to be traversed
	 * @param index     the InvertedIndex where the files will be added
	 * @param queue     the work queue
	 * @throws IOException          if FileNotFound
	 * @throws NullPointerException if value is null
	 */
	private static void traverseDirectory(Path directory, MultiThreadInvertedIndex index, WorkQueue queue)
			throws IOException, NullPointerException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					traverseDirectory(path, index, queue);
				} else if (InvertedIndexBuilder.isTextFile(path)) {
					queue.execute(new Task(path, index));
				}
			}
		}
	}

	/**
	 * @author kyle static inner class to run the task
	 *
	 */
	private static class Task implements Runnable {
		/**
		 * Path of File
		 */
		private final Path file;

		/**
		 * The multithread index
		 */
		private final MultiThreadInvertedIndex index;

		/**
		 * @param file  file path
		 * @param index the inverted index
		 */
		public Task(Path file, MultiThreadInvertedIndex index) {
			this.file = file;
			this.index = index;
		}

		/**
		 * runs the task
		 */
		@Override

		public void run() {
			InvertedIndex local = new InvertedIndex();
			try {
				InvertedIndexBuilder.addFile(file, local);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			index.addAll(local);
		}
	}
}