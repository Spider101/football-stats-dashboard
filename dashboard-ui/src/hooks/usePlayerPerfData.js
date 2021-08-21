import { useQuery } from 'react-query';

import { fetchPlayerPerformanceData } from '../clients/DashboardClient';
import { useUserAuth } from '../context/authProvider';
import { queryKeys } from '../utils';

export default function(playerId) {
    const { authData } = useUserAuth();
    return useQuery(
        [queryKeys.PLAYER_PERFORMANCE_DATA, { authData, playerId }],
        fetchPlayerPerformanceData
    );
}