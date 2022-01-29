import { render, screen } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import { Route, Router } from 'react-router-dom';

import { AuthContextProvider, useUserAuth } from '../context/authProvider';
import PrivateRoute from './PrivateRoute';
import { createQueryWrapper } from '../testUtils';

const PrivateComponent = () => <>Private!</>;
const NonPrivateComponent = () => <>Not Private!</>;

jest.mock('../context/authProvider.js', () => ({
    ...jest.requireActual('../context/authProvider.js'),
    useUserAuth: jest.fn()
}));

let history;
beforeEach(() => {
    jest.resetAllMocks();
    history = createMemoryHistory({initialEntries: ['/private']});
});

test('user is redirected to signIn page if not authenticated', () => {
    // setup
    const isUserLoggedIn = jest.fn().mockReturnValue(false);
    useUserAuth.mockReturnValue({ isUserLoggedIn });

    // execute
    render(
        <Router history={history}>
            {createQueryWrapper(
                <AuthContextProvider>
                    <PrivateRoute exact path='/private' component={PrivateComponent} />
                    <Route exact path='/auth/signIn' component={NonPrivateComponent} />
                </AuthContextProvider>
            )}
        </Router>
    );

    // assert
    expect(isUserLoggedIn).toBeCalled();
    expect(history.location.pathname).toBe('/auth/signIn');
    expect(screen.queryByText('Not Private!')).toBeInTheDocument();
    expect(screen.queryByText('Private!')).not.toBeInTheDocument();
});

test('user is not redirected to signIn page if authenticated', () => {
    // setup
    const isUserLoggedIn = jest.fn().mockReturnValue(true);
    useUserAuth.mockReturnValue({ isUserLoggedIn });

    // execute
    render(
        <Router history={history}>
            {createQueryWrapper(
                <AuthContextProvider>
                    <PrivateRoute exact path='/private' component={PrivateComponent} />
                    <Route exact path='/auth/signIn' component={NonPrivateComponent} />
                </AuthContextProvider>
            )}
        </Router>
    );

    // assert
    expect(isUserLoggedIn).toBeCalled();
    expect(history.location.pathname).not.toBe('/auth/signIn');
    expect(screen.queryByText('Not Private!')).not.toBeInTheDocument();
    expect(screen.queryByText('Private!')).toBeInTheDocument();
});