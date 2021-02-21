// TODO: change this when backend is ready
const baseUrl = 'http://localhost:4000/';


export const fetchSquadHubData = async () => {
    return await fetchDataFromEndpoint('squadPlayers');
};

export const fetchPlayerData = async ({ queryKey }) => {
    const [ _key, { playerId } ] = queryKey;
    return await fetchDataFromEndpoint(`players/${playerId}`);
};

export const fetchPlayerPerformanceData = async ({ queryKey }) => {
    const [ _key, { playerId } ] = queryKey;
    let playerPerformanceData = await fetchDataFromEndpoint(`players/${playerId}/performance`);

    playerPerformanceData = playerPerformanceData.map(performanceData => {
        return Object.keys(performanceData).filter(key => key !== 'id' && key !== 'playerId')
            .reduce((result, key) => {
                if (key === 'competitionId') {
                    result['id'] = performanceData[key];
                    return result;
                }
                result[key] = performanceData[key];
                return result;
            }, {});
    });

    return playerPerformanceData;

};

async function fetchDataFromEndpoint(endpointFragment) {
    const res = await fetch(`${baseUrl}${endpointFragment}`);

    return await res.json();
}
