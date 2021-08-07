import { useQuery } from 'react-query';
import { fetchUser } from '../clients/AuthClient';
import { useUserAuth } from '../context/authProvider';
import { queryKeys } from '../utils';

export default function() {
    const { authData } = useUserAuth();

    const { isLoading, isSuccess, data } = useQuery(
        [queryKeys.USER_DATA, { authData }],
        fetchUser, {
            retry: 0,
            staleTime: 1000 * 60 * 60 * 8,
            // don't run the query if authToken is not valid
            enabled: !!authData
        }
    );
    return {
        isLoading,
        isLoggedIn: isSuccess && data,
        userData: isLoading || !isSuccess ? null : data
    };
}