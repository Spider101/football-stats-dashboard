import { createContext, useState, useContext } from 'react';
import PropTypes from 'prop-types';
import { useQueryClient } from 'react-query';
import { useHistory } from 'react-router-dom';

import { authenticateUser, createUser } from '../clients/AuthClient';
import { AUTH_DATA_LS_KEY, queryKeys } from '../constants';

const AuthContext = createContext();

function AuthContextProvider({ children }) {
    const history = useHistory();
    const existingAuthData = JSON.parse(localStorage.getItem(AUTH_DATA_LS_KEY));
    const [authData, setAuthData] = useState(existingAuthData);
    const queryClient = useQueryClient();

    const isUserLoggedIn = () => !!authData;

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
            localStorage.setItem(AUTH_DATA_LS_KEY, JSON.stringify(authData));
            queryClient.invalidateQueries(queryKeys.USER_DATA);
            setAuthData(authData);
            history.push('/');
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

        // redirect to home page (login form) after removing auth data from localStorage and state
        localStorage.removeItem(AUTH_DATA_LS_KEY);
        setAuthData(null);
        history.push('/');

    };

    const value = {
        authData,
        setAuthData,
        isUserLoggedIn,
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
    const userAuthContext = useContext(AuthContext);

    if (userAuthContext === undefined) {
        throw new Error('useUserAuth must be used inside AuthContextProvider');
    }

    return userAuthContext;
}

export { useUserAuth, AuthContextProvider };