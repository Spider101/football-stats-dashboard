import fetchDataFromEndpoint from './utils';

export const fetchCountryFlagUrls = async () => {
    const res = await fetchDataFromEndpoint('lookup/countryFlags', 'GET', {});
    return await res.json();
};