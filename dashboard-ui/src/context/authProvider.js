import React from 'react';
import PropTypes from 'prop-types';

const AuthContext = React.createContext();

function AuthContextProvider({ children }) {
    const [currentUser, setCurrentUser] = React.useState();

    const value = {
        currentUser
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

AuthContextProvider.propTypes = {
    children: PropTypes.node
};

function useUserAuth() {
    const userAuth = React.useContext(AuthContext);

    if (userAuth === undefined) {
        throw new Error('useUserAuth must be used inside AuthContextProvider');
    }

    return userAuth;
}

export { useUserAuth, AuthContextProvider };