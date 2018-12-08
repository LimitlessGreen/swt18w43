package bioladen.datahistory;

import lombok.Getter;

public enum EntityLevel {
	CREATED("add"),
	MODIFIED("edit"),
	DELETED("delete");

	private final @Getter String className;

	EntityLevel(String className) {
		this.className = className;
	}
}
