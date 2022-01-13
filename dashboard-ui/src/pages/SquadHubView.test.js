import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import { Default, NoPlayers } from '../stories/SquadHubView.stories';

const getSanitizedProps = ({ players, ...rest }) => {
    return {
        ...rest,
        players: players.map(player => {
            // removing form because apex charts is not working with react-testing library for some reason
            // eslint-disable-next-line no-unused-vars
            const { form, ...rest } = player;
            return rest;
        })
    };
};

it('should render the player data when passed in', () => {
    render(
        <MemoryRouter>
            <Default {...getSanitizedProps(Default.args)} />
        </MemoryRouter>
    );

    const { players } = Default.args;
    const rows = screen.getAllByRole('row');
    // 1 extra row for the table header
    expect(rows.length).toEqual(players.length + 1);
});

it('should always render add player widget', () => {
    // taking snapshot here mainly to track any breaking changes due to add player widget
    // not passing any player data to avoid snapshot changing due to randomly generated data
    const { container } = render(
        <MemoryRouter>
            <NoPlayers {...NoPlayers.args} />
        </MemoryRouter>
    );

    expect(screen.queryByLabelText('add')).toBeInTheDocument();
    expect(container).toMatchSnapshot();
});