import { render, screen } from '@testing-library/react';

import { Default, NoClubs } from '../stories/HomePageView.stories';
import { MemoryRouter } from 'react-router';

const noClubViewText = 'No clubs have been created yet! Please create a club to proceed.';
it('should render the club data passed in', () => {
    render(
        <MemoryRouter>
            <Default {...Default.args} />
        </MemoryRouter>
    );

    expect(screen.queryByText(noClubViewText)).not.toBeInTheDocument();
    Default.args.clubs.map(club => {
        const clubItem = screen.getByRole('button', { name: club.name });
        expect(clubItem).toHaveAttribute('href', `/club/${club.id}`);
    });
});

it('should render helpful text when no club data is passed in', () => {
    render(
        <MemoryRouter initialEntries={['/']}>
            <NoClubs {...NoClubs.args} />
        </MemoryRouter>
    );
    expect(screen.queryByText(noClubViewText)).toBeInTheDocument();
});

it('should always render add club widget', () => {
    const { rerender, container } = render(
        <MemoryRouter initialEntries={['/']}>
            <NoClubs {...NoClubs.args} />
        </MemoryRouter>
    );
    expect(screen.queryByLabelText('add')).toBeInTheDocument();

    // taking snapshot here mainly to track any breaking changes due to add club widget
    expect(container).toMatchSnapshot();

    // not taking snapshot here as club props are dynamically generated for test
    rerender(
        <MemoryRouter initialEntries={['/']}>
            <Default {...Default.args} />
        </MemoryRouter>
    );
    expect(screen.queryByLabelText('add')).toBeInTheDocument();
});