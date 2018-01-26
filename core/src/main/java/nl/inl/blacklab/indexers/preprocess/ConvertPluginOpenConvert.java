package nl.inl.blacklab.indexers.preprocess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ConvertPluginOpenConvert implements ConvertPlugin {
	private static final Logger logger = LogManager.getLogger(ConvertPluginOpenConvert.class);

	@Override
	public String getId() {
		return "OpenConvert";
	}

	@Override
	public String getDisplayName() {
		return "OpenConvert";
	}

	@Override
	public String getDescription() {
		return "File converter using the OpenConvert library";
	}

	private static final Set<String> inputFormats = new HashSet<>(Arrays.asList("doc", "docx", "text", "txt"));
	@Override
	public Set<String> getSupportedInputFormats() {
		return inputFormats;
	}

	private static final Set<String> outputFormats = new HashSet<>(Arrays.asList("folia"));
	@Override
	public Set<String> getSupportedOutputFormats() {
		return outputFormats;
	}

	@Override
	public Set<String> getSupportedInputFormats(String outputFormat) {
		if (outputFormats.contains(outputFormat))
			return inputFormats;

		return Collections.emptySet();
	}

	@Override
	public Set<String> getSupportedOutputFormats(String inputFormat) {
		if (inputFormats.contains(inputFormat))
			return outputFormats;

		return Collections.emptySet();
	}


	private Class<?>  OpenConvertClass;
	private Method    OpenConvertGetConverter;

	private Class<?>  SimpleInputOutputProcessClass;

	private Class<?>  DirectoryHandlingClass;
	private Method    DirectoryHandlingTraverseDirectory;

	public ConvertPluginOpenConvert() {
		//
	}

	@Override
	public void init(ObjectNode config) throws PluginException {
		logger.info("initializing plugin " + getDisplayName());

		if (config == null)
			throw new PluginException("This plugin requires a configuration.");

		URL jarUrl;
		try {
			jarUrl = Paths.get(configStr(config, "jarPath")).toUri().toURL();
		} catch (MalformedURLException e1) {
			throw new PluginException(e1);
		}


		try (URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl})) {
			OpenConvertClass = loader.loadClass("org.ivdnt.openconvert.converters.OpenConvert");
			OpenConvertGetConverter = OpenConvertClass.getMethod("getConverter", String.class, String.class, boolean.class);

			SimpleInputOutputProcessClass = loader.loadClass("org.ivdnt.openconvert.filehandling.SimpleInputOutputProcess");

			DirectoryHandlingClass = loader.loadClass("org.ivdnt.openconvert.filehandling.DirectoryHandling");
			DirectoryHandlingTraverseDirectory = DirectoryHandlingClass.getMethod("traverseDirectory", SimpleInputOutputProcessClass, String.class, String.class, FileFilter.class);
		} catch (ClassNotFoundException | IOException | NoSuchMethodException | SecurityException e) {
			throw new PluginException("Error loading the OpenConvert jar", e);
		}
	}

	@Override
	public boolean isSupported(String inputFileType, String outputFileType) {
		return 	inputFormats.contains(inputFileType) && outputFormats.contains(outputFileType);
	}

	@Override
	public void perform(ByteArrayInputStream is, String inputFileName, ByteArrayOutputStream os, String outputFileName) throws PluginException {
		if (!isSupported(inputFileName, outputFileName))
			throw new PluginException("This extractor does not support conversion from '" + FilenameUtils.getExtension(inputFileName) + "' to '" + FilenameUtils.getExtension(outputFileName) + "'");

		// TODO: don't create temp files but pass memory streams to OpenConvert if at all possible, need to implement first though
		File tmpInput = null, tmpOutput = null;
		try {
			tmpInput = File.createTempFile("___", inputFileName);
			tmpOutput = File.createTempFile("___", outputFileName);
			try (FileOutputStream fos = new FileOutputStream(tmpInput)) {
				IOUtils.copy(is, fos);
			}

			try {
				Object OpenConvertInstance = OpenConvertClass.newInstance();
				Object SimpleInputOutputProcessInstance = OpenConvertGetConverter.invoke(OpenConvertInstance, tmpInput.toPath().toString(), tmpOutput.toPath().toString(), false);

				DirectoryHandlingTraverseDirectory.invoke(null, SimpleInputOutputProcessInstance, tmpInput, tmpOutput, null);

			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new PluginException("Exception while running text extraction: ", e);
			}

			try (FileInputStream fis = new FileInputStream(tmpOutput)) {
				IOUtils.copy(fis, os);
			}
		} catch (IOException e) {
			throw new PluginException("Could not create temp files for conversion from '" + inputFileName + "' to '" + outputFileName + "'", e);
		} finally {
			cleanup(tmpInput, tmpOutput);
		}
	}

	private static void cleanup(File tmpFile1, File tmpFile2) {
		if (tmpFile1 != null)
			tmpFile1.delete();
		if (tmpFile2 != null)
			tmpFile2.delete();
	}

	private static String configStr(ObjectNode config, String nodeName) throws PluginException {
		JsonNode n = config.get(nodeName);
		if (n == null || n instanceof NullNode)
			throw new PluginException("Missing configuration value " + nodeName);

		return n.asText();
	}
}
