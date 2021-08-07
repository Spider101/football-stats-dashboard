import React from 'react';
import PropTypes from 'prop-types';
import faker from 'faker';
import { useQueryClient } from 'react-query';

import { authenticateUser, createUser } from '../clients/DashboardClient';
import { queryKeys } from '../utils';

const AuthContext = React.createContext();

function AuthContextProvider({ children }) {
    const existingAuthToken = JSON.parse(localStorage.getItem('auth-token'));
    const [authToken, setAuthToken] = React.useState(existingAuthToken);
    const queryClient = useQueryClient();

    const login = async ({ email, password }, setAuthToken) => {
        let authToken;
        let errorMessage = null;

        try {
            authToken = await authenticateUser({ username: email, password });
        } catch (err) {
            errorMessage = `${err.message}. Please create an account first.`;
        }

        if (errorMessage == null) {
            console.info('Persisting auth token in localStorage and Context: ' + authToken);

            // persist the auth data including the bearer token to localStorage and in context provider via state setter
            localStorage.setItem('auth-token', JSON.stringify(authToken));
            queryClient.invalidateQueries(queryKeys.USER_DATA);
            setAuthToken(authToken);
        }

        return errorMessage;
    };

    const createAccount = async newUserDetails => {
        const newUserData = {
            firstName: newUserDetails.firstName,
            lastName: newUserDetails.lastName,
            email: newUserDetails.email,
            password: newUserDetails.newPassword,
            authToken: faker.random.uuid()
        };

        let errorMessage = null;
        try {
            await createUser(newUserData);
        } catch (err) {
            errorMessage = `${err.message} Try logging in instead.`;
        }

        return errorMessage;
    };

    const logOut = () => {
        console.info('Logging user out ...');
        localStorage.removeItem('auth-token');
        setAuthToken(null);
    };

    const value = {
        authToken,
        setAuthToken,
        login,
        createAccount,
        logOut
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

AuthContextProvider.propTypes = {
    children: PropTypes.node
};

/**
 * wrapper method over useContext to handle undefined values
 * @returns a valid value that is stored in the context
 */
function useUserAuth() {
    const userAuthContext = React.useContext(AuthContext);

    if (userAuthContext === undefined) {
        throw new Error('useUserAuth must be used inside AuthContextProvider');
    }

    return userAuthContext;
}

export { useUserAuth, AuthContextProvider };