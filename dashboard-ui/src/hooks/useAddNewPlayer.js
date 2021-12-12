import { useMutation, useQueryClient } from 'react-query';

import { createNewPlayer } from '../clients/DashboardClient';
import { useUserAuth } from '../context/authProvider';
import { queryKeys } from '../utils';

export default function () {
    const { authData } = useUserAuth();
    const queryClient = useQueryClient();

    const { mutateAsync } = useMutation(createNewPlayer, {
        onSuccess: () => {
            queryClient.invalidateQueries(queryKeys.SQUAD_DATA);
        }
    });

    return {
        addNewPlayerAciton: async newPlayerData => {
            try {
                await mutateAsync({ newPlayerData, authToken: authData.id });
            } catch (err) {
                if (err instanceof Error) {
                    return err.message;
                }
            }
            return null;
        }
    };
}