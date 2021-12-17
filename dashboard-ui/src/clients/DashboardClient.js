import fetchDataFromEndpoint from './utils';
import { httpStatus } from '../utils';

export const createNewPlayer = async ({ newPlayerData, authToken }) => {
    const res = await fetchDataFromEndpoint('player', 'POST', { Authorization: `BEARER ${authToken}`}, newPlayerData);
    if (res.ok) {
        return await res.json();
    } else if (res.status === httpStatus.BAD_STATUS) {
        const { message: errorMessage } = await res.json();
        throw new Error(errorMessage);
    }
};

export const fetchPlayerData = async ({ queryKey, meta: { authData } }) => {
    const playerId = queryKey[1];
    const res = await fetchDataFromEndpoint(`players/${playerId}`, 'GET', { Authorization: `BEARER ${authData.id}` });
    return await res.json();
};

export const fetchPlayerPerformanceData = async ({ queryKey, meta: { authData } }) => {
    const playerId = queryKey[1];
    const competitionId = '8c853fa8-e4ab-47a5-98c2-fe01a15c29d2';
    const res = await fetchDataFromEndpoint(
        `match-performance/lookup/${playerId}?competitionId=${competitionId}`,
        'GET',
        {
            Authorization: `BEARER ${authData.id}`
        }
    );

    const playerPerformanceData = await res.json();
    return playerPerformanceData.map(performanceData =>
        Object.keys(performanceData)
            .filter(key => key !== 'id' && key !== 'playerId')
            .reduce((result, key) => {
                if (key === 'competitionId') {
                    result['id'] = performanceData[key];
                    return result;
                } else if (key === 'matchRating') {
                    result['matchRatingHistory'] = performanceData[key].history;
                    return result;
                }
                result[key] = performanceData[key];
                return result;
            }, {})
    );
};