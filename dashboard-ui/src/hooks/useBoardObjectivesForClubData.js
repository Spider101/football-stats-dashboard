import { useQuery } from 'react-query';
import { queryKeys } from '../constants';
import { useUserAuth } from '../context/authProvider';
import { useCurrentClub } from '../context/clubProvider';
import { fetchAllBoardObjectivesForClub } from '../clients/BoardObjectiveClient';

export default function() {
    const { authData } = useUserAuth();
    const { currentClubId } = useCurrentClub();

    const { isLoading, data: boardObjectivesForClubData } = useQuery(
        [queryKeys.ALL_BOARD_OBJECTIVES, currentClubId],
        fetchAllBoardObjectivesForClub,
        { meta: { authData } }
    );

    return {
        isLoading,
        data: isLoading ? [] : boardObjectivesForClubData
    };
}