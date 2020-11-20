/**
 * 
 */
package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author martin
 *
 */
public class FileEntityIDHandler {
	private static Logger log = new MyLogger(false).getLogger();
	/**
	 * List of entity_ids and files combined.
	 */
	private List<DataFileEntity> fileEntity_ids = new ArrayList<>();
	
	public void setFileEntityIds(List<DataFileEntity> fileEntity_ids) {
		if (fileEntity_ids!=null) {
			this.fileEntity_ids=fileEntity_ids;
		}
	}
	/**
	 * Save every entry to a single File, so we have a file with entrys for every
	 * InputFile.
	 * 
	 * @param fileEntity_ids2
	 */
	public boolean saveAllFileEntityIds(List<DataFileEntity> fileEntity_ids2) {
		return saveAllFileEntityIds(fileEntity_ids2, false);
	}

	/**
	 * Save every entry to a single File, so we have a file with entrys for every
	 * InputFile. Now we use the analyzerDataDir and the uuid of the original file
	 * to save in central place.
	 * 
	 * @param fileEntity_ids2
	 * @param skipIfAlreadyAFile Skip saving if file is already there.
	 */
	public boolean saveAllFileEntityIds(List<DataFileEntity> fileEntity_ids2, boolean skipIfAlreadyAFile) {
		//
		// to make life easier we simply put a .dat extension to every importfileName.
		boolean retVal = false;
		boolean singleRetVal = false;
		if (fileEntity_ids2 == null) {
			log.info("no Entity Data, no saving.");
			return retVal;
		}
		for (DataFileEntity dataFileEntity : fileEntity_ids2) {
			// Save data of this entry to file

			singleRetVal = saveSingleFileEntityIds(dataFileEntity, skipIfAlreadyAFile);
			log.config("    Saving of ImportFileEntityIds to file went" + singleRetVal);
		} // end for each entry
		log.info("Saved all Entity Data.");

		return retVal;
	}

	/***
	 * Save Entitys of an importfile. Save to single file all entries plus info of
	 * importfile plus id of enty, so we could do some good old referencing with the
	 * old ids.
	 * 
	 * @param dataFileEntity
	 * @param skipIfAlreadyAFile
	 * @return
	 */
	public boolean saveSingleFileEntityIds(DataFileEntity dataFileEntity, boolean skipIfAlreadyAFile) {
		boolean retVal = false;
		File file;
//		String dataFilePathAndName = Config.getAnalyzerDataDir().concat("\\").concat(dataFileEntity.getId().toString())
//				.concat(".dat");
		StringBuilder filePathAndName = new StringBuilder(
				Config.getAnalyzerDataDir().concat("\\").concat(dataFileEntity.getId().toString()).concat(".dat"));
		if (dataFileEntity == null || dataFileEntity.getId() == null

				|| dataFileEntity.getFilePathAndName().length() == 0

				|| dataFileEntity.getFilePathAndName().equals("toDelete")) {
			log.fine("Dont save all Entity Data to empty filename file:" + dataFileEntity.getFilePathAndName()
					+ " New Filename:" + filePathAndName + ".dat");
			return retVal;
		}

		if (dataFileEntity.getEntitys().isEmpty()) {
			log.fine("Dont save all Entity Data if there are no entities. file:" + dataFileEntity.getFilePathAndName()
					+ " New Filename:" + filePathAndName);
			return retVal;
		}

		// filePathAndName = new
		// StringBuilder(dataFileEntity.getFilePathAndName().trim() + ".dat");

		file = new File(filePathAndName.toString());
		if (file.exists() && skipIfAlreadyAFile) {
			log.info("Entity Data for this file already saved. So do not save.");
			return retVal;
		}
		log.fine("Save all Entity Data to file:" + filePathAndName.toString());
		try {
			@SuppressWarnings("resource")
			BufferedWriter bwriter = new BufferedWriter(new FileWriter(file));
			bwriter.append(dataFileEntity.toFileString());
			bwriter.append("\n");
			bwriter.flush();
			retVal = true;
		} catch (IOException e) {
			//
			e.printStackTrace();
			log.severe(e.toString());
		}
		return retVal;
	}

	
	/**
	 * Load all FileEntityIds. They have .dat extension and are in the same dir as
	 * the data import files if there is na analyzerDataDir defined.
	 * 
	 * @param fileEntity_ids2
	 * @return true if everything went well.
	 */
	 public boolean loadAllFileEntityIds() {
		 return loadAllFileEntityIds(this.fileEntity_ids);
	 }
	
	/**
	 * Load all FileEntityIds. They have .dat extension and are in the same dir as
	 * the data import files if there is na analyzerDataDir defined.
	 * 
	 * @param fileEntity_ids2
	 * @return true if everything went well.
	 */
	public boolean loadAllFileEntityIds(List<DataFileEntity> fileEntity_ids2) {
		boolean retVal = false;
		boolean loadingAlreadyAnalyzed = new Boolean(Config.getPropertieValue("loadanalyzeddata"));
		if (!loadingAlreadyAnalyzed) {
			log.info("!We have set loadanalyzeddata:" + loadingAlreadyAnalyzed + " so we skip loading analyzer filedata.");
			return false;
		}

		String importDir = Config.getAnalyzerDataDir();

		// importDir = Config.getPropertieValue("importdir");
		File file = new File(importDir);
		if (!file.exists()) {
			log.severe("AnayzerDataDir existiert nicht oder es gibt keinen Zugriff.");
			return retVal;
		}

		File[] imports = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".dat");
			}
		});
		if (imports.length == 0) {
			log.info("Es gibt keine EntityId Import Dateien.");
		} else {

			retVal = true;

			log.info("Es gibt " + imports.length + " DataFileEntityId Dateien.");
			for (File infile : imports) {
				log.fine("Fileimport of File is: " + loadFileEntityIds(infile.getAbsolutePath(), fileEntity_ids2));
			}
		}
		return retVal;
	}

	/**
	 * Load single fileEntityId, use FilePathandName to identify and store Data in
	 * given List.
	 * 
	 * @param fileNameAndPath
	 * @param fileEntity_ids2
	 * @return
	 */
	public boolean loadFileEntityIds(String fileNameAndPath, List<DataFileEntity> fileEntity_ids2) {
		boolean retVal = false;

		if (fileEntity_ids2 == null) {
			fileEntity_ids2 = new ArrayList<DataFileEntity>();
		}

		if (fileNameAndPath == null) {
			log.severe("No PathAndFilename given.");
			return retVal;
		}

		try {
			BufferedReader breader = new BufferedReader(new FileReader(new File(fileNameAndPath)));
			StringBuilder inLine;
			while ((inLine = new StringBuilder(breader.readLine())) != null) {
				// System.out.println(inLine.toString());
				// We have to take care if we load an existing entry. bollocks.
				fileEntity_ids2.add(new DataFileEntity(inLine));
			}
			retVal = true;
		} catch (NullPointerException e) {
			log.info("null catched, end of file reached");
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
		log.info("loaded EntityData from file.");
		return retVal;
	}

	
	/*
	 * Methods to handle FileEntityId Stuff
	 */
	/**
	 * Gets the DataFileEntity for a given uuid.
	 * @param uuidStr
	 * @return null or entityId object
	 */
	public DataFileEntity getDataFileEntityForID(String uuidStr) {
		DataFileEntity retVal=null;
		if (uuidStr != null) {
			for (DataFileEntity dfe : this.fileEntity_ids) {
				if (dfe.getId().toString().equals(uuidStr)) {
					log.info("-> Found fileEntity Object with id.");
					retVal= dfe;
					break;
				}
			}
		}
		return retVal;
	}
	
	/**
	 * Gets the HashSet<String> with the EntityIds for an object with a given uuid.
	 * @param uuidStr 
	 * @return null or hashSet<String> whith entity_id for this object.
	 */
	public HashSet<String> getAllDataFileEntitysForID(String uuidStr) {
		HashSet<String> retVal=null;
		if (uuidStr != null) {
			for (DataFileEntity dfe : this.fileEntity_ids) {
				if (dfe.getId().toString().equals(uuidStr)) {
					log.info("-> Found fileEntity_ids with id.");
					retVal= dfe.getEntitys();
					break;
				}
			}
		}
		return retVal;
	}
}
