import { MemoryRouter } from 'react-router-dom';

import { moraleIconsMap } from '../utils';

import SquadHubView from '../views/SquadHubView';
import { Success } from './AddPlayer.stories';

import { getSquadHubPlayerData } from './utils/storyDataGenerators';

export default {
    component: SquadHubView,
    title: 'Views/SquadHubView',
    argTypes: { addPlayerWidget: { control: { disable: true } } },
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

const moraleList = moraleIconsMap.map(entity => entity.morale);

const Template = args => <SquadHubView {...args} />;

export const Default = Template.bind({});
Default.args = {
    ...getSquadHubPlayerData(10, moraleList),
    addPlayerWidget: <Success {...Success.args} />
};

export const NoPlayers = Template.bind({});
NoPlayers.args = {
    ...Default.args,
    players: []
};