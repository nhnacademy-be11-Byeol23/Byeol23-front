

document.getElementById("publisherCreateForm").addEventListener("submit", e => {
    e.preventDefault();
    const name = document.getElementById("root").value.trim();
    if (!name) return alert("태그명을 입력하세요.");
    fetch("/admin/publishers", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            publisherName: name
        })
    })
        .then(res => {
            if (!res.ok) throw new Error("등록 실패");
            // 👇 [수정] 새로고침 전 해시(#)를 설정
            location.hash = '#publishers';
            location.reload();
        })
        .catch(err => alert(err));
});

function toggleUpdate(publisherId){
    const updateLine = document.getElementById("publisherUpdateDiv" + publisherId);
    if(updateLine.style.display === "none"){
        updateLine.style.display = "flex";
    } else {
        updateLine.style.display = "none";
    }
}

async function updatePublisher(publisherId){
    const input = document.getElementById("publisherUpdateInput" + publisherId);
    const newName = input.value.trim();
    if (newName === "") {alert("수정할 이름을 입력하십시오"); return;}
    console.log(newName);
    const res = await fetch(`/admin/publishers/`+publisherId , {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        redirect: 'follow',
        body: JSON.stringify({ publisherName: newName })
    });
    if (!res.ok) throw new Error("수정 실패");
    window.location.href = '/admin/publishers';
}



function deletePublisher(button) {
    console.log('[publisher-js] deletePublisher called');
    const id = button.dataset.id;
    if (!id) return;

    fetch(`/admin/publishers/${id}`, { method: "DELETE"}) // ← use your real API path
        .then((res) => {
            if (!res.ok) throw new Error("삭제 실패");
            // Remove item without full reload for better UX
            window.location.href = '/admin/publishers';
        })
        .catch((err) => {
            console.error(err);
            alert(err.message || err);
        });
}

function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, (c) => ({
        "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"
    }[c]));
}

