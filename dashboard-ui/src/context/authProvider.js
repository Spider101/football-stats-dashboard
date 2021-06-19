import React from 'react';
import PropTypes from 'prop-types';

const AuthContext = React.createContext();

function AuthContextProvider({ children }) {
    const existingAuthToken = localStorage.getItem('auth-token');
    const [authToken, setAuthToken] = React.useState(existingAuthToken);

    const value = {
        authToken,
        setAuthToken
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