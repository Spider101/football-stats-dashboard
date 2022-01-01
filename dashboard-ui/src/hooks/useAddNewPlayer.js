import { useMutation, useQueryClient } from 'react-query';

import { createNewPlayer } from '../clients/DashboardClient';
import { useUserAuth } from '../context/authProvider';
import { useCurrentClub } from '../context/clubProvider';
import { queryKeys } from '../utils';

export default function () {
    const { authData } = useUserAuth();
    const { currentClubId } = useCurrentClub();
    const queryClient = useQueryClient();

    const { mutateAsync } = useMutation(createNewPlayer, {
        onSuccess: () => {
            queryClient.invalidateQueries(queryKeys.SQUAD_DATA);
        }
    });

    return {
        addNewPlayerAction: async newPlayerData => {
            try {
                await mutateAsync({ newPlayerData, clubId: currentClubId, authToken: authData.id });
            } catch (err) {
                if (err instanceof Error) {
                    return err.message;
                }
            }
            return null;
        }
    };
}