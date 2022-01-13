import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import { Default, NoClubs } from '../stories/HomePageView.stories';
import { snapshotFriendlyRender } from '../testUtils';

const noClubViewText = 'No clubs have been created yet! Please create a club to proceed.';
it('should render the club data passed in', () => {
    render(
        <MemoryRouter>
            <Default {...Default.args} />
        </MemoryRouter>
    );

    expect(screen.queryByText(noClubViewText)).not.toBeInTheDocument();

    const { clubSummaries } = Default.args;

    const clubSummaryButtons = screen.getAllByRole('button', { name: 'Open' });
    expect(clubSummaryButtons.length).toBe(clubSummaries.length);
    clubSummaryButtons.forEach((clubSummaryButton, idx) => {
        // assuming ordering is maintained between the prop data and the rendered elements
        expect(clubSummaryButton).toHaveAttribute('href', `/club/${clubSummaries[idx].clubId}`);

        // fallback assertion if above one fails due to any ordering issues
        // expect(clubSummaryButton).toHaveAttribute('href', expect.stringContaining('/club/'));
    });

    const clubSummaryItems = screen.getAllByRole('listitem');
    expect(clubSummaryItems.length).toBe(clubSummaries.length);
    clubSummaryItems.forEach((clubSummaryItem, idx) => {
        // assuming ordering is maintained between the prop data and the rendered elements
        expect(clubSummaryItem).toHaveTextContent(clubSummaries[idx].name);
        expect(clubSummaryItem).toHaveTextContent(clubSummaries[idx].createdDate);
    });
});

it('should render helpful text when no club data is passed in', () => {
    render(
        <MemoryRouter>
            <NoClubs {...NoClubs.args} />
        </MemoryRouter>
    );
    expect(screen.queryByText(noClubViewText)).toBeInTheDocument();
});

it('should always render add club widget', () => {
    const { rerender, container } = snapshotFriendlyRender(
        <MemoryRouter>
            <NoClubs {...NoClubs.args} />
        </MemoryRouter>
    );
    expect(screen.queryByLabelText('add')).toBeInTheDocument();

    // taking snapshot here mainly to track any breaking changes due to add club widget
    expect(container).toMatchSnapshot();

    // not taking snapshot here as club props are dynamically generated for test
    rerender(
        <MemoryRouter>
            <Default {...Default.args} />
        </MemoryRouter>
    );
    expect(screen.queryByLabelText('add')).toBeInTheDocument();
});