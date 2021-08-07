import { useQuery, useQueryClient } from 'react-query';

import { fetchSquadHubData } from '../clients/DashboardClient';
import { useUserAuth } from '../context/authProvider';
import { queryKeys } from '../utils';

export default function() {
    const queryClient = useQueryClient();
    const { authData } = useUserAuth();

    const { isLoading, data } = useQuery(
        [queryKeys.SQUAD_DATA, { authData }],
        fetchSquadHubData, {
            initialData: () => queryClient.getQueryData(queryKeys.SQUAD_DATA),
            staleTime: 10 * 1000,
            initialDataUpdatedAt: queryClient.getQueryState(queryKeys.SQUAD_DATA)?.dataUpdatedAt
        }
    );

    return {
        isLoading,
        data: isLoading ? [] : data
    };
}