import React from 'react';
import PropTypes from 'prop-types';
import { useQueryClient } from 'react-query';

import { authenticateUser, createUser } from '../clients/AuthClient';
import { queryKeys } from '../utils';

const AuthContext = React.createContext();

const authDataKey = 'auth-data';
function AuthContextProvider({ children }) {
    const existingAuthData = JSON.parse(localStorage.getItem(authDataKey));
    const [authData, setAuthData] = React.useState(existingAuthData);
    const queryClient = useQueryClient();

    const login = async ({ email, password }, setAuthData) => {
        let authData;
        let errorMessage = null;

        try {
            authData = await authenticateUser({ username: email, password });
        } catch (err) {
            errorMessage = err.message;
        }

        if (errorMessage == null) {
            console.info('Persisting auth data in localStorage and Context: ' + authData);

            // persist the auth data including the bearer token to localStorage and in context provider via state setter
            localStorage.setItem(authDataKey, JSON.stringify(authData));
            queryClient.invalidateQueries(queryKeys.USER_DATA);
            setAuthData(authData);
        }

        return errorMessage;
    };

    const createAccount = async newUserDetails => {
        const newUserData = {
            firstName: newUserDetails.firstName,
            lastName: newUserDetails.lastName,
            email: newUserDetails.email,
            password: newUserDetails.newPassword
        };

        let errorMessage = null;
        try {
            await createUser(newUserData);
        } catch (err) {
            errorMessage = err.message;
        }

        return errorMessage;
    };

    const logOut = () => {
        console.info('Logging user out ...');
        localStorage.removeItem(authDataKey);
        setAuthData(null);
    };

    const value = {
        authData,
        setAuthData,
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