const baseUrl = process.env.REACT_APP_SERVER_ENDPOINT;

export default async function fetchDataFromEndpoint(endpointFragment, method, headers, data = {}) {
    const contentType = data instanceof FormData ? 'multipart/form-data' : 'application/json';
    const body = data instanceof FormData ? data : JSON.stringify(data);
    const options = {
        method,
        headers: {
            ...headers,
            'Content-Type': contentType
        },
        // body is not allowed when method is GET/HEAD
        ...(method !== 'GET' && { body })
    };
    return await fetch(`${baseUrl}/${endpointFragment}`, options);
}