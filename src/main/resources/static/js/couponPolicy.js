// DOM이 완전히 로드된 후 스크립트가 실행되도록 보장합니다.
document.addEventListener('DOMContentLoaded', function() {

    // 스크립트가 coupon-policy div 내부에서만 작동하도록 범위를 지정합니다.
    const policyDiv = document.getElementById('coupon-policy');

    // 이 스크립트가 로드되는 페이지에 'coupon-policy' div가 없을 수도 있으므로
    // 에러 방지를 위해 null 체크를 합니다.
    if (!policyDiv) {
        // console.log('쿠폰 정책 폼이 없는 페이지입니다.'); // 디버깅용
        return;
    }

    const rateRadio = policyDiv.querySelector('#rateDiscount');
    const fixedRadio = policyDiv.querySelector('#fixedDiscount');
    const rateOptions = policyDiv.querySelector('#rateOptions');
    const fixedOptions = policyDiv.querySelector('#fixedOptions');

    // 요소가 모두 존재하는지 확인
    if (!rateRadio || !fixedRadio || !rateOptions || !fixedOptions) {
        console.error('쿠폰 정책 폼 스크립트: 필요한 요소를 찾을 수 없습니다.');
        return;
    }

    function toggleDiscountOptions() {
        if (rateRadio.checked) {
            rateOptions.style.display = 'block';
            fixedOptions.style.display = 'none';
        } else if (fixedRadio.checked) {
            rateOptions.style.display = 'none';
            fixedOptions.style.display = 'block';
        }
    }

    // 라디오 버튼에 이벤트 리스너 추가
    rateRadio.addEventListener('change', toggleDiscountOptions);
    fixedRadio.addEventListener('change', toggleDiscountOptions);

    // 페이지 로드 시(DOM 로드 완료 시) 초기 상태를 한 번 설정
    toggleDiscountOptions();
});