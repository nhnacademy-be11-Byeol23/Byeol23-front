(() => {
    const form = document.getElementById('mypage-edit-form');
    if (!form) return;

    // ===== Helpers =====
    const qs = (sel, root = document) => root.querySelector(sel);
    const qsa = (sel, root = document) => Array.from(root.querySelectorAll(sel));

    const fmtPoint = (n) => {
        if (n == null) return '0';
        try {
            // format with thousands separators, no decimals
            return Number(n).toLocaleString(undefined, { maximumFractionDigits: 0 });
        } catch (_) {
            return String(n);
        }
    };

    const trimOrEmpty = (s) => (s == null ? '' : String(s).trim());

    const deriveDisplayName = (nickname, memberName) => {
        const nn = trimOrEmpty(nickname);
        if (nn.length > 0) return nn;
        const mn = trimOrEmpty(memberName);
        if (mn.length > 0) return mn;
        return '';
    };

    const firstInitial = (name) => {
        const s = trimOrEmpty(name);
        return s.length > 0 ? s.substring(0, 1) : 'U';
    };

    const toast = (msg, type = 'success') => {
        // Minimal inline toast. Replace with Bootstrap toast if you already have it.
        const box = document.createElement('div');
        box.textContent = msg;
        box.style.position = 'fixed';
        box.style.zIndex = 9999;
        box.style.left = '50%';
        box.style.top = '24px';
        box.style.transform = 'translateX(-50%)';
        box.style.padding = '10px 14px';
        box.style.borderRadius = '8px';
        box.style.color = '#fff';
        box.style.background = type === 'success' ? '#198754' : '#dc3545';
        box.style.boxShadow = '0 6px 16px rgba(0,0,0,.2)';
        document.body.appendChild(box);
        setTimeout(() => box.remove(), 2000);
    };

    const setLoading = (isLoading) => {
        const btn = qs('button[type="submit"]', form);
        if (!btn) return;
        btn.disabled = isLoading;
        if (isLoading) {
            btn.dataset._label = btn.textContent;
            btn.textContent = '저장 중...';
        } else if (btn.dataset._label) {
            btn.textContent = btn.dataset._label;
            delete btn.dataset._label;
        }
    };


    // ===== Choose request style =====
    // A) If you created a REST endpoint (recommended):
    //    PUT /api/members/me  -> returns JSON MemberResponse
    // B) If you only have the form POST /mypage that redirects with HTML:
    //    set USE_REST = false and it will fallback to plain form submit.

    const USE_REST = true; // flip to false if you don’t have a JSON API yet.

    // ===== Submit handler =====
    form.addEventListener('submit', async (e) => {
        if (!USE_REST) {
            // Let the browser submit normally (server re-renders the page)
            return;
        }
        e.preventDefault();

        setLoading(true);
        try {
            // Collect fields (match your form input names)
            const fd = new FormData(form);
            // These names should match your MemberResponse / update DTO field names
            const payload = {
                memberName: trimOrEmpty(fd.get('memberName')),
                nickname: trimOrEmpty(fd.get('nickname')),
                phoneNumber: trimOrEmpty(fd.get('phoneNumber')),
                email: trimOrEmpty(fd.get('email')),
                birthDate: trimOrEmpty(fd.get('birthDate')) || null, // 'yyyy-MM-dd'
            };

            const res = await fetch('/api/members/mypage', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload),
                credentials: 'same-origin'
            });

            if (!res.ok) {
                // Try to read validation messages
                let errText = '프로필 저장에 실패했습니다.';
                try {
                    const err = await res.json();
                    if (err?.message) errText = err.message;
                } catch (_) {}
                throw new Error(errText);
            }

            const updated = await res.json(); // expects MemberResponse JSON

            // ===== Update UI =====
            const displayName = deriveDisplayName(updated.nickname, updated.memberName);
            const initial = firstInitial(displayName);

            const nameNode = qs('[data-display="name"]');
            if (nameNode) nameNode.textContent = displayName || '사용자';

            const avatarNode = qs('[data-display="avatar-initial"]');
            if (avatarNode) avatarNode.textContent = initial;

            const loginNode = qs('[data-display="loginId"]');
            if (loginNode && updated.loginId != null) loginNode.textContent = updated.loginId;

            const pointNode = qs('[data-display="currentPoint"]');
            if (pointNode) pointNode.textContent = fmtPoint(updated.currentPoint);

            // Also reflect values back in the form (in case server normalized something)
            const s = (name, val) => { const el = qs(`[name="${name}"]`, form); if (el) el.value = val ?? ''; };
            s('nickname', updated.nickname);
            s('phoneNumber', updated.phoneNumber);
            s('email', updated.email);
            s('birthDate', updated.birthDate); // yyyy-MM-dd

            toast('프로필이 저장되었습니다.', 'success');
        } catch (err) {
            console.error(err);
            toast(err.message || '저장 중 오류가 발생했습니다.', 'error');
        } finally {
            setLoading(false);
        }
    });

})();

let modalInstance;

// Open modal
function passwordUpdateModal() {
    // const el = document.getElementById('passwordUpdateModal');
    // if (!modalInstance) modalInstance = new bootstrap.Modal(el);
    // resetPwModal();
    // modalInstance.show();

    document.getElementById("passwordUpdateModal").style.display = "block";
}
//
// function togglePw(id) {
//     const input = document.getElementById(id);
//     input.type = input.type === 'password' ? 'text' : 'password';
// }
//
// function resetPwModal() {
//     ['currentPassword','newPassword','confirmPassword'].forEach(id => {
//         const el = document.getElementById(id);
//         if (el) el.value = '';
//         if (el && el.type !== 'password') el.type = 'password';
//     });
//
//     hide('#verifyError'); hide('#updateError'); hide('#updateSuccess');
//
//     show('#pw-step-verify'); hide('#pw-step-update');
//     hide('#btnBack'); show('#btnVerify'); hide('#btnUpdate');
// }
//
// function pwGoBack() {
//     hide('#pw-step-update'); show('#pw-step-verify');
//     hide('#btnBack'); show('#btnVerify'); hide('#btnUpdate');
//     hide('#updateError'); hide('#updateSuccess');
// }
//
// // === DUMMY: verify "current password" strictly equals "1234" ===
// async function verifyCurrentPassword() {
//     hide('#verifyError');
//
//     const current = document.getElementById('currentPassword').value.trim();
//     if (!current) {
//         showError('#verifyError', '현재 비밀번호를 입력하세요.');
//         return;
//     }
//
//     // client-only check (no network)
//     if (current !== '1234') {
//         showError('#verifyError', '비밀번호가 일치하지 않습니다. (더미: 1234)');
//         return;
//     }
//
//     // go to step 2
//     hide('#pw-step-verify'); show('#pw-step-update');
//     show('#btnBack'); hide('#btnVerify'); show('#btnUpdate');
//
//     // live strength check
//     document.getElementById('newPassword').addEventListener('input', updateStrengthRules);
//     document.getElementById('confirmPassword').addEventListener('input', updateStrengthRules);
//     updateStrengthRules();
// }
//
// function updateStrengthRules() {
//     const pw = document.getElementById('newPassword').value;
//     const confirm = document.getElementById('confirmPassword').value;
//
//     setRule('#rule-length', pw.length >= 8);
//     const kinds = [
//         /[a-z]/.test(pw),
//         /[A-Z]/.test(pw),
//         /[0-9]/.test(pw),
//         /[^A-Za-z0-9]/.test(pw)
//     ].filter(Boolean).length;
//     setRule('#rule-mix', kinds >= 3);
//     setRule('#rule-match', pw && pw === confirm);
// }
//
// function setRule(selector, ok) {
//     const el = document.querySelector(selector);
//     if (!el) return;
//     el.classList.toggle('text-success', ok);
//     el.classList.toggle('text-danger', !ok);
// }
//
// // === DUMMY: "update" locally, do not navigate or call APIs ===
// async function submitNewPassword() {
//     const pw = document.getElementById('newPassword').value;
//     const confirm = document.getElementById('confirmPassword').value;
//
//     // 이후 pw 규칙에 맞게 수정
//     // if (pw.length < 8) { showError('#updateError', '8자 이상으로 설정해주세요.'); return; }
//     // if (kinds < 3) { showError('#updateError', '영문 대/소문자, 숫자, 특수문자 중 3종류 이상을 포함하세요.'); return; }
//     if (pw !== confirm) { showError('#updateError', '비밀번호 확인이 일치하지 않습니다.'); return; }
//
//     fetch("members/mypage/password",{
//         method: 'PUT',
//         body: JSON.stringify({pw})
//     })
//         .then(res => {
//             if (!res.ok) {
//                 let msg = '비밀번호 변경에 실패했습니다.';
//                 try { const err = res.json(); msg = err.message || msg; } catch {}
//                 throw new Error(msg);
//             }
//             alert('비밀번호 변경에 성공하였습니다.');
//             location.reload();
//         })
// }
//
// // Helpers
// function show(selector){ document.querySelector(selector).classList.remove('d-none'); }
// function hide(selector){ document.querySelector(selector).classList.add('d-none'); }
// function showError(selector, msg){
//     const el = document.querySelector(selector);
//     el.textContent = msg;
//     el.classList.remove('d-none');
// }
//
// window.passwordUpdateModal = passwordUpdateModal;