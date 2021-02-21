import { useQuery } from 'react-query';

import { fetchPlayerPerformanceData } from '../clients/DashboardClient';
import { queryKeys } from '../utils';

export default function(playerId) {
    return useQuery(
        [queryKeys.PLAYER_PERFORMANCE_DATA, { playerId }],
        fetchPlayerPerformanceData
    );
}