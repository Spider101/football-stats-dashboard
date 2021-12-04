import { MemoryRouter } from 'react-router';

import { moraleIconsMap, nationalityFlagMap } from '../utils';

import SquadHubView from '../views/SquadHubView';

import { getSquadHubPlayerData } from './utils/storyDataGenerators';

export default {
    component: SquadHubView,
    title: 'Views/SquadHubView',
    parameters: {
        docs: {
            description: {
                component:
                    'View representing key information about the players in the squad with tools to focus on a' +
                    ' subset of the players or the associated information.'
            }
        }
    },
    decorators: [
        Story => (
            <MemoryRouter>
                <Story />
            </MemoryRouter>
        )
    ]
};

const nations = nationalityFlagMap.map(entity => entity.nationality);
const moraleList = moraleIconsMap.map(entity => entity.morale);

const Template = args => <SquadHubView {...args} />;

export const Default = Template.bind({});
Default.args = getSquadHubPlayerData(10, nations, moraleList);