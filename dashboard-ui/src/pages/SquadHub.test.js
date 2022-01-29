import { render } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import { Router } from 'react-router-dom';

import { useCurrentClub } from '../context/clubProvider';
import { AuthContextProvider } from '../context/authProvider';
import SquadHub from './SquadHub';
import { createQueryWrapper } from '../testUtils';

jest.mock('../context/clubProvider.js', () => ({
    ...jest.requireActual('../context/clubProvider.js'),
    useCurrentClub: jest.fn()
}));

let history;
beforeEach(() => {
    history = createMemoryHistory({initialEntries: ['/squadHub']});
    jest.resetAllMocks();
});

it('redirects to home page if a club is not selected', () => {
    // setup
    useCurrentClub.mockReturnValue({ currentClubId: null });

    // execute
    render(
        <Router history={history}>
            {createQueryWrapper(
                <AuthContextProvider>
                    <SquadHub />
                </AuthContextProvider>
            )}
        </Router>
    );

    // assert
    expect(useCurrentClub).toBeCalled();
    expect(history.location.pathname).toBe('/');
});

it('does not redirect to home page if a club is selected', () => {
    // setup
    useCurrentClub.mockReturnValue({ currentClubId: 'fakeClubId' });

    // execute
    render(
        <Router history={history}>
            {createQueryWrapper(
                <AuthContextProvider>
                    <SquadHub />
                </AuthContextProvider>
            )}
        </Router>
    );

    // assert
    expect(useCurrentClub).toBeCalled();
    expect(history.location.pathname).not.toBe('/');
});