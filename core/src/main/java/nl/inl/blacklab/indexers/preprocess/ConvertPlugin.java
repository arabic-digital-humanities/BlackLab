package nl.inl.blacklab.indexers.preprocess;

import java.util.Set;

public interface ConvertPlugin extends Plugin {
	public Set<String> getSupportedInputFormats();
	public Set<String> getSupportedOutputFormats();
	public Set<String> getSupportedInputFormats(String outputFormat);
	public Set<String> getSupportedOutputFormats(String inputFormat);
}
