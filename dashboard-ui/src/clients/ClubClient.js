import fetchDataFromEndpoint from './utils';
import { httpStatus } from '../utils';

export const fetchAllClubs = async ({ meta: { authData } }) => {
    const res = await fetchDataFromEndpoint('club/all', 'GET', {
        Authorization: `BEARER ${authData.id}`
    });
    return await res.json();
};

export const createNewClub = async ({ newClubData, authToken }) => {
    const res = await fetchDataFromEndpoint('club', 'POST', { Authorization: `BEARER ${authToken}` }, newClubData);
    if (res.ok) {
        return await res.json();
    } else if (res.status === httpStatus.BAD_REQUEST) {
        throw new Error('Something went wrong trying to create new club!');
    }
};