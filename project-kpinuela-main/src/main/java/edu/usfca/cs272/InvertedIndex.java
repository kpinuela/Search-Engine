package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author kyle
 *
 */
public class InvertedIndex {
	/**
	 * Map for the inverted index
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> map;
	/**
	 * Map to keep track of wordCOunt
	 */
	private final Map<String, Integer> countMap;

	/**
	 * Constructor method
	 */
	public InvertedIndex() {
		map = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		countMap = new TreeMap<String, Integer>();
	}

	/**
	 * writes the Index to the JSON
	 * 
	 * @param path path from file writes the Index
	 * @throws IOException input output exception
	 */
	public void indexWrite(Path path) throws IOException {
		InvertedIndexWriter.write(map, path);
	}

	/**
	 * writes the Count to the index
	 * 
	 * @param path path to be written on
	 * @throws IOException input output exception
	 */
	public void countWrite(Path path) throws IOException {
		SimpleJsonWriter.writeObject(countMap, path);

	}

	/**
	 * Adds a word and path to the index, and adds to the count.
	 * 
	 * @param word  the string in the file //
	 * @param path  path of file
	 * @param value where the string is
	 * @throws IOException input ouput Exception
	 */
	public void add(String word, String path, int value) throws IOException {
		map.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		map.get(word).putIfAbsent(path, new TreeSet<Integer>());
		map.get(word).get(path).add(value);
		if (!countMap.containsKey(path) || countMap.get(path) < value) {
			countMap.put(path, value);
		}
	}

	/**
	 * adds the contests of one Inverted Index to another
	 * 
	 * @param local local instance of InvertedIndex
	 * 
	 */
	public void addAll(InvertedIndex local) {
		for (String word : local.map.keySet()) {
			if (this.map.containsKey(word)) {
				for (String location : local.map.get(word).keySet()) {
					if (this.map.get(word).containsKey(location)) {
						this.map.get(word).get(location).addAll(local.map.get(word).get(location));
					} else {
						this.map.get(word).put(location, local.map.get(word).get(location));
					}
				}
			} else {
				this.map.put(word, local.map.get(word));
			}
		}
		for (String location : local.countMap.keySet()) {
			if (!this.countMap.containsKey(location)) {
				this.countMap.put(location, local.countMap.get(location));
			} else {
				this.countMap.put(location, this.countMap.get(location) + local.countMap.get(location));
			}
		}
	}

	/**
	 * returns the word count
	 * 
	 * @param path path of file
	 * @return word count
	 */
	public int getWordCount(String path) {
		return countMap.getOrDefault(path, null);
	}

	/**
	 * returns which search is to be conducted
	 * 
	 * @param queryStems stems
	 * @param exact      type of search
	 * @return array list of Results
	 */
	public ArrayList<Result> search(Set<String> queryStems, boolean exact) {
		return exact ? exactSearch(queryStems) : partialSearch(queryStems);
	}

	/**
	 * Exact search Method
	 * 
	 * @param queryStems stemmed from queries
	 * @return arrav list of result
	 */
	public ArrayList<Result> exactSearch(Set<String> queryStems) {
		ArrayList<Result> resultList = new ArrayList<Result>();
		HashMap<String, Result> lookup = new HashMap<String, Result>();
		for (String stem : queryStems) {
			if (map.containsKey(stem)) {
				searchHelper(resultList, lookup, stem);
			}
		}
		Collections.sort(resultList);
		return resultList;
	}

	/**
	 * Partial Search Method
	 * 
	 * @param queryStems stemmed from queries
	 * @return arraylist of result
	 */
	public ArrayList<Result> partialSearch(Set<String> queryStems) {
		ArrayList<Result> resultList = new ArrayList<Result>();
		HashMap<String, Result> lookup = new HashMap<String, Result>();
		for (String stem : queryStems) {
			for (String word : map.tailMap(stem).keySet()) {
				if (!word.startsWith(stem)) {
					break;
				} else {
					searchHelper(resultList, lookup, word);
				}
			}
		}
		Collections.sort(resultList);
		return resultList;
	}

	/**
	 * A helper method for the search
	 * 
	 * @param resultList list of results
	 * @param lookup     the lookup map
	 * @param word       the word in the search
	 */
	private void searchHelper(ArrayList<Result> resultList, HashMap<String, Result> lookup, String word) {
		for (String file : map.get(word).keySet()) {
			if (!lookup.containsKey(file)) {
				Result newResult = new Result(file);
				lookup.put(file, newResult);
				resultList.add(newResult);
			}
			lookup.get(file).update(word);
		}

	}

	/**
	 * A Size function that returns the size of the inverted index
	 * 
	 * @return # of words or keys in map
	 */
	public int size() {
		return map.size();
	}

	/**
	 * A Size function that returns the size of the inverted index
	 * 
	 * @param word in index
	 * @return # of locations for the word in the inner map
	 */
	public int size(String word) {
		if (map.containsKey(word)) {
			return map.get(word).size();
		}
		return 0;
	}

	/**
	 * A Size function that returns the size of the inverted index
	 * 
	 * @param word     word in map
	 * @param location location in map
	 * @return # of positions of the word and location in the map
	 */
	public int size(String word, String location) {
		if (contains(word, location)) {
			return this.map.get(word).get(location).size();
		}
		return 0;
	}

	/**
	 * A Boolean Function that checks if the index contains the following parameters
	 * 
	 * @param word string in the file
	 * @return True if string is in the InvertedIndex
	 */
	public boolean contains(String word) {
		return map.containsKey(word);
	}

	/**
	 * 
	 * A Boolean Function that checks if the index contains the following parameters
	 * 
	 * @param word     string in index
	 * @param location location in text file
	 * @return true if string is in index
	 */
	public boolean contains(String word, String location) {
		return contains(word) && map.get(word).containsKey(location);
	}

	/**
	 * A Boolean Function that checks if the index contains the following parameters
	 * 
	 * @param word     word in index
	 * @param location the location of the word
	 * @param position position in the index
	 * @return true if contains, false if not
	 */
	public boolean contains(String word, String location, int position) {
		return contains(word) && map.get(word).get(location).contains(position);
	}

	@Override
	public String toString() {
		return map.toString();
	}

	/**
	 * a get Function for the set of keys from the Inverted Index
	 * 
	 * @return gets the value assigned to the key, if empty returns empty
	 */

	public Set<String> get() {
		return Collections.unmodifiableSet(map.keySet());
	}

	/**
	 * a get Function for the set of keys from the Inverted Index
	 * 
	 * @param word word in set
	 * @return set of keys, or an empty set if it is empty
	 */
	public Set<String> get(String word) {
		if (contains(word)) {
			return Collections.unmodifiableSet(map.get(word).keySet());
		}
		return Collections.emptySet();
	}

	/**
	 * a get Function for the set of keys from the Inverted Index
	 * 
	 * @param word     word in index
	 * @param location the location of the word
	 * @return set of keys or empty set
	 */
	public Set<String> get(String word, String location) {
		if (contains(word, location)) {
			return Collections.unmodifiableSet(map.keySet());
		}
		return Collections.emptySet();
	}

	/**
	 * Result Class for Queries
	 * 
	 * @author kyle
	 *
	 */
	public class Result implements Comparable<Result> {
		/**
		 * name of file
		 */
		private String location;

		/**
		 * count for query
		 */
		private int queryCount;

		/**
		 * scored based on word count
		 */
		private double score;

		/**
		 * cosntructor method
		 * 
		 * @param location location of file
		 */
		public Result(String location) {
			this.location = location;
			this.score = 0;
			this.queryCount = 0;
		}

		@Override
		/**
		 * compares by score, count
		 * 
		 * @param o result
		 */

		public int compareTo(Result o) {
			if (Double.compare(o.score, score) == 0) {
				if (Integer.compare(o.getCount(), getCount()) == 0) {
					return location.compareToIgnoreCase(o.location);
				}
				return Integer.compare(o.queryCount, queryCount);
			} else {
				return Double.compare(o.score, score);
			}

		}

		/**
		 * get method for name
		 * 
		 * @return location
		 */
		public String getLocation() {
			return location;
		}

		/**
		 * get method for count
		 * 
		 * @return count
		 */
		public int getCount() {
			return queryCount;
		}

		/**
		 * get method to reformat and get score
		 * 
		 * @param name file to get score from
		 * @return score reformatted to a string
		 */
		public String getScore(String name) {
			String formatted = String.format("%.8f", score);
			return formatted;
		}

		/**
		 * updates the index count
		 * 
		 * @param word word in index
		 */
		private void update(String word) {
			this.queryCount += map.get(word).get(location).size();
			score = (double) queryCount / (double) countMap.get(location);
		}
	}
}