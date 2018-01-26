package nl.inl.blacklab.indexers.preprocess;

import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import nl.inl.blacklab.index.DocIndexer;

public class DocIndexerConvertAndTag extends DocIndexer {
	String converter;
	String outputFormat;
	String tagger;

	public DocIndexerConvertAndTag(String converter, String outputFormat, String tagger) {
		this.converter = converter;
		this.outputFormat = outputFormat;
		this.tagger = tagger;
	}


	@Override
	public void setDocument(Reader reader) {
		// TODO Auto-generated method stub



	}

	@Override
	public void index() throws Exception {
		if ("openconvert".equals(converter)) {

		}

	}

	@Override
	public int getCharacterPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void reportCharsProcessed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reportTokensProcessed() {
		// TODO Auto-generated method stub

	}


	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * Get the list of output formats supported by the currently loaded plugins
	 * @return a list of format names understood by this class
	 */
	// TODO delegate to conversion plugin(s)
	public static List<String> getAvailableOutputFormats() {
		return Arrays.asList("folia");
	}

	/**
	 * Get the list of plugin ids that support converting to the given format
	 * @param format
	 * @return a list of converter names understood by this class
	 */
	// TODO delegate to conversion plugin(s)
	public static List<String> getAvailableConvertersForFormat(String format) {
		switch (format) {
		case "folia": return Arrays.asList("openconvert");
		default: return Collections.emptyList();
		}
	}

	/**
	 * Get the list of plugin ids that support tagging the given format
	 * @param format
	 * @return a list of tagger names understood by this class
	 */
	// TODO delegete to tagging plugin(s)
	public static List<String> getAvailableTaggersForFormat(String format) {
		switch (format) {
		case "folia": return Arrays.asList("frog");
		default: return Collections.emptyList();
		}
	}
}
