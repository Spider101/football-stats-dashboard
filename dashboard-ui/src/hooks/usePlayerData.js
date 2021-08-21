import { useQuery } from 'react-query';

import { fetchPlayerData } from '../clients/DashboardClient';
import { useUserAuth } from '../context/authProvider';

export default function(queryKey, playerId) {
    const { authData } = useUserAuth();

    const { isLoading, isIdle, data: playerData } = useQuery(
        [queryKey, { playerId, authData }],
        fetchPlayerData, {
            // don't run the query if playerId value is not a valid value
            enabled: playerId !== -1
        });

    return {
        isLoading,
        data: isLoading || isIdle ? {} : {
            playerMetadata: playerData.metadata,
            playerRoles: playerData.roles,
            playerOverall: {
                currentValue: playerData.ability.current,
                history: playerData.ability.history
            },
            playerAttributes: playerData.attributes
        }
    };
}