package com.server.domain.folder.entity;

public enum FolderColor {
	PINK("#EB6DFF"),
	RED("#F87171"),
	ORANGE("#F4B37D"),
	YELLOW("#FFEA41"),
	GREEN("#7CF982"),
	SKY_BLUE("#38BDF8"),
	BLUE("#3B82F6"),
	GRAY("#E8E8E8");

	private final String hexCode;

	FolderColor(String hexCode) {
		this.hexCode = hexCode;
	}

	public String getHexCode() {
		return hexCode;
	}
}
