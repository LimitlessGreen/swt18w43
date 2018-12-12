package bioladen.datahistory;

import lombok.Getter;

@Getter
public class DataHistoryRequest {
	private Class clazz;
	private EntityLevel entityLevel;

	public DataHistoryRequest(Class clazz, EntityLevel entityLevel) {
		this.clazz = clazz;
		this.entityLevel = entityLevel;
	}
}
