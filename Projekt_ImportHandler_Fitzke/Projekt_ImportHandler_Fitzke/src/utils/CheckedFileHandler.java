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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author martin
 *
 */
public class CheckedFileHandler {
	private static Logger log = new MyLogger(false).getLogger();
	private List<DataCheckedFile> checkedFiles;

	/**
	 * Caller creates and sets alList of files that have been checked. This is an
	 * easy way to handle Files.
	 * 
	 * @param checkedFiles
	 */
	public void setCheckedFiles(List<DataCheckedFile> checkedFiles) {
		this.checkedFiles = checkedFiles;
	}

	/**
	 * This tests if there is a checkedData file. if not it creates one.
	 * If there is a file it loads the checked file data.
	 */
	public void testAndLoadCheckedData( ) {
		boolean checked = testCheckedData();
		log.config("CheckData tested:" + checked);

		if (!checked) {
			log.info("Create new checkedData file and skip load.");
			boolean createChecked = createCheckedData();
			log.info("Created new empty CheckedDatafile: " + createChecked);
		} else {
			boolean loadData = loadCheckedData();
			log.config("Loaded checkedData:" + loadData);
		}
	}
	
	
	/**
	 * Create checkedDataFile.
	 */
	public boolean createCheckedData() {
		boolean retVal = false;
		File file = new File(Config.getPropertieValue("checkedDataFilePathAndName"));

		try {
			file.createNewFile();

			FileWriter pw = new FileWriter(file);
			pw.close();
			log.fine("CheckedDataFile Created.");
			retVal = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retVal;
	}

	/**
	 * remove checked Data File.
	 * @return true if everything went well.
	 */
	public boolean deleteCheckedData() {
			boolean retVal = false;
		File file = new File(Config.getPropertieValue("checkedDataFilePathAndName"));

		try {
			retVal=file.delete();
			log.fine("CheckedDataFile deleted.");
			retVal = true;
		} catch (SecurityException  e) {
			log.severe("Deleting checked data went wrong. File:"+Config.getPropertieValue("checkedDataFilePathAndName")+" exception:"+e);
		}
		return retVal;
	}
	/**
	 * Test if we do have a checkedData File.
	 * 
	 * @return true if we have one
	 */
	public boolean testCheckedData() {
		boolean retVal = false;
		String checkedDataFilePathAndName = Config.getPropertieValue("checkedDataFilePathAndName");
		if (checkedDataFilePathAndName == null) {
			log.severe("Kein ckeckedDataFilePathAndName in den Properties gesetzt.");
			return retVal;
		}
		File file = new File(checkedDataFilePathAndName);
		if (!file.exists()) {
			log.info("CheckedDataFile existiert nicht oder es gibt keinen Zugriff.");
			return retVal;
		}
		log.info("CheckedDataFile existiert.");
		retVal = true;
		return retVal;
	}

	/**
	 * Load already checkedDataFile into structure.
	 * 
	 * @return true if all went well.
	 */
	public boolean loadCheckedData() {
		boolean retVal = false;
		String checkedDataFilePathAndName = Config.getPropertieValue("checkedDataFilePathAndName");
		if (checkedDataFilePathAndName == null) {
			log.severe("Kein ckeckedDataFilePathAndName in den Properties gesetzt.");
			return retVal;
		}

		File file = new File(checkedDataFilePathAndName);
		try {
			@SuppressWarnings("resource")
			BufferedReader breader = new BufferedReader(new FileReader(file));
			if (this.checkedFiles == null) {
				this.checkedFiles = new ArrayList<DataCheckedFile>();
				log.info("no checkedData, create empty one.");
			} else {
				this.checkedFiles.clear();
				log.fine("Cleaned checkedData, load new data");
			}
			StringBuilder inLine;
			while ((inLine = new StringBuilder(breader.readLine())) != null) {
				// System.out.println(inLine.toString());
				this.checkedFiles.add(new DataCheckedFile(inLine.toString()));
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
		log.info("loaded CheckedData.");
		return retVal;
	}

	/**
	 * Save structure with already checked Files.
	 */
	public boolean saveCheckedData() {
		boolean retVal = false;
		String checkedDataFilePathAndName = Config.getPropertieValue("checkedDataFilePathAndName");
		if (checkedDataFilePathAndName == null) {
			log.severe("Kein ckeckedDataFilePathAndName in den Properties gesetzt.");
			return retVal;
		}
		if (this.checkedFiles == null) {
			this.checkedFiles = new ArrayList<DataCheckedFile>();
			log.info("no checkedData, no saving.");
		}
		File file = new File(checkedDataFilePathAndName);
		try {
			@SuppressWarnings("resource")
			BufferedWriter bwriter = new BufferedWriter(new FileWriter(file));
			this.checkedFiles.forEach((DataCheckedFile data) -> {
				if (data.getFilePathAndName() == null 
						|| data.getFilePathAndName().equals("toDelete")
						|| data.getFilePathAndName().length() < 3) {
					log.finer("Ignore this entry, its:" + data.getFilePathAndName());
				} else {
					try {
						bwriter.append(data.toFileString());
						bwriter.append("\n");
						bwriter.flush();
					} catch (IOException e) {
						e.printStackTrace();
						log.severe(e.toString());
					}
				}
			});

			retVal = true;

		} catch (IOException e) {
			//
			e.printStackTrace();
			log.severe(e.toString());
		}
		log.info("saved CheckedData.");
		return retVal;
	}

	/**
	 * Looks for entries in checkedFiles that have lost their File. This is a
	 * neccessety for longer running stuff.
	 */
	public boolean cleanCheckedData() {
		boolean retVal = false;
		if (this.checkedFiles == null || this.checkedFiles.size() == 0) {
			log.fine("No checkedFiles to test.");
			return retVal;
		}
		String importDir = Config.getPropertieValue("importdir");
		if (importDir == null) {
			log.severe("Kein Importdir in den Properties gesetzt.");
			return retVal;
		}

		// get Files from import dir
		File[] imports = new File(importDir).listFiles();
		if (imports.length == 0) {
			log.fine("There are no Files in import dir, so clear all.");
			this.checkedFiles.clear();
			retVal = true;
		} else {
			log.fine("There are " + imports.length
					+ " importfiles, lets check how many we have in our checkedFiles File.");
			int checkedFileSize = this.checkedFiles.size();
			log.fine("There are " + checkedFileSize + " entrys in our checkedFiles file.");
			boolean entryIsFile = false;
			for (DataCheckedFile dataInputFile : this.checkedFiles) {
				entryIsFile = false;
				for (int index = 0; index < imports.length; index++) {
					if (dataInputFile.getFilePathAndName().equals(imports[index].getAbsolutePath())) {
						entryIsFile = true;
						break;
					}

				} // end for each file
				if (!entryIsFile) {
					log.severe("Entry has no file, delete entry for: " + dataInputFile.getFilePathAndName());
					dataInputFile.setFilePathAndName("toDelete");
				}
			} // end for each entry

			DataCheckedFile deleteEntry = new DataCheckedFile();
			deleteEntry.setFilePathAndName("toDelete");

			while (this.checkedFiles.remove(deleteEntry)) {
				log.config("Removed Entry.");
			}

			log.info("Done cleaning.");
			retVal = true;
		}

		return retVal;
	}

	/**
	 * Gets the element with a given FilePathAndName, or null if we dont have it.
	 * If we dont have it it may be a wrong filename or the list has not been loaded.
	 * @param filePathAndName
	 * @return
	 */
	public DataCheckedFile getDataInputFileForFilePathAndName(String filePathAndName) {
		DataCheckedFile retVal = null;
		for (DataCheckedFile cfh : this.checkedFiles) {
			if (cfh.getFilePathAndName().equals(filePathAndName)) {
				retVal = cfh;		
				log.info("-> Found checkedFile with name.");
				break;
			}
		}
		return retVal;
	}
	
	
}
