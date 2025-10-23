$(document).ready(() => {
  
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
    const bookId = $cartItem.data('cart-book-id');
    const quantity = parseInt($quantityInput.val());

    // TODO: Ajax로 서버에 전송 (나중에 구현)

    // 현재는 로컬에서 계산
    const salePriceText = $cartItem.find('.sale-price').text();
    const salePrice = parsePrice(salePriceText);
    const itemTotal = salePrice * quantity;

    // 개별 항목 합계 업데이트
    updateItemTotal($cartItem, itemTotal);
    
    // 주문 요약 업데이트
    updateSummary();
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

    const shippingFeeText = $('.shipping-item .form-check-label').text();
    const shippingFee = parsePrice(shippingFeeText);
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
