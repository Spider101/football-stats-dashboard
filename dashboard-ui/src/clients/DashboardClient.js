// import fetch from 'fetch';

// TODO: change this when backend is ready
const baseUrl = 'http://localhost:3001';


export const getSquadHubData = async () => {
    const res = await fetch(`${baseUrl}/squadPlayers`);

    return await res.json();
};

export const getPlayerData = async (playerId) => {
    const res = await fetch(`${baseUrl}/players/${playerId}`);

    return await res.json();
};