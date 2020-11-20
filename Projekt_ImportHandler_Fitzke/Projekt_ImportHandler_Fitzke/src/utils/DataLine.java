/**
 * 
 */
package utils;

import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * @author martin
 *
 */
public class DataLine implements IUsingDateTimeFormatter {
	//private static Logger log = new MyLogger().getLogger();
	private String last_changed;
	private String state;
	private String entity_id;

	/**
	 * @return the last_changed
	 */
	public String getLast_changed() {
		return this.last_changed;
	}

	/**
	 * @param last_changed the last_changed to set
	 */
	public void setLast_changed(String last_changed) {
		this.last_changed = last_changed;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return this.state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the entity_id
	 */
	public String getEntity_id() {
		return this.entity_id;
	}

	/**
	 * @param entity_id the entity_id to set
	 */
	public void setEntity_id(String entity_id) {
		this.entity_id = entity_id;
	}

	public DataLine() {
		this("", "", "");
	}

	public DataLine(String inputLine) {
//		String[] parts = inputLine.split(",");
//
//		if (parts.length < 3) {
//			// we have a crappy line, so fupp it and
//			// shovel all in state, last_changed will be now()
//			String last_changed = LocalDateTime.now().format(IUsingDateTimeFormatter.GetFormatter());
////			new DataLine(last_changed, inputLine, "importError");
//			setLast_changed(last_changed);
//			setState("importError");
//		} else {
////			new DataLine(parts[0], parts[1], parts[2]);
//			setLast_changed(parts[0]);
//			setState(parts[1]);
//			setEntity_id(parts[2]);
//		}

		int devider1 = inputLine.indexOf(",");
		int posOfLastDevider = inputLine.lastIndexOf(",");
		// there was a bad mixup with the entity ids, so now we use this.
		this.setLast_changed(inputLine.substring(0, devider1));
		this.setState(inputLine.substring(++devider1, posOfLastDevider));
		this.setEntity_id(inputLine.substring(++posOfLastDevider));
	}

	/**
	 */
	public DataLine(String last_changed, String state, String entity_id) {
		super();
		this.setLast_changed(last_changed);
		this.setState(state);
		this.setEntity_id(entity_id);
	}

	/**
	 * @param actTimeStamp
	 * @param stateString
	 * @param entity_idString
	 */
	public DataLine(LocalDateTime actTimeStamp, String stateString, String entity_idString) {
		this.setLast_changed(actTimeStamp.format(IUsingDateTimeFormatter.GetFormatter()));
		this.setState(state);
		this.setEntity_id(entity_id);
	}

	public String toSFiletring() {
		return (this.getLast_changed() != null ? this.getLast_changed() + "," : ",")
				+ (this.getState() != null ? this.getState() + "," : ",")
				+ (this.getEntity_id() != null ? this.getEntity_id() : "");
	}

	@Override
	public String toString() {
		return "DataLine ["
				+ (this.getLast_changed() != null ? "getLast_changed()=" + this.getLast_changed() + ", " : "null, ")
				+ (this.getState() != null ? "getState()=" + this.getState() + ", " : "null, ")
				+ (this.getEntity_id() != null ? "getEntity_id()=" + this.getEntity_id() : "null") + "]";
	}

	/**
	 * Test expression for each line.
	 * 
	 * @return true if it fits.
	 */
	public boolean isFittingState(String expression) {
		//log.finest("State: "+this.getState()+": expre"+expression);
		if (  expression.isEmpty() || this.getState() == null)
			return false;
		return this.getState().contains(expression);
	}

	/**
	 * Test expression for each line.
	 * 
	 * @return true if it fits.
	 */
	public boolean isFittingEntity_id(String expression) {
		if ( expression.isEmpty() || this.getEntity_id() == null)
			return false;
		return this.getEntity_id().contains(expression);
	}

}
