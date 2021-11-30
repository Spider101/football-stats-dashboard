import fetchDataFromEndpoint from './utils';

export const fetchAllClubs = async ({ meta: { authData }}) => {
    const res = await fetchDataFromEndpoint('club/all', 'GET', {
        Authorization: `BEARER ${authData.id}`
    });
    return await res.json();
};