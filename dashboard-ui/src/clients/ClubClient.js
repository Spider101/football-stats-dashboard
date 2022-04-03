import makeRequestToEndpoint from './utils';
import { httpStatus } from '../constants';

export const fetchClubSummaries = async ({ meta: { authData } }) => {
    const res = await makeRequestToEndpoint('club/all', 'GET', {
        Authorization: `BEARER ${authData.id}`
    });
    return await res.json();
};

export const fetchClub = async ({ queryKey, meta: { authData }}) => {
    const clubId = queryKey[1];
    const res = await makeRequestToEndpoint(`club/${clubId}`, 'GET', { Authorization: `BEARER ${authData.id}`});
    return await res.json();
};

export const createNewClub = async ({ newClubData, authToken }) => {
    const res = await makeRequestToEndpoint('club', 'POST', { Authorization: `BEARER ${authToken}` }, newClubData);
    if (res.ok) {
        return await res.json();
    } else if (res.status === httpStatus.BAD_REQUEST) {
        const { message: errorMessage } = await res.json();
        throw new Error(errorMessage);
    }
};

export const fetchSquadHubData = async ({ queryKey, meta: { authData } }) => {
    const clubId = queryKey[1];
    const res = await makeRequestToEndpoint(`club/${clubId}/squadPlayers`, 'GET', {
        Authorization: `BEARER ${authData.id}`
    });
    return await res.json();
};