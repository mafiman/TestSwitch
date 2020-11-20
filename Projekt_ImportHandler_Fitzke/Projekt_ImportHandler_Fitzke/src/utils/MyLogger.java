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
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author martin
 *
 */
public class MyLogger {

	private static Logger MYLOGGER;
	private static String filePathAndName = "./logging.properties";
	private static String ressourceName = "logging.properties";;
	private static boolean initFirst = true;

	/**
	 * This is the method to call.
	 * 
	 * @return gets the logger to log
	 */
	public Logger getLogger() {
		return MYLOGGER;
	}

	public void setMyLogger(Logger myLogger) {
		MyLogger.MYLOGGER = myLogger;
	}

	public MyLogger() {
		this(false);
	}

	public MyLogger(boolean init) {

//		if (init) {
		if (MyLogger.initFirst) {
			MyLogger.initFirst=false;
			this.loadProperties();
			MYLOGGER.info("Init Logger from file.");
		}
	}

	public static String getResourcesPath() {
		return MyLogger.filePathAndName;
	}

	public static String getResourcesName() {
		return MyLogger.ressourceName;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MyLogger ml = new MyLogger(true);

		// ml.saveProperties(MyLogger.filePathAndName);
		// ml.loadProperties(MyLogger.ressourceName);
		// ml.loadProperties();
		MYLOGGER.log(Level.INFO, "Start Logging");

		ml.getLogger().log(Level.INFO, "This works too.");

	}

	/**
	 * Load Logging Stuff from properties File
	 */
	public void loadProperties() {
		if (!(new java.io.File(MyLogger.filePathAndName).exists())) {
			saveProperties(MyLogger.filePathAndName);
		}

		loadProperties(MyLogger.ressourceName);
	}

	/**
	 * @param filePathAndName
	 */
	private void loadProperties(String filePathAndFileName) {
		InputStream is;
		// read from File, so user can handle changes.
		try {
			is = new FileInputStream(new java.io.File(filePathAndFileName));
			LogManager.getLogManager().readConfiguration(is);
			MYLOGGER = Logger.getLogger("MyLogger");
		} catch (FileNotFoundException e1) {
			saveProperties(filePathAndFileName);
			System.out.println("Saved config to " + filePathAndFileName);
			try {
				is = new FileInputStream(new java.io.File(filePathAndFileName));
				LogManager.getLogManager().readConfiguration(is);
				MYLOGGER = Logger.getLogger("MyLogger");
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

//		// read Logging properties that we want
//
//		InputStream is = MyLogger.class.getClassLoader().getResourceAsStream(ressourceName);
//		try {
//			LogManager.getLogManager().readConfiguration(is);
//			MYLOGGER = Logger.getLogger("MyLogger");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
////    show my hanlders
//		Handler[] allHandler = MYLOGGER.getHandlers();
//		for (Handler h : allHandler) {
//			System.out.println("Class: " + h.getClass().getSimpleName());
//		}
//		allHandler[0].setLevel(Level.CONFIG);
	}

	/**
	 * If we dont have properties, we create stuff we need with this one.
	 */
	public void saveProperties() {
		saveProperties(MyLogger.filePathAndName);
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
			Properties prop = new Properties();
			prop.setProperty("java.util.logging.FileHandler.formatter", "java.util.logging.SimpleFormatter");
			prop.setProperty("java.util.logging.FileHandler.pattern", "log%g_%u.log");
			prop.setProperty("java.util.logging.FileHandler.level", "ALL");
			prop.setProperty("java.util.logging.ConsoleHandler.formatter", "java.util.logging.SimpleFormatter");
			prop.setProperty("java.util.logging.ConsoleHandler.level", "FINEST");
			prop.setProperty(".level", "SEVERE"); // Dies ist die Lösung. Mit .level werden ALLE Level eingestellt,
			// also auch die angehoben die sonst auf SEVERE gestellt sind.
			prop.setProperty("handlers", "java.util.logging.ConsoleHandler, java.util.logging.FileHandler");
			prop.setProperty("java.util.logging.FileHandler.append", "false");

			prop.setProperty("MyLogger.level", "ALL");
			prop.setProperty("MyLogger.handlers", "java.util.logging.ConsoleHandler, java.util.logging.FileHandler");
			prop.setProperty("MyLogger.useParentHandlers", "false");

			prop.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-13s][%2$-10s]:%5$-25s %n");
			prop.store(pw, "This are my Loggingpoperties. Saved:"
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
