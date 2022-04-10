const baseUrl = process.env.REACT_APP_SERVER_ENDPOINT;

export default async function makeRequestToEndpoint(endpointFragment, method, headers, data = {}) {
    const body = data instanceof FormData ? data : JSON.stringify(data);
    const options = {
        method,
        headers: {
            ...headers,
            'Content-Type': 'application/json'
        },
        // body is not allowed when method is GET/HEAD
        ...(method !== 'GET' && { body })
    };
    if (data instanceof FormData) {
        delete options.headers['Content-Type'];
    }
    return await fetch(`${baseUrl}/${endpointFragment}`, options);
}