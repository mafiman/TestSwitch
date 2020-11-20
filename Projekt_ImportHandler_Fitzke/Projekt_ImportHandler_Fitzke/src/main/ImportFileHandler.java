/**
 * 
 */
package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import utils.Config;
import utils.DataFileEntity;
import utils.IUsingDateTimeFormatter;
import utils.DataCheckedFile;
import utils.MyLogger;
import utils.StateObject;
import utils.TimeValues;

/**
 * @author martin
 *
 */
public class ImportFileHandler implements IUsingDateTimeFormatter {
	private static Logger log = new MyLogger().getLogger();

	/**
	 * Test if there is a defined Analyzerdatadir Property and if this dir exists
	 * and can be reached. If there is no dir we try to create it. If that fails we
	 * fail. if not we test and do stuff to dir.
	 * 
	 * @return true if ther eis all peachy with this propertie and the dir we want
	 *         to be.
	 */
	public boolean testAnalyzerDataDir() {
		boolean retVal = false;
		String AnalyzerDataDir = Config.getPropertieValue("analyzerdatadir");
		if (AnalyzerDataDir == null) {
			log.severe("Kein AnalyzerDataDir in den Properties gesetzt.");
			return retVal;
		}
		File file = new File(AnalyzerDataDir);
		if (!file.exists()) {

			boolean secChance = file.mkdir();
			log.severe("Versuch das Verzeichnis anzulegen war: " + secChance);

			if (!secChance) {
				log.severe("AnalyzerDataDir existiert nicht oder es gibt keinen Zugriff. Anlegen war erfolglos.");
				return retVal;
			}
		}

		File[] imports = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".dat");
			}
		});
		if (imports.length == 0) {
			log.info("Es gibt keine Anlayser Dateien.");
			retVal = true;

		} else {

			retVal = true;
			log.info("Es gibt " + imports.length + " Analyzer Dateien.");
		}

		return retVal;

	}

	public boolean deleteAnalyzerdirFiles() {
		boolean retVal = false;
		String importDir = Config.getPropertieValue("analyzerdatadir");
		if (importDir == null) {
			log.severe("Kein analyzerdatadir in den Properties gesetzt.");
			return retVal;
		}
		File file = new File(importDir);
		if (!file.exists()) {
			log.info("analyzerdatadir existiert nicht oder es gibt keinen Zugriff.");
			return true;
		}

		File[] imports = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".dat");
			}
		});
		if (imports.length == 0) {
			log.info("Es gibt keine analyzerdatadir Dateien.");
			retVal = true;

		} else {
			log.info("Es gibt  " + imports.length + " analyzerdatadir Dateien.");
			for (File delFile : imports) {
				log.info("Deleting: " + delFile.getAbsoluteFile() + " went: " + delFile.delete());
			}
			retVal = true;
			imports = file.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".dat");
				}
			});
			log.info("Es gibt danach " + imports.length + " analyzerdatadir Dateien.");
		}

		return retVal;
	}

	
	/**
	 * Tests if Importdir is set and if importdir is a valid dir. And if it contains
	 * any files.
	 * 
	 * @return if everything is ok, return true.
	 */
	public boolean testImportdir() {
		boolean retVal = false;
		String importDir = Config.getPropertieValue("importdir");
		if (importDir == null) {
			log.severe("Kein Importdir in den Properties gesetzt.");
			return retVal;
		}
		File file = new File(importDir);
		if (!file.exists()) {
			log.severe("Importdir existiert nicht oder es gibt keinen Zugriff.");
			return retVal;
		}

		File[] imports = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".csv");
			}
		});
		if (imports.length == 0) {
			log.info("Es gibt keine Import Dateien.");

		} else {

			retVal = true;
			log.info("Es gibt " + imports.length + " Import Dateien.");
		}

		return retVal;
	}

	public boolean deleteImportdirFiles() {
		boolean retVal = false;
		String importDir = Config.getPropertieValue("importdir");
		if (importDir == null) {
			log.severe("Kein Importdir in den Properties gesetzt.");
			return retVal;
		}
		File file = new File(importDir);
		if (!file.exists()) {
			log.severe("Importdir existiert nicht oder es gibt keinen Zugriff.");
			return true;
		}

		File[] imports = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".csv");
			}
		});
		if (imports.length == 0) {
			log.info("Es gibt keine Import Dateien.");
			retVal = true;

		} else {
			log.info("Es gibt " + imports.length + " Import Dateien.");
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
			log.info("Es gibt danach " + imports.length + " Import  Dateien.");
		}

		return retVal;
	}

	/**
	 * Has our list of checked Files the filePathAndName in Question? If
	 * checkifanalyzeddataisthere Propertie is set true, we even check if the
	 * datafile is existent and create a new one if its lost. If list is not
	 * initialized this will initialize and return.
	 * 
	 * @param filePathAndName
	 * @return true if the File is in our list.
	 */
	private boolean hasFilePathAndNameAndIsFileStillThere(String filePathAndName,
			List<DataCheckedFile> checkedFilesParam, StringBuilder uUIDOfDataFile) {
		boolean retVal = false;
		if (checkedFilesParam == null || checkedFilesParam.size() == 0)
			return retVal;
		/// checkifanalyzeddataisthere -> check if
		boolean checkifanalyzeddataisthere = new Boolean(Config.getPropertieValue("checkifanalyzeddataisthere"));
		//
		log.fine("\tcheck if file is still there.");

		for (DataCheckedFile dataInput : checkedFilesParam) {
			if (dataInput.isFilePathAndName(filePathAndName)) {
				// get uuid of found file to build new filename
				uUIDOfDataFile.append(dataInput.getID());

				// System.out.println("Filename: " + dataInput.getFilePathAndName() + " ID:" +
				// uUIDOfDataFile);

				if (checkifanalyzeddataisthere) {
					// so check if there is still the file with the data
					return new File(Config.getAnalyzerDataDir().concat("/").concat(dataInput.getID()).concat(".dat"))
							.exists();
				} else {
					// only check if we have a already checked importdata file
					retVal = true;
				}

				break;
			}
		} // end for
		return retVal;
	}

	/**
	 * Checks whole directory of files. Imports unknown will be checked and added to
	 * list
	 * 
	 * @param checkedFilesParam   List of {@link DataCheckedFile} entrys, so results
	 *                            can be added to that.
	 * @param fileEntity_idsParam we need to know this list, so we can add entityids
	 *                            of this file to it.
	 * @return
	 */
	public boolean checkImportdir(List<DataCheckedFile> checkedFilesParam, List<DataFileEntity> fileEntity_idsParam) {
		boolean retVal = false;
		String importDir = Config.getPropertieValue("importdir");
		File[] imports = new File(importDir).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".csv");
			}
		});

		if (imports.length == 0) {
			log.info("Es gibt keine Import Dateien.");

		} else {

			StringBuilder sb;
			StringBuilder sbUUIDOfOurFile;
			log.info("Found importfiles: " + imports.length);
			for (int index = 0; index < imports.length; index++) {
				sb = new StringBuilder(imports[index].getAbsolutePath());
				sbUUIDOfOurFile = new StringBuilder();
				log.info("File: " + sb.toString());
				if (hasFilePathAndNameAndIsFileStillThere(sb.toString(), checkedFilesParam, sbUUIDOfOurFile)) {
					log.info("\tFile already checked.");
				} else {
					log.info("\t check file. ID:" + sbUUIDOfOurFile);
					this.checkImportFile(sb.toString(), checkedFilesParam, sbUUIDOfOurFile, fileEntity_idsParam);
				}
				sb = null;
				sbUUIDOfOurFile = null;
			}
			retVal = true;
		}

		return retVal;
	}

	/**
	 * Check one Importfile. If list of checkedFiles is not initialized do that. if
	 * file is not ready break and return false. If file checked data is added to
	 * List and File is analyzed. return true is delivered.
	 * 
	 * @param filePathAndName
	 * @param checkedFilesParam   List of {@link DataCheckedFile}, so we can add
	 *                            results to this.
	 * @param fileEntity_idsParam we need to know this list, so we can add entityids
	 *                            of this file to it.
	 * @param sbUUIDOfOurFile
	 * 
	 * 
	 * @return
	 */
	public boolean checkImportFile(String filePathAndName, List<DataCheckedFile> checkedFilesParam,
			StringBuilder sbUUIDOfOurFile, List<DataFileEntity> fileEntity_idsParam) {
		boolean retVal = false;
		String nowTime = LocalDateTime.now().format(IUsingDateTimeFormatter.GetFormatter());// DateTimeFormatter.ofPattern("yyyy.MM.dd
																							// HH:mm:ss.SSSSS"));

		// safety first...
		if (checkedFilesParam == null) {
			checkedFilesParam = new ArrayList<DataCheckedFile>();
			log.info("No checkedData, create new one.");
		}

		File file = new File(filePathAndName);
		if (!file.exists()) {
			log.severe("Importfile existiert nicht oder es gibt keinen Zugriff.");
			return retVal;
		}
		log.fine("\t\tCheck file " + filePathAndName);

		DataCheckedFile dataToAnalyze = null;
		boolean onlyUpdateFileData = false;
		// we have to check if we have sbUUIDOfOurFile as an id. If we have we MUST use
		// the same
		// element or else we would start creating a new element with the same ID
		for (DataCheckedFile data : checkedFilesParam) {
			if (data.getID().equals(sbUUIDOfOurFile.toString())) {
				onlyUpdateFileData = true;
				dataToAnalyze = data;
				break;
			}
		}
		if (!onlyUpdateFileData) {
			dataToAnalyze = new DataCheckedFile(filePathAndName, nowTime, null, null, null);
			dataToAnalyze.setID(sbUUIDOfOurFile.toString());
		}
		if (analyseImportFile(dataToAnalyze, fileEntity_idsParam, true)) {
			if (!onlyUpdateFileData) {
				checkedFilesParam.add(dataToAnalyze);
				log.info("Datafile analyzed and added.");
			} else {
				log.info("Dont add Datafile entry, update only.");
			}

		}
		log.fine("\t\tCheck file done." + filePathAndName);

		retVal = true;
		return retVal;
	}

	/**
	 * Method to analyze the file. Put data in given {@link DataCheckedFile}
	 * 
	 * @param dataInputFileCheck  Element with data which file to check and what
	 *                            data we have.
	 * @param fileEntity_idsParam initialized list of {@link DataFileEntity}, in
	 *                            there we put the result of the analysis-
	 * @return true if all went well.
	 */
	public boolean analyseImportFile(DataCheckedFile dataInputFileCheck, List<DataFileEntity> fileEntity_idsParam) {
		return analyseImportFile(dataInputFileCheck, fileEntity_idsParam, true, false);
	}

	/**
	 * Method to anlayze the file. Put data in given {@link DataCheckedFile}
	 * 
	 * @param dataInputFileCheck  Element with data which file to check and what
	 *                            data we have.
	 * @param fileEntity_idsParam initialized list of {@link DataFileEntity}, in
	 *                            there we put the result of the analysis-
	 * @param skipFirstLine       well, skip first line if there is only
	 *                            columnCaptures fe.
	 * @return true if all went well.
	 */
	public boolean analyseImportFile(DataCheckedFile dataInputFileCheck, List<DataFileEntity> fileEntity_idsParam,
			boolean skipFirstLine) {
		return analyseImportFile(dataInputFileCheck, fileEntity_idsParam, skipFirstLine, false);
	}

	/**
	 * Method to anlayze the file. Put data in given {@link DataCheckedFile}
	 * 
	 * @param dataInputFileCheck  Element with data which file to check and what
	 *                            data we have.
	 * @param fileEntity_idsParam initialized list of {@link DataFileEntity}, in
	 *                            there we put the result of the analysis. If we use
	 *                            null we can forget the analysis, for export for
	 *                            example.
	 * @param skipFirstLine       well, skip first line if there is only
	 *                            columnCaptures fe.
	 * @param exportFile          Export file to exportfile, according to properties
	 *                            settings.
	 * @return true if all went well.
	 */
	public boolean analyseImportFile(DataCheckedFile dataInputFileCheck, List<DataFileEntity> fileEntity_idsParam,
			boolean skipFirstLine, boolean exportFile) {
		boolean retVal = true;

		// we want:
		// min and max timestamp
		// all entity_id
		long lineCounter = 0;
		TimeValues timeValues = new TimeValues();

		dataInputFileCheck.setFirstTimestamp(timeValues.getMinTimeFormattedString());
		dataInputFileCheck.setLastTimeStamp(timeValues.getMaxTimeFormattedString());

		StringBuilder inLine;
		DataFileEntity actFileEntity;

		String dataFilePathAndName = dataInputFileCheck.getFilePathAndName();

		if (dataFilePathAndName == null) {
			retVal = false;
			log.severe("No fileAndPathname set to analyse. Abort.");
		} else if (dataFilePathAndName.equals("toDelete") || dataFilePathAndName.length() <= 4) {
			retVal = false;
			log.severe("Bad fileAndPathname set to analyse. Data:'" + dataFilePathAndName + "' Abort.");
		}
		if (retVal) {
			// create new element for our list of filesEntity Elements.
			actFileEntity = new DataFileEntity(dataFilePathAndName);
			// with this we can set the id to the id from the checkedData Object.
			actFileEntity.setId(UUID.fromString(dataInputFileCheck.getID()));
			// now we have a filePathAndName that most likely is a file, so check it
			File file = new File(dataFilePathAndName);
			try {
				@SuppressWarnings("resource")
				BufferedReader breader = new BufferedReader(new FileReader(file));
				// added so we can get the state of the object to use
				StateObject stateObj = new StateObject();

				log.info("Opened Datafile. id:" + dataInputFileCheck.getID());
				while ((inLine = new StringBuilder(breader.readLine())) != null) {
					if (lineCounter == 0) {

						if (skipFirstLine) {
							log.finer("Skip first line of file.");
							lineCounter++;
							continue;
						}
					}
					lineCounter++;
					this.analyzeImportFileLine(inLine, actFileEntity, timeValues, stateObj);

				}
				retVal = true;

			} catch (NullPointerException e) {
				dataInputFileCheck.setFirstTimestamp(timeValues.getMinTimeFormattedString());
				dataInputFileCheck.setLastTimeStamp(timeValues.getMaxTimeFormattedString());

				dataInputFileCheck.setNumberOfLines(lineCounter);
				if (fileEntity_idsParam != null)
					fileEntity_idsParam.add(actFileEntity);
				log.info("Number of entities in actual file:" + actFileEntity.getEntitys().size());
				log.info("  catched, end of file reached. Lines read:" + lineCounter);
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

			log.fine("Analyze file done." + dataInputFileCheck.getFilePathAndName());
		}
		return retVal;
	}

	/**
	 * Analyze single Line of importfile, data is saved to {@link DataFileEntity}
	 * actFileEntity-
	 * 
	 * @param inLine
	 * @param actFileEntity
	 * @param timeValues    Object with min and max timevalues.
	 * @param stateOfLine   Object with state of line, so we also have that.
	 */
	private void analyzeImportFileLine(StringBuilder inLine, DataFileEntity actFileEntity, TimeValues timeValues,
			StateObject stateOfLine) {

		LocalDateTime actTimeStamp;
		String entity_idString;
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("\"yyyy-MM-dd HH:mm:ss.SSSSSS\"");
		int posOfDevider = 0; // to hold the Position of the devider from entity_id field.

		int devider1 = inLine.indexOf(",");
		actTimeStamp = LocalDateTime.parse(inLine.substring(0, devider1), IUsingDateTimeFormatter.GetFormatter());
		if (actTimeStamp.isBefore(timeValues.getMinTime())) {
			timeValues.setMinTime(actTimeStamp);
		}
		if (actTimeStamp.isAfter(timeValues.getMaxTime())) {
			timeValues.setMaxTime(actTimeStamp);
		}
		// entity_id check, put it in List
		// to avoid split lets do this:
//	
//		// there was a bad mixup with the entity ids, so now we use this.
		posOfDevider = inLine.lastIndexOf(",");

		stateOfLine.setState(inLine.substring(++devider1, posOfDevider));

		entity_idString = inLine.substring(++posOfDevider);
		// System.out.println(entity_idString);
		actFileEntity.getEntitys().add(entity_idString);
		//
	}

}
