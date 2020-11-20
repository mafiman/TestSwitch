/**
 * 
 */
package utils;

import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * @author martin
 *
 */
public class DataFileEntity {
	private static Logger log = new MyLogger(false).getLogger();
	private UUID id;
	private HashSet<String> entitys;
	private String filePathAndName;

	/**
	 * @return the id
	 */
	public UUID getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * @return the entitys
	 */
	public HashSet<String> getEntitys() {
		return this.entitys;
	}

	/**
	 * @param entitys the entitys to set
	 */
	public void setEntitys(HashSet<String> entitys) {
		this.entitys = entitys;
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

	public DataFileEntity() {
		this(new HashSet<String>(), "random");
	}

	public DataFileEntity(String filePathAndName) {
		this(new HashSet<String>(), filePathAndName);
	}

	/**
	 * @param entitys
	 * @param filePathAndName
	 */
	public DataFileEntity(HashSet<String> entitys, String filePathAndName) {
		super();
		this.setId(UUID.randomUUID());
		this.setEntitys(entitys);
		this.setFilePathAndName(filePathAndName);
	}

	/**
	 * Create new {@link DataFileEntity} from line of File. Each Entity will reside in an own entry in a set of entities.
	 * 
	 * @param inLine Line from File.
	 */
	public DataFileEntity(StringBuilder inLine) {
		this();
		String[] parts = inLine.toString().split(",");
		this.setId(UUID.fromString(parts[0]));
		this.setFilePathAndName(parts[1].trim());
		String inLineEntitys = "";
		this.entitys= new HashSet<>();
		try {
			inLineEntitys = inLine.substring(inLine.indexOf("[")+1, inLine.lastIndexOf("]"));
//			System.out.println(inLineEntitys);
			String parts2[]= inLineEntitys.split(",");
			for (int index=0; index<parts2.length; index++) {
				this.entitys.add(parts2[index].trim());
			}
			log.fine("---Added "+parts2.length+" entity ids");
		} catch (Exception e) {
			 
			e.printStackTrace();
		}
		 
	}

	@Override
	public String toString() {
		return "DataFileEntity [" + (this.getId() != null ? "getId()=" + this.getId() + ", " : ", ")
				+ (this.getFilePathAndName() != null ? "getFilePathAndName()=" + this.getFilePathAndName() + ", "
						: ", ")
				+ (this.getEntitys() != null ? "getEntitys()=" + this.getEntitys() : " ") + "]";
	}

	public String toFileString() {
		return (this.getId() != null ? this.getId() + "," : ",")
				+ (this.getFilePathAndName() != null ? this.getFilePathAndName() + "," : ",")
				+ (this.getEntitys() != null ? this.getEntitys() : "");
	}
}
