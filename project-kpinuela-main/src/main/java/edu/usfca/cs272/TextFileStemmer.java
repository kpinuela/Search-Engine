package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Utility class for parsing and stemming text and text files into collections
 * of stemmed words.
 *
 * @see TextParser
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class TextFileStemmer {
	/**
	 * stems each line in a collection
	 * 
	 * @param line    line to be parsed
	 * @param stemmer to stem files
	 * @param stems   data structure to hold the stems
	 */
	public static void stemLine(String line, Stemmer stemmer, Collection<String> stems) {
		String[] parsed = TextParser.parse(line);
		for (String word : parsed) {
			String stemmed = stemmer.stem(word).toString();
			stems.add(stemmed);
		}
	}

	/**
	 * Parses each line into cleaned and stemmed words.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a list of cleaned and stemmed words in parsed order
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static Collection<String> listStems(String line, Stemmer stemmer) {
		List<String> stems = new ArrayList<>();
		stemLine(line, stemmer, stems);
		return stems;
	}

	/**
	 * Parses each line into cleaned and stemmed words using the default stemmer.
	 *
	 * @param line the line of words to parse and stem
	 * @return a list of cleaned and stemmed words in parsed order
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see #listStems(String, Stemmer)
	 */
	public static Collection<String> listStems(String line) {
		return listStems(line, new SnowballStemmer(ENGLISH));
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words
	 * using the default stemmer.
	 *
	 * @param input the input file to parse and stem
	 * @return a list of stems from file in parsed order
	 * @throws IOException if unable to read or parse file
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see StandardCharsets#UTF_8
	 * @see #listStems(String, Stemmer)
	 */
	public static List<String> listStems(Path input) throws IOException {
		try (BufferedReader br = Files.newBufferedReader(input, StandardCharsets.UTF_8);) {
			Stemmer stemmer = new SnowballStemmer(ENGLISH);
			ArrayList<String> list = new ArrayList<String>();
			String reader;
			while ((reader = br.readLine()) != null) {
				stemLine(reader, stemmer, list);
			}
			return list;
		}
	}

	/**
	 * Parses the line into unique, sorted, cleaned, and stemmed words.
	 *
	 * @param line    the line of words to parse and stem
	 * @param stemmer the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static Set<String> uniqueStems(String line, Stemmer stemmer) {
		Set<String> set = new TreeSet<String>();
		stemLine(line, stemmer, set);
		return set;
	}

	/**
	 * Parses the line into unique, sorted, cleaned, and stemmed words using the
	 * default stemmer.
	 *
	 * @param line the line of words to parse and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static Set<String> uniqueStems(String line) {
		return uniqueStems(line, new SnowballStemmer(ENGLISH));
	}

	/**
	 * Reads a file line by line, parses each line into unique, sorted, cleaned, and
	 * stemmed words using the default stemmer.
	 *
	 * @param input the input file to parse and stem
	 * @return a sorted set of unique cleaned and stemmed words from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see StandardCharsets#UTF_8
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static Set<String> uniqueStems(Path input) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(input.toString()))) {
			String line;
			Set<String> set = new TreeSet<String>();
			Stemmer stemmer = new SnowballStemmer(ENGLISH);
			while ((line = reader.readLine()) != null) {
				stemLine(line, stemmer, set);
			}
			return set;
		}
	}

	/**
	 * Reads a file line by line, parses each line into unique, sorted, cleaned, and
	 * stemmed words using the default stemmer, and adds the set of unique sorted
	 * stems to a list per line in the file.
	 *
	 * @param input the input file to parse and stem
	 * @return a list where each item is the sets of unique sorted stems parsed from
	 *         a single line of the input file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see StandardCharsets#UTF_8
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static List<Set<String>> listUniqueStems(Path input) throws IOException {
		ArrayList<Set<String>> finalList = new ArrayList<Set<String>>();
		Stemmer stemmer = new SnowballStemmer(ENGLISH);
		try (BufferedReader br = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {
			String read;
			while ((read = br.readLine()) != null) {
				finalList.add(uniqueStems(read, stemmer));
			}
		}

		return finalList;
	}

}
