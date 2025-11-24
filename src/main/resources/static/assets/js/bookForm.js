// 도서 폼 제출 및 초기화 관련 함수들
(function() {
    'use strict';

    let editor = null;
    let tocEditor = null;

    // Toast UI Editor 초기화
    function initEditors() {
        const editorElement = document.querySelector('#descriptionEditor');
        if (editorElement && typeof toastui !== 'undefined' && toastui.Editor) {
            const initialContent = document.getElementById('descriptionContent')?.value;
            editor = new toastui.Editor({
                el: editorElement,
                height: '300px',
                initialEditType: 'wysiwyg',
                previewStyle: 'vertical',
                initialValue: initialContent || ''
            });
            window.editor = editor;
        }

        const tocEditorElement = document.querySelector('#tocEditor');
        if (tocEditorElement && typeof toastui !== 'undefined' && toastui.Editor) {
            const initialTocContent = document.getElementById('tocContent')?.value;
            tocEditor = new toastui.Editor({
                el: tocEditorElement,
                height: '300px',
                initialEditType: 'wysiwyg',
                previewStyle: 'vertical',
                initialValue: initialTocContent || ''
            });
            window.tocEditor = tocEditor;
        }
    }

    // 에디터 내용을 hidden input에 저장
    function saveEditorContent() {
        if (editor) {
            document.getElementById('description').value =
                editor.getHTML()
                    .replaceAll(/<\/p>/gi, '\n')
                    .replaceAll(/<br\s*\/?>/gi, '\n')
                    .replaceAll(/<[^>]*>/g, '')
                    .trim();
        }

        if (tocEditor) {
            document.getElementById('toc').value =
                tocEditor.getHTML()
                    .replaceAll(/<\/p>/gi, '\n')
                    .replaceAll(/<br\s*\/?>/gi, '\n')
                    .replaceAll(/<[^>]*>/g, '')
                    .trim();
        }
    }

    // 폼 제출 처리
    function handleFormSubmit(event) {
        event.preventDefault();
        
        const bookIdInput = document.getElementById('bookId');
        const isUpdateForm = bookIdInput && bookIdInput.value;
        
        saveEditorContent();

        const formData = new FormData(event.target);
        const url = event.target.getAttribute('action') || '/admin/books/new';

        fetch(url, {
            method: 'POST',
            body: formData,
            redirect: 'manual'
        })
            .then(response => {
                if (response.status === 302 || response.status === 0) {
                    const location = response.headers.get('Location');
                    if (location && location.includes('/admin/books') && 
                        !location.includes('/admin/books/new') && 
                        !location.includes('/admin/books/update')) {
                        alert(isUpdateForm ? '도서가 성공적으로 수정되었습니다.' : '도서가 성공적으로 생성되었습니다.');
                        window.location.href = location;
                    } else if (location && (location.includes('/admin/books/new') || location.includes('/admin/books/update'))) {
                        alert(isUpdateForm ? '도서 수정에 실패했습니다. 페이지를 새로고침하여 오류 메시지를 확인하세요.' : '도서 생성에 실패했습니다. 페이지를 새로고침하여 오류 메시지를 확인하세요.');
                        window.location.href = location;
                    } else {
                        window.location.href = location || '/admin/books';
                    }
                } else if (response.ok) {
                    alert(isUpdateForm ? '도서가 성공적으로 수정되었습니다.' : '도서가 성공적으로 생성되었습니다.');
                    window.location.href = '/admin/books';
                } else {
                    response.text().then(text => {
                        console.error('도서 처리 실패:', text);
                        alert(isUpdateForm ? '도서 수정에 실패했습니다. 페이지를 새로고침하여 오류 메시지를 확인하세요.' : '도서 생성에 실패했습니다. 페이지를 새로고침하여 오류 메시지를 확인하세요.');
                        window.location.href = isUpdateForm ? url : '/admin/books/new';
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('오류가 발생했습니다: ' + error.message);
            });
    }

    // 이미지 미리보기
    function initImagePreview() {
        const bookImagesInput = document.getElementById('bookImages');
        if (bookImagesInput) {
            bookImagesInput.addEventListener('change', function(e) {
                const previewDiv = document.getElementById('imagePreviewList');
                if (previewDiv) {
                    previewDiv.innerHTML = '';
                    const files = Array.from(e.target.files);

                    if (files.length > 0) {
                        files.forEach((file) => {
                            if (file.type.startsWith('image/')) {
                                const reader = new FileReader();
                                reader.onload = function(event) {
                                    const img = document.createElement('img');
                                    img.src = event.target.result;
                                    img.className = 'img-thumbnail';
                                    img.style.width = '100px';
                                    img.style.height = '100px';
                                    img.style.objectFit = 'cover';
                                    img.title = file.name;
                                    previewDiv.appendChild(img);
                                };
                                reader.readAsDataURL(file);
                            }
                        });

                        const fileCount = document.createElement('small');
                        fileCount.className = 'text-muted d-block mt-2';
                        fileCount.textContent = `선택된 이미지: ${files.length}개`;
                        previewDiv.appendChild(fileCount);
                    }
                }
            });
        }
    }

    // 수정 폼 초기화 (기존 선택된 값들 표시)
    function initUpdateForm() {
        const bookIdInput = document.getElementById('bookId');
        if (!bookIdInput || !bookIdInput.value) return;

        // 카테고리
        let selectedCategoryIdsList = [];
        let selectedCategoriesList = [];

        if (typeof globalThis.selectedCategoryIds !== 'undefined' && globalThis.selectedCategoryIds.length > 0) {
            selectedCategoryIdsList = globalThis.selectedCategoryIds;
        } else if (typeof selectedCategoryIds !== 'undefined' && selectedCategoryIds.length > 0) {
            selectedCategoryIdsList = selectedCategoryIds;
        }

        if (typeof globalThis.selectedCategories !== 'undefined' && globalThis.selectedCategories.length > 0) {
            selectedCategoriesList = globalThis.selectedCategories;
        } else if (typeof selectedCategories !== 'undefined' && selectedCategories.length > 0) {
            selectedCategoriesList = selectedCategories;
        }

        const categoryIdsInput = document.getElementById('selectedCategoryIds');
        if (categoryIdsInput && selectedCategoryIdsList.length > 0) {
            categoryIdsInput.value = selectedCategoryIdsList.join(',');
        }

        const categoryDisplayDiv = document.getElementById('selectedCategoriesDisplay');
        if (categoryDisplayDiv && selectedCategoriesList.length > 0) {
            categoryDisplayDiv.innerHTML = '';
            selectedCategoriesList.forEach(category => {
                const badge = document.createElement('span');
                badge.className = 'badge bg-info me-1 mb-1';
                badge.textContent = category.categoryName || category.name || `카테고리 ${category.id}`;
                categoryDisplayDiv.appendChild(badge);
            });
        }

        // 태그
        let selectedTagIdsList = [];
        let selectedTagsList = [];

        if (typeof globalThis.selectedTagIds !== 'undefined' && globalThis.selectedTagIds.length > 0) {
            selectedTagIdsList = globalThis.selectedTagIds;
        } else if (typeof selectedTagIds !== 'undefined' && selectedTagIds.length > 0) {
            selectedTagIdsList = selectedTagIds;
        }

        if (typeof globalThis.selectedTags !== 'undefined' && globalThis.selectedTags.length > 0) {
            selectedTagsList = globalThis.selectedTags;
        } else if (typeof selectedTags !== 'undefined' && selectedTags.length > 0) {
            selectedTagsList = selectedTags;
        }

        const tagIdsInput = document.getElementById('selectedTagIds');
        if (tagIdsInput && selectedTagIdsList.length > 0) {
            tagIdsInput.value = selectedTagIdsList.join(',');
        }

        const tagDisplayDiv = document.getElementById('selectedTagsDisplay');
        if (tagDisplayDiv && selectedTagsList.length > 0) {
            tagDisplayDiv.innerHTML = '';
            selectedTagsList.forEach(tag => {
                const badge = document.createElement('span');
                badge.className = 'badge bg-primary me-1 mb-1';
                badge.textContent = tag.tagName || tag.name || `태그 ${tag.tagId || tag.id}`;
                tagDisplayDiv.appendChild(badge);
            });
        }

        if (selectedTagIdsList.length > 0) {
            setTimeout(() => {
                selectedTagIdsList.forEach(tagId => {
                    const tagCheckbox = document.getElementById(`tag_${tagId}`);
                    if (tagCheckbox) {
                        tagCheckbox.checked = true;
                    }
                });
            }, 200);
        }

        // 기여자
        let selectedContributorIdsList = [];
        let selectedContributorsList = [];

        if (typeof globalThis.selectedContributorIds !== 'undefined' && globalThis.selectedContributorIds.length > 0) {
            selectedContributorIdsList = globalThis.selectedContributorIds;
        } else if (typeof selectedContributorIds !== 'undefined' && selectedContributorIds.length > 0) {
            selectedContributorIdsList = selectedContributorIds;
        }

        if (typeof globalThis.selectedContributors !== 'undefined' && globalThis.selectedContributors.length > 0) {
            selectedContributorsList = globalThis.selectedContributors;
        } else if (typeof selectedContributors !== 'undefined' && selectedContributors.length > 0) {
            selectedContributorsList = selectedContributors;
        }

        const contributorIdsInput = document.getElementById('selectedContributorIds');
        if (contributorIdsInput && selectedContributorIdsList.length > 0) {
            contributorIdsInput.value = selectedContributorIdsList.join(',');
        }

        const contributorDisplayDiv = document.getElementById('selectedContributorsDisplay');
        if (contributorDisplayDiv && selectedContributorsList.length > 0) {
            contributorDisplayDiv.innerHTML = '';
            selectedContributorsList.forEach(contributor => {
                const badge = document.createElement('span');
                badge.className = 'badge bg-success me-1 mb-1';
                badge.textContent = contributor.contributorName || contributor.name || `기여자 ${contributor.contributorId || contributor.id}`;
                contributorDisplayDiv.appendChild(badge);
            });
        }

        if (selectedContributorIdsList.length > 0) {
            setTimeout(() => {
                selectedContributorIdsList.forEach(contributorId => {
                    const contributorCheckbox = document.getElementById(`contributor_${contributorId}`);
                    if (contributorCheckbox) {
                        contributorCheckbox.checked = true;
                    }
                });
            }, 200);
        }
    }

    // 초기화
    document.addEventListener('DOMContentLoaded', function() {
        initEditors();
        initImagePreview();
        initUpdateForm();

        const bookForm = document.getElementById('bookForm');
        if (bookForm) {
            bookForm.addEventListener('submit', handleFormSubmit);
        }
    });
})();

// 도서 삭제 함수
window.softDeleteBook = function(bookId) {    if (confirm('정말로 이 도서의 삭제 상태를 변경하시겠습니까?')) {
        fetch(`/admin/books/${bookId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    alert('도서의 삭제 상태가 변경되었습니다.');
                    location.reload();
                } else {
                    response.text().then(text => {
                        console.error('삭제 실패:', text);
                        alert('상태 변경에 실패했습니다: ' + text);
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('오류가 발생했습니다: ' + error.message);
            });
    }
};

