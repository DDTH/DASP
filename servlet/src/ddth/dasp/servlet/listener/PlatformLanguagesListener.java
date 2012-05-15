package ddth.dasp.servlet.listener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddth.dasp.servlet.mls.ILanguage;
import ddth.dasp.servlet.mls.LanguageManager;
import ddth.dasp.servlet.mls.PropertiesBasedLanguage;
import ddth.dasp.utils.PropsUtils;

/**
 * This {@link ServletContextListener} is responsible for initializing and
 * destroying the language manager.
 * 
 * @author NBThanh <btnguyen2k@gmail.com>
 */
public class PlatformLanguagesListener implements ServletContextListener {

	private final static String LANGUAGE_LOCATION = "/WEB-INF/languages";
	private final static String LANGUAGE_NAME = "wfp";

	private Logger LOGGER = LoggerFactory
			.getLogger(PlatformLanguagesListener.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		LanguageManager.destroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		LanguageManager.init();
		ServletContext servletContext = event.getServletContext();
		Properties props;
		try {
			props = loadLanguageRegistry(servletContext);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		String temp = props.getProperty("mls.locales");
		String[] locales = temp != null ? temp.split("[,\\s]+") : new String[0];
		if (locales.length == 0) {
			String msg = "No language pack defined!";
			LOGGER.error(msg);
			throw new RuntimeException(msg);
		}
		for (String str : locales) {
			Locale locale = buildLocale(str);
			String location = props
					.getProperty("mls." + str + ".location", str);
			ILanguage language = loadLanguagePack(servletContext, locale,
					LANGUAGE_NAME, location);
			// LanguageManager.register(locale, LANGUAGE_NAME, language);
			LanguageManager.register(language);
		}
	}

	private ILanguage loadLanguagePack(ServletContext servletContext,
			Locale locale, String name, String location) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Loading language pack [" + name + "], locale ["
					+ locale + "]...");
		}
		PropertiesBasedLanguage language = new PropertiesBasedLanguage(locale,
				name);
		Properties master = new Properties();
		Set<?> resources = servletContext.getResourcePaths(LANGUAGE_LOCATION
				+ "/" + location);
		if (resources != null) {
			for (Object temp : resources) {
				String resource = temp.toString();
				InputStream is = servletContext.getResourceAsStream(resource);
				Properties props = PropsUtils.loadProperties(is, resource
						.endsWith(".xml"));
				master.putAll(props);
			}
		}
		language.loadLanguage(master);
		return language;
	}

	private Locale buildLocale(String str) {
		String[] tokens = str.split("_");
		return tokens.length > 1 ? new Locale(tokens[0], tokens[1])
				: new Locale(tokens[0]);
	}

	private Properties loadLanguageRegistry(ServletContext servletContext)
			throws IOException {
		InputStream is = servletContext.getResourceAsStream(LANGUAGE_LOCATION
				+ "/registry.properties");
		return PropsUtils.loadProperties(is);
	}
}
