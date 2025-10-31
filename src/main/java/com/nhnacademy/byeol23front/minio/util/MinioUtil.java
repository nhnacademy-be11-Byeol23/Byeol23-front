package com.nhnacademy.byeol23front.minio.util;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioUtil {

	private String host;
	private int port;
	private String accessKey;
	private String secretKey;
	private String bucketName;

	// 재사용을 위한 단일 인스턴스
	private volatile S3Client s3Client;

	@PostConstruct
	void initClient() {
		// 바인딩된 프로퍼티로 초기 1회 생성
		this.s3Client = buildClient();
	}

	@PreDestroy
	void shutdownClient() {
		S3Client c = this.s3Client;
		if (c != null) {
			c.close();
		}
	}

	// 필요 시 자격 증명/엔드포인트 변경 후 재생성
	public synchronized void refreshS3Client() {
		if (this.s3Client != null) {
			this.s3Client.close();
		}
		this.s3Client = buildClient();
	}

	// 호출 시 항상 유효한 클라이언트를 반환(지연 로딩 + DCL)
	private S3Client getS3Client() {
		S3Client c = this.s3Client;
		if (c == null) {
			synchronized (this) {
				if (this.s3Client == null) {
					this.s3Client = buildClient();
				}
				c = this.s3Client;
			}
		}
		return c;
	}

	private S3Client buildClient() {
		return S3Client.builder()
			.endpointOverride(URI.create("http://" + host + ":" + port))
			.region(Region.US_EAST_1)
			.credentialsProvider(
				StaticCredentialsProvider.create(
					AwsBasicCredentials.create(accessKey, secretKey)
				)
			)
			.forcePathStyle(true)
			.build();
	}



	public String getPresignedUrl(ImageType type, long id) {
		try {
			MinioClient minioClient = MinioClient.builder()
				.endpoint(host, port, false)
				.credentials(accessKey, secretKey)
				.build();

			GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
				.method(Method.GET)
				.bucket(bucketName)
				.object(generateObjectKey(type, id))
				.expiry(1, TimeUnit.HOURS)
				.build();

			return minioClient.getPresignedObjectUrl(args);
		} catch (MinioException e) {
			log.error("MinIO 오류 발생: {}", e.getMessage());
			log.error("HTTP 추적: {}", e.httpTrace());
			throw new RuntimeException("MinIO 오류 발생", e);
		} catch (Exception e) {
			log.error("일반 오류 발생: {}", e.getMessage());
			throw new RuntimeException("일반 오류 발생", e);
		}
	}

	public List<String> listUrls(ImageType type, long id) {
		List<String> urls = null;
		String prefix = String.format("%s/%d", type.name(), id);
		try{
			urls = getS3Client().listObjectsV2Paginator(req -> req.bucket(bucketName).prefix(prefix))
				.contents()
				.stream()
				.map(S3Object::key)
				.map(this::fileName2Url)
				.toList();
		} catch (Exception e){
			log.error("파일 목록 조회 오류 발생: {}", e.getMessage());
			throw new RuntimeException("파일 목록 조회 오류 발생", e);
		}
		return urls;
	}

	private String fileName2Url(String fileName){
		return "http://" + host + ":" + port + "/" + bucketName + "/" + fileName;
	}

	public String putObject(ImageType type, long id, MultipartFile file) {
		try{
			S3Client s3 = getS3Client();
			String fileName = generateObjectKey(type, id);
			s3.putObject(req -> req.bucket(bucketName).key(fileName), RequestBody.fromBytes(file.getBytes()));
			return fileName2Url(fileName);
		} catch (Exception e){
			log.error("파일 업로드 오류 발생: {}", e.getMessage());
			throw new RuntimeException("파일 업로드 오류 발생", e);
		}
	}

	public void deleteObject(ImageType type, long id) {
		try{
			S3Client s3 = getS3Client();
			s3.deleteObject(req -> req.bucket(bucketName).key(generateObjectKey(type, id)));
		} catch (Exception e){
			log.error("파일 삭제 오류 발생: {}", e.getMessage());
			throw new RuntimeException("파일 삭제 오류 발생", e);
		}
	}

	private String generateObjectKey(ImageType type, long id) {
		return String.format("%s/%d.jpg", type.name(), id);
	}
}
