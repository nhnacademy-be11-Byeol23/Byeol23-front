document.getElementById("contributorCreateForm").addEventListener("submit", e => {
    e.preventDefault();
    const name = document.getElementById("root").value.trim();
    const role = document.getElementById("contributor-role-input").value;
    if (!name) return alert("기여자명을 입력하세요.");
    fetch("/admin/cont", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            contributorName: name,
            contributorRole: role
        })
    })
        .then(res => {
            if (!res.ok) {
                return res.json().then(errorBody => { throw new Error(errorBody.message || "추가 실패"); });
            }
            location.hash = '#contributors';
            location.reload();
        })
        .catch(err => alert(err));
});

function toggleUpdate(contributorId){
    const updateLine = document.getElementById("contributorUpdateDiv" + contributorId);
    if(updateLine.style.display === "none"){
        updateLine.style.display = "flex";
    } else {
        updateLine.style.display = "none";
    }
}

async function updateContributor(contributorId){
    const input = document.getElementById("contributorUpdateInput" + contributorId);
    const newName = input.value.trim();
    const newRole = document.getElementById("contributor-role-update" + contributorId).value;
    if (newName === "") {alert("수정할 이름을 입력하십시오"); return;}
    console.log(newName);
    console.log(newRole);
    const res = await fetch(`/admin/cont/put/`+contributorId , {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        redirect: 'follow',
        body: JSON.stringify({
            contributorName: newName,
            contributorRole: newRole
        }
        )
    });
    if (!res.ok) {
        return res.json().then(errorBody => { throw new Error(errorBody.message || "수정 실패"); });
    }
    window.location.href = '/admin/cont';
}



function deleteContributor(button) {
    console.log('[contributor-js] deleteContributor called');
    const id = button.dataset.id;
    if (!id) return;

    fetch(`/admin/cont/delete/${id}`, { method: "POST"}) // ← use your real API path
        .then((res) => {
            if (!res.ok) {
                return res.json().then(errorBody => { throw new Error(errorBody.message || "삭제 실패"); });
            }
            window.location.href = '/admin/cont';
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