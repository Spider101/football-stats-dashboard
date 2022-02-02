import { useQuery } from 'react-query';
import { fetchUser } from '../clients/AuthClient';
import { useUserAuth } from '../context/authProvider';
import { queryKeys } from '../constants';

export default function () {
    const { authData, logOut } = useUserAuth();

    const { isLoading, isSuccess, data } = useQuery(queryKeys.USER_DATA, fetchUser, {
        meta: { authData },
        retry: 0,
        staleTime: 1000 * 60 * 60 * 8,
        // don't run the query if authToken is not valid
        enabled: !!authData,
        onError: err => {
            if (err) {
                logOut();
            }
        }
    });

    return {
        isLoading,
        isLoggedIn: isSuccess && data,
        userData: isLoading || !isSuccess ? null : data
    };
}