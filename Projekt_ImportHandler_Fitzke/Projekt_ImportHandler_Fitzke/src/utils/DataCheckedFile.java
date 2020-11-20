/**
 * 
 */
package utils;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * @author martin
 *
 */
public class DataCheckedFile {
	private static Logger log = new MyLogger(false).getLogger();
	private UUID id;
	private String filePathAndName;
	private String lastChecked;
	private String lastExported;
	private String firstTimestamp;
	private String lastTimeStamp;
	private Long numberOfLines;

	/**
	 * Last time the importfile was exported
	 * @return
	 */
	public String getLastExported() {
		return this.lastExported;
	}
	/**
	 * set last Time this file was exported
	 * @param lastExported
	 */
	public void setLastExported(String lastExported) {
		this.lastExported= lastExported;
	}
	
	
	public String getID() {
		return id.toString();
	}

	public void setID(String uuidStr) {
		try {
			this.id = UUID.fromString(uuidStr);
		} catch (Exception e) {
			log.severe(
					"We used a new UUID for this entry, bc there was a Problem with " + uuidStr + " " + e.toString());
			this.id = UUID.randomUUID();
		}
	}

	/**
	 * @return the filePathAndName
	 */
	public String getFilePathAndName() {
		return this.filePathAndName;
	}

	/**
	 * @param filePathAndName the filePathAndName to set
	 */
	public void setFilePathAndName(String filePathAndName) {
		this.filePathAndName = filePathAndName;
	}

	/**
	 * @return the lastChecked
	 */
	public String getLastChecked() {
		return this.lastChecked;
	}

	/**
	 * @param lastChecked the lastChecked to set
	 */
	public void setLastChecked(String lastChecked) {
		this.lastChecked = lastChecked;
	}

	/**
	 * @return the firstTimestamp
	 */
	public String getFirstTimestamp() {
		return this.firstTimestamp;
	}

	/**
	 * @param firstTimestamp the firstTimestamp to set
	 */
	public void setFirstTimestamp(String firstTimestamp) {
		this.firstTimestamp = firstTimestamp;
	}

	/**
	 * @return the lastTimeStamp
	 */
	public String getLastTimeStamp() {
		return this.lastTimeStamp;
	}

	/**
	 * @param lastTimeStamp the lastTimeStamp to set
	 */
	public void setLastTimeStamp(String lastTimeStamp) {
		this.lastTimeStamp = lastTimeStamp;
	}

	public DataCheckedFile() {
		this.id = UUID.randomUUID();
	}

	/**
	 * @return the numberOfLines
	 */
	public Long getNumberOfLines() {
		return this.numberOfLines;
	}

	/**
	 * @param numberOfLines the numberOfLines to set
	 */
	public void setNumberOfLines(Long numberOfLines) {
		this.numberOfLines = numberOfLines;
	}

	/**
	 * Now we create a new Object with a uuid from file. So we have allways the same
	 * uuid for the data entry
	 * 
	 * @param readLine
	 */
	public DataCheckedFile(String inputLine) {
		String[] parts = inputLine.split(",");

		if (parts.length < 7) {
			// we have a crappy line, so fupp it
			log.severe("importError: lentgh:" + parts.length + "  inputline: " + inputLine);
			this.id = UUID.randomUUID();
			this.setFilePathAndName("toDelete");
		} else {
			log.finer("DataInputFileCheck2: " + parts.length + " " + parts[0] + " " + parts[1]);
			// new DataCheckedFile(parts);
			this.setID(parts[0].trim());
			this.setFilePathAndName(parts[1]);
			this.setLastChecked(parts[2]);	
			this.setLastExported(parts[3]);
			this.setFirstTimestamp(parts[4]);
			this.setLastTimeStamp(parts[5]);
			try {
				this.setNumberOfLines(new Long(parts[6].trim()));
				// System.out.println("Linenumbers:"+parts[6]);
			} catch (NumberFormatException e) {
				System.out.println("In exception Linenumbers:" + parts[6] + e.getMessage());
				this.setNumberOfLines(0L);
			}
		
			// this.id = UUID.randomUUID();
		}
	}

	/**
	 * @param filePathAndName
	 * @param lastChecked
	 * @param firstTimestamp
	 * @param lastTimeStamp
	 */
	public DataCheckedFile(String filePathAndName, String lastChecked, String firstTimestamp, String lastTimeStamp) {
		this.id = UUID.randomUUID();
		this.setFilePathAndName(filePathAndName);
		this.setLastChecked(lastChecked);
		this.setFirstTimestamp(firstTimestamp);
		this.setLastTimeStamp(lastTimeStamp);

		// log.finer("DataInputFileCheck2: " + filePathAndName + " " + lastChecked + " "
		// + firstTimestamp);
	}

	/**
	 * 
	 * @param filePathAndName
	 * @param lastChecked
	 * @param lastExported
	 * @param firstTimestamp
	 * @param lastTimeStamp
	 * @param numberOfLines
	 */
	public DataCheckedFile(String filePathAndName, String lastChecked, String lastExported, String firstTimestamp, String lastTimeStamp,
			Long numberOfLines) {
		this(filePathAndName,   lastChecked,   firstTimestamp,   lastTimeStamp);
		this.setLastExported(lastExported);
	}

	/**
	 * 
	 * @param filePathAndName
	 * @param lastChecked
	 * @param firstTimestamp
	 * @param lastTimeStamp
	 * @param numberOfLines
	 */
	public DataCheckedFile(String filePathAndName, String lastChecked, String firstTimestamp, String lastTimeStamp,
			Long numberOfLines) {
		this.id = UUID.randomUUID();
		this.setFilePathAndName(filePathAndName);
		this.setLastChecked(lastChecked);
		this.setFirstTimestamp(firstTimestamp);
		this.setLastTimeStamp(lastTimeStamp);
		this.setNumberOfLines(numberOfLines);
	}

		
	@Override
	public String toString() {
		return "DataCheckedFile [" + (this.getID() != null ? "ID=" + this.getID() + ", " : ", ")
				+ (this.getFilePathAndName() != null ? "getFilePathAndName()=" + this.getFilePathAndName() + ", "
						: ", ")
				+ (this.getLastChecked() != null ? "getLastChecked()=" + this.getLastChecked() + ", " : ", ")	
				+ (this.getLastExported() != null ? "getLastExported()=" + this.getLastExported () + ", " : ", ")
				+ (this.getFirstTimestamp() != null ? "getFirstTimestamp()=" + this.getFirstTimestamp() + ", " : ", ")
				+ (this.getLastTimeStamp() != null ? "getLastTimeStamp()=" + this.getLastTimeStamp() + ", " : ",")
				+ (this.getNumberOfLines() != null ? "getNumberOfLines()=" + this.getNumberOfLines() : " ") + "]";
	}

	public String toFileString() {
		return ((this.getID() != null ? this.getID() + "," : " ,")
				+ (this.getFilePathAndName() != null ? this.getFilePathAndName() + "," : ",")
				+ (this.getLastChecked() != null ? this.getLastChecked() + "," : ",")
				+ (this.getLastExported () != null ? this. getLastExported() + "," : ",")
				+ (this.getFirstTimestamp() != null ? this.getFirstTimestamp() + "," : ",")
				+ (this.getLastTimeStamp() != null ? this.getLastTimeStamp() + "," : ",")
				+ (this.getNumberOfLines() != null ? this.getNumberOfLines() : ""));
	}

	public boolean isFilePathAndName(String filePathAndName) {
		if (this.getFilePathAndName() == null)
			return false;
		return this.getFilePathAndName().equals(filePathAndName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataCheckedFile other = (DataCheckedFile) obj;
		if (this.filePathAndName == null) {
			if (other.filePathAndName != null)
				return false;
		} else if (!this.filePathAndName.equals(other.filePathAndName))
			return false;
		return true;
	}

}
