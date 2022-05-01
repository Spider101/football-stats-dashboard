import { useQuery } from 'react-query';
import { queryKeys } from '../constants';
import { useUserAuth } from '../context/authProvider';
import { fetchAllBoardObjectivesForClub } from '../clients/BoardObjectiveClient';

export default function(clubId) {
    const { authData } = useUserAuth();

    const { isLoading, data: boardObjectivesForClubData } = useQuery(
        [queryKeys.ALL_BOARD_OBJECTIVES, clubId],
        fetchAllBoardObjectivesForClub,
        { meta: { authData } }
    );

    return {
        isLoading,
        data: isLoading ? [] : boardObjectivesForClubData
    };
}