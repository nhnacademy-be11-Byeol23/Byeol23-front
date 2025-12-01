

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

            if (!res.ok){
                return res.json().then(errorBody => { throw new Error(errorBody.message || "추가 실패"); });
            }
            window.location.reload();
        })
        .catch(err => alert(err));
});

function toggleUpdate(tagId){
    const updateLine = document.getElementById("tagUpdateDiv" + tagId);
    if(updateLine.style.display === "none"){
        updateLine.style.display = "flex";
    } else {
        updateLine.style.display = "none";
    }
}

async function updateTag(tagId){
    const input = document.getElementById("tagUpdateInput" + tagId);
    const newName = input.value.trim();
    if (newName === "") {alert("수정할 이름을 입력하십시오"); return;}
    console.log(newName);
    try {
        const res = await fetch(`/admin/tags/put/` + tagId, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({ tagName: newName })
        });

        if (!res.ok) {
            return res.json().then(errorBody => { throw new Error(errorBody.message || "수정 실패"); });
        }

        window.location.href = '/admin/tags';

    } catch (err) {
        console.error(err);
        alert(err.message || "수정 중 오류가 발생했습니다.");
    }
}

function deleteTag(button) {
    console.log('[tag-js] deleteTag called');
    const id = button.dataset.id;
    if (!id) return;

    fetch(`/admin/tags/delete/${id}`, { method: "POST"})
        .then((res) => {
            if (!res.ok) {
                return res.json().then(errorBody => { throw new Error(errorBody.message || "삭제 실패"); });
            }
            // ✨ 성공: 삭제 후 태그 목록 페이지로 이동합니다.
            window.location.href = '/admin/tags';
        })
        .catch((err) => {
            console.error(err);
            alert(err.message || "삭제 중 오류가 발생했습니다.");
        });
}

function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, (c) => ({
        "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"
    }[c]));
}
