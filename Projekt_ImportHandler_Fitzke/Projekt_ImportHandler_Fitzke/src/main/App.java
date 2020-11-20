/**
 * 
 */
package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

import utils.ChooseExportSetting;
import utils.MyLogger;
import java.util.function.Predicate;
/**
 * @author martin
 *
 */
public class App {
	private static Logger log = new MyLogger().getLogger();
	/*
	 * List of possible arguments, so we can decide what is an argument and what is
	 * a parameter for an argument.
	 */
	private List<String> arguments = new ArrayList<>(Arrays.asList("clear",

			"import",

			"sad",

			"sd",

			"generate",

			"help",

			"version"));

	/**
	 * @param Arguments to start. Which Arguments and which Prameters are possible
	 *                  are shown in help. Remeber to add new Arguments to list of
	 *                  arguments.
	 */
	public static void main(String[] args) {
		// this will be the startpoint,
		App app = new App();

		boolean testArgs = app.importAndGenerateInRightOrder(args);

		if (!testArgs) {
			System.out.println(
					"Attention: There has to be import before generate in the arguments. Or only one of them.");
			log.severe("Attention: There has to be import before generate in the arguments. Or only one of them.");
			return;
		}
		// so, what to do:
		for (String arg : args) {
			switch (arg) {
			case "import":
				app.importData();
				break;
			case "generate":
				app.generateOutputData(args);
				break;
			case "version":
				app.showVersion();
				break;
			case "sd":
				app.showData(args);
				break;
			case "sad":
				app.showAnalyzedData(args);
				break;
			case "help":
				app.showHelp();
				break;
			case "clear":
				app.clear(args);
				break;
			default:

				break;
			}
		}
	}

	/**
	 * Clear data. Parameters are
	 * 
	 * @param analayzeddata, outputfiles, importedfiles, alltogether
	 */
	private void clear(String[] args) {
		String parameter = getParametersForArgument("clear", args);
		log.severe("User clears Data:" + parameter);

		ImportHandler importHandler = new ImportHandler();
		importHandler.init();
		boolean removeOK = importHandler.removeData(parameter);
		log.info("Cleaning went: " + removeOK);
	}

	/**
	 * If we have both import and generate as arguments, make sure import is before
	 * generate.
	 * 
	 * @return true if we have only one of both or they are in right order, false
	 *         else.
	 */
	private boolean importAndGenerateInRightOrder(String[] args) {
		boolean retVal = false;
		byte indexImport = -1, indexGenerate = -1;
		byte index = 0;
		for (String arg : args) {
			if (arg.equalsIgnoreCase("import")) {
				indexImport = index;
			} else if (arg.equalsIgnoreCase("generate")) {
				indexGenerate = index;
			}
			index++;
		}

		if (indexGenerate > indexImport || indexGenerate == -1) {
			retVal = true;
		}

		return retVal;
	}

	/**
	 * Shows Help to arguments and their parameters.
	 */
	private void showHelp() {
		System.out.println("I know Commands:");
		System.out.println("\"import\" \r\n" + "\t importData();\r\n" +

				"\"version\"\r\n" + "\t showVersion();\r\n" +

				"\"sd\"\r\n" + "\t showData(args);\r\n" +

				"\"sad\"\r\n" + "\t showAnalyzedData(args);\r\n" +

				"\t args: 0-> File Path And Name only.\r\n" +

				"\t 1 -> firstTimestamp, \r\n" +

				"\t 2 -> lastTimestamp,  \r\n " +

				"\t 3 -> NumberOfFileLines,\r\n" +

				"\"generate\"\r\n" + "\t generateOutputData(args);\r\n "

				+ "\t args: all -> export all imported files, \r\n "

				+ "\t last -> only last imported file,  \r\n"

				+ "\t allforced -> export all imported files ignoring already exported, \r\n "

				+ "\t lastforced -> only last imported file ignoring already exported,  \r\n" +

				"\"clear\" -> remove files. PAY ATTENTION what you remove. \r\n" +

				"\t args: analayzeddata -> data in analyzeddir, \r\n "

				+ "\t outputfiles ->  files in output dir \r\n"

				+ "\t importedfiles -> file that holds the imported Files information Not the importfiles themselfs., \r\n"

				+ "\t alltogether -> everything. do not do this except you are me. \r\n" +

				"\"help\":\r\n");
	}

	/**
	 * Gets parameter for given argument. Meaning if i have "generate all" in my
	 * args are "generate" and "all" and i get "all" for "generate"
	 * 
	 * @param argument
	 * @param args
	 * @return Parameter or null if no parameter is found or the parameter is itself
	 *         an argument.
	 */
	private String getParametersForArgument(String argument, String[] args) {
		String retVal = null;
		 Predicate <String> findMe = other -> other.equals(argument) ; 
		
		//Optional<String> erg = Stream.of(args).filter(other -> other.equals(argument)).findFirst();
		Optional<String> erg = Stream.of(args).filter(findMe).findFirst();
		
		System.out.println("Optional stuff:" + erg);

		int index = 0;
		for (String arg : args) {
			if (arg.equalsIgnoreCase(argument) && args.length > index + 1) {
				System.out.println("Found parameter! Parameter is:" + args[++index]);
				retVal = args[index];
				break;
			}
			index++;
		}
		if (arguments.contains(retVal)) {
			log.fine(" Parameter is an argument. so " + retVal + " is no parameter.");
			System.out.println(" Parameter is an argument. so " + retVal + " is no parameter.");
			retVal = null;
		}
		return retVal;
	}

	/**
	 * Generates output data file, depending on entries of properties. (filename and
	 * path, what sensordata to include.)
	 * 
	 */
	private void generateOutputData(String[] args) {
		ImportHandler importHandler = new ImportHandler();
		importHandler.init();
		String parameter = getParametersForArgument("generate", args);
		if (parameter == null || parameter.equals("all")) {
			System.out.println(
					"\nGenerate Output shows: " + importHandler.runExport(ChooseExportSetting.everyImportedFile));
		} else if (parameter.equals("latest")) {
			System.out.println(
					"\nGenerate Output shows: " + importHandler.runExport(ChooseExportSetting.latestImportedFile));
		} else if (parameter.equals("allforced")) {
			System.out.println(
					"\nGenerate Output shows: " + importHandler.runExport(ChooseExportSetting.everyImportedFileForced));
		} else if (parameter.equals("latestforced")) {
			System.out.println("\nGenerate Output shows: "
					+ importHandler.runExport(ChooseExportSetting.latestImportedFileForced));
		}
	}

	/**
	 * Show analyzed data. The data is stored in files, which have to be load
	 * previous to show them.
	 * 
	 * @param args
	 */
	private void showAnalyzedData(String[] args) {

		ImportHandler importHandler = new ImportHandler();
		importHandler.init();

		System.out.println("\n" + importHandler.getAnalyzedDataToShow());

	}

	/**
	 * Show already imported files. The arguments
	 * 
	 * @param args
	 */
	private void showData(String[] args) {
		ImportHandler importHandler = new ImportHandler();
		importHandler.init();
		String parameter = getParametersForArgument("sd", args);
		if (parameter == null) {
			System.out.println("\n" + importHandler.getLoadedImportfilesDataToShow(0));
		} else
			switch (parameter) {
			case "1":
				System.out.println("\n" + importHandler.getLoadedImportfilesDataToShow(1));
				break;
			case "2":
				System.out.println("\n" + importHandler.getLoadedImportfilesDataToShow(2));
				break;
			case "3":
				System.out.println("\n" + importHandler.getLoadedImportfilesDataToShow(3));
				break;
			default:
				System.out.println("\n" + importHandler.getLoadedImportfilesDataToShow(0));
				break;
			}

	}

	/**
	 * Show Version of tool.
	 */
	private void showVersion() {
		System.out.println("Version: 2");
		System.out.println("All Batteries included, no rights to whatsoever granted. Everything is safe.");

	}

	/**
	 * Import and analyze Data. Import only means the fielname and metadata is
	 * included in checkedData.csv and an analyzed data file is generated in
	 * anayzeddata folder. Folder name and path are part of properties. Also the
	 * generation of analyzed data is depending on parameters from properties.
	 * 
	 */
	private void importData() {
		ImportHandler importHandler = new ImportHandler();

		importHandler.init();

		boolean settingOK = importHandler.testDirsAndSetting();

		log.config(" testDirsAndSetting tested: " + settingOK);
		if (!settingOK) {
			log.severe("\t\tSetting not ok, aboard.");
			return;
		}
		System.out.println("Import Data start.");
		boolean stateOfImport;
		log.config("Import Data run: " + (stateOfImport = importHandler.runImport()));
		System.out.println("Import data finished. Says:" + stateOfImport);

	}

}
