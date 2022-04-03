import { setupServer } from 'msw/node';

import { getUserHandlers, getClubHandlers, getPlayerHandlers, getFileUploadHandlers } from './handlers';

const baseUrl = 'http://localhost' + `${process.env.REACT_APP_SERVER_ENDPOINT.replace(/\/$/, '')}`;
const handlers = [
    ...getUserHandlers(baseUrl, 'fakeUserId', true),
    ...getClubHandlers(baseUrl, 'fakeClubId'),
    ...getPlayerHandlers(baseUrl),
    ...getFileUploadHandlers(baseUrl)
    // TODO: 03/08/22 add lookup data handler when required by a test suite
];
export const server = setupServer(...handlers);
