import { setupServer } from 'msw/node';

import { getUserHandlers, getClubHandlers } from './handlers';

const baseUrl = 'http://localhost' + `${process.env.REACT_APP_SERVER_ENDPOINT.replace(/\/$/, '')}`;

const handlers = [
    ...getUserHandlers(baseUrl, 'fakeUserId', true),
    ...getClubHandlers(baseUrl, 'fakeClubId'),
    ...getUserHandlers(baseUrl)
];
export const server = setupServer(...handlers);
