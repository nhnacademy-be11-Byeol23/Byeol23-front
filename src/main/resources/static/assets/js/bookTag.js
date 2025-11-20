// 태그 관련 함수들
(function() {
    'use strict';

    // 태그 선택 모달 열기
    globalThis.openTagModal = function() {
        const modalElement = document.getElementById('tagModal');
        if (!modalElement) {
            console.error('태그 모달을 찾을 수 없습니다.');
            return;
        }

        const tagIdsInput = document.getElementById('selectedTagIds');
        let selectedTagIdsList = [];
        
        if (tagIdsInput && tagIdsInput.value) {
            selectedTagIdsList = tagIdsInput.value.split(',').filter(id => id.trim()).map(id => Number.parseInt(id.trim(), 10)).filter(id => !isNaN(id));
        } else {
            if (typeof globalThis.selectedTagIds !== 'undefined' && globalThis.selectedTagIds.length > 0) {
                selectedTagIdsList = globalThis.selectedTagIds;
            } else if (typeof selectedTagIds !== 'undefined' && selectedTagIds.length > 0) {
                selectedTagIdsList = selectedTagIds;
            }
        }
        
        if (selectedTagIdsList.length > 0) {
            selectedTagIdsList.forEach(tagId => {
                const tagCheckbox = document.getElementById(`tag_${tagId}`);
                if (tagCheckbox) {
                    tagCheckbox.checked = true;
                }
            });
        }

        const modal = new bootstrap.Modal(modalElement);

        modalElement.addEventListener('shown.bs.modal', function() {
            setTimeout(() => {
                if (selectedTagIdsList.length > 0) {
                    selectedTagIdsList.forEach(tagId => {
                        const tagCheckbox = document.getElementById(`tag_${tagId}`);
                        if (tagCheckbox && !tagCheckbox.checked) {
                            tagCheckbox.checked = true;
                        }
                    });
                }
            }, 100);
        }, { once: true });

        modal.show();
    };

    // 선택된 태그 저장
    globalThis.saveSelectedTags = function() {
        const checkedTags = document.querySelectorAll('.tag-checkbox:checked');
        const selectedTagIds = Array.from(checkedTags).map(cb => Number.parseInt(cb.value, 10));
        const selectedTagNames = Array.from(checkedTags).map(cb => cb.dataset.tagName);

        const tagIdsInput = document.getElementById('selectedTagIds');
        if (tagIdsInput) {
            tagIdsInput.value = selectedTagIds.join(',');
        }

        const displayDiv = document.getElementById('selectedTagsDisplay');
        if (displayDiv) {
            displayDiv.innerHTML = '';
            selectedTagNames.forEach((name) => {
                const badge = document.createElement('span');
                badge.className = 'badge bg-primary me-1';
                badge.textContent = name;
                displayDiv.appendChild(badge);
            });
        }

        const modalElement = document.getElementById('tagModal');
        if (modalElement) {
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) {
                modal.hide();
            }
        }
    };
})();

