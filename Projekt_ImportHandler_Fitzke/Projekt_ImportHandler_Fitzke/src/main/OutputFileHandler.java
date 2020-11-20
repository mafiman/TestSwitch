/**
 * 
 */
package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import utils.Config;
import utils.DataCheckedFile;
import utils.DataFileEntity;
import utils.DataLine;
import utils.IUsingDateTimeFormatter;
import utils.MyLogger;

/**
 * @author martin
 *
 */
public class OutputFileHandler {
	private static Logger log = new MyLogger().getLogger();
	/**
	 * Outputfile, only in this class used.
	 */
	private static File outFile;
	private static BufferedWriter bufferedOutFileWriter;

	/**
	 * Tests outputfiledir and other properties for output a file. default values
	 * are set and a dir is tried to
	 * 
	 * @return
	 */
	public boolean testOutputDirAndProperties() {
		boolean retVal = false;
		String AnalyzerDataDir = Config.getPropertieValue("outputdatadir");
		if (AnalyzerDataDir == null) {
			log.severe("Kein OutputDataDir in den Properties gesetzt.");
			return retVal;
		}
		File file = new File(AnalyzerDataDir);
		if (!file.exists()) {

			boolean secChance = file.mkdir();
			log.severe("Versuch das Verzeichnis anzulegen war: " + secChance);

			if (!secChance) {
				log.severe("OutputDataDir existiert nicht oder es gibt keinen Zugriff. Anlegen war erfolglos.");
				return retVal;
			}
		}

		String propertieName = "outputfilename";
		String propertieValue = Config.getPropertieValue(propertieName);
		if (propertieValue != null) {
			log.fine(propertieName + " is set to:" + propertieValue);
			retVal = true;
		} else {
			log.info(propertieName + " was not set, using: outputfile: ");
			Config.setPropertieValue(propertieName, "outputfile");
			retVal = true;
		}

		propertieName = "outputfilenameextension";
		propertieValue = Config.getPropertieValue(propertieName);
		if (propertieValue != null) {
			log.fine(propertieName + " is set to:" + propertieValue);
			retVal = true;
		} else {
			log.info(propertieName + " was not set, using: outputfile: ");
			Config.setPropertieValue(propertieName, ".csv");
			retVal = true;
		}

		propertieName = "outputfileexpression";
		propertieValue = Config.getPropertieValue(propertieName);
		if (propertieValue != null) {
			log.fine(propertieName + " is set to:" + propertieValue);
			retVal = true;
		} else {
			log.info(propertieName + " was not set, using: outputfile: ");
			Config.setPropertieValue(propertieName, "true");
			retVal = true;
		}
		return retVal;
	}

	public boolean deleteOutputDirFiles() {
		boolean retVal = false;
		String AnalyzerDataDir = Config.getPropertieValue("outputdatadir");
		if (AnalyzerDataDir == null) {
			log.severe("Kein OutputDataDir in den Properties gesetzt.");
			return retVal;
		}
		File file = new File(AnalyzerDataDir);
		if (!file.exists()) {

			log.info("There was no outputDir to clean.");
			retVal = true;
			return retVal;
		}

		File[] imports = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".csv");
			}
		});
		if (imports.length == 0) {
			log.info("Es gibt keine OutputDataDir Dateien.");
			retVal = true;

		} else {
			log.info("Es gibt  " + imports.length + " OutputDataDir  Dateien.");
			for (File delFile : imports) {
				log.info("Deleting: " + delFile.getAbsoluteFile() + " went: " + delFile.delete());
			}
			retVal = true;
			imports = file.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".csv");
				}
			});
			log.info("Es gibt danach " + imports.length + "  OutputDataDir Dateien.");
		}

		return retVal;
	}

	/**
	 * gets a String with Properties for Outputfile generation.
	 * 
	 * @return String with Properties and Values.
	 */
	public String getOutputPropertiesToShow() {
		String retVal = "";
		StringBuilder sbOut = new StringBuilder();
		// The following Properties are used to generate output files
		// dir for output data files
		// Property("outputdatadir", ".//outputdata");
		String propertieName = "outputdatadir";
		sbOut.append(Config.getPropertieValueAsString(propertieName));

		// filename for Outputfiles. Filename is appended with datetimestamp and
		// extension
		// prop.setProperty("outputfilename", "outputdata");
		propertieName = "outputfilename";
		sbOut.append(Config.getPropertieValueAsString(propertieName));
		// filename for Outputfiles. Filename is appended with datetimestamp and ending
		// prop.setProperty("outputfilenameextension", ".csv");
		propertieName = "outputfilenameextension";
		sbOut.append(Config.getPropertieValueAsString(propertieName));
		// Expression for each dataline to be included in outputfile.
		// prop.setProperty("outputfileexpression", "true");
		propertieName = "outputfileexpression";
		sbOut.append(Config.getPropertieValueAsString(propertieName));
		return retVal;
	}

	/**
	 * Generate File Path and Name according to propertie values for certain
	 * nameParts.
	 * 
	 * @return String with filePathAndName.
	 */
	private String generateFileOutputPathAndFilename() {
		StringBuilder sbFilePathAndName = new StringBuilder(Config.getPropertieValue("outputdatadir"));
		String dateTimePart = LocalDateTime.now().format(IUsingDateTimeFormatter.GetFilePartFormatter());
		sbFilePathAndName.append("//").append(Config.getPropertieValue("outputfilename")).append(dateTimePart)
				.append(Config.getPropertieValue("outputfilenameextension"));
		log.fine("New Filename generated: " + sbFilePathAndName.toString());
		return sbFilePathAndName.toString();
	}

	/**
	 * Create outputFile according to propertie values
	 * 
	 * @return
	 */
	public boolean createOutputFile() {
		boolean retVal = false;
		log.finer("Try to open and create outputfile.");
		try {
			if (OutputFileHandler.outFile != null && OutputFileHandler.bufferedOutFileWriter != null) {
				// file already created, so we close this
				OutputFileHandler.bufferedOutFileWriter.flush();
				OutputFileHandler.outFile = null;
			}
			OutputFileHandler.outFile = new File(generateFileOutputPathAndFilename());
			if (outFile.exists()) {
				log.warning("Outputfile " + outFile.getAbsolutePath() + " already existing. overwrite File.");
			}

			OutputFileHandler.bufferedOutFileWriter = new BufferedWriter(new FileWriter(outFile));
			this.appendToOutputFile("last_changed,state,entity_id\n");
			retVal = true;
		} catch (IOException e) {
			log.severe("Attention, upsy happened while ouputFile Creation: " + e.toString());
			e.printStackTrace();
			retVal = false;
		}
		return retVal;
	}

	private String getOutputFilePathAndName() {
		return OutputFileHandler.outFile.getAbsolutePath();
	}

	/**
	 * Append line to outputFile. If Outputfile is not created, creation will
	 * happen.
	 * 
	 * @param outputLine String with Data to output
	 * @return true if all went well.
	 */
	public boolean appendToOutputFile(String outputLine) {
		boolean retVal = true;
		if (OutputFileHandler.outFile == null || OutputFileHandler.bufferedOutFileWriter == null) {
			if (createOutputFile()) {
				log.finer("Ouptputfile created, ready to append.");
				retVal = true;
			} else {
				log.severe("Outputfile " + OutputFileHandler.outFile.getAbsolutePath()
						+ " coulkd not be created. Abort writing.");
				retVal = false;
			}
		}
		if (retVal) {
			try {
				OutputFileHandler.bufferedOutFileWriter.append(outputLine);

			} catch (IOException e) {
				log.severe("Attention: Upsy happend while outputfile appending: " + e.toString());
				e.printStackTrace();
			}
		}

		return retVal;
	}

	/**
	 * Flush and close outputFile. Set static attributes to null, so we know we have
	 * noc longer an output File and have to create a new one next time.
	 * 
	 * @return
	 */
	public boolean finishOutpuFile() {
		boolean retVal = false;
		try {
			log.finer("Try to close outputfile.");
			OutputFileHandler.bufferedOutFileWriter.flush();
			OutputFileHandler.bufferedOutFileWriter.close();
			OutputFileHandler.bufferedOutFileWriter = null;
			OutputFileHandler.outFile = null;
		} catch (Exception e) {

			log.severe("Attention: Upsy happend while outputfile closing: " + e.toString());
			e.printStackTrace();
		}

		return retVal;
	}

	/**
	 * Use to checck and export all files (loaded previously) to one single
	 * outputfile.
	 * 
	 * @param checkedFilesParam
	 * @param forcedExport      true if we want the export happening regardless of
	 *                          previous export.
	 * @returnif all went well.
	 */
	public boolean checkAllImportedFilesAndOutputFiles(List<DataCheckedFile> checkedFilesParam, boolean forcedExport) {
		boolean retVal = false;
		if (checkedFilesParam.size() == 0) {
			log.info("No imported files. Nothing to export.");
			return retVal;
		}
		// Create the single outputfile.
		this.createOutputFile();

		for (DataCheckedFile difc : checkedFilesParam) {
			if (!forcedExport) {
				if (difc.getLastExported() == null || difc.getLastExported().isEmpty()) {
					log.fine(" not already exported, go ahead.");
				} else {
					log.severe("  already exported, aboard exporting for this importfile.");
					continue;
				}
			}
			retVal = checkImportFileAndOutputFile(difc.getFilePathAndName(), true);
			log.info("Export imported File: " + difc.getFilePathAndName() + " All went:" + retVal);
		}
		log.info("Exported all imported Files.");
		// flush and close the single outputfile.
		this.finishOutpuFile();
		return retVal;
	}

	/**
	 * Checks if available and exports content of file to outputfile. Name and Path
	 * of Outputfile is depending on properties setting. Also the Line is checked
	 * with propertie setting outputfileexpression against each import file line is
	 * checked.
	 * 
	 * @param filePathAndName
	 * @param exportMultipleFilesToOne
	 * @return true if everything went well and we have a outputfile.
	 */
	public boolean checkImportFileAndOutputFile(String filePathAndName, boolean exportMultipleFilesToOne) {
		boolean retVal = false;

		File file = new File(filePathAndName);
		if (!file.exists()) {
			log.severe("Importfile existiert nicht oder es gibt keinen Zugriff.");
			return retVal;
		}
		log.fine("\t\tCheck for output file " + filePathAndName);

		if (!exportMultipleFilesToOne)
			this.createOutputFile();

		if (exportImportFile(filePathAndName, true)) {
			log.info("Datafile analyzed and exported.");
		} else {
			log.info("Bad importfile, skip.");
		}

		log.info("\t\tCheck and export file done." + filePathAndName + " exported to:"
				+ this.getOutputFilePathAndName());
		if (!exportMultipleFilesToOne)
			this.finishOutpuFile();
		retVal = true;
		return retVal;
	}

	/**
	 * Exports a single importfile. If there is a expression for the datalines set
	 * each line is only exported if the expression is valid. If there is no
	 * expression or it is simply true, we skip testing and export simply each line.
	 * 
	 * @param dataFilePathAndName
	 * @param skipFirstLine       use true if first line contains columninfo.
	 * @return true if all went peachy.
	 */
	private boolean exportImportFile(String dataFilePathAndName, boolean skipFirstLine) {
		boolean retVal = true;
		long lineCounter = 0;
		long lineCounterOutput = 0;

		DataLine dataline = null;
		StringBuilder inLine;
		boolean lineFits = true;
		// Set expression for each file.
		String expressionState = Config.getExpressionState();
		String expressionEntity_id = Config.getExpressionEntity();
		log.fine("Expression to check:  entity_id:" + expressionEntity_id + " isEmpty:" + expressionEntity_id.isEmpty()
				+ "   state:" + expressionState + "  isEmpty:" + expressionState.isEmpty());

		if (dataFilePathAndName == null) {
			retVal = false;
			log.severe("No fileAndPathname set to analyse. Abort.");
		} else if (dataFilePathAndName.equals("toDelete") || dataFilePathAndName.length() <= 4) {
			retVal = false;
			log.severe("Bad fileAndPathname set to analyse. Data:'" + dataFilePathAndName + "' Abort.");
		}
		if (retVal) {

			// object to hold line of data and converts it.
			DataLine dataLine;

			// now we have a filePathAndName that most likely is a file, so check it
			File file = new File(dataFilePathAndName);
			try {
				@SuppressWarnings("resource")
				BufferedReader breader = new BufferedReader(new FileReader(file));

				log.info("Opened Datafile. ");
				while ((inLine = new StringBuilder(breader.readLine())) != null) {
					if (lineCounter == 0) {

						if (skipFirstLine) {
							log.finer("Skip first line of file.");
							lineCounter++;
							continue;
						}
					}
					lineCounter++;
					// Works fine, is painfull.
					// lineFits = this.analyzeImportFileLine(inLine, dataline);

					if (!expressionEntity_id.isEmpty() || !expressionState.isEmpty()) {
						dataline = new DataLine(inLine.toString());
						lineFits = dataline.isFittingEntity_id(expressionEntity_id)| dataline.isFittingState(expressionState);
					}
					if (lineFits) {
						// System.out.println("does fit:" + inLine);
						this.appendToOutputFile(inLine.toString() + "\n");
						lineCounterOutput++;
					}
				}
				retVal = true;

			} catch (NullPointerException e) {

				log.info("  catched, end of file reached. Lines read:" + lineCounter + " Outputlines written:"
						+ lineCounterOutput);
				retVal = true;
			} catch (FileNotFoundException e) {
				//
				e.printStackTrace();
				log.severe(e.toString());
			} catch (IOException e) {
				//
				e.printStackTrace();
				log.severe(e.toString());
			}

			log.fine("Export file done." + dataFilePathAndName);
		}
		return retVal;
	}

	

}
