package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Builder Class for the Inverted Index
 * 
 * @author kyle
 */
public class InvertedIndexBuilder {
	/**
	 * builds the class
	 * 
	 * @param start first path
	 * @param map   inverted index
	 * @throws IOException          input output exception
	 * @throws NullPointerException null pointer exception
	 */
	public static void build(Path start, InvertedIndex map) throws IOException, NullPointerException {
		if (Files.isDirectory(start)) {
			InvertedIndexBuilder.traverseDirectory(start, map);
		} else {
			InvertedIndexBuilder.addFile(start, map);
		}
	}
	
	/**
	 * Checks for each path of a directory and adds it to the inverted index
	 * 
	 * @param directory the directory to be traversed
	 * @param map       the InvertedIndex where the files will be added
	 * @throws IOException          if FileNotFound
	 * @throws NullPointerException if value is null
	 */
	private static void traverseDirectory(Path directory, InvertedIndex map) throws IOException, NullPointerException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					traverseDirectory(path, map);
				}
				if (Files.isRegularFile(path)) {
					if (isTextFile(path)) {
						addFile(path, map);
					}
				}
			}
		}
	}

	/**
	 * checks if a file is a Text file
	 * 
	 * @param path path of file
	 * @return True if is text file, false if else
	 */
	public static boolean isTextFile(Path path) {
		String lower = path.toString().toLowerCase();
		return lower.endsWith(".txt") || lower.endsWith(".text");
	}

	/**
	 * adds the file and stems to the inverted index
	 * 
	 * @param file file to be added
	 * @param map  Inverted Index Object
	 * @throws IOException          Input Output exception
	 * @throws NullPointerException null pointer exception
	 */
	public static void addFile(Path file, InvertedIndex map) throws IOException, NullPointerException {
		try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
			Stemmer stemmer = new SnowballStemmer(ENGLISH);
			int count = 1;
			String location = file.toString();
			String reader;
			while ((reader = br.readLine()) != null) {
				String[] parsed = TextParser.parse(reader);
				for (String word : parsed) {
					String stemmed = stemmer.stem(word).toString();
					map.add(stemmed, location, count);
					count++;
				}
			}
		}
	}
}
