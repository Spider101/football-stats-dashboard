import { useMutation, useQueryClient } from 'react-query';

import { createNewPlayer } from '../clients/PlayerClient';
import { useUserAuth } from '../context/authProvider';
import { useCurrentClub } from '../context/clubProvider';
import { queryKeys } from '../constants';

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
            const { technicalAttributes, mentalAttributes, physicalAttributes, role, ...rest } = newPlayerData;
            const attributes = [
                ...Object.entries(technicalAttributes).map(([key, value]) => ({ name: key, value })),
                ...Object.entries(physicalAttributes).map(([key, value]) => ({ name: key, value })),
                ...Object.entries(mentalAttributes).map(([key, value]) => ({ name: key, value }))
            ];
            try {
                await mutateAsync({
                    newPlayerData: { attributes, roles: [role], ...rest },
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