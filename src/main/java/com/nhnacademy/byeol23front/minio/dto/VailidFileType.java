// src/main/java/com/nhnacademy/byeol23front/minio/dto/VailidFileType.java
package com.nhnacademy.byeol23front.minio.dto;

public enum VailidFileType {
	JPEG("jpeg"),
	JPG("jpg"),
	PNG("png"),
	GIF("gif");

	private final String fileType;

	VailidFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileType() {
		return fileType;
	}

	// 확장자(예: "jpg", "jpeg")로부터 enum을 안전하게 얻는 헬퍼
	public static VailidFileType fromExtension(String ext) {
		if (ext == null || ext.isBlank()) {
			throw new IllegalArgumentException("확장자가 비었습니다.");
		}
		String key = ext.trim().toLowerCase();
		for (VailidFileType t : values()) {
			if (t.fileType.equalsIgnoreCase(key) || t.name().equalsIgnoreCase(key)) {
				return t;
			}
		}
		throw new IllegalArgumentException("지원하지 않는 파일 확장자: " + ext);
	}
}
