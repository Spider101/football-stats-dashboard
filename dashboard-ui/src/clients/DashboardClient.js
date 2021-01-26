// TODO: change this when backend is ready
const baseUrl = 'http://localhost:4000/';


export const getSquadHubData = async () => {
    return await fetchDataFromEndpoint('squadPlayers');
};

export const getPlayerData = async (playerId) => {
    return await fetchDataFromEndpoint(`players/${playerId}`);
};

export const getPlayerPerformanceData = async (playerId) => {
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

}

async function fetchDataFromEndpoint(endpointFragment) {
    const res = await fetch(`${baseUrl}${endpointFragment}`);

    return await res.json();
}
