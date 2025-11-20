// 기여자 관련 함수들
(function() {
    'use strict';

    // 기여자 선택 모달 열기
    globalThis.openContributorModal = function() {
        const modalElement = document.getElementById('contributorModal');
        if (!modalElement) {
            console.error('기여자 모달을 찾을 수 없습니다.');
            return;
        }

        const contributorIdsInput = document.getElementById('selectedContributorIds');
        let selectedContributorIdsList = [];

        if (contributorIdsInput && contributorIdsInput.value) {
            selectedContributorIdsList = contributorIdsInput.value.split(',').filter(id => id.trim()).map(id => Number.parseInt(id.trim(), 10)).filter(id => !isNaN(id));
        } else {
            if (typeof globalThis.selectedContributorIds !== 'undefined' && globalThis.selectedContributorIds.length > 0) {
                selectedContributorIdsList = globalThis.selectedContributorIds;
            } else if (typeof selectedContributorIds !== 'undefined' && selectedContributorIds.length > 0) {
                selectedContributorIdsList = selectedContributorIds;
            }
        }

        if (selectedContributorIdsList.length > 0) {
            for (const contributorId of selectedContributorIdsList) {
                const contributorCheckbox = document.getElementById(`contributor_${contributorId}`);
                if (contributorCheckbox) {
                    contributorCheckbox.checked = true;
                }
            }
        }

        const modal = new bootstrap.Modal(modalElement);

        modalElement.addEventListener('shown.bs.modal', function() {
            setTimeout(() => {
                if (selectedContributorIdsList.length > 0) {
                    for (const contributorId of selectedContributorIdsList) {
                        const contributorCheckbox = document.getElementById(`contributor_${contributorId}`);
                        if (contributorCheckbox && !contributorCheckbox.checked) {
                            contributorCheckbox.checked = true;
                        }
                    }
                }
            }, 100);
        }, { once: true });

        modal.show();
    };

    // 선택된 기여자 저장
    globalThis.saveSelectedContributors = function() {
        const checkedContributors = document.querySelectorAll('.contributor-checkbox:checked');
        const selectedContributorIds = Array.from(checkedContributors).map(cb => Number.parseInt(cb.value, 10));
        const selectedContributorNames = Array.from(checkedContributors).map(cb => cb.dataset.contributorName);

        const contributorIdsInput = document.getElementById('selectedContributorIds');
        if (contributorIdsInput) {
            contributorIdsInput.value = selectedContributorIds.join(',');
        }

        const displayDiv = document.getElementById('selectedContributorsDisplay');
        if (displayDiv) {
            displayDiv.innerHTML = '';
            for (const name of selectedContributorNames) {
                const badge = document.createElement('span');
                badge.className = 'badge bg-success me-1';
                badge.textContent = name;
                displayDiv.appendChild(badge);
            }
        }
        
        const modalElement = document.getElementById('contributorModal');
        if (modalElement) {
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) {
                modal.hide();
            }
        }
    };
})();

