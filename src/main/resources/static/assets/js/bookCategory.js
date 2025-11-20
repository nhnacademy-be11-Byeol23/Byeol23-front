// 카테고리 관련 함수들
(function() {
    'use strict';

    // 카테고리 트리 토글
    globalThis.toggleCategory = function(element) {
        const categoryId = element.dataset.id;
        const isExpanded = element.dataset.expanded === 'true';
        const childrenUl = document.getElementById(`children-${categoryId}`);
        const icon = element.querySelector('i');

        if (!childrenUl) return;

        if (isExpanded) {
            childrenUl.style.display = 'none';
            icon.classList.remove('bi-chevron-down');
            icon.classList.add('bi-chevron-right');
            element.dataset.expanded = 'false';
        } else {
            if (childrenUl.dataset.loaded !== 'true') {
                globalThis.loadCategoryChildren(categoryId, childrenUl);
            } else {
                childrenUl.style.display = 'block';
            }
            icon.classList.remove('bi-chevron-right');
            icon.classList.add('bi-chevron-down');
            element.dataset.expanded = 'true';
        }
    };

    // 자식 카테고리 로드
    window.loadCategoryChildren = function(parentId, parentUl) {
        const bookForm = document.getElementById('bookForm');
        let selectedCategoryIds = [];
        
        if (typeof globalThis.selectedCategoryIds !== 'undefined' && globalThis.selectedCategoryIds.length > 0) {
            selectedCategoryIds = globalThis.selectedCategoryIds;
        } else if (bookForm) {
            const checkedBoxes = bookForm.querySelectorAll('input[name="categoryIds"]:checked');
            selectedCategoryIds = Array.from(checkedBoxes).map(cb => Number.parseInt(cb.value, 10));
        }
        
        fetch(`/categories/${parentId}/children`)
            .then(response => response.json())
            .then(children => {
                parentUl.innerHTML = '';
                children.forEach(child => {
                    const hasChildren = child.hasChildren;
                    const isChecked = selectedCategoryIds.includes(child.id);
                    
                    const li = document.createElement('li');
                    li.innerHTML = `
                        <div class="d-flex align-items-center mb-2">
                            ${hasChildren ? 
                                `<span class="category-toggle me-2" 
                                       style="cursor: pointer; user-select: none;"
                                       data-id="${child.id}" 
                                       data-expanded="true"
                                       onclick="toggleCategory(this)">
                                    <i class="bi bi-chevron-down"></i>
                                </span>` : 
                                '<span style="width: 20px; display: inline-block;"></span>'
                            }
                            <div class="form-check">
                                <input class="form-check-input" 
                                       type="checkbox" 
                                       id="category_${child.id}" 
                                       name="categoryIds" 
                                       value="${child.id}"
                                       ${hasChildren ? 'disabled' : ''}
                                       ${isChecked ? 'checked' : ''}>
                                <label class="form-check-label" 
                                       for="category_${child.id}" 
                                       style="${hasChildren ? 'font-weight: 600; color: #666;' : ''}">
                                    ${child.name}
                                </label>
                            </div>
                        </div>
                        ${hasChildren ? 
                            `<ul id="children-${child.id}" 
                                 class="ms-4" 
                                 style="list-style: none; padding-left: 0; display: block;">
                            </ul>` : ''
                        }
                    `;
                    parentUl.appendChild(li);
                    
                    if (hasChildren) {
                        const childUl = document.getElementById(`children-${child.id}`);
                        const bookIdInput = document.getElementById('bookId');
                        if (bookIdInput && bookIdInput.value) {
                            childUl.style.display = 'block';
                            globalThis.loadCategoryChildren(child.id, childUl);
                        }
                    }
                });
                parentUl.dataset.loaded = 'true';
                parentUl.style.display = 'block';
            })
            .catch(error => {
                console.error('카테고리 로드 실패:', error);
                alert('카테고리를 불러오는데 실패했습니다.');
            });
    };

    // 모달용 카테고리 로드
    globalThis.loadCategoryChildrenInModal = function(parentId, parentUl) {
        let selectedCategoryIds = [];
        
        if (typeof globalThis.selectedCategoryIds !== 'undefined' && globalThis.selectedCategoryIds.length > 0) {
            selectedCategoryIds = globalThis.selectedCategoryIds;
        } else if (typeof selectedCategoryIds !== 'undefined' && selectedCategoryIds.length > 0) {
            selectedCategoryIds = selectedCategoryIds;
        } else {
            const categoryIdsInput = document.getElementById('selectedCategoryIds');
            if (categoryIdsInput && categoryIdsInput.value) {
                selectedCategoryIds = categoryIdsInput.value.split(',').filter(id => id.trim()).map(id => Number.parseInt(id.trim(), 10));
            }
        }
        
        fetch(`/categories/${parentId}/children`)
            .then(response => response.json())
            .then(children => {
                parentUl.innerHTML = '';
                children.forEach(child => {
                    const hasChildren = child.hasChildren;
                    const isChecked = selectedCategoryIds.includes(child.id);
                    
                    const li = document.createElement('li');
                    li.innerHTML = `
                        <div class="d-flex align-items-center mb-2">
                            ${hasChildren ? 
                                `<span class="category-toggle me-2" 
                                       style="cursor: pointer; user-select: none;"
                                       data-id="${child.id}" 
                                       data-expanded="true"
                                       onclick="toggleCategoryInModal(this)">
                                    <i class="bi bi-chevron-down"></i>
                                </span>` : 
                                '<span style="width: 20px; display: inline-block;"></span>'
                            }
                            <div class="form-check">
                                <input class="form-check-input category-checkbox" 
                                       type="checkbox" 
                                       id="category_modal_${child.id}" 
                                       value="${child.id}"
                                       data-category-name="${child.name}"
                                       ${hasChildren ? 'disabled' : ''}
                                       ${isChecked ? 'checked' : ''}>
                                <label class="form-check-label" 
                                       for="category_modal_${child.id}" 
                                       style="${hasChildren ? 'font-weight: 600; color: #666;' : ''}">
                                    ${child.name}
                                </label>
                            </div>
                        </div>
                        ${hasChildren ? 
                            `<ul id="children-modal-${child.id}" 
                                 class="ms-4" 
                                 style="list-style: none; padding-left: 0; display: block;">
                            </ul>` : ''
                        }
                    `;
                    parentUl.appendChild(li);
                    
                    if (hasChildren) {
                        const childUl = document.getElementById(`children-modal-${child.id}`);
                        globalThis.loadCategoryChildrenInModal(child.id, childUl);
                    }
                });
                parentUl.dataset.loaded = 'true';
                parentUl.style.display = 'block';
            })
            .catch(error => {
                console.error('카테고리 로드 실패:', error);
            });
    };

    // 모달 내부 카테고리 토글
    globalThis.toggleCategoryInModal = function(element) {
        const categoryId = element.dataset.id;
        const isExpanded = element.dataset.expanded === 'true';
        const childrenUl = document.getElementById(`children-modal-${categoryId}`);
        const icon = element.querySelector('i');
        
        if (!childrenUl) return;
        
        if (isExpanded) {
            childrenUl.style.display = 'none';
            icon.classList.remove('bi-chevron-down');
            icon.classList.add('bi-chevron-right');
            element.dataset.expanded = 'false';
        } else {
            if (childrenUl.dataset.loaded !== 'true') {
                globalThis.loadCategoryChildrenInModal(categoryId, childrenUl);
            } else {
                childrenUl.style.display = 'block';
            }
            icon.classList.remove('bi-chevron-right');
            icon.classList.add('bi-chevron-down');
            element.dataset.expanded = 'true';
        }
    };

    // 카테고리 선택 모달 열기
    globalThis.openCategoryModal = function() {
        const modalElement = document.getElementById('categoryModal');
        if (!modalElement) {
            console.error('카테고리 모달을 찾을 수 없습니다.');
            return;
        }

        const categoryIdsInput = document.getElementById('selectedCategoryIds');
        let selectedCategoryIdsList = [];
        
        if (categoryIdsInput && categoryIdsInput.value) {
            selectedCategoryIdsList = categoryIdsInput.value.split(',').filter(id => id.trim()).map(id => Number.parseInt(id.trim(), 10));
        } else {
            if (typeof globalThis.selectedCategoryIds !== 'undefined' && globalThis.selectedCategoryIds.length > 0) {
                selectedCategoryIdsList = globalThis.selectedCategoryIds;
            } else if (typeof selectedCategoryIds !== 'undefined' && selectedCategoryIds.length > 0) {
                selectedCategoryIdsList = selectedCategoryIds;
            }
        }
        
        modalElement.addEventListener('shown.bs.modal', function() {
            const categoryTree = document.getElementById('category-modal-tree');
            if (categoryTree) {
                const rootCategories = categoryTree.querySelectorAll('li > div > .category-toggle');
                rootCategories.forEach(toggle => {
                    const categoryId = toggle.dataset.id;
                    const childrenUl = document.getElementById(`children-modal-${categoryId}`);
                    if (childrenUl && childrenUl.dataset.loaded !== 'true') {
                        globalThis.loadCategoryChildrenInModal(categoryId, childrenUl);
                    }
                });

                setTimeout(() => {
                    if (selectedCategoryIdsList.length > 0) {
                        selectedCategoryIdsList.forEach(categoryId => {
                            const checkbox = document.getElementById(`category_modal_${categoryId}`);
                            if (checkbox && !checkbox.disabled) {
                                checkbox.checked = true;
                            }
                        });
                    }
                }, 500);
            }
        }, { once: true });

        const modal = new bootstrap.Modal(modalElement);
        modal.show();
    };

    // 선택된 카테고리 저장
    globalThis.saveSelectedCategories = function() {
        const checkedCategories = document.querySelectorAll('#categoryModal .category-checkbox:checked:not(:disabled)');
        const selectedCategoryIds = Array.from(checkedCategories).map(cb => Number.parseInt(cb.value, 10));
        const selectedCategoryNames = Array.from(checkedCategories).map(cb => cb.dataset.categoryName);

        const categoryIdsInput = document.getElementById('selectedCategoryIds');
        if (categoryIdsInput) {
            categoryIdsInput.value = selectedCategoryIds.join(',');
        }

        const displayDiv = document.getElementById('selectedCategoriesDisplay');
        if (displayDiv) {
            displayDiv.innerHTML = '';
            selectedCategoryNames.forEach((name) => {
                const badge = document.createElement('span');
                badge.className = 'badge bg-info me-1';
                badge.textContent = name;
                displayDiv.appendChild(badge);
            });
        }

        const modalElement = document.getElementById('categoryModal');
        if (modalElement) {
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) {
                modal.hide();
            }
        }
    };
})();

