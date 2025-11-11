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
    }

    return response;
}