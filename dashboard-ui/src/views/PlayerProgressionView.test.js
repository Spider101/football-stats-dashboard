import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { faker } from '@faker-js/faker';

import { Default as PlayerProgressionView } from '../stories/PlayerProgressionView.stories';
import { playerAttributes } from '../constants';

it('renders successfully', () => {
    // setup
    const { playerMetadata, playerAttributes: playerAttributeData } = PlayerProgressionView.args;

    // execute
    render(<PlayerProgressionView {...PlayerProgressionView.args} />);
    const images = screen.getAllByRole('img');
    const attributeTableColumnHeaders = screen.getAllByRole('columnheader');
    const attributeTableColumnNames = attributeTableColumnHeaders.map(columnHeader => columnHeader.textContent.trim());

    const attributeRows = screen.getAllByRole('row');
    const attributeCells = screen.getAllByRole('cell').filter(cell => cell.textContent.trim());
    const chartTabs = screen.getAllByRole('tab');
    const chartTabNames = chartTabs.map(chartTab => chartTab.textContent.trim());

    // assert
    expect(screen.queryByText(playerMetadata.name)).toBeInTheDocument();
    expect(screen.queryByText(playerMetadata.club)).toBeInTheDocument();
    expect(screen.queryByText(playerMetadata.country)).toBeInTheDocument();

    // two avatar images for club and country logo
    expect(images.length).toBe(2);
    expect(images.some(image => image.src === playerMetadata.countryLogo)).toBeTruthy();
    expect(images.some(image => image.src === playerMetadata.clubLogo)).toBeTruthy();

    expect(attributeTableColumnNames.length).toBe(3);
    expect(attributeTableColumnNames.sort()).toEqual(playerAttributes.CATEGORIES.sort());

    // the heading row is also considered a row
    expect(attributeRows.length).toBeGreaterThan(1);

    expect(attributeCells.length).toBe(playerAttributeData.length);

    expect(chartTabs.length).toBe(2);
    expect(chartTabNames.sort()).toEqual(['Attribute Progress', 'Ability Progress'].sort());
});

it('displays roles when picker is clicked', async () => {
    // setup
    const { playerRoles } = PlayerProgressionView.args;
    const playerRoleNames = playerRoles.map(playerRole => playerRole.name);

    // execute
    render(<PlayerProgressionView {...PlayerProgressionView.args} />);

    userEvent.click(screen.getByRole('button', { name: 'None' }));

    const roleOptions = await screen.findAllByRole('option');
    const roleOptionNames = roleOptions.map(roleOption => roleOption.textContent.trim());

    // assert
    // 'None' is one of the options by default
    expect(roleOptions.length).toBeGreaterThan(1);
    expect(roleOptionNames).toEqual(['None', ...playerRoleNames]);
});

it('highlights associated attributes in table when role is selected', async () => {
    // setup
    const { playerRoles } = PlayerProgressionView.args;

    // execute
    render(<PlayerProgressionView {...PlayerProgressionView.args} />);
    userEvent.click(screen.getByRole('button', { name: 'None' }));
    const roleOptions = await screen.findAllByRole('option');
    const selectedRoleOption = faker.helpers.randomize(
        roleOptions.filter(option => option.textContent.trim() != 'None')
    );
    userEvent.selectOptions(screen.getByRole('listbox'), selectedRoleOption);

    // assert
    expect(roleOptions.length).toBe(playerRoles.length + 1);
    expect(screen.queryByRole('button', { name: selectedRoleOption.textContent.trim() })).toBeInTheDocument();
    const selectedRole = playerRoles.find(role => role.name === selectedRoleOption.textContent.trim());
    const highlightedCells = screen
        .getAllByRole('cell')
        .filter(cell => selectedRole.associatedAttributes.includes(cell.textContent.replace(/[0-9]/g, '')));
    highlightedCells.forEach(cell => {
        // the first child is the `div` with the applied styles
        expect(cell.firstChild.className).toContain('highlighted');
    });
});

it('tab is switched to be active when clicked on', () => {
    // execute
    render(<PlayerProgressionView {...PlayerProgressionView.args} />);
    const currentTab = screen.getByRole('tab', { selected: true });
    userEvent.click(screen.getByRole('tab', { selected: false }));

    // assert
    expect(currentTab).toHaveTextContent('Attribute Progress');

    const newCurrentTab = screen.getByRole('tab', { selected: true });
    const newCurrentTabPanel = screen.getByRole('tabpanel');
    expect(newCurrentTab).toHaveTextContent('Ability Progress');
    expect(newCurrentTabPanel).toHaveAccessibleName('Ability Progress Chart');
});