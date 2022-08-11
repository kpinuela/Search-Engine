package edu.usfca.cs272;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.usfca.cs272.InvertedIndex.Result;

/**
 * Extension of simpleJSONwriter for the results
 * 
 * @author kyle
 *
 */
public class ResultsWriter extends SimpleJsonWriter {

	/**
	 * writes the JSON
	 * 
	 * @param resultMap map of results
	 * @param writer    writer
	 * @param indent    indent
	 * @throws IOException input Output
	 */
	public static void writeResult(TreeMap<String, ArrayList<Result>> resultMap, Writer writer, int indent)
			throws IOException {
			writer.write("{");
			Iterator<Entry<String, ArrayList<Result>>> iterate = resultMap.entrySet().iterator();
			if (iterate.hasNext()) {
				Entry<String, ArrayList<Result>> entry = iterate.next();
				if (!entry.getKey().isEmpty()) {
					writer.write("\n");
					writeIndent(writer, indent);
					writeQuote(entry.getKey(), writer, indent + 1);
					writer.write(": ");
					writer.write("[");
					ArrayList<Result> values = entry.getValue();
					writeValues(values, writer, indent);
					writer.write("\n");
					writeIndent(writer, indent + 1);
					writer.write("]");
					if (iterate.hasNext()) {
						writer.write(",");
					}
				}
			}
			while (iterate.hasNext()) {
				Entry<String, ArrayList<Result>> entry = iterate.next();
				if (!entry.getKey().isEmpty()) {
					writer.write("\n");
					writeIndent(writer, indent);
					writeQuote(entry.getKey(), writer, indent + 1);
					writer.write(": ");
					writer.write("[");
					ArrayList<Result> values = entry.getValue();
					writeValues(values, writer, indent);
					writer.write("\n");
					writeIndent(writer, indent + 1);
					writer.write("]");
					if (iterate.hasNext()) {
						writer.write(",");
					}
				}
			}
			writer.write("\n");
			writer.write("}");
		}
	/**
	 * writes the inner TreeMap within the index
	 * @param values values of the map 
	 * @param writer  writer 
	 * @param indent indent 
	 * @throws IOException  IOException
	 * 
	 */
	public static void writeValues(ArrayList<Result> values, Writer writer, int indent) throws IOException {
		Iterator<Result> vIterate = values.iterator();
		if (vIterate.hasNext()) {
			writer.write("\n");
			perQuery(vIterate.next(), writer, indent + 1);
		}
		while (vIterate.hasNext()) {
			writer.write(",");
			writer.write("\n");
			perQuery(vIterate.next(), writer, indent + 1);
		}
	}
	/**
	 * writes the innermost of the JSON with count score and where
	 * 
	 * @param result result
	 * @param writer writer
	 * @param indent writeIndent
	 * @throws IOException input output exception
	 */
	public static void perQuery(Result result, Writer writer, int indent) throws IOException {
		writeIndent("{", writer, indent + 1);
		writer.write("\n");
		writeQuote("count", writer, indent + 2);
		writer.write(": ");
		writeIndent(Integer.toString(result.getCount()), writer, 0);
		writer.write(',');
		writer.write('\n');
		writeQuote("score", writer, indent + 2);
		writer.write(": ");
		writer.write((result.getScore(result.getLocation())));
		writer.write(',');
		writer.write('\n');
		writeQuote("where", writer, indent + 2);
		writer.write(": ");
		writeQuote(result.getLocation(), writer, 0);
		writer.write('\n');
		writeIndent("}", writer, indent + 1);
	}

	/**
	 * writes to the the given path
	 * 
	 * @param resultMap Map of results and stems
	 * @param path      path to be written to
	 * @throws IOException          input outputException
	 * @throws NullPointerException if map is Empty
	 */
	public static void write(TreeMap<String, ArrayList<Result>> resultMap, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writeResult(resultMap, writer, 0);
		}
	}
}
