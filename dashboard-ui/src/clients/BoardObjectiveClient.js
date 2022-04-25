import makeRequestToEndpoint from './utils';

export const createNewBoardObjective = async ({ newBoardObjectiveData, clubId, authToken }) => {
    const res = await makeRequestToEndpoint(
        `club/${clubId}/board-objective`,
        'POST',
        { Authorization: `BEARER ${authToken}` },
        newBoardObjectiveData
    );
    if (res.ok) {
        return await res.json();
    } else {
        // TODO: 04/20/22 be more specific about the status codes if need be
        const { message: errorMessage } = await res.json();
        throw new Error(errorMessage);
    }
};

export const fetchAllBoardObjectivesForClub = async ({ queryKey, meta: { authData }}) => {
    const clubId = queryKey[1];
    const res = await makeRequestToEndpoint(`club/${clubId}/board-objective/all`, 'GET', {
        Authorization: `BEARER ${authData.id}`
    });
    return await res.json();
};