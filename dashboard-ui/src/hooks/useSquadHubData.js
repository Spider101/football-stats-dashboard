import { useQuery, useQueryClient } from 'react-query';

import { fetchSquadHubData } from '../clients/DashboardClient';
import { useUserAuth } from '../context/authProvider';
import { queryKeys } from '../utils';

export default function() {
    const queryClient = useQueryClient();
    const { authData } = useUserAuth();

    const { isLoading, data: squadPlayersData } = useQuery(queryKeys.SQUAD_DATA, fetchSquadHubData, {
        meta: { authData },
        initialData: () => queryClient.getQueryData(queryKeys.SQUAD_DATA),
        staleTime: 10 * 1000,
        initialDataUpdatedAt: queryClient.getQueryState(queryKeys.SQUAD_DATA)?.dataUpdatedAt
    });

    return {
        isLoading,
        data: isLoading ? [] : squadPlayersData.map(squadPlayerData => ({
            playerId: squadPlayerData.playerId,
            name: squadPlayerData.name,
            nationality: squadPlayerData.country,
            current_ability: squadPlayerData.currentAbility,
            role: squadPlayerData.role
        }))
    };
}