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

        // 3) Initialize the editor
        const editor = new toastui.Editor({
            el,
            height: "300px",
            initialEditType: "wysiwyg", // or 'wysiwyg'
            previewStyle: "vertical",
            placeholder: "도서 설명을 입력하세요…",
            toolbarItems: [
                ["heading", "bold", "italic", "strike"],
                ["hr", "quote"],
                ["ul", "ol", "task"],
                ["link"],
                ["code", "codeblock"]
            ]
        });

        // 4) Prefill from sessionStorage if present
        try {
            const raw = sessionStorage.getItem("aladinBookDraft");
            if (raw) {
                const b = JSON.parse(raw);
                const desc = b?.description || "";
                console.log(desc);
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
            const value = editor.description; // or editor.getMarkdown()
            const hidden = document.getElementById("description");
            if (hidden) hidden.value = value;
        });

        //추가 버튼
        const bookForm = document.getElementById('bookForm');
        if (bookForm) {
            console.log("생성 버튼 눌림");
            bookForm.addEventListener('submit', function (event) {
                event.preventDefault();

                const requestBody = {
                    bookName: document.getElementById("bookName").value,
                    toc: document.getElementById("toc").value,
                    description: document.getElementById("description").value,
                    regularPrice: parseFloat(document.getElementById("regularPrice").value),
                    salePrice: parseFloat(document.getElementById("salePrice").value),
                    publishDate: document.getElementById("publishDate").value,
                    isPack: bookForm.querySelector('#isPack').checked,
                    bookStatus: document.getElementById("bookStatus").value,
                    stock: parseInt(document.getElementById("stock").value, 10),
                    publisherId: parseInt(document.getElementById("publisherId").value, 10)
                };

                console.log(requestBody);


                const url = '/admin/books/new';


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
                            response.text().then(text => alert('도서 생성') + '에 실패했습니다: ' + text);
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
    const raw = sessionStorage.getItem("aladinBookDraft");
    if (!raw) return;
    try {
        const b = JSON.parse(raw);

        const set = (id, v) => { const el = document.getElementById(id); if (el && v != null) el.value = v; };

        set("bookName",        b.title);
        set("author",       b.author);
        // set("publisher",    b.publisher);
        //TODO: api로 받아오는 String type의 새로운 publisher 처리 필요
        set("isbn",       b.isbn13);
        set("description",  b.description);
        set("salePrice",   b.priceSales);
        set("regularPrice",b.priceStandard);

        // Optional: normalize pubDate to yyyy-MM-dd for <input type="date">
        if (b.pubDate) {
            // If pubDate like "2021-07-15" it's fine; if "20210715", normalize:
            const iso = b.pubDate.replace(/[^r0-9]/g, "").replace(/^(\d{4})(\d{2})(\d{2})$/, "$1-$2-$3");
            set("publishDate", iso);
        }

        // If you only want to use it once:
        // sessionStorage.removeItem("aladinBookDraft");
    } catch (e) {
        console.error("Failed to parse aladinBookDraft:", e);
    }
})();

