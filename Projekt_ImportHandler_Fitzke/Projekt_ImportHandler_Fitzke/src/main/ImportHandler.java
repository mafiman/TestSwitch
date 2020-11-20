/**
 * 
 */
package main;

import java.sql.Savepoint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import utils.CheckedFileHandler;
import utils.ChooseExportSetting;
import utils.Config;
import utils.DataFileEntity;
import utils.DataCheckedFile;
import utils.FileEntityIDHandler;
import utils.IUsingDateTimeFormatter;
import utils.MyLogger;

/**
 * @author martin
 *
 */
public class ImportHandler {
	private static Logger log = new MyLogger().getLogger();

	/**
	 * List of DataCheckedFile Elements for already checkedFiles.
	 */
	private List<DataCheckedFile> checkedFiles = new ArrayList<>();
	/**
	 * List of entity_ids and files combined.
	 */
	private List<DataFileEntity> fileEntity_ids = new ArrayList<>();

	private CheckedFileHandler checkedFileHandler;

	private FileEntityIDHandler fileEntityIDHandler;

	private ImportFileHandler importFileHandler;

	private OutputFileHandler outputFileHandler;

	/**
	 * @return the checkedFiles
	 */
	public List<DataCheckedFile> getCheckedFiles() {
		return this.checkedFiles;
	}

	/**
	 * @param checkedFiles the checkedFiles to set
	 */
	public void setCheckedFiles(List<DataCheckedFile> checkedFiles) {
		this.checkedFiles = checkedFiles;
	}

	/**
	 * @return the fileEntity_ids
	 */
	public List<DataFileEntity> getFileEntity_ids() {
		return this.fileEntity_ids;
	}

	/**
	 * @param fileEntity_ids the fileEntity_ids to set
	 */
	public void setFileEntity_ids(List<DataFileEntity> fileEntity_ids) {
		this.fileEntity_ids = fileEntity_ids;
	}

	/**
	 * @return the checkedFileHandler
	 */
	public CheckedFileHandler getCheckedFileHandler() {
		return this.checkedFileHandler;
	}

	/**
	 * @param checkedFileHandler the checkedFileHandler to set
	 */
	private void setCheckedFileHandler(CheckedFileHandler checkedFileHandler) {
		this.checkedFileHandler = checkedFileHandler;
	}

	/**
	 * @return the fileEntityIDHandler
	 */
	public FileEntityIDHandler getFileEntityIDHandler() {
		return this.fileEntityIDHandler;
	}

	/**
	 * @param fileEntityIDHandler the fileEntityIDHandler to set
	 */
	private void setFileEntityIDHandler(FileEntityIDHandler fileEntityIDHandler) {
		this.fileEntityIDHandler = fileEntityIDHandler;
	}

	/**
	 * @return the importFileHandler
	 */
	public ImportFileHandler getImportFileHandler() {
		return this.importFileHandler;
	}

	/**
	 * @param importFileHandler the importFileHandler to set
	 */
	private void setImportFileHandler(ImportFileHandler importFileHandler) {
		this.importFileHandler = importFileHandler;
	}

	/**
	 * @return the outputFileHandler
	 */
	public OutputFileHandler getOutputFileHandler() {
		return this.outputFileHandler;
	}

	/**
	 * @param outputFileHandler the importFileHandler to set
	 */
	private void setOutputFileHandler(OutputFileHandler outputFileHandler) {
		this.outputFileHandler = outputFileHandler;
	}

	/**
	 * 
	 */
	public ImportHandler() {
		super();
		new Config(true);
	}

	/**
	 * For testing purposes. See content.
	 */
	public static void main(String[] args) {
		ImportHandler importHandler = new ImportHandler();

		importHandler.init();

		// next line is optional.
		log.config("Init done- Importdir:" + Config.getPropertieValue("importdir") + " and \nanalyzerdata dir:"
				+ Config.getPropertieValue("analyzerdatadir"));

		boolean settingOK = importHandler.testDirsAndSetting();

		log.config(" testDirsAndSetting tested: " + settingOK);
		if (!settingOK) {
			log.severe("\t\tSetting not ok, aboard.");
			return;
		}

		log.config("Import Data run: " + importHandler.runImport());

		/*
		 * // now for some tests :)
		 * 
		 * 
		 * // how many entities has importfile x? // if we have all data at hand: // ->
		 * // search importfile x in checkedFiles // getID // get fileEntity_ids entry
		 * with id // count entity_ids of entry
		 */
//		log.info("The file c:\\tmp\\importdata\\sensor.long.01.csv has how many entity_ids?");
//		log.info("\t First get id, load checked files if necc.");
//		String searchedFilePathAndFilename = "c:\\tmp\\importdata\\sensor.long.01.csv";
//		log.info("\t -> The importfile " + searchedFilePathAndFilename + " has "
//				+ importHandler.getCountOfEntityIdsImportFromFile(searchedFilePathAndFilename) + " entities");
		/*
		 * log.info("Same for very long sensor all:"); searchedFilePathAndFilename =
		 * "c:\\tmp\\importdata\\sensor.all.2020-10-26.csv";
		 * log.info("\t -> The importfile " + searchedFilePathAndFilename + " has " +
		 * importHandler .getCountOfEntityIdsImportFromFile(searchedFilePathAndFilename,
		 * checkedFileHandler, fileEntityIDHandler) + " entities");
		 * 
		 * 
		 */

	}

	public boolean testDirsAndSetting() {
		boolean retval = false;
		retval = this.getImportFileHandler().testImportdir();
		log.config("Importdir tested: " + retval);

		boolean retval2 = this.getImportFileHandler().testAnalyzerDataDir();
		log.config("AnalyzerDataDir tested: " + retval2);

		return retval && retval2;
	}

	/**
	 * Initialize ImportHandler. make sure to call this after declaration.
	 */
	public void init() {

		checkedFileHandler = new CheckedFileHandler();
		checkedFileHandler.setCheckedFiles(this.checkedFiles);

		fileEntityIDHandler = new FileEntityIDHandler();
		fileEntityIDHandler.setFileEntityIds(this.fileEntity_ids);

		importFileHandler = new ImportFileHandler();

		outputFileHandler = new OutputFileHandler();

	}

	/**
	 * Gets the number of entityIds From a given File.
	 * 
	 * @param filePathAndName
	 * 
	 * @return number of files, -1 if there is no entry in checkedFiles, -2 if there
	 *         is no entitys entry in datafileEntrys-
	 */
	public int getCountOfEntityIdsImportFromFile(String filePathAndName) {
		return getCountOfEntityIdsImportFromFile(filePathAndName, getCheckedFileHandler(), getFileEntityIDHandler());
	}

	/**
	 * Gets the number of entityIds From a given File.
	 * 
	 * @param filePathAndName
	 * @param checkedFileHandler
	 * @param fileEntityIDHandler
	 * @return number of files, -1 if there is no entry in checkedFiles, -2 if there
	 *         is no entitys entry in datafileEntrys-
	 */
	private int getCountOfEntityIdsImportFromFile(

			String filePathAndName,

			CheckedFileHandler checkedFileHandler,

			FileEntityIDHandler fileEntityIDHandler) {
		// how many entities has importfile x?
		// if we have all data at hand:
		// ->
		// search importfile x in checkedFiles
		// getID
		// get fileEntity_ids entry with id
		// count entity_ids of entry

		int retVal = -1;
		DataCheckedFile difc = checkedFileHandler.getDataInputFileForFilePathAndName(filePathAndName);
		if (difc == null) {
			retVal = -1;
		} else {
			String uuidStr = difc.getID();
			// Long numberOfLines= difc.getNumberOfLines();

			if (uuidStr != null) {
				try {
					retVal = fileEntityIDHandler.getAllDataFileEntitysForID(uuidStr).size();
				} catch (NullPointerException e) {
					// If we dont have values or we didnt find anything getbla is null.
					log.info("we dindt find an fileentity entry, so we get nothing.");
					retVal = -3;
				}
			} else {
				retVal = -2;
			}
		}
		return retVal;
	}

	/**
	 * Makes one run over the importdir and loads checked importfiles from
	 * checkedImportData file, also loads analyzed data from analyzedDataDir. The
	 * later can be suppressed by propertie. After all we check if there is a new
	 * import file. If so, we analyze it and save the newly computed data to
	 * analyzed datafile. If an analyzed datafile is deleted we ca newly write it.
	 * (again see porperties.)
	 * 
	 * @param importFileHandler
	 * @param checkedFileHandler
	 * @param fileEntityIDHandler
	 * @return
	 */
	public boolean runImport() {
		return runImport(this.getImportFileHandler(), getCheckedFileHandler(), getFileEntityIDHandler());
	}

	/**
	 * Makes one run over the importdir and loads checked importfiles from
	 * checkedImportData file, also loads analyzed data from analyzedDataDir. The
	 * later can be suppressed by propertie. After all we check if there is a new
	 * import file. If so, we analyze it and save the newly computed data to
	 * analyzed datafile. If an analyzed datafile is deleted we ca newly write it.
	 * (again see porperties.)
	 * 
	 * @param importFileHandler
	 * @param checkedFileHandler
	 * @param fileEntityIDHandler
	 * @return
	 */
	private boolean runImport(

			ImportFileHandler importFileHandler,

			CheckedFileHandler checkedFileHandler,

			FileEntityIDHandler fileEntityIDHandler) {
		boolean retVal = true;

		log.config("Checked and loaded checkedFile Data.");
		checkedFileHandler.testAndLoadCheckedData();

//		importHandler.checkedFiles.forEach((DataCheckedFile dat) -> System.out.println(dat.toString()));
		log.config("The flag loadanalyzeddata is set to:" + Config.getPropertieValue("loadanalyzeddata"));

		log.config("Load all fileEntityIds from analyzer Files: "
				+ fileEntityIDHandler.loadAllFileEntityIds(this.fileEntity_ids));

		log.config("Importdir checked and files checked: "
				+ importFileHandler.checkImportdir(this.checkedFiles, this.fileEntity_ids));

// 	    log.config("Clean checkedFile List: " + checkedFileHandler.cleanCheckedData());

//		importHandler.checkedFiles.forEach((DataCheckedFile dat) -> System.out.println(dat.toString()));
//		System.out.println("--------------------");
//		importHandler.fileEntity_ids.forEach((DataFileEntity dat) -> System.out.println(dat.toFileString()));

		log.config("All fileEntityIds saved: " + fileEntityIDHandler.saveAllFileEntityIds(this.fileEntity_ids,
				new Boolean(Config.getPropertieValue("skipifalreadyafile"))));

		log.config("CheckedData saved: " + checkedFileHandler.saveCheckedData());

		return retVal;
	}

	/**
	 * Get the analyzed Data from files to show in commandline.
	 * 
	 * @return String with preformatted list of filenames with # of Entity_ids and
	 *         the filename of analyzed data.
	 */
	public String getAnalyzedDataToShow() {
		String retVal = "";
		StringBuilder sbRetVal = new StringBuilder();

		boolean settingOK = this.testDirsAndSetting();
		if (settingOK) {
			this.getCheckedFileHandler().testAndLoadCheckedData(); // loadCheckedData();
			Config.setPropertieValue("loadanalyzeddata", "true");
			this.getFileEntityIDHandler().loadAllFileEntityIds();

			sbRetVal.append("\nList of analyzed importfiles:\n");
			sbRetVal.append("ImportFile \t\t #EntityIDs: \t\t  Filename:\n");
			for (DataFileEntity f : this.getFileEntity_ids()) {
				sbRetVal.append(f.getFilePathAndName() + "\t\t: " + f.getEntitys().size() + "\t:" + f.getId().toString()
						+ ".dat\n");
			}
			retVal = sbRetVal.append("\n\n").toString();
		}

		return retVal;
	}

	/**
	 * Get preformatted String to show in commandline containing names and data of
	 * loaded files. Loaded files are the ones in checkedData.csv, the get there
	 * after an import run.
	 * 
	 * @param levelOfInfo Which level of information is wanted. filename + 1
	 *                    firstTimestamp + 2 lastTimestamp + 3 NumberOfFileLines
	 * @return Preformatted String to display.
	 */
	public String getLoadedImportfilesDataToShow(int levelOfInfo) {
		String retVal = "";
		StringBuilder sbRetVal = new StringBuilder();
		boolean settingOK = this.testDirsAndSetting();
		if (settingOK) {
			this.getCheckedFileHandler().testAndLoadCheckedData();// loadCheckedData();
			sbRetVal.append("\nList of loaded importfiles:\n");
			sbRetVal.append("ImportFileName");
			sbRetVal.append("\t Exported ");
			switch (levelOfInfo) {
			case 3:
				sbRetVal.append("\t NumberOfLines");
			case 2:
				sbRetVal.append("\t LastTime");
			case 1:
				sbRetVal.append("\t FirstTime");
			}

			for (DataCheckedFile dif : this.getCheckedFiles()) {
				sbRetVal.append("\n" + dif.getFilePathAndName()).append("\t " + dif.getLastExported());
				switch (levelOfInfo) {
				case 3:
					sbRetVal.append("\t" + dif.getNumberOfLines());
				case 2:
					sbRetVal.append("\t" + dif.getLastTimeStamp());
				case 1:
					sbRetVal.append("\t" + dif.getFirstTimestamp());
				}
			}
			retVal = sbRetVal.append("\n\n").toString();
		}

		return retVal;
	}

	/**
	 * Get me a List of loaded importfiles, PathAndFilenames only. To get the whole
	 * shebang use getcheckedFiles().
	 * 
	 * @return String[] of loaded importfiles.
	 */
	public String[] getLoadedImportFilesListToShow() {
		List<String> retVal = new ArrayList<>();
		for (DataCheckedFile dif : this.getCheckedFiles()) {
			retVal.add(dif.getFilePathAndName());
		}
		return retVal.toArray(new String[0]);
	}

	/**
	 * Makes one run over the importdir and loads checked importfiles from
	 * checkedImportData file. After that we make a run over (again see porperties.)
	 * 
	 */
	public boolean runExport(ChooseExportSetting exportSetting) {
		return runExport(this.getImportFileHandler(),

				getCheckedFileHandler(),

				getFileEntityIDHandler(),

				getOutputFileHandler(),

				exportSetting);
	}

	/**
	 * Generate output file. Which data is included in output file is determined by
	 * parameter in properties. We can choose if the output is only generated for
	 * the latest imported data, or for all imported data, or for data with
	 * Timestamp >= someTimestamp.
	 * 
	 * @return
	 */
	private boolean runExport(

			ImportFileHandler importFileHandler,

			CheckedFileHandler checkedFileHandler,

			FileEntityIDHandler fileEntityIDHandler,

			OutputFileHandler outputFileHandler,

			ChooseExportSetting exportSetting) {
		boolean retVal = true;





		log.config("Checked and loaded checkedFile Data.");
		checkedFileHandler.testAndLoadCheckedData();

		this.checkedFiles.forEach((DataCheckedFile dat) -> System.out.println(dat.toString()));

		log.config("The flag loadanalyzeddata is set to:" + Config.getPropertieValue("loadanalyzeddata"));
		Config.setPropertieValue("loadanalyzeddata", "false");
//
//		log.config("Load all fileEntityIds from analyzer Files: "
//				+ fileEntityIDHandler.loadAllFileEntityIds(this.fileEntity_ids));

		boolean propertiesOk = outputFileHandler.testOutputDirAndProperties();
		log.info("Check Properties for outputfile Handler. All was:" + propertiesOk);

		if (exportSetting.equals(ChooseExportSetting.latestImportedFile)
				|| exportSetting.equals(ChooseExportSetting.latestImportedFileForced)) {
			DataCheckedFile latestdif = getLatestImportFile();
			if (latestdif == null) {
				log.severe("No checked importfiles or forgotten to load them. Abort import.");
				return false;
			}
			
			if (exportSetting.equals(ChooseExportSetting.latestImportedFileForced)) {
				log.info("Generate OutputFile for: latest importFile forced, regardless of already export do it.");
			} else {
				log.info("Generate OutputFile for: latest importFile");
				// check if latest dif is already exported. Aboard if so
				if (latestdif.getLastExported() == null || latestdif.getLastExported().isEmpty()) {
					log.info("File is not exported. Importfile:"
							+ latestdif.getFilePathAndName());
				} else {
					log.severe("File is already exported. Use forced version if you are sure what you do. Importfile:"
							+ latestdif.getFilePathAndName());
					return false;
				}
			}

			boolean checkAndOutputOK = outputFileHandler.checkImportFileAndOutputFile(latestdif.getFilePathAndName(),
					false);
			log.info("Export file went:" + checkAndOutputOK);
			if (checkAndOutputOK) {
				log.fine("Set latestExporttime to now.");
				latestdif.setLastExported(LocalDateTime.now().format(IUsingDateTimeFormatter.GetFormatter()));
				log.fine("   update checkedData file went:" + this.getCheckedFileHandler().saveCheckedData());
			}
		} else if (exportSetting.equals(ChooseExportSetting.everyImportedFile)
				|| exportSetting.equals(ChooseExportSetting.everyImportedFileForced)) {
			log.info("Generate OutputFile for: all importFiles");
			boolean checkAndOutputOK = outputFileHandler.checkAllImportedFilesAndOutputFiles(this.getCheckedFiles(),
					exportSetting.equals(ChooseExportSetting.everyImportedFileForced));
			log.info("Export file went:" + checkAndOutputOK);
			if (checkAndOutputOK) {
				log.fine("Set latestExporttime to now.");
				for (DataCheckedFile actdifc : this.getCheckedFiles()) {
					actdifc.setLastExported(LocalDateTime.now().format(IUsingDateTimeFormatter.GetFormatter()));
				}

				log.fine("   update checkedData file went:" + this.getCheckedFileHandler().saveCheckedData());
			}
		}

//		log.config("Importdir checked and files checked: "
//				+ importFileHandler.checkImportdir(this.checkedFiles, this.fileEntity_ids));

// 	    log.config("Clean checkedFile List: " + checkedFileHandler.cleanCheckedData());

		return retVal;
	}

	/**
	 * This gets the last imported filePathandName.
	 * 
	 * @return PathAndName of latest imported Data or Null.
	 */
	private DataCheckedFile getLatestImportFile() {
		//
		DataCheckedFile retVal = null;
		LocalDateTime maxCheckedData = LocalDateTime.MIN;

		LocalDateTime actCheckedData = null;
		for (DataCheckedFile actFile : this.getCheckedFiles()) {
			try {
				actCheckedData = LocalDateTime.parse(actFile.getLastChecked(), IUsingDateTimeFormatter.GetFormatter());
				if (actCheckedData.isAfter(maxCheckedData)) {
					retVal = actFile;
					maxCheckedData = actCheckedData;

				}
			} catch (Exception e) {
				log.finer("Upsi happened while finding the latest import file. Skip this one.");
			}

		}
		if (retVal != null) {
			log.info("Found latest checked File: " + retVal.getFilePathAndName() + " Last checked:"
					+ retVal.getLastChecked());
		}

		return retVal;
	}

	/**
	 * Remove Data from stuff.
	 * 
	 * @param parameter what to clean.
	 * @return true if everything went ok.
	 */
	public boolean removeData(String parameter) {
		boolean retVal = false;
		if (parameter == null) {
			log.info("user wants to clear data, doesnot know which. Aboard.");
			return true;
		}
//				"analayzeddata -> data in analyzeddir, \r\n "
//		
//				+ "outputfiles ->  files in output dir \r\n"
//				
//				+ "importedfiles -> file that holds the imported Files information Not the importfiles themselfs., \r\n"
//				
//				+ "alltogether -> everything. 
		switch (parameter) {
		case "analayzeddata":
			retVal = this.getImportFileHandler().deleteAnalyzerdirFiles();
			break;
		case "outputfiles":
			retVal = this.getOutputFileHandler().deleteOutputDirFiles();
			break;
		case "importedfiles":
			retVal = this.getCheckedFileHandler().deleteCheckedData();
			break;
		case "alltogether":
			retVal = this.getImportFileHandler().deleteAnalyzerdirFiles() ;

					this.getOutputFileHandler().deleteOutputDirFiles() ;

					this.getCheckedFileHandler().deleteCheckedData();
			break;

		}

		return retVal;
	}
}
