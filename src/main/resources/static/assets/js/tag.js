

document.getElementById("tagCreateForm").addEventListener("submit", e => {
    e.preventDefault();
    const name = document.getElementById("root").value.trim();
    if (!name) return alert("태그명을 입력하세요.");
    fetch("/admin/tags", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            tagName: name
        })
    })
        .then(res => {
            if (!res.ok) throw new Error("등록 실패");
            // 👇 [수정] 새로고침 전 해시(#)를 설정
            location.hash = '#tags';
            location.reload();
        })
        .catch(err => alert(err));
});

function toggleUpdate(tagId){
    const updateLine = document.getElementById("tagUpdateDiv" + tagId);
    if(updateLine.style.display === "none"){
        updateLine.style.display = "list-item";
    } else {
        updateLine.style.display = "none";
    }
}

async function updateTag(tagId){
    const input = document.getElementById("tagUpdateInput" + tagId);
    const newName = input.value.trim();
    if (newName === "") alert("수정할 이름을 입력하십시오"); return;
    console.log(newName);
    const res = await fetch(`/admin/tags/`+tagId , {
        method: "PUT", // or PATCH
        headers: {"Content-Type": "application/json"},
        redirect: 'follow',
        body: JSON.stringify({ tagName: newName })
    });
    if (!res.ok) throw new Error("수정 실패");
    window.location.href = '/admin/tags';
}



function deleteTag(button) {
    console.log('[tag-js] deleteTag called');
    const id = button.dataset.id;
    if (!id) return;

    fetch(`/admin/tags/${id}`, { method: "DELETE"}) // ← use your real API path
        .then((res) => {
            if (!res.ok) throw new Error("삭제 실패");
            // Remove item without full reload for better UX
            window.location.href = '/admin/tags';
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

