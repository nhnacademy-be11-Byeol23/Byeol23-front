package com.nhnacademy.byeol23front.orderset.order.controller;

import com.nhnacademy.byeol23front.bookset.book.dto.BookInfoRequest;
import com.nhnacademy.byeol23front.bookset.book.dto.BookOrderRequest;
import com.nhnacademy.byeol23front.bookset.category.client.CategoryApiClient;
import com.nhnacademy.byeol23front.orderset.delivery.client.DeliveryApiClient;
import com.nhnacademy.byeol23front.orderset.delivery.dto.DeliveryPolicyInfoResponse;
import com.nhnacademy.byeol23front.orderset.packaging.client.PackagingApiClient;
import com.nhnacademy.byeol23front.orderset.packaging.dto.PackagingInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderUtilTest {

	@Mock
	private DeliveryApiClient deliveryApiClient;
	@Mock
	private PackagingApiClient packagingApiClient;

	@Mock
	private CategoryApiClient categoryApiClient;
	@Mock
	private Model model; // Model 객체 자체를 Mocking

	@InjectMocks
	private OrderUtil orderUtil;

	// ArgumentCaptor: Model.addAttribute(String, Object)에 전달되는 인자 캡처
	@Captor
	private ArgumentCaptor<String> stringArgumentCaptor;
	@Captor
	private ArgumentCaptor<Object> objectArgumentCaptor;

	private List<BookInfoRequest> bookList;

	private BookOrderRequest bookOrderRequest;

	@BeforeEach
	void setUp() {
		// 테스트용 BookInfoRequest 리스트 준비
		BookInfoRequest book1 = new BookInfoRequest(1L, "책1", null, true, new BigDecimal("20000"), new BigDecimal("18000"), null, 2, null, null);
		BookInfoRequest book2 = new BookInfoRequest(2L, "책2", null, false, new BigDecimal("10000"), new BigDecimal("10000"), null, 1, null, null);
		bookList = List.of(book1, book2);

		bookOrderRequest = new BookOrderRequest(bookList);

	}

	@Test
	@DisplayName("addDeliveryDatesToModel: 모델에 배송 날짜 목록 추가")
	void testAddDeliveryDatesToModel() {
		// when
		orderUtil.addDeliveryDatesToModel(model);

		// then
		// 2번 호출되었는지 (deliveryDates, defaultDeliveryDate)
		verify(model, times(2)).addAttribute(stringArgumentCaptor.capture(), objectArgumentCaptor.capture());

		List<String> attributeNames = stringArgumentCaptor.getAllValues();
		List<Object> values = objectArgumentCaptor.getAllValues();

		assertThat(attributeNames).containsExactlyInAnyOrder("deliveryDates", "defaultDeliveryDate");

		// "deliveryDates"로 추가된 값이 List 타입인지, 5개인지 확인
		int datesIndex = attributeNames.indexOf("deliveryDates");
		assertThat(values.get(datesIndex)).isInstanceOf(List.class);
		List<?> deliveryDates = (List<?>) values.get(datesIndex);
		assertThat(deliveryDates).hasSize(5);
		assertThat(deliveryDates.get(0)).isInstanceOf(Map.class);
	}

	@Test
	@DisplayName("addDeliveryFeeToModel: 무료 배송 (총액이 40000 이상)")
	void testAddDeliveryFeeToModel_FreeShipping() {
		// given
		BookInfoRequest highPriceBook = new BookInfoRequest(1L, "비싼 책", null, true, new BigDecimal("50000"), new BigDecimal("50000"), null, 1, null, null);
		BookOrderRequest request = new BookOrderRequest(List.of(highPriceBook));

		DeliveryPolicyInfoResponse policy = new DeliveryPolicyInfoResponse( new BigDecimal("3000"), new BigDecimal("40000"), LocalDateTime.now());
		given(deliveryApiClient.getCurrentDeliveryPolicy()).willReturn(ResponseEntity.ok(policy));

		// when
		orderUtil.addDeliveryFeeToModel(model, request);

		// then
		verify(deliveryApiClient, times(1)).getCurrentDeliveryPolicy();
		verify(model, times(1)).addAttribute("totalBookPrice", new BigDecimal("50000"));
		verify(model, times(1)).addAttribute("deliveryFee", BigDecimal.ZERO); // 무료 배송
		verify(model, times(1)).addAttribute("actualOrderPrice", new BigDecimal("50000"));
	}

	@Test
	@DisplayName("addDeliveryFeeToModel: 유료 배송 (총액이 40000 미만)")
	void testAddDeliveryFeeToModel_PaidShipping() {
		// given
		BookInfoRequest lowPriceBook = new BookInfoRequest(1L, "저렴한 책", null, true, new BigDecimal("10000"), new BigDecimal("10000"), null, 1, null, null);
		BookOrderRequest request = new BookOrderRequest(List.of(lowPriceBook));

		DeliveryPolicyInfoResponse policy = new DeliveryPolicyInfoResponse(new BigDecimal("40000"), new BigDecimal("3000"), LocalDateTime.now());
		given(deliveryApiClient.getCurrentDeliveryPolicy()).willReturn(ResponseEntity.ok(policy));

		// when
		orderUtil.addDeliveryFeeToModel(model, request);

		// then
		verify(deliveryApiClient, times(1)).getCurrentDeliveryPolicy();

		verify(model, times(1)).addAttribute("deliveryFee", new BigDecimal("3000")); // 유료 배송
		verify(model, times(1)).addAttribute("totalBookPrice", new BigDecimal("10000"));
		verify(model, times(1)).addAttribute("actualOrderPrice", new BigDecimal("13000")); // 10000 + 3000
	}

	@Test
	@DisplayName("addDeliveryFeeToModel: 배송 정책 API 응답이 null일 경우")
	void testAddDeliveryFeeToModel_PolicyApiReturnsNull() {
		// given
		BookInfoRequest book = new BookInfoRequest(1L, "책", null, true, new BigDecimal("10000"), new BigDecimal("10000"), null, 1, null, null);
		BookOrderRequest request = new BookOrderRequest(List.of(book));

		// API 응답 body가 null인 경우
		given(deliveryApiClient.getCurrentDeliveryPolicy()).willReturn(ResponseEntity.ok(null));

		// when
		orderUtil.addDeliveryFeeToModel(model, request);

		// then
		verify(model, times(1)).addAttribute("totalBookPrice", new BigDecimal("10000"));
		verify(model, times(1)).addAttribute("deliveryFee", BigDecimal.ZERO); // 기본값 0
		verify(model, times(1)).addAttribute("actualOrderPrice", new BigDecimal("10000")); // 10000 + 0
	}

	@Test
	@DisplayName("addOrderSummary: 모델에 orderItem 추가")
	void testAddOrderSummary() {
		// when
		orderUtil.addOrderSummary(model, bookOrderRequest);

		// then
		verify(model, times(1)).addAttribute("bookOrderRequest", bookOrderRequest);
	}

	@Test
	@DisplayName("addTotalQuantity: 모델에 총 수량 추가")
	void testAddTotalQuantity() {
		// when
		orderUtil.addTotalQuantity(model, bookList);

		// then
		// 2 (책1) + 1 (책2) = 3
		verify(model, times(1)).addAttribute("totalQuantity", 3);
	}

	@Test
	@DisplayName("addPackagingOption: 모델에 포장 옵션 추가")
	void testAddPackagingOption() {
		// given
		List<PackagingInfoResponse> mockPackagingList = List.of(
			new PackagingInfoResponse(1L, "포장지A", BigDecimal.TEN, "url")
		);
		given(packagingApiClient.getAllPackagingLists()).willReturn(mockPackagingList);

		// when
		orderUtil.addPackagingOption(model);

		// then
		verify(packagingApiClient, times(1)).getAllPackagingLists();
		verify(model, times(1)).addAttribute("packagingOptions", mockPackagingList);
	}
}