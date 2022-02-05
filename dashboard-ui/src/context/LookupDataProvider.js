import { createContext, useContext, useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { fetchCountryFlagUrls } from '../clients/LookupClient';

const LookupDataContext = createContext();

function LookupDataContextProvider({ children }) {
    const [countryFlagMetadata, setCountryFlagMetadata] = useState([]);

    const loadFlagUrls = async () => {
        const countryFlagUrls = await fetchCountryFlagUrls();
        setCountryFlagMetadata(countryFlagUrls);
    };

    useEffect(() => {
        loadFlagUrls();
    }, []);

    const value = {
        countryFlagMetadata
    };

    return (
        <LookupDataContext.Provider value={value}>
            {children}
        </LookupDataContext.Provider>
    );
}

LookupDataContextProvider.propTypes = {
    children: PropTypes.node
};

/**
 * wrapper method over useContext to handle undefined values
 * @returns a valid value that is stored in the context
 */
function useLookupData() {
    const lookupDataContext = useContext(LookupDataContext);

    if (lookupDataContext === undefined) {
        throw new Error('useLookupData must be used inside LookupDataContextProvider');
    }

    return lookupDataContext;
}

export { useLookupData, LookupDataContextProvider };