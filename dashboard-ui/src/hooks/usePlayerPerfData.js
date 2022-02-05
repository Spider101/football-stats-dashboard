import { useQuery } from 'react-query';

import { fetchPlayerPerformanceData } from '../clients/PlayerClient';
import { useUserAuth } from '../context/authProvider';
import { queryKeys } from '../constants';

export default function (playerId) {
    const { authData } = useUserAuth();
    return useQuery([queryKeys.PLAYER_PERFORMANCE_DATA, playerId], fetchPlayerPerformanceData, { meta: { authData } });
}