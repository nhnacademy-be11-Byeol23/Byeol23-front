/**
 * Grade Modal JavaScript
 * 커스텀 등급 정보 모달 관리
 */

(function() {
    'use strict';
    
    let savedScrollPosition = 0;
    let escapeKeyHandler = null;
    
    /**
     * 등급 모달 열기
     */
    window.openGradeModal = function() {
        const modal = document.getElementById('gradeModal');
        if (!modal) {
            console.warn('등급 모달을 찾을 수 없습니다.');
            return;
        }
        
        // 현재 스크롤 위치 저장
        savedScrollPosition = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
        
        // body 스크롤 막기
        document.body.style.top = `-${savedScrollPosition}px`;
        document.body.classList.add('custom-modal-open');
        
        // 모달 표시
        modal.classList.add('show');
        
        // ESC 키로 닫기 핸들러 등록
        escapeKeyHandler = function(event) {
            if (event.key === 'Escape' || event.keyCode === 27) {
                closeGradeModal();
            }
        };
        document.addEventListener('keydown', escapeKeyHandler);
        
        // 모달이 표시된 후 포커스 관리
        setTimeout(function() {
            const closeButton = modal.querySelector('.custom-modal-close');
            if (closeButton) {
                closeButton.focus();
            }
        }, 100);
    };
    
    /**
     * 등급 모달 닫기
     */
    window.closeGradeModal = function() {
        const modal = document.getElementById('gradeModal');
        if (!modal) {
            return;
        }
        
        // 모달 숨기기
        modal.classList.remove('show');
        
        // body 스크롤 복원
        document.body.classList.remove('custom-modal-open');
        document.body.style.top = '';
        
        // 스크롤 위치 복원
        window.scrollTo(0, savedScrollPosition);
        savedScrollPosition = 0;
        
        // ESC 키 리스너 제거
        if (escapeKeyHandler) {
            document.removeEventListener('keydown', escapeKeyHandler);
            escapeKeyHandler = null;
        }
    };
    
    // DOM이 로드된 후 초기화
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            // 모달 배경 클릭 시 닫기 (이미 HTML에서 onclick으로 처리됨)
            const modal = document.getElementById('gradeModal');
            if (modal) {
                const backdrop = modal.querySelector('.custom-modal-backdrop');
                if (backdrop && !backdrop.onclick) {
                    backdrop.addEventListener('click', closeGradeModal);
                }
            }
        });
    } else {
        // 이미 로드된 경우
        const modal = document.getElementById('gradeModal');
        if (modal) {
            const backdrop = modal.querySelector('.custom-modal-backdrop');
            if (backdrop && !backdrop.onclick) {
                backdrop.addEventListener('click', closeGradeModal);
            }
        }
    }
})();

