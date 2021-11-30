import fetchDataFromEndpoint from './utils';

export const fetchAllClubs = async ({ queryKey }) => {
    const [_key, { authData }] = queryKey;
    const res = await fetchDataFromEndpoint('club/all', 'GET', {
        Authorization: `BEARER ${authData.id}`
    });
    return await res.json();
};