import { useQuery, useQueryClient } from 'react-query';

import { fetchSquadHubData } from '../clients/DashboardClient';
import { queryKeys } from '../utils';

export default function() {
    const queryClient = useQueryClient();
    const { isLoading, data } = useQuery(
        queryKeys.SQUAD_DATA,
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