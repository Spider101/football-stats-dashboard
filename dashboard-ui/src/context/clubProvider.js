import PropTypes from 'prop-types';
import { createContext, useContext, useState } from 'react';

const ClubContext = createContext();

function ClubContextProvider({ children }) {
    const [currentClubId, setCurrentClubId] = useState();
    const value = {
        currentClubId,
        setCurrentClubId
    };

    return (
        <ClubContext.Provider value={value}>
            {children}
        </ClubContext.Provider>
    );
}

ClubContextProvider.propTypes = {
    children: PropTypes.node
};

function useCurrentClub() {
    const currentClubContext = useContext(ClubContext);

    if (currentClubContext === undefined) {
        throw new Error('useCurrentClub must be used inside ClubContextProvider');
    }

    return currentClubContext;
}

export { useCurrentClub, ClubContextProvider };