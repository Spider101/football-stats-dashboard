import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import {
    Default as PlayerComparisonView,
    SinglePlayer as SinglePlayerView
} from '../stories/PlayerComparisonView.stories';
import { Default as CardWithFilter } from '../stories/CardWithFilter.stories';
import { playerAttributes } from '../constants';

it('renders single player view successfully', () => {
    // setup
    const {
        basePlayer: { playerMetadata }
    } = SinglePlayerView.args;

    // execute
    render(<SinglePlayerView {...SinglePlayerView.args} />);
    const images = screen.getAllByRole('img');
    const chartTabs = screen.getAllByRole('tab');
    const chartTabNames = chartTabs.map(chartTab => chartTab.textContent.trim());

    // assert
    expect(screen.queryByText(playerMetadata.name)).toBeInTheDocument();
    expect(screen.queryByText(playerMetadata.club)).toBeInTheDocument();
    expect(screen.queryByText(playerMetadata.country)).toBeInTheDocument();

    // two avatar images for club and country logo
    expect(images.length).toBe(2);
    expect(images.some(image => image.src === playerMetadata.countryLogo)).toBeTruthy();
    // clubLogo is a filekey so the image src would contain it instead of a hard equals
    expect(images.some(image => image.src.includes(playerMetadata.clubLogo))).toBeTruthy();

    expect(chartTabs.length).toBe(2);
    expect(chartTabNames.sort()).toEqual(['Overview', 'Attributes'].sort());

    // verify player filter is present
    expect(screen.queryByRole('button', { name: 'Players' })).toBeInTheDocument();
});

it('renders player comparison view successfully', () => {
    // setup
    const {
        comparedPlayer: { playerMetadata }
    } = PlayerComparisonView.args;

    // execute
    render(<PlayerComparisonView {...PlayerComparisonView.args} />);
    const images = screen.getAllByRole('img');

    // assert
    expect(screen.queryByText(playerMetadata.name)).toBeInTheDocument();
    expect(screen.queryByText(playerMetadata.club)).toBeInTheDocument();
    expect(screen.queryByText(playerMetadata.country)).toBeInTheDocument();

    // 4 avatar images (2 each for compared and base player) for club and country logo
    expect(images.length).toBe(4);
    // already asserted that the base player's logos are present so just assert the compared player's data here
    expect(images.some(image => image.src === playerMetadata.countryLogo)).toBeTruthy();
    // clubLogo is a filekey so the image src would contain it instead of a hard equals
    expect(images.some(image => image.src.includes(playerMetadata.clubLogo))).toBeTruthy();

    // player filter is replaced by the bio card of the player being compared
    // so verify the filter is not present anymore
    expect(screen.queryByRole('button', { name: 'Players' })).not.toBeInTheDocument();
});

it('displays players to compare against when picker is clicked', async () => {
    // setup
    const { allPossibleValues: filterOptions } = CardWithFilter.args;
    const playerNames = filterOptions.map(filterOption => filterOption.text);
    
    // execute
    render(<SinglePlayerView {...SinglePlayerView.args} />);
    userEvent.click(screen.getByRole('button', { name: 'Players' }));

    const playerOptions = await screen.findAllByRole('option');
    const playerOptionNames = playerOptions.map(playerOption => playerOption.textContent.trim());

    // assert
    // 'None' is one of the options by default
    expect(playerOptionNames.length).toBeGreaterThan(1);
    expect(playerOptionNames).toEqual(['None', ...playerNames]);
});

it('tab is switched to be active when clicked on', () => {
    // execute
    render(<PlayerComparisonView {...PlayerComparisonView.args} />);
    const currentTab = screen.getByRole('tab', { selected: true });
    userEvent.click(screen.getByRole('tab', { selected: false }));

    // assert
    expect(currentTab).toHaveTextContent('Overview');

    const newCurrentTab = screen.getByRole('tab', { selected: true });
    const newCurrentTabPanel = screen.getByRole('tabpanel');
    expect(newCurrentTab).toHaveTextContent('Attributes');
    expect(newCurrentTabPanel).toHaveAccessibleName('Player Attribute Category Comparison');
});

it('renders attribute comparison table successfully', () => {
    // setup
    const { basePlayer, comparedPlayer } = PlayerComparisonView.args;

    // execute
    render(<PlayerComparisonView {...PlayerComparisonView.args} />);
    userEvent.click(screen.getByRole('tab', { selected: false }));
    const columnHeaders = screen.getAllByRole('columnheader');
    const columnNames = columnHeaders.map(columnHeader => columnHeader.textContent.trim());
    const rows = screen.getAllByRole('row');
    const cells = screen.getAllByRole('cell').filter(cell => cell.textContent.trim());

    // assert
    expect(screen.queryByRole('table')).toBeInTheDocument();

    expect(columnNames.sort()).toEqual(playerAttributes.CATEGORIES.sort());

    // the heading row is also considered a row
    expect(rows.length).toBeGreaterThan(1);

    const maxNumberOfAttributes = Math.max(comparedPlayer.playerAttributes.length, basePlayer.playerAttributes.length);
    expect(cells.length).toBe(maxNumberOfAttributes);
});