import { useMutation, useQueryClient } from 'react-query';
import { createNewClub } from '../clients/ClubClient';
import { useUserAuth } from '../context/authProvider';
import { queryKeys } from '../utils';

export default function () {
    const { authData } = useUserAuth();
    const queryClient = useQueryClient();

    const { mutateAsync } = useMutation(createNewClub, {
        onSuccess: () => {
            queryClient.invalidateQueries(queryKeys.ALL_CLUBS);
        }
    });

    return {
        addNewClubAction: async newClubData => {
            const { income, expenditure, managerFunds, ...rest } = newClubData;
            try {
                await mutateAsync({
                    newClubData: {
                        ...rest,
                        income: { current: Number(income) },
                        expenditure: { current: Number(expenditure) },
                        managerFunds: Number(managerFunds)
                    },
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