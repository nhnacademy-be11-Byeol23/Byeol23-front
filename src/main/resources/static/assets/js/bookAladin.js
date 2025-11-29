function apiSearch() {
    const input = document.getElementById("keyword").value.trim();
    const size = document.getElementById("size").value;
    
    // 현재 URL에서 키워드 파라미터 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const currentKeyword = urlParams.get('keyword') || '';
    
    // 키워드가 변경되었으면 페이지를 1로 리셋, 같으면 현재 페이지 유지
    let page = 1; // 기본값은 1
    if (input === currentKeyword) {
        // 키워드가 같으면 현재 페이지 유지
        const currentPage = document.getElementById("page").value;
        page = currentPage || 1;
    }
    
    window.location.href =
        `/admin/bookApi?keyword=${encodeURIComponent(input)}&page=${page}&size=${size}`;
}

function addApiBook(btn) {
    // Fallback if you forget to pass `this` in onclick
    btn = btn || (window.event && window.event.currentTarget);
    if (!btn) return;

    const d = btn.dataset;
    const payload = {
        title: d.title || "",
        author: d.author || "",
        publisher: d.publisher || "",
        description: d.description || "",
        priceSales: d.priceSales ? String(d.priceSales) : "",
        priceStandard: d.priceStandard ? String(d.priceStandard) : "",
        isbn13: d.isbn13 || "",
        pubDate: toYmdFromJavaDate(d.pubDate) || "",
        imageUrl: (d.cover || btn.getAttribute("data-cover") || "").replace("coversum", "cover500")
    };

    sessionStorage.setItem("aladinBookDraft", JSON.stringify(payload));
    console.log("알라딘 데이터 저장:", payload);
    window.location.href = "/admin/bookApi/new";
}

function toYmdFromJavaDate(s) {
    if (!s) return "";
    const mMap = {Jan:"01",Feb:"02",Mar:"03",Apr:"04",May:"05",Jun:"06",
        Jul:"07",Aug:"08",Sep:"09",Oct:"10",Nov:"11",Dec:"12"};
    // EEE MMM dd HH:mm:ss zzz yyyy
    const re = /^[A-Za-z]{3}\s([A-Za-z]{3})\s(\d{1,2})\s\d{2}:\d{2}:\d{2}\s[A-Za-z]{2,4}\s(\d{4})$/;
    const m = re.exec(s.trim());
    if (!m) return ""; // not in expected format
    const mon = mMap[m[1]];
    const dd  = String(m[2]).padStart(2, "0");
    const yyyy= m[3];
    return `${yyyy}-${mon}-${dd}`;
}
