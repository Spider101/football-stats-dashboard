import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import { Default, NoPlayers } from '../stories/SquadHubView.stories';
import { snapshotFriendlyRender } from '../testUtils';

it('should render the player data when passed in', () => {
    // execute
    render(
        <MemoryRouter>
            <Default {...Default.args} />
        </MemoryRouter>
    );
    const rows = screen.getAllByRole('row');
    const columnHeaders = screen.getAllByRole('columnheader');

    // assert
    const { players } = Default.args;
    // 1 extra row for the table header
    expect(rows.length).toBe(players.length + 1);

    const playerFields = Object.keys(players[0]);
    // one of the fields is `id` which is not used in the table
    expect(columnHeaders.length).toBe(playerFields.length - 1);
});

it('should always render add player widget', () => {
    // taking snapshot here mainly to track any breaking changes due to add player widget
    // not passing any player data to avoid snapshot changing due to randomly generated data
    const { container } = snapshotFriendlyRender(
        <MemoryRouter>
            <NoPlayers {...NoPlayers.args} />
        </MemoryRouter>
    );

    expect(screen.queryByLabelText('add')).toBeInTheDocument();
    expect(container).toMatchSnapshot();
});