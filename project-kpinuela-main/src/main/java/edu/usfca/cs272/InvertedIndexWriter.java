package edu.usfca.cs272;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Extends SimpleJSONwriter and takes in an arguement of the InvertedIndex
 * 
 * @author kyle
 */
public class InvertedIndexWriter extends SimpleJsonWriter {

	/**
	 * writes the data in JSON
	 * 
	 * @param map    Inverted Index
	 * @param writer writer to form JSON
	 * @param indent indents lines
	 * @throws IOException if File is not Found
	 */
	public static void write(TreeMap<String, TreeMap<String, TreeSet<Integer>>> map, Writer writer, int indent)
			throws IOException {
		Iterator<Entry<String, TreeMap<String, TreeSet<Integer>>>> iterate;
		iterate = map.entrySet().iterator();
		writer.write("{");
		writer.write("\n");
		if (iterate.hasNext()) {
			writeIndent(writer, indent);
			String key = iterate.next().getKey();
			writeQuote(key, writer, indent + 1);
			writer.write(": ");
			writeNestedArray(map.get(key), writer, indent + 1);
			while (iterate.hasNext()) {
				writer.write(",");
				writer.write("\n");
				String next = iterate.next().getKey();
				writeQuote(next, writer, indent + 1);
				writer.write(": ");
				writeNestedArray(map.get(next), writer, indent + 1);
			}
			writer.write("\n");
		}
		writeIndent("}", writer, indent);
	}
	/**
	 * write method without the writer as a parameter
	 * 
	 * @param map  Inverted Index
	 * @param path path of file
	 * @throws IOException if file is null
	 */
	public static void write(TreeMap<String, TreeMap<String, TreeSet<Integer>>> map, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			write(map, writer, 0);
		}
	}

	/**
	 * String version of write function
	 * 
	 * @param map  Inverted Index
	 * @param path path of file
	 * @return string version of writer
	 * @throws IOException if file is null
	 */
	public static String writeString(TreeMap<String, TreeMap<String, TreeSet<Integer>>> map, Path path)
			throws IOException {
		try {
			StringWriter writer = new StringWriter();
			write(map, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}
}