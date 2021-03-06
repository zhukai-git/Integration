package com.zhukai.framework.fast.rest.common;

import java.io.InputStream;

public class FileEntity {

	private String fileName;
	private InputStream inputStream;

	public FileEntity() {
	}

	public FileEntity(String fileName, InputStream inputStream) {
		this.fileName = fileName;
		this.inputStream = inputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
