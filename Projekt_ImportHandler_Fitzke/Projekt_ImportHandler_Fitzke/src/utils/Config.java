/**
 * 
 */
package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 * @author martin
 *
 */
public class Config implements IUsingDateTimeFormatter {
	private static String fileSystemPathAndName = "./handler.properties";
	private static String filePathAndName = "resources/handler.properties";
	private static String ressourceName = "handler.properties";
	private static Properties props;
	private static Properties propDefaults;

	public static String getPropertieValue(String propertieName) {
		return Config.props.getProperty(propertieName);
	}

	public static void setPropertieValue(String propertieName, String propertieValue) {
		Config.props.setProperty(propertieName, propertieValue);
	}

	public Config() {
		this(false);
	}

	public Config(boolean init) {
		if (init) {
			loadProperties();
			 
		}
	}

	public static String getResourcesPath() {
		return Config.filePathAndName;
	}

	public static String getResourcesName() {
		return Config.ressourceName;
	}

	public static String getAnalyzerDataDir() {
		String retVal = Config.props.getProperty("analyzerdatadir");
		if (retVal == null)
			retVal = Config.props.getProperty("importdir");
		return retVal;
	}

	public static String getExpressionState() {
		return Config.props.getProperty("expressionstate");
	}
	
	public static String getExpressionEntity() {
		return Config.props.getProperty("expressionentity") ;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		Config ml = new Config();
//		ml.loadProperties();
		new Config(true);
		System.out.println(Config.getPropertieValue("importdir"));
	}

	/**
	 * Load Logging Stuff from properties File
	 */
	public void loadProperties() {

		loadFileProperties(Config.fileSystemPathAndName);

//		 {
//			if (!(new java.io.File(Config.filePathAndName).exists())) {
//				saveProperties(Config.filePathAndName);
//				// here we have to tell classloader to relfresh and reload.
//				// as long as we do not overdo this...
//			}
//
//			loadProperties(Config.ressourceName);
//		}
	}

	private void loadFileProperties(String filePathAndFileName) {
		InputStream ins;
		if (props == null) {
			props = new Properties(createMyProperties());
		}
		// Load the properties
		try {
			ins = new FileInputStream(new java.io.File(filePathAndFileName));
			props.load(ins);

		} catch (FileNotFoundException e1) {
			saveProperties(Config.fileSystemPathAndName);
			System.out.println("Saved config to " + filePathAndFileName);
			try {
				ins = new FileInputStream(new java.io.File(filePathAndFileName));
				props.load(ins);
			} catch (FileNotFoundException e2) {
				System.err.println("Failed secound time. Bollocks.");
			} catch (IOException e) {
				System.err.println("Inner exception while loading handler properties.");
				e.printStackTrace();
			}

		} catch (IOException e) {
			//
			e.printStackTrace();
		}
	}

	/**
	 * @param filePathAndName
	 */
	private void loadProperties(String ressourceName) {
		InputStream ins = Config.class.getClassLoader().getResourceAsStream(ressourceName);
		// Load the properties
		try {
			if (props == null) {
				props = new Properties();
			}
			props.load(ins);
		} catch (IOException e) {
			//
			e.printStackTrace();
		}
	}

	/**
	 * If we dont have properties, we create stuff we need with this one.
	 */
	public void saveProperties() {
		saveProperties(Config.filePathAndName);
	}

	/**
	 * @param filePathAndName
	 */
	private void saveProperties(String filePathAndName) {
		File file = new File(filePathAndName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileWriter pw = new FileWriter(file);
			Properties prop = createMyProperties();
//			Properties prop = new Properties();
//			// example propertie
//			prop.setProperty("java.util.logging.FileHandler.formatter", "java.util.logging.SimpleFormatter");
//			// show gui or not.
//			prop.setProperty("showgui", "false");
//			// path to dir with datafiles to by analyzed and handled
//			prop.setProperty("importdir", "c://tmp//importdata");
//			// skip saving of analyzer Data if the file is already there. if we want forced
//			// saving set to false
//			prop.setProperty("skipifalreadyafile", "true");
//			// check if already analyzed data has a file in place.
//			prop.setProperty("checkifanalyzeddataisthere", "false");
//			//
//			prop.setProperty("saveana", "false");
//			// load analyzed data of already analyzed data files. not necc for simply catch
//			// up and add new data.
//			prop.setProperty("loadanalyzeddata", "false");
//
//			// path and filename of already checked importfiles with their data.
//			prop.setProperty("checkedDataFilePathAndName", "./checkedData.csv");
//			// dir for analyzer data
//			prop.setProperty("analyzerdatadir", ".//analyzerdata");
//
//			// dir for output data files
//			prop.setProperty("outputdatadir", ".//outputdata");
//			// filename for Outputfiles. Filename is appended with datetimestamp and
//			// extension
//			prop.setProperty("outputfilename", "outputdata");
//			// filename for Outputfiles. Filename is appended with datetimestamp and ending
//			prop.setProperty("outputfilenameextension", ".csv");
//			// Expression for each dataline to be included in outputfile.
//			prop.setProperty("expressionentity", "");
//			// Expression for each dataline to be included in outputfile.
//			prop.setProperty("expressionstate", "{");
			prop.store(pw, "This are my Programproperties. Saved:"
					+ LocalDateTime.now().format(IUsingDateTimeFormatter.GetFormatter()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * Creates the properties i want. This way we can have the Values and Keys in a default Propertie cachingle
	 * in case something goes south.
	 * @return properties with keys values set to our needs. 
	 */
	private Properties createMyProperties() {
		Properties prop = new Properties();
//		// example propertie
//		prop.setProperty("java.util.logging.FileHandler.formatter", "java.util.logging.SimpleFormatter");
		// show gui or not.
		prop.setProperty("showgui", "false");
		// path to dir with datafiles to by analyzed and handled
		prop.setProperty("importdir", "c://tmp//importdata");
		// skip saving of analyzer Data if the file is already there. if we want forced
		// saving set to false
		prop.setProperty("skipifalreadyafile", "true");
		// check if already analyzed data has a file in place.
		prop.setProperty("checkifanalyzeddataisthere", "false");
		//
		prop.setProperty("saveana", "false");
		// load analyzed data of already analyzed data files. not necc for simply catch
		// up and add new data.
		prop.setProperty("loadanalyzeddata", "false");

		// path and filename of already checked importfiles with their data.
		prop.setProperty("checkedDataFilePathAndName", "./checkedData.csv");
		// dir for analyzer data
		prop.setProperty("analyzerdatadir", ".//analyzerdata");

		// dir for output data files
		prop.setProperty("outputdatadir", ".//outputdata");
		// filename for Outputfiles. Filename is appended with datetimestamp and
		// extension
		prop.setProperty("outputfilename", "outputdata");
		// filename for Outputfiles. Filename is appended with datetimestamp and ending
		prop.setProperty("outputfilenameextension", ".csv");
		// Expression for each dataline to be included in outputfile.
		prop.setProperty("expressionentity", "");
		// Expression for each dataline to be included in outputfile.
		prop.setProperty("expressionstate", "");
		 
		return prop;
	}

	/**
	 * Put all known Properties to logfile.
	 */
	public void listProperties() {
		props.list(System.out);
	}
	/**
	 * Gets a Propertie and Value String with Key: and Value: parts.
	 * @param propertieName
	 * @return String with Value and additional content.
	 */
	public static String getPropertieValueAsString(String propertieName) {
		StringBuilder sbOut=new StringBuilder();
		sbOut.append("Key: ");
		sbOut.append(propertieName);
		sbOut.append("  Value: ");
		sbOut.append(Config.getPropertieValue(propertieName));
		return sbOut.toString();
	}
}
