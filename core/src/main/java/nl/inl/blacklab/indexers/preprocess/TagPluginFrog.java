package nl.inl.blacklab.indexers.preprocess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TagPluginFrog implements TagPlugin {
	private static final Logger logger = LogManager.getLogger(TagPluginFrog.class);

	@Override
	public String getId() {
		return "frog";
	}

	@Override
	public String getDisplayName() {
		return "frog";
	}

	@Override
	public String getDescription() {
		return "Folia tagger using the Frog library";
	}


	@Override
	public void init(ObjectNode config) throws PluginException {
		logger.info("initializing plugin " + getDisplayName());
	}

	@Override
	public boolean isSupported(String inputFileType, String outputFileType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void perform(ByteArrayInputStream is, String inputFileName, ByteArrayOutputStream os, String outputFileName)
			throws PluginException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getInputFormat() {
		// TODO Auto-generated method stub
		return null;
	}
}
