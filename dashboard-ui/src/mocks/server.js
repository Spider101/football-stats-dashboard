import { setupServer } from 'msw/node';
import { getUserHandlers, getClubHandlers } from './handlers';

export const mockAuthData = {
    id: 'fakeAuthToken',
    userId: 'fakeUserId'
};

const handlers = [ ...getUserHandlers(mockAuthData.userId), ...getClubHandlers()];
export const server = setupServer(...handlers);