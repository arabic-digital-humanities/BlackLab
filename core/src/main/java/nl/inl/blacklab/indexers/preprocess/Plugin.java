package nl.inl.blacklab.indexers.preprocess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Interface of converting a plugin (including using external services)
 * Only a single instance of a plugin is constructed, so plugins must be threadsafe.
 *
 * A plugin must define a no-argument constructor.
 */
public interface Plugin {

	public static class PluginException extends Exception {
		public PluginException() {
			super();
		}

		public PluginException(String message, Throwable cause) {
			super(message, cause);
		}

		public PluginException(String message) {
			super(message);
		}

		public PluginException(Throwable cause) {
			super(cause);
		}
	}

	/**
	 * Return a globally unique id for this plugin class.
	 * This ID must be constant across runs and versions.
	 *
	 * @return the global identifier for this plugin
	 */
	public String getId();

	/**
	 * Return a user-friendly name for this plugin that can be used in messages, etc.
	 *
	 * @return a user-friendly name for this plugin
	 */
	public String getDisplayName();


	public String getDescription();

	/**
	 * Initializes the plugin, called once after the initial loading of the class.
	 *
	 * @param config the config settings for this plugin, may be null.
	 * @throws PluginException
	 */
	public void init(ObjectNode config) throws PluginException;

	/**
	 * @param inputFileType type of the input file, can be either a simple file extension or a format, up to the implementation
	 * @param outputFileType type of the output file, can be either a simple file extension or a format, up to the implementation
	 * @return true if supported
	 */
	public boolean isSupported(String inputFileType, String outputFileType);

	/**
	 * @param is
	 * @param inputFileName full name of the input file, including extension, but excluding path information
	 * @param os
	 * @param outputFileName full name of the output file, including extension, but excluding path information
	 * @throws PluginException
	 */
	public void perform(ByteArrayInputStream is, String inputFileName, ByteArrayOutputStream os, String outputFileName) throws PluginException;
}

