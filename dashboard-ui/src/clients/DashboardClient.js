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

export const authenticateUser = async ({ username, password }) => {
    const userData = await fetchDataFromEndpoint(`users?email=${username}&password=${password}`);
    if (userData.length > 0) {
        const { authToken } = userData[0];
        return authToken;
    } else {
        throw new Error(`Unable to find account with email: ${username}!`)
    }
};

export const fetchUser = async ({ queryKey }) => {
    const [ _key, { authToken }] = queryKey;
    // TODO: adding authToken in query params to replicate server behavior in json-server
    // remove once integrated with backend
    const res = await fetch(`${baseUrl}users?authToken=${authToken}`, {
        method: 'GET',
        headers: { 'Authentication': `BEARER ${authToken}` }
    });
    if (res.ok) {
        const userData = await res.json();
        if (userData.length > 0) {
            return await userData[0];
        }
        throw new Error(`No user found with given auth token: ${authToken}`);
    }
};

export const createUser = async (newUserData) => {
    const { email } = newUserData;
    // check for existing user via email manually
    // TODO: remove this when integrating with backend as duplicate user validation is baked in
    const existingUser = await fetchDataFromEndpoint(`users?email=${email}`);
    if (existingUser.length > 0 && existingUser[0]) {
        throw new Error(`User with email address: ${email}, already exists!`);
    }

    const res = await fetch(`${baseUrl}users/`, {
        method: 'POST',
        body: JSON.stringify(newUserData),
        headers: { 'Content-Type': 'application/json' }
    });
    return await res.json();
};

async function fetchDataFromEndpoint(endpointFragment) {
    const res = await fetch(`${baseUrl}${endpointFragment}`);

    return await res.json();
}
