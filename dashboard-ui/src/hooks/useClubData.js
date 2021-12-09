import { useQuery } from 'react-query';
import { fetchAllClubs, fetchClub } from '../clients/ClubClient';
import { useUserAuth } from '../context/authProvider';
import { queryKeys } from '../utils';

export function useAllClubData() {
    const { authData } = useUserAuth();

    const { isLoading, data: allClubsData } = useQuery(queryKeys.ALL_CLUBS, fetchAllClubs, { meta: { authData }});

    return {
        isLoading,
        data: isLoading ? {} : allClubsData
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