import makeRequestToEndpoint from './utils';

export const fetchCountryFlagUrls = async () => {
    const res = await makeRequestToEndpoint('lookup/countryFlags', 'GET', {});
    return await res.json();
};