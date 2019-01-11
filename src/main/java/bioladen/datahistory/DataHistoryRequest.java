package bioladen.datahistory;

import lombok.Getter;

/**
 * A request, used in each entity to get the right data for the diagrams
 */
@Getter
public class DataHistoryRequest {
	private Class clazz;
	private EntityLevel entityLevel;

	public DataHistoryRequest(Class clazz, EntityLevel entityLevel) {
		this.clazz = clazz;
		this.entityLevel = entityLevel;
	}
}
