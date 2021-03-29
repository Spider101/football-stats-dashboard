// TODO: change this when backend is ready
const baseUrl = 'http://localhost:4000/';

export const savePlayerProgressData = async updatedPlayerData => {
    const playerId = updatedPlayerData.id;
    const res = await fetch(`${baseUrl}players/${playerId}`, {
        method: 'PUT',
        body: JSON.stringify(updatedPlayerData),
        headers: {
            'Content-Type': 'application/json',
        }
    });

    return await res.json();
};

export const addTransferData = async newTransferData => {
    const res = await fetch(`${baseUrl}transfers`, {
        method: 'POST',
        body: JSON.stringify(newTransferData),
        headers: {
            'Content-Type': 'application/json'
        }
    });

    return await res.json();
};

export const fetchSquadHubData = async () => {
    return await fetchDataFromEndpoint('squadPlayers');
};

export const fetchPlayerData = async ({ queryKey }) => {
    const [ _key, playerId ] = queryKey;
    return await fetchDataFromEndpoint(`players/${playerId}`);
};

export const fetchPlayerPerformanceData = async ({ queryKey }) => {
    const [ _key, { playerId } ] = queryKey;
    let playerPerformanceData = await fetchDataFromEndpoint(`players/${playerId}/performance`);

    // TODO: don't do this because we can't do PUT calls on this data without `id` field
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

// TODO: update this to fetch transfers by club
export const fetchTransferActivityData = async () => {
    return await fetchDataFromEndpoint('transfers');
}

async function fetchDataFromEndpoint(endpointFragment) {
    const res = await fetch(`${baseUrl}${endpointFragment}`);

    return await res.json();
}
