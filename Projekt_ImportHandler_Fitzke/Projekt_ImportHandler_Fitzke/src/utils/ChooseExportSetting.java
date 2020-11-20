/**
 * 
 */
package utils;

 

/**
 * @author martin
 *
 */
public enum ChooseExportSetting  {
	latestImportedFile("last"), 
	
	everyImportedFile("all"), 

	latestImportedFileForced("lastforced"), 
	
	everyImportedFileForced("allforced"), 
	
	timeStamp("newerthen") ;
	 String val;
	
	/**
	 * 
	 */
	private ChooseExportSetting(String val) {
		 this.val=val;
	 
	}
	
	/**
	 * Gets the name of the selected value used in menue etc.
	 */
	public String getChosenValue() {
		return this.val;
	}
	
};