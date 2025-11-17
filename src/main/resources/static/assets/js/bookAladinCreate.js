(function () {
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
            // previewStyle 제거 - WYSIWYG 모드에서는 필요 없음
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
        let tocEditor = null;
        if (tocEditorElement) {
            tocEditor = new toastui.Editor({
                el: tocEditorElement,
                height: "300px",
                initialEditType: "wysiwyg",
                // previewStyle 제거 - WYSIWYG 모드에서는 필요 없음 (회색 줄 제거)
                placeholder: "도서 목차를 입력하세요…",
                toolbarItems: [
                    ["heading", "bold", "italic", "strike"],
                    ["hr", "quote"],
                    ["ul", "ol", "task"],
                    ["link"],
                    ["code", "codeblock"]
                ],
                // 빈 상태에서 회색 줄 제거를 위한 설정
                events: {
                    load: function() {
                        // 에디터 로드 후 스타일 조정
                        const editorEl = tocEditorElement.querySelector('.toastui-editor-contents');
                        if (editorEl) {
                            editorEl.style.minHeight = '0';
                        }
                    }
                }
            });
            window.tocEditor = tocEditor; // 전역 변수로 저장
            
            // 에디터 초기화 후 스타일 조정 (회색 줄 제거)
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
            if (tocEditor) {
                const tocValue = tocEditor.getHTML();
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

                // 에디터 내용을 hidden input에 저장
                if (editor) {
                    document.getElementById('description').value = editor.getHTML();
                }
                if (tocEditor) {
                    document.getElementById('toc').value = tocEditor.getHTML();
                }

                // String 값으로 받아서 처리
                const regularPriceStr = document.getElementById("regularPrice").value;
                const salePriceStr = document.getElementById("salePrice").value;
                const stockStr = document.getElementById("stock").value;
                const publisherIdStr = document.getElementById("publisherId").value;

                const requestBody = {
                    bookName: document.getElementById("bookName").value,
                    author: document.getElementById("author").value,  // 추가
                    publisher: document.getElementById("publisherName").value,  // 추가
                    description: document.getElementById("description").value,
                    regularPrice: regularPriceStr ? parseFloat(regularPriceStr) : null,
                    salePrice: salePriceStr ? parseFloat(salePriceStr) : null,
                    isbn: document.getElementById("isbn").value,
                    publishDate: document.getElementById("publishDate").value,
                    imageUrl: document.getElementById("imageUrl").value,  // 추가
                    toc: document.getElementById("toc").value,
                    stock: stockStr ? parseInt(stockStr, 10) : null,
                    isPack: bookForm.querySelector('#isPack').checked,
                    bookStatus: document.getElementById("bookStatus").value,
                    categoryIds: [],  // 선택된 카테고리 ID들
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
                })
                    .then(response => {
                        if (response.ok) {
                            alert('도서가 성공적으로 생성되었습니다.');
                            window.location.href = '/admin/books';
                        } else {
                            response.text().then(text => alert('도서 생성에 실패했습니다: ' + text));
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('오류가 발생했습니다.');
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
			set("author", b.author);
			set("isbn", b.isbn13);
			set("salePrice", b.priceSales);
			set("regularPrice", b.priceStandard);
			
			// 이미지 URL 설정 - 더 자세한 디버깅
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
			
			// 출판사 처리 - 이름을 먼저 표시하고, ID를 찾아서 설정
			if (b.publisher) {
				// 출판사 이름 표시
				set("publisherName", b.publisher);
				
				// 출판사 ID 찾기 또는 생성
				findOrCreatePublisher(b.publisher).then(publisherId => {
					if (publisherId) {
						const publisherIdEl = document.getElementById("publisherId");
						if (publisherIdEl) {
							publisherIdEl.value = String(publisherId);
							console.log("출판사 ID 설정:", publisherId);
						}
					}
				}).catch(err => {
					console.error("출판사 처리 실패:", err);
				});
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

// 출판사 찾기 또는 생성 함수
async function findOrCreatePublisher(publisherName) {
    try {
        const response = await fetch('/api/publishers/find-or-create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                publisherName: publisherName
            })
        });
        
        if (response.ok) {
            const publisher = await response.json();
            return publisher.publisherId;
        } else {
            console.error('출판사 찾기/생성 실패:', response.status);
            return null;
        }
    } catch (error) {
        console.error('출판사 처리 중 오류:', error);
        return null;
    }
}

