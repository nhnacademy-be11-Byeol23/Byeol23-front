(function () {
    function sanitizeEditorContent(html) {
        if (!html) {
            return "";
        }
        return html
            .replace(/<\/p>/gi, '\n')
            .replace(/<br\s*\/?>/gi, '\n')
            .replace(/<[^>]*>/g, '')
            .replace(/\n{2,}/g, '\n')
            .trim();
    }

    function initEditor() {
        // 1) Make sure Toast UI is loaded
        if (!window.toastui || !toastui.Editor) {
            console.error("Toast UI Editor not loaded. Check script order / network.");
            return;
        }

        // 2) Make sure the editor container exists on this page
        const el = document.querySelector("#descriptionEditor");
        if (!el) return; // this JS might be included on pages without the editor

        // 3) Initialize the description editor
        const editor = new toastui.Editor({
            el,
            height: "300px",
            initialEditType: "wysiwyg",
            placeholder: "도서 설명을 입력하세요…",
            toolbarItems: [
                ["heading", "bold", "italic", "strike"],
                ["hr", "quote"],
                ["ul", "ol", "task"],
                ["link"],
                ["code", "codeblock"]
            ]
        });

        window.editor = editor;

        // 4) Initialize the TOC editor
        const tocEditorElement = document.querySelector("#tocEditor");
        if (tocEditorElement) {
            const tocEditor = new toastui.Editor({
                el: tocEditorElement,
                height: "300px",
                initialEditType: "wysiwyg",
                placeholder: "도서 목차를 입력하세요…",
                initialValue: ' ',
                toolbarItems: [
                    ["heading", "bold", "italic", "strike"],
                    ["hr", "quote"],
                    ["ul", "ol", "task"],
                    ["link"],
                    ["code", "codeblock"]
                ]
            });
            window.tocEditor = tocEditor; // 전역 변수로 저장

            setTimeout(() => {
                const editorWrapper = tocEditorElement.querySelector('.toastui-editor');
                if (editorWrapper) {
                    const contentsDiv = editorWrapper.querySelector('.toastui-editor-contents');
                    if (contentsDiv) {
                        // 빈 상태에서 나타나는 border나 background 제거
                        contentsDiv.style.border = 'none';
                        contentsDiv.style.minHeight = '0';
                    }
                    // 에디터 전체의 불필요한 border 제거
                    const wysiwygDiv = editorWrapper.querySelector('.toastui-editor-contents div[contenteditable="true"]');
                    if (wysiwygDiv) {
                        wysiwygDiv.style.outline = 'none';
                        wysiwygDiv.style.border = 'none';
                    }
                }
            }, 100);
        }

        // 5) Prefill from sessionStorage if present
        try {
            const raw = sessionStorage.getItem("aladinBookDraft");
            if (raw) {
                const b = JSON.parse(raw);
                const desc = b?.description || "";
                console.log("에디터에 설명 설정:", desc);
                if (/<[a-z][\s\S]*>/i.test(desc)) {
                    editor.changeMode("wysiwyg", true);
                    editor.setHTML(desc);
                } else {
                    editor.setMarkdown(desc);
                }
            }
        } catch (e) {
            console.warn("Prefill failed:", e);
        }

        // 5) Copy content to hidden input on submit (SSR-friendly)
        const form = document.querySelector("form"); // or a more specific selector
        form?.addEventListener("submit", () => {
            const value = editor.getHTML(); // or editor.getMarkdown()
            const hidden = document.getElementById("description");
            if (hidden) hidden.value = value;
            
            // 목차 에디터 내용도 저장
            if (window.tocEditor) {
                const tocValue = window.tocEditor.getHTML();
                const tocHidden = document.getElementById("toc");
                if (tocHidden) tocHidden.value = tocValue;
            }
        });

        //추가 버튼
        const bookForm = document.getElementById('bookForm');
        if (bookForm) {
            console.log("생성 버튼 눌림");
            bookForm.addEventListener('submit', function (event) {
                event.preventDefault();
                if (editor) {
                    document.getElementById('description').value = sanitizeEditorContent(editor.getHTML());
                }
                // TOC는 항상 빈 문자열로 저장
                const tocInput = document.getElementById('toc');
                if (tocInput) {
                    tocInput.value = '';
                }

                // String 값으로 받아서 처리
                const regularPriceStr = document.getElementById("regularPrice").value;
                const salePriceStr = document.getElementById("salePrice").value;
                const stockStr = document.getElementById("stock").value;
                const publisherIdStr = document.getElementById("publisherId").value;

                // 카테고리 ID 파싱
                const categoryIdsInput = document.getElementById("selectedCategoryIds");
                const categoryIdsStr = categoryIdsInput ? categoryIdsInput.value : "";
                const categoryIds = categoryIdsStr 
                    ? categoryIdsStr.split(',').map(id => parseInt(id.trim(), 10)).filter(id => !isNaN(id))
                    : [];

                const requestBody = {
                    bookName: document.getElementById("bookName").value,
                    author: document.getElementById("author").value,
                    translator: document.getElementById("translator").value || "",
                    publisher: document.getElementById("publisherName").value,
                    description: document.getElementById("description").value,
                    regularPrice: regularPriceStr ? parseFloat(regularPriceStr) : null,
                    salePrice: salePriceStr ? parseFloat(salePriceStr) : null,
                    isbn: document.getElementById("isbn").value,
                    publishDate: document.getElementById("publishDate").value,
                    imageUrl: document.getElementById("imageUrl").value,
                    toc: document.getElementById("toc").value,
                    stock: stockStr ? parseInt(stockStr, 10) : null,
                    isPack: bookForm.querySelector('#isPack').checked,
                    bookStatus: document.getElementById("bookStatus").value,
                    categoryIds: categoryIds,  // 선택된 카테고리 ID들
                    tagIds: [],       // 선택된 태그 ID들
                    contributorIds: []  // 파싱된 기여자 ID들 (서버에서 처리)
                };

                console.log(requestBody);


                const url = '/admin/bookApi/new';  // 변경


                fetch(url, {
                    method: "POST",
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(requestBody),
                    redirect: 'manual'  // 리다이렉트를 수동으로 처리
                })
                    .then(response => {
                        if (response.status === 302 || response.status === 0) {
                            // 302 리다이렉트 응답 또는 CORS 리다이렉트
                            const location = response.headers.get('Location');
                            if (location && location.includes('/admin/books')) {
                                // 성공 리다이렉트
                                alert('도서가 성공적으로 생성되었습니다.');
                                window.location.href = location;
                            } else if (location && location.includes('/admin/bookApi/new')) {
                                // 실패 리다이렉트 (에러 메시지가 플래시 속성으로 전달됨)
                                alert('도서 생성에 실패했습니다. 페이지를 새로고침하여 오류 메시지를 확인하세요.');
                                window.location.href = location;
                            } else {
                                // 알 수 없는 리다이렉트
                                window.location.href = location || '/admin/books';
                            }
                        } else if (response.ok) {
                            // 성공 응답
                            alert('도서가 성공적으로 생성되었습니다.');
                            window.location.href = '/admin/books';
                        } else {
                            // 에러 응답
                            response.text().then(text => {
                                console.error('도서 생성 실패:', text);
                                alert('도서 생성에 실패했습니다. 페이지를 새로고침하여 오류 메시지를 확인하세요.');
                                window.location.href = '/admin/bookApi/new';
                            });
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('오류가 발생했습니다: ' + error.message);
                    });
            });
        }
    }

    // Ensure DOM is ready before init
    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", initEditor);
    } else {
        initEditor();
    }
})();



(function prefillFromSession() {
	// DOM이 로드될 때까지 대기
	function tryPrefill() {
		const raw = sessionStorage.getItem("aladinBookDraft");
		if (!raw) {
			console.log("sessionStorage에 알라딘 데이터가 없습니다.");
			return;
		}
		
		try {
			const b = JSON.parse(raw);
			console.log("알라딘 데이터 로드:", b);
			console.log("전체 데이터 객체:", JSON.stringify(b, null, 2));  // 전체 객체 확인

			const set = (id, v) => { 
				const el = document.getElementById(id); 
				if (el && v != null && v !== "") {
					el.value = String(v);
					console.log(`필드 설정: ${id} = ${v}`);
				} else {
					console.warn(`필드 설정 실패: ${id}, 값: ${v}`);
				}
			};

			// 알라딘 정보 표시 영역 표시
			const aladinInfo = document.getElementById("aladinInfo");
			if (aladinInfo && b.title) {
				aladinInfo.style.display = "block";
				
				// 이미지 표시
				const bookImage = document.getElementById("aladinBookImage");
				if (bookImage && b.imageUrl) {
					bookImage.src = b.imageUrl;
					bookImage.alt = b.title || "도서 이미지";
				}
				
				// 출판사 이름 표시
				const publisherNameEl = document.getElementById("aladinPublisherName");
				if (publisherNameEl) {
					publisherNameEl.textContent = b.publisher || "정보 없음";
				}
				
				// 도서명 표시
				const bookTitleEl = document.getElementById("aladinBookTitle");
				if (bookTitleEl) {
					bookTitleEl.textContent = b.title || "";
				}
				
				// 저자 표시
				const authorEl = document.getElementById("aladinAuthor");
				if (authorEl) {
					authorEl.textContent = b.author || "정보 없음";
				}
				
				// ISBN 표시
				const isbnEl = document.getElementById("aladinIsbn");
				if (isbnEl) {
					isbnEl.textContent = b.isbn13 || "정보 없음";
				}
			}

			// 기본 필드 설정
			set("bookName", b.title);
			set("isbn", b.isbn13);
			set("salePrice", b.priceSales);
			set("regularPrice", b.priceStandard);
            const authorDisplay = document.getElementById("authorDisplay");
            if (authorDisplay) {
                authorDisplay.textContent = b.author || "정보 없음";
            }
			console.log("이미지 URL 확인 - b.imageUrl:", b.imageUrl);
			console.log("이미지 URL 확인 - typeof:", typeof b.imageUrl);
			if (b.imageUrl) {
				console.log("이미지 URL 설정 시도:", b.imageUrl);
				set("imageUrl", b.imageUrl);
				
				// 실제로 설정되었는지 확인
				const imageUrlEl = document.getElementById("imageUrl");
				if (imageUrlEl) {
					console.log("이미지 URL 필드 값:", imageUrlEl.value);
				}
			} else {
				console.warn("이미지 URL이 없습니다. b 객체:", b);
				console.warn("사용 가능한 키:", Object.keys(b));
			}

			// 출판일 처리
			if (b.pubDate) {
				let iso = b.pubDate;
				if (!/^\d{4}-\d{2}-\d{2}$/.test(iso)) {
					iso = iso.replace(/[^0-9]/g, "").replace(/^(\d{4})(\d{2})(\d{2})$/, "$1-$2-$3");
				}
				set("publishDate", iso);
			}

			// 설명은 에디터가 초기화된 후에 설정
			if (b.description && window.editor) {
				const desc = b.description || "";
				if (/<[a-z][\s\S]*>/i.test(desc)) {
					window.editor.changeMode("wysiwyg", true);
					window.editor.setHTML(desc);
				} else {
					window.editor.setMarkdown(desc);
				}
			}

			if (b.publisher) {
				// 출판사 이름만 표시
				set("publisherName", b.publisher);
				// 출판사 ID는 백엔드에서 처리하므로 여기서는 설정하지 않음
			}
		} catch (e) {
			console.error("Failed to parse aladinBookDraft:", e);
		}
	}

	// DOM이 로드되면 실행
	if (document.readyState === "loading") {
		document.addEventListener("DOMContentLoaded", tryPrefill);
	} else {
		setTimeout(tryPrefill, 200);  // 시간을 조금 늘림
	}
})();


