import { useQuery } from 'react-query';
import { fetchUser } from "../clients/AuthClient";
import { queryKeys } from '../utils';

export default function(authToken) {
    const { isLoading, isSuccess, data } = useQuery(
        [queryKeys.USER_DATA, { authToken }],
        fetchUser, {
            retry: 0,
            staleTime: 1000 * 60 * 60 * 8,
            // don't run the query if authToken is not valid
            enabled: !!authToken
        }
    );
    return {
        isLoading,
        isLoggedIn: isSuccess && data,
        userData: isLoading || !isSuccess ? null : data
    };
}