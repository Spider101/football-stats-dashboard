import { useQuery } from 'react-query';

import { fetchPlayerData } from '../clients/DashboardClient';

export default function(queryKey, playerId) {
    const { isLoading, isIdle, data: playerData } = useQuery(
        [queryKey, parseInt(playerId)],
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