package com.nhnacademy.byeol23front.cartset.cart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.byeol23front.bookset.book.client.BookApiClient;
import com.nhnacademy.byeol23front.bookset.book.dto.BookResponse;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartBookResponse;
import com.nhnacademy.byeol23front.cartset.cart.dto.CartResponse;
import com.nhnacademy.byeol23front.cartset.cart.exception.GuestCartSerializationException;
import com.nhnacademy.byeol23front.minio.dto.back.GetUrlResponse;
import com.nhnacademy.byeol23front.minio.service.MinioService;
import com.nhnacademy.byeol23front.minio.util.ImageDomain;
import com.nhnacademy.byeol23front.orderset.delivery.client.DeliveryApiClient;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GuestCartService {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final BookApiClient bookApiClient;
    private final DeliveryApiClient deliveryApiClient;
    private final MinioService minioService;

    private static final String CART_KEY_PREFIX = "cart:guest:";
    private static final Duration CART_EXPIRATION = Duration.ofDays(30);

    public void addCartItem(String guestId, Long bookId, int quantity) {
        String key = generateCartKey(guestId);

        Map<Long, Integer> cart = getCart(guestId);

        cart.merge(bookId, quantity, Integer::sum);

        saveCart(key, cart);
    }

    public Map<Long, Integer> getCart(String guestId) {
        String key = generateCartKey(guestId);
        String cartJson = stringRedisTemplate.opsForValue().get(key);

        if (cartJson == null) return new HashMap<>();

        try {
            return objectMapper.readValue(cartJson, new TypeReference<Map<Long, Integer>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public void deleteCartItem(String guestId, Long bookId) {
        String key = generateCartKey(guestId);
        Map<Long, Integer> cart = getCart(guestId);
        cart.remove(bookId);

        // 장바구니가 전부 비워졌을 경우에는 redis키 자체를 삭제
        if (cart.isEmpty()) {
            stringRedisTemplate.delete(key);
        } else {
            saveCart(key, cart);
        }
    }

    public void updateCartItemQuantity(String guestId, Long bookId, int quantity) {
        String key = generateCartKey(guestId);
        Map<Long, Integer> cart = getCart(guestId);

        cart.put(bookId, quantity);

        saveCart(key, cart);
    }

    // redis에 장바구니 저장
    private void saveCart(String key, Map<Long, Integer> cart) {
        try {
            stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(cart), CART_EXPIRATION);
        } catch (JsonProcessingException e) {
            throw new GuestCartSerializationException("비회원 장바구니 직렬화 중 오류 발생", e);
        }
    }

    // 책 정보를 포함한 장바구니 조회 (뷰 렌더링용)
    public CartResponse getGuestCartWithBooks(String guestId) {
        Map<Long, Integer> cart = getCart(guestId);
        
        // 장바구니가 비어있으면 빈 응답 반환
        if (cart.isEmpty()) {
            return new CartResponse(null, new ArrayList<>(), null, null);
        }
        
        // Redis에서 가져온 bookId 목록으로 책 정보 조회
        List<Long> bookIds = new ArrayList<>(cart.keySet());
        List<BookResponse> books = bookApiClient.getBooksByIds(bookIds);
        
        // BookResponse를 CartBookResponse로 변환하면서 수량 정보 추가
        List<CartBookResponse> cartBooks = new ArrayList<>();
        for (BookResponse book : books) {
            Integer quantity = cart.get(book.bookId());
            
            // 이미지 URL 첫 번째 이미지 사용
            String imageUrl = null;
            try {
                List<GetUrlResponse> imageUrls = minioService.getImageUrl(ImageDomain.BOOK, book.bookId());
                if (!imageUrls.isEmpty()) {
                    imageUrl = imageUrls.get(0).imageUrl();
                }
            } catch (Exception e) {
            }
            
            CartBookResponse cartBook = new CartBookResponse(
                -1l, // 비회원
                book.bookId(),
                book.bookName(),
                imageUrl,
                book.isPack(),
                book.regularPrice(),
                book.salePrice(),
                book.publisher(),
                quantity != null ? quantity : 0,
                book.contributors(),
                null
            );
            cartBooks.add(cartBook);
        }
        
        // 배송비 정책 조회
        ResponseEntity<DeliveryPolicyInfoResponse> response = deliveryApiClient.getCurrentDeliveryPolicy();
        DeliveryPolicyInfoResponse deliveryPolicy = response.getBody();
        
        return new CartResponse(
            -1l,  // 비회원은
            cartBooks,
            deliveryPolicy != null ? deliveryPolicy.deliveryFee() : null,
            deliveryPolicy != null ? deliveryPolicy.freeDeliveryCondition() : null
        );
    }

    // 장바구니 키 생성
    private String generateCartKey(String guestId) {
        return CART_KEY_PREFIX + guestId;
    }
}
