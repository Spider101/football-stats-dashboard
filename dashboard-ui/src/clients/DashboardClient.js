const baseUrl = process.env.REACT_APP_SERVER_ENDPOINT;
export const fetchSquadHubData = async () => {
    return await fetchDataFromEndpoint('squadPlayers');
};

export const fetchPlayerData = async ({ queryKey }) => {
    const [_key, { playerId }] = queryKey;
    return await fetchDataFromEndpoint(`players/${playerId}`);
};

export const fetchPlayerPerformanceData = async ({ queryKey }) => {
    const [_key, { playerId }] = queryKey;
    let playerPerformanceData = await fetchDataFromEndpoint(`players/${playerId}/performance`);

    playerPerformanceData = playerPerformanceData.map(performanceData => {
        return Object.keys(performanceData)
            .filter(key => key !== 'id' && key !== 'playerId')
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
    const res = await fetch(`${baseUrl}/users/authenticate`, {
        method: 'POST',
        body: JSON.stringify({
            email: username,
            password
        }),
        headers: { 'Content-Type': 'application/json' }
    });
    if (res.ok) {
        // auth data containing bearer token, userId and refresh information
        return await res.json();
    } else {
        throw new Error(`Unable to find account with email: ${username}!`);
    }
};

export const fetchUser = async ({ queryKey }) => {
    const [_key, { authToken }] = queryKey;
    const userEndpoint =
        process.env.NODE_ENV === 'development'
            ? `${baseUrl}/users/${authToken.userId}`
            : `${baseUrl}/users?authToken=${authToken}`;
    const res = await fetch(userEndpoint, {
        method: 'GET',
        headers: { Authentication: `BEARER ${authToken.id}` }
    });
    if (res.ok) {
        // user data containing userId, email, firstName, lastName and encrypted password
        return await res.json();
    } else {
        throw new Error(`No user found with given auth token: ${authToken}`);
    }
};

export const createUser = async newUserData => {
    const { email } = newUserData;

    if (process.env.NODE_ENV !== 'development') {
        // check for existing user via email manually
        // not needed when integrated with backend as duplicate user validation is baked in
        const existingUser = await fetchDataFromEndpoint(`users?email=${email}`);
        if (existingUser.length > 0 && existingUser[0]) {
            throw new Error(`User with email address: ${email}, already exists!`);
        }
    }

    const res = await fetch(`${baseUrl}/users`, {
        method: 'POST',
        body: JSON.stringify(newUserData),
        headers: { 'Content-Type': 'application/json' }
    });
    if (res.ok) {
        return await res.json();
    } else if (res.status === 409) {
        throw new Error(`User with email address: ${email}, already exists!`);
    } else {
        throw new Error(`Something went wrong in creating new user! Please try again`);
    }
};

// TODO: refactor this to accommodate headers and other rest verbs
async function fetchDataFromEndpoint(endpointFragment) {
    const res = await fetch(`${baseUrl}/${endpointFragment}`);

    return await res.json();
}
