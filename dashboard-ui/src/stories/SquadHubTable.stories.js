import { MemoryRouter } from 'react-router-dom';

import SortableTable from '../widgets/SortableTable';

import { getSquadHubTableData } from './utils/storyDataGenerators';
import { MORALE_ICON_MAPPING } from '../constants';

export default {
    component: SortableTable,
    title: 'Widgets/SquadHubView/SquadHubTable',
    parameters: {
        docs: {
            description: {
                component: 'Widget to display all the players in the squad with some key information and attributes.'
            }
        }
    }
};

const Template = args => <SortableTable { ...args } />;

export const Default = Template.bind({});
Default.args = getSquadHubTableData(10, MORALE_ICON_MAPPING);


export const WithRouterLink = Template.bind({});
WithRouterLink.decorators = [Story => <MemoryRouter><Story/></MemoryRouter>];
WithRouterLink.args = getSquadHubTableData(10, MORALE_ICON_MAPPING, true);

export const EmptyRows = Template.bind({});
EmptyRows.args = {
    headers: Default.args.headers,
    rows: []
};

export const EmptyTable = Template.bind({});
EmptyTable.args = {
    headers: [],
    rows: []
};