package com.nhnacademy.byeol23front.minio.util.file;

import java.net.URL;

import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

	public static MultipartFile fromUrl(String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			String fileName = extractName(url.getPath());
			String contentType = guessContentType(fileName);

			byte[] bytes = url.openStream().readAllBytes();

			return new UrlMultipartFile(bytes, fileName, contentType);
		} catch (Exception e) {
			throw new RuntimeException("URL → Multipart 변환 실패", e);
		}
	}

	private static String extractName(String path) {
		return path.substring(path.lastIndexOf("/") + 1);
	}

	private static String guessContentType(String fileName) {
		if (fileName.endsWith(".png")) return "image/png";
		if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
		if (fileName.endsWith(".gif")) return "image/gif";
		return "application/octet-stream";
	}
}
