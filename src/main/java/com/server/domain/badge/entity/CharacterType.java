package com.server.domain.badge.entity;


public enum CharacterType {
	RABBIT("토끼"),
	BEAR("곰돌이");

	private final String displayName;

	CharacterType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
