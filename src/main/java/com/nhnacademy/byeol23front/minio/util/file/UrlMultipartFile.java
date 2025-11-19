package com.nhnacademy.byeol23front.minio.util.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class UrlMultipartFile implements MultipartFile {

	private final byte[] fileContent;
	private final String originalFilename;
	private final String contentType;

	public UrlMultipartFile(byte[] fileContent, String originalFilename, String contentType) {
		this.fileContent = fileContent;
		this.originalFilename = originalFilename;
		this.contentType = contentType;
	}

	@Override
	public String getName() {
		return originalFilename;
	}

	@Override
	public String getOriginalFilename() {
		return originalFilename;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public boolean isEmpty() {
		return fileContent.length == 0;
	}

	@Override
	public long getSize() {
		return fileContent.length;
	}

	@Override
	public byte[] getBytes() {
		return fileContent;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(fileContent);
	}

	@Override
	public void transferTo(File dest) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(dest)) {
			fos.write(fileContent);
		}
	}
}
