import fetchDataFromEndpoint from './utils';

export const fetchSquadHubData = async ({ queryKey }) => {
    const [_key, { authData }] = queryKey;
    // TODO: hard-coding the clubId until we can create a club from the UI
    const clubId = 'b60a9dc6-81a3-4ca1-b24b-088220fdca59';
    const res = await fetchDataFromEndpoint(`/club/${clubId}/squadPlayers`, 'GET', {
        Authorization: `BEARER ${authData.id}`
    });
    return await res.json();
};

export const fetchPlayerData = async ({ queryKey }) => {
    const [_key, { playerId, authData }] = queryKey;
    const res = await fetchDataFromEndpoint(`players/${playerId}`, 'GET', { Authorization: `BEARER ${authData.id}` });
    return await res.json();
};

export const fetchPlayerPerformanceData = async ({ queryKey }) => {
    const [_key, { playerId, authData }] = queryKey;
    const res = await fetchDataFromEndpoint(`players/${playerId}/performance`, 'GET', {
        Authorization: `BEARER ${authData.id}`
    });

    const playerPerformanceData = await res.json();
    return playerPerformanceData.map(performanceData =>
        Object.keys(performanceData)
            .filter(key => key !== 'id' && key !== 'playerId')
            .reduce((result, key) => {
                if (key === 'competitionId') {
                    result['id'] = performanceData[key];
                    return result;
                }
                result[key] = performanceData[key];
                return result;
            }, {})
    );
};