import fetchDataFromEndpoint from './utils';

export const fetchSquadHubData = async () => {
    const res = fetchDataFromEndpoint('squadPlayers', 'GET', {});
    return await res.json();
};

export const fetchPlayerData = async ({ queryKey }) => {
    const [_key, { playerId }] = queryKey;
    const res = fetchDataFromEndpoint(`players/${playerId}`, 'GET', {});
    return await res.json();
};

export const fetchPlayerPerformanceData = async ({ queryKey }) => {
    const [_key, { playerId }] = queryKey;
    const res = await fetchDataFromEndpoint(`players/${playerId}/performance`, 'GET', {});

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