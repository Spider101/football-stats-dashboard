import PlayerProgressionCharts from '../widgets/PlayerProgressionCharts';
import { Default as AttributeProgressChart } from './AttributeProgressChart.stories';
import { Default as AbilityProgressChart } from './AbilityProgressChart.stories';

export default {
    component: PlayerProgressionCharts,
    title: 'Widgets/PlayerProgressionView/PlayerProgressionCharts',
    parameters: {
        docs: {
            description: {
                component: 'Widget for displaying a player\'s progress in their attributes over time. It consists of'
                + ' a tabbed view with charts representing the _ability_ and _individual_ attribute progress.'
            }
        }
    },
    argTypes: {
        playerAttributeProgressData: {
            name: 'Attribute Progression Data',
            control: { type: 'object' }
        },
        playerAbilityProgressData: {
            name: 'Ability Progression Data',
            control: { type: 'object' }
        }
    }
};

const Template = args => <PlayerProgressionCharts { ...args } />;

export const Default = Template.bind({});
Default.args = {
    playerAttributeProgressData: AttributeProgressChart.args,
    playerAbilityProgressData: AbilityProgressChart.args
};