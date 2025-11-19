package com.nhnacademy.byeol23front.bookset.book.client;

import com.nhnacademy.byeol23front.bookset.book.dto.BookOutboxEventType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "BYEOL23-GATEWAY",
        contextId = "bookDocumentSyncApiClient"
)
public interface BookDocumentSyncApiClient {
    @PostMapping("/api/books/{book-id}/publish")
    void publishBookOutbox(@PathVariable("book-id") Long bookId, @RequestBody BookOutboxEventType eventType);
}
