import makeRequestToEndpoint from './utils';
import { httpStatus } from '../constants';

export const createNewPlayer = async ({ newPlayerData, clubId, authToken }) => {
    const res = await makeRequestToEndpoint(
        'players',
        'POST',
        { Authorization: `BEARER ${authToken}` },
        { clubId, ...newPlayerData }
    );
    if (res.ok) {
        return await res.json();
    } else if (res.status === httpStatus.BAD_STATUS) {
        const { message: errorMessage } = await res.json();
        throw new Error(errorMessage);
    } else {
        throw new Error('Something went wrong when trying to create new player!');
    }
};

export const fetchPlayerData = async ({ queryKey, meta: { authData } }) => {
    const playerId = queryKey[1];
    const res = await makeRequestToEndpoint(`players/${playerId}`, 'GET', { Authorization: `BEARER ${authData.id}` });
    return await res.json();
};

export const fetchPlayerPerformanceData = async ({ queryKey, meta: { authData } }) => {
    const playerId = queryKey[1];
    // TODO: 05/02/22 replace hard coded value with value from queryKey when match performance page is ready
    const competitionId = '8c853fa8-e4ab-47a5-98c2-fe01a15c29d2';
    const res = await makeRequestToEndpoint(
        `match-performance/lookup/${playerId}?competitionId=${competitionId}`,
        'GET',
        { Authorization: `BEARER ${authData.id}` }
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