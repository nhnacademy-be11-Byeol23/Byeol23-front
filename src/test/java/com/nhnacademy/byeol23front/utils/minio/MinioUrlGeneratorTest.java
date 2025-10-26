package com.nhnacademy.byeol23front.utils.minio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MinioUrlGeneratorTest {

    @Autowired
	MinioUtil generator;

    @Test
    @DisplayName("minio.* properties가 @ConfigurationProperties로 정상 바인딩된다")
    void configurationPropertiesBinding() {
        assertNotNull(generator, "MinioUrlGenerator 빈 주입 실패");
        assertEquals("storage.java21.net", generator.getHost());
        assertEquals(8000, generator.getPort());
        assertEquals("V2CnisSrBMa0w2QtE8cP", generator.getAccessKey());
        assertEquals("Bfyx9qcRRI2J7LHF5r5ZoFFWIFSZ40d4K9FEpDnh", generator.getSecretKey());
        assertEquals("starbooklimg", generator.getBucketName());
    }

    @Test
    @DisplayName("sample.jpg에 대한 presigned URL이 발급된다(다운로드는 하지 않음)")
    void presignedUrlIssued() {
        String url = generator.getPresignedUrl("sample.jpg");
        assertNotNull(url, "발급된 URL이 null이면 안 됨");
        assertFalse(url.isBlank(), "발급된 URL이 비어있으면 안 됨");
        assertTrue(url.startsWith("http://storage.java21.net:8000"), "엔드포인트가 포함되어야 함: " + url);
        assertTrue(url.contains("sample.jpg"), "객체명이 URL에 포함되어야 함: " + url);
        assertTrue(url.contains("X-Amz-Signature") || url.contains("X-Amz-Credential"), "서명 쿼리 파라미터가 포함되어야 함: " + url);
    }
}