function apiSearch() {
    const input = document.getElementById("keyword").value.trim();
    const page = document.getElementById("page").value;
    const size = document.getElementById("size").value;
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
        imageUrl: d.cover || btn.getAttribute("data-cover") || ""  // 여러 방법으로 시도
    };

    // sessionStorage에 저장
    sessionStorage.setItem("aladinBookDraft", JSON.stringify(payload));
    console.log("알라딘 데이터 저장:", payload);  // 디버깅용

    // bookAladinCreate.html 페이지로 이동
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
