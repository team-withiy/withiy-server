package com.server.domain.folder.entity;

public enum FolderType {
	DEFAULT,   // 회원가입 시 자동 생성되는 기본 폴더
	CUSTOM,    // 유저가 직접 생성한 폴더
	VIRTUAL    // 가상 폴더(예: 저장한 모든 장소 폴더)
}