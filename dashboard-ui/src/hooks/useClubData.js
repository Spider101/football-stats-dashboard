import { useQuery } from 'react-query';
import { fetchAllClubs } from '../clients/ClubClient';
import { useUserAuth } from '../context/authProvider';
import { queryKeys } from '../utils';

export default function () {
    const { authData } = useUserAuth();

    const { isLoading, data: allClubsData } = useQuery(queryKeys.ALL_CLUBS, fetchAllClubs, { meta: { authData }});

    return {
        isLoading,
        data: isLoading ? {} : allClubsData
    };
}
