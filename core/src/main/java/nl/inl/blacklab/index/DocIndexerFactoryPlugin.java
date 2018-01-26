package nl.inl.blacklab.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.inl.blacklab.index.config.YamlJsonReader;
import nl.inl.blacklab.indexers.preprocess.Plugin;
import nl.inl.blacklab.indexers.preprocess.Plugin.PluginException;

/**
 * Responsible for loading file conversion and tagging plugins, and exposing chained series of plugins to {@link DocumentFormats}.
 *<p>
 * It will attempt to load all conversion plugins on the classpath and initialize them with their respective configurations in the main blacklab config.
 * It will then query the plugins to see what sorts of conversions they support (for example from plaintext => tei, or  from tei => folia),
 * and create "chains" of plugins that feed into each other, to eventually end up with a format that is supported by a regular format in the DocumentFormats list.
 * It will then register its own formats that extend/wrap those existing formats, so that users get a selection of available conversion chains automatically.
 *<p>
 * Plugins are loaded from the classpath, according to the {@link ServiceLoader} system.
 * So a jar that wishes to register a plugin must contain a file named "nl.inl.blacklab.indexers.preprocess.Plugin" inside "META-INF/services/",
 * containing the qualified classNames of the implementations they contain.
 */
public class DocIndexerFactoryPlugin implements DocIndexerFactory {
	private static final Logger logger = LogManager.getLogger(DocIndexerFactoryPlugin.class);

	private static final String FORMATIDENTIFIER_PREFIX = "$plugin/";

	private static boolean isInitialized = false;

	private static Map<String, Plugin> plugins = new HashMap<>();

	/**
	 * Attempts to load and initialize all plugin classes on the classpath, passing the values in the config to the matching plugin.
	 *
	 * @param pluginConfig the plugin configurations collection object. The format for this object is <pre>
	 * {
	 *   "pluginId": {
	 *     // arbitrary plugin config here
	 *   },
	 *
	 *   "anotherPluginId": { ... },
	 *   ...
	 * } </pre>
	 */
	public static void initPlugins(ObjectNode pluginConfig) {
		if (isInitialized)
			throw new RuntimeException("PluginManager already initialized");

		logger.info("Initializing plugin system");

		Iterator<Plugin> it = ServiceLoader.load(Plugin.class).iterator();
		while (it.hasNext()) {
			String id = null;

			try {
				Plugin plugin = it.next();
				id = plugin.getId();

				JsonNode config = pluginConfig.get(plugin.getId());
				if (config == null || config instanceof NullNode)
					plugin.init(null);
				else
					plugin.init(YamlJsonReader.obj(config, plugin.getId()));

				plugins.put(id, plugin);
				logger.debug("Initialized plugin " + plugin.getDisplayName());
			} catch (ServiceConfigurationError e) {
				logger.error("Plugin failed to load: " + e.getMessage(), e);
			} catch (PluginException e) {
				logger.error("Plugin "+id+" failed to initialize: " + e.getMessage(), e);
			} catch (Exception e) {
				logger.error("Plugin " + (id == null ? "(unknown)" : id) + " failed to load: " + e.getMessage(), e);
			}
		}
	}

	public Plugin getPlugin(String pluginId) {
		return plugins.get(pluginId);
	}

	public DocIndexerFactoryPlugin() {

	}

	@Override
	public void init() {
		//
	}

	@Override
	public boolean isSupported(String formatIdentifier) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Format> getFormats() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Format getFormat(String formatIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocIndexer get(String formatIdentifier, Indexer indexer, String documentName, Reader reader)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocIndexer get(String formatIdentifier, Indexer indexer, String documentName, InputStream is, Charset cs)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocIndexer get(String formatIdentifier, Indexer indexer, String documentName, File f, Charset cs)
			throws UnsupportedOperationException, FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocIndexer get(String formatIdentifier, Indexer indexer, String documentName, byte[] b, Charset cs)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}
}
