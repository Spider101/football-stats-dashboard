import { useQuery } from 'react-query';

import { fetchTransferActivityData } from '../clients/DashboardClient';
import { queryKeys } from '../utils';

export default function() {
    const { isLoading, data } = useQuery(
        queryKeys.TRANSFER_ACTIVITY_DATA,
        fetchTransferActivityData
    );

    return {
        isLoading,
        data: isLoading ? [] : data
    };
}