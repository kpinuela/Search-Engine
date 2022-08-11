package edu.usfca.cs272;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author kyle
 *
 */
public class InvertedIndexWriter extends SimpleJsonWriter {


	/**
	 * writes the data in JSON 
	 * @param map Inverted Index 
	 * @param writer writer to form JSON
	 * @param indent indents lines
	 * @throws IOException if File is not Found
	 */
	public static void write(Map<String, Map<String, Set<Integer>>> map, Writer writer, int indent) throws IOException {
		Iterator<Entry<String, Map<String, Set<Integer>>>> iterate;
		iterate = map.entrySet().iterator();
		try {
			writer.write("{");
			
			if (iterate.hasNext()) {

				while (iterate.hasNext()) {
					Entry<String, Map<String, Set<Integer>>> entry = iterate.next();
					writer.write("\n");
					writeQuote(entry.getKey(), writer, indent + 1);
					writer.write(": ");
					writeNestedArray(entry.getValue(), writer, indent + 1);
					System.out.println( "Get Value:    " + entry.getValue().toString());
					if (iterate.hasNext()) {
						writer.write(",");
					}
				}

			}
		} catch (IOException e) {
			
		}
		if (!iterate.hasNext()) {
			writer.write("\n");
		}
		writeIndent("}", writer, indent);

	}
	/**
	 * @param map Inverted Index
	 * @param path path of file
	 * @throws IOException if file is null
	 */
	public static void write(Map<String, Map<String, Set<Integer>>> map, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			write(map, writer,0);
		}
		catch (IOException e){
			
			
		}
	}
	



}
	


