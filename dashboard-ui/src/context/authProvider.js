import React from 'react';
import PropTypes from 'prop-types';
import faker from 'faker';

import { authenticateUser, createUser } from '../clients/DashboardClient';

const AuthContext = React.createContext();

function AuthContextProvider({ children }) {
    const existingAuthToken = localStorage.getItem('auth-token');
    const [authToken, setAuthToken] = React.useState(existingAuthToken);

    const login = async ({ email, password }, setAuthToken) => {
        let authToken;
        let errorMessage = null;
        try {
            authToken = await authenticateUser({ username: email, password });
        } catch (err) {
            errorMessage = `${err.message}. Please create an account first.`;
        }

        console.info('Persisting auth token in localStorage and Context: ' + authToken);

        // persist the token to localStorage and in context provider via state setter
        // TODO: figure out if we need to invalidate the `user` react-query
        localStorage.setItem('auth-token', authToken);
        setAuthToken(authToken);

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
            errorMessage = `${err.message}. Try logging in instead.`;
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