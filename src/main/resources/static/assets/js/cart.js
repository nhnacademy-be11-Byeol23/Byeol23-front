$(document).ready(() => {
  
  // 디바운싱을 위한 타이머
  let updateTimer = null;
  
  // 삭제 버튼
  $('.remove-item').on('click', function(e) {
    e.preventDefault();
    const itemId = $(this).data('item-id');  // 회원: cartBookId, 비회원: bookId
    
    if (confirm('이 상품을 장바구니에서 삭제하시겠습니까?')) {
      // 통합 API 호출 (컨트롤러에서 회원/비회원 자동 분기)
      $.ajax({
        url: `/carts/items/${itemId}`,
        type: 'DELETE',
        success: function() {
          console.log('삭제 성공:', itemId);
          // 페이지 새로고침
          location.reload();
        },
        error: function(xhr, status, error) {
          console.error('삭제 실패:', error);
          alert('상품 삭제에 실패했습니다.');
        }
      });
    }
  });
  
  // 수량 증가 버튼
  $('.quantity-btn.increase').on('click', (e) => {
    e.preventDefault();
    const $input = $(e.currentTarget).closest('.quantity-selector').find('.quantity-input');
    const currentValue = parseInt($input.val());
    const maxValue = parseInt($input.attr('max'));

    if (currentValue <= maxValue) {
      updateCartItem($input);
    }
  });

  // 수량 감소 버튼
  $('.quantity-btn.decrease').on('click', (e) => {
    e.preventDefault();
    const $input = $(e.currentTarget).closest('.quantity-selector').find('.quantity-input');
    const currentValue = parseInt($input.val());
    const minValue = parseInt($input.attr('min'));
    
    if (currentValue >= minValue) {
      updateCartItem($input);
    }
  });

  // 수량 직접 입력
  $('.quantity-input').on('change', (e) => {
    const $input = $(e.currentTarget);
    const minValue = parseInt($input.attr('min'));
    const maxValue = parseInt($input.attr('max'));
    let value = parseInt($input.val());
    
    if (isNaN(value) || value < minValue) {
      value = minValue;
    } else if (value > maxValue) {
      value = maxValue;
    }
    
    $input.val(value);
    updateCartItem($input);
  });

  // 수량 변경 처리
  const updateCartItem = ($quantityInput) => {
    const $cartItem = $quantityInput.closest('.cart-item');
    const itemId = $cartItem.data('item-id');  // 회원: cartBookId, 비회원: bookId
    const quantity = parseInt($quantityInput.val());

    // 먼저 UI를 즉시 업데이트 (사용자 경험 향상)
    const salePriceText = $cartItem.find('.sale-price').text();
    const salePrice = parsePrice(salePriceText);
    const itemTotal = salePrice * quantity;
    
    updateItemTotal($cartItem, itemTotal);
    updateSummary();

    // 이전 타이머가 있으면 취소 (디바운싱)
    if (updateTimer) {
      clearTimeout(updateTimer);
    }

    // 2초 후에 서버로 요청 (사용자가 수량 변경을 멈춘 후 전송)
    updateTimer = setTimeout(() => {
      // 통합 API 호출 (컨트롤러에서 회원/비회원 자동 분기)
      $.ajax({
        url: `/carts/items/${itemId}?quantity=${quantity}`,
        type: 'PUT',
        success: function() {
          console.log('수량 업데이트 성공:', itemId, quantity);
        },
        error: function(xhr, status, error) {
          console.error('수량 업데이트 실패:', error);
          alert('수량 업데이트에 실패했습니다.');
        }
      });
    }, 1000);
  };

  // 개별 항목 합계 업데이트
  const updateItemTotal = ($cartItem, itemTotal) => {
    $cartItem.find('.item-total span').text(formatPrice(itemTotal));
  };

  // 주문 요약 업데이트
  const updateSummary = () => {
    // TODO: Ajax로 서버에서 가져오기 (나중에 구현)

    // 현재는 로컬에서 계산
    let subtotal = 0;
    
    $('.cart-item').each((index, element) => {
      const itemTotalText = $(element).find('.item-total span').text();
      const itemTotal = parsePrice(itemTotalText);
      subtotal += itemTotal;
    });

    // data-delivery-fee 속성에서 배송비 가져오기 (무료면 0, 아니면 실제 배송비)
    const shippingFee = parseInt($('.shipping-item .form-check-label').attr('data-delivery-fee')) || 0;
    const total = subtotal + shippingFee;

    // DOM 업데이트
    $('.summary-item').first().find('.summary-value').text(formatPrice(subtotal));
    $('.summary-total .summary-value').text(formatPrice(total));
  };

  // 가격 문자열을 숫자로 변환 ("5,850원" -> 5850)
  const parsePrice = (priceText) => {
    return parseInt(priceText.replace(/[^0-9]/g, ''));
  };

  // 숫자를 가격 문자열로 변환 (5850 -> "5,850원")
  const formatPrice = (price) => {
    return price.toLocaleString('ko-KR') + '원';
  };

});
