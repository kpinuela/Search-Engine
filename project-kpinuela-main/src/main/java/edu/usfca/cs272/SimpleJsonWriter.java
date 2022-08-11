package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using tabs.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class SimpleJsonWriter {
	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level; the first bracket is not indented,
	 *                 inner elements are indented by one, and the last bracket is
	 *                 indented at the initial indentation level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeArray(Collection<Integer> elements, Writer writer, int indent) throws IOException {
		Iterator<Integer> iterate = elements.iterator();
		writer.write("[");
		writer.write("\n");
		if (iterate.hasNext()) {
			writeIndent(iterate.next().toString(), writer, indent + 1);
			while (iterate.hasNext()) {
				writer.write(",");
				writer.write("\n");
				writeIndent(iterate.next().toString(), writer, indent + 1);
			}
			writer.write("\n");
		}
		writeIndent("]", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param indent   the initial indent level; the first bracket is not indented,
	 *                 inner elements are indented by one, and the last bracket is
	 *                 indented at the initial indentation level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeObject(Map<String, Integer> elements, Writer writer, int indent) throws IOException {
		writer.write("{");
		writer.write("\n");
		Iterator<String> iterate = elements.keySet().iterator();
		if (iterate.hasNext()) {
			String key = iterate.next();
			writeQuote(key, writer, indent + 1);
			writer.write(": " + elements.get(key));
			while (iterate.hasNext()) {
				writer.write(",");
				writer.write("\n");
				String next = iterate.next();
				writeQuote(next, writer, indent + 1);
				writer.write(": " + elements.get(next));
			}
			writer.write("\n");
		}
		writeIndent("}", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of integer objects.
	 *
	 * @param map    the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *               inner elements are indented by one, and the last bracket is
	 *               indented at the initial indentation level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeNestedArray(Map<String, ? extends Collection<Integer>> map, Writer writer, int indent)
			throws IOException {
		Iterator<String> iterate = map.keySet().iterator();
		writer.write("{");
		writer.write("\n");
		if (iterate.hasNext()) {
			writeIndent(writer, indent);
			var key = iterate.next();
			writeQuote(key.toString(), writer, indent);
			writer.write(": ");
			writeArray(map.get(key), writer, indent + 1);
			while (iterate.hasNext()) {
				writer.write(",");
				writer.write("\n");
				writeIndent(writer, indent);
				var next = iterate.next();
				writeQuote(next.toString(), writer, indent);
				writer.write(": ");
				writeArray(map.get(next), writer, indent + 1);
			}
			writer.write("\n");
		}
		writeIndent("}", writer, indent);
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static void writeArray(Collection<Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #writeObject(Map, Writer, int)
	 */
	public static void writeObject(Map<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #writeNestedArray(Map, Writer, int)
	 */
	public static void writeNestedArray(Map<String, ? extends Collection<Integer>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeNestedArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static String writeArray(Collection<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #writeObject(Map, Writer, int)
	 */
	public static String writeObject(Map<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #writeNestedArray(Map, Writer, int)
	 */
	public static String writeNestedArray(Map<String, ? extends Collection<Integer>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeNestedArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}
	/**
	 * Indents the writer by the specified number of times. Does nothing if the
	 * indentation level is 0 or less.
	 *
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(Writer writer, int indent) throws IOException {
		while (indent-- > 0) {
			writer.write('\t');
		}
	}
	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param indent  the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param indent  the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeQuote(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}
}
