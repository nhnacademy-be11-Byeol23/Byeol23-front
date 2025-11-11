async function authFetch(url, options = {}) {
    const token = sessionStorage.getItem('accessToken');
    const headers = new Headers(options.headers || {});

    if (token) {
        headers.set('Authorization', `Bearer ${token}`);
    }

    options.headers = headers;
    options.credentials = 'include';

    const response = await fetch(url, options);

    if (response.status === 401) {
        console.warn('AccessToken 만료');
        // AJAX 요청인 경우 JSON 응답에서 redirect 정보 확인
        try {
            const data = await response.json();
            if (data.redirect) {
                window.location.href = data.redirect;
            } else {
                window.location.href = '/members/login';
            }
        } catch {
            window.location.href = '/members/login';
        }
    }

    return response;
}