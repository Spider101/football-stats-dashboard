import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { AuthContextProvider } from '../context/authProvider';
import { useCurrentClub } from '../context/clubProvider';
import { createQueryWrapper } from '../testUtils';
import Player from './Player';

jest.mock('../context/clubProvider.js', () => ({
    ...jest.requireActual('../context/clubProvider.js'),
    useCurrentClub: jest.fn()
}));

it('should render player progression view by default', async () => {
    // setup
    render(
        <MemoryRouter>
            {createQueryWrapper(
                <AuthContextProvider>
                    <Player />
                </AuthContextProvider>
            )}
        </MemoryRouter>
    );

    // execute
    const links = await screen.findAllByRole('link');
    const chartTabs = await screen.findAllByRole('tab');
    const chartTabNames = chartTabs.map(chartTab => chartTab.textContent.trim());
    const playerProgressLink = await screen.findByRole('link', { name: /player progress/i });

    // assert
    expect(links.length).toBe(3);
    expect(chartTabs.length).toBe(2);
    expect(chartTabNames.sort()).toEqual(['Attribute Progress', 'Ability Progress'].sort());
    expect(screen.queryByRole('table')).toBeInTheDocument();

    // cannot use the toHaveClass matcher directly because mui adds dynamic prefixes to the classname
    expect(playerProgressLink.className).toContain('selectedPage');
});

it('should render player comparison view when navigating to /compare page', async () => {
    // setup
    useCurrentClub.mockReturnValue({ currentClubId: 'fakeClubId' });

    render(
        <MemoryRouter>
            {createQueryWrapper(
                <AuthContextProvider>
                    <Player />
                </AuthContextProvider>
            )}
        </MemoryRouter>
    );

    // execute
    const playerComparisonLink = await screen.findByRole('link', { name: /player comparison/i});
    userEvent.click(playerComparisonLink);
    const chartTabs = await screen.findAllByRole('tab');
    const chartTabNames = chartTabs.map(chartTab => chartTab.textContent.trim());

    // assert
    expect(playerComparisonLink.className).toContain('selectedPage');
    expect(chartTabs.length).toBe(2);
    expect(chartTabNames.sort()).toEqual(['Overview', 'Attributes'].sort());
});