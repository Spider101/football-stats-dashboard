// jest-dom adds custom jest matchers for asserting on DOM nodes.
// allows you to do things like:
// expect(element).toHaveTextContent(/react/i)
// learn more: https://github.com/testing-library/jest-dom
import '@testing-library/jest-dom/extend-expect';
import { server, mockAuthData } from './mocks/server';
import { AUTH_DATA_LS_KEY } from './utils';

beforeAll(() => {
    localStorage.setItem(AUTH_DATA_LS_KEY, JSON.stringify(mockAuthData));
    server.listen({
        onUnhandledRequest: 'warn'
    });
});

afterEach(() => server.resetHandlers());

afterAll(() => server.close());