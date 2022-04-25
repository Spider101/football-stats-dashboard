import { useMutation, useQueryClient } from 'react-query';
import { createNewBoardObjective } from '../clients/BoardObjectiveClient';
import { queryKeys } from '../constants';
import { useUserAuth } from '../context/authProvider';
import { useCurrentClub } from '../context/clubProvider';

export default function() {
    const { authData } = useUserAuth();
    const { currentClubId } = useCurrentClub();
    const queryClient = useQueryClient();

    const { mutateAsync } = useMutation(createNewBoardObjective, {
        onSuccess: () => {
            queryClient.invalidateQueries(queryKeys.ALL_BOARD_OBJECTIVES);
        }
    });

    return {
        addNewBoardObjectiveAction: async newBoardObjectiveData => {
            try {
                await mutateAsync({
                    newBoardObjectiveData: {
                        ...newBoardObjectiveData,
                        isCompleted: false
                    },
                    clubId: currentClubId,
                    authToken: authData.id
                });
            } catch (err) {
                if (err instanceof Error) {
                    return err.message;
                }
            }
            return null;
        }
    };
}