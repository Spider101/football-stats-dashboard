const baseUrl = process.env.REACT_APP_SERVER_ENDPOINT;

export default async function fetchDataFromEndpoint(endpointFragment, method, headers, data = {}) {
    const options = {
        method,
        headers: {
            ...headers,
            'Content-Type': 'application/json'
        },
        // body is not allowed when method is GET/HEAD
        ...(method !== 'GET' && { body: JSON.stringify(data) })
    };
    return await fetch(`${baseUrl}/${endpointFragment}`, options);
}