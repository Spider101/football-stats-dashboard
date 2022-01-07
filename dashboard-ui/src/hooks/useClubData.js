import { useQuery } from 'react-query';
import { fetchClubSummaries, fetchClub } from '../clients/ClubClient';
import { useUserAuth } from '../context/authProvider';
import { queryKeys } from '../utils';

export function useClubSummariesData() {
    const { authData } = useUserAuth();

    const { isLoading, data: clubSummariesData } = useQuery(queryKeys.ALL_CLUBS, fetchClubSummaries, {
        meta: { authData }
    });

    return {
        isLoading,
        data: isLoading ? {} : clubSummariesData
    };
}

export function useClubData(clubId) {
    const { authData } = useUserAuth();
    const { isLoading, data: clubData } = useQuery(['club', clubId], fetchClub, { meta: { authData }});

    return {
        isLoading,
        data: isLoading ? {} : clubData
    };
}