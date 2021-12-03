import React from 'react';
import { render } from '@testing-library/react';

import { Default, NoClubs } from '../stories/HomePageView.stories';
import { MemoryRouter } from 'react-router';

const noClubViewText = 'No clubs have been created yet! Please create a club to proceed.';
it('should render the club data passed in', () => {
    const { getByRole, queryByText } = render(
        <MemoryRouter>
            <Default {...Default.args} />
        </MemoryRouter>
    );

    expect(queryByText(noClubViewText)).toBeNull();
    Default.args.clubs.map(club => {
        const clubItem = getByRole('button', { name: club.name });
        expect(clubItem).toHaveAttribute('href', `/club/${club.id}`);
    });
});

it('should render helpful text when no club data is passed in', () => {
    const { queryByText } = render(
        <MemoryRouter initialEntries={['/']}>
            <NoClubs {...NoClubs.args} />
        </MemoryRouter>
    );
    expect(queryByText(noClubViewText)).not.toBeNull();
});

it('should always render add club widget', () => {
    const { rerender, queryByLabelText, container } = render(
        <MemoryRouter initialEntries={['/']}>
            <NoClubs {...NoClubs.args} />
        </MemoryRouter>
    );
    expect(queryByLabelText('add')).not.toBeNull();

    // taking snapshot here mainly to track any breaking changes due to add club widget
    expect(container).toMatchSnapshot();

    // not taking snapshot here as club props are dynamically generated for test
    rerender(
        <MemoryRouter initialEntries={['/']}>
            <Default {...Default.args} />
        </MemoryRouter>
    );
    expect(queryByLabelText('add')).not.toBeNull();
});