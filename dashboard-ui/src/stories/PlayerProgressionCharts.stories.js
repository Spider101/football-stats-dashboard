import PlayerProgressionCharts from '../widgets/PlayerProgressionCharts';
import { Default as AttributeProgressChart } from './AttributeProgressChart.stories';
import { Default as OverallProgressChart } from './OverallProgressChart.stories';

export default {
    component: PlayerProgressionCharts,
    title: 'Widgets/PlayerProgressionView/PlayerProgressionCharts',
    parameters: {
        docs: {
            description: {
                component: 'Widget for displaying a player\'s progress in their attributes over time. It consists of'
                + ' a tabbed view with charts representing the _overall_ and _individual_ attribute progress.'
            }
        }
    },
    argTypes: {
        playerAttributeProgressData: {
            name: 'Attribute Progression Data',
            control: { type: 'object' }
        },
        playerOverallProgressData: {
            name: 'Overall Progression Data',
            control: { type: 'object' }
        }
    }
};

const Template = args => <PlayerProgressionCharts { ...args } />;

export const Default = Template.bind({});
Default.args = {
    playerAttributeProgressData: AttributeProgressChart.args,
    playerOverallProgressData: OverallProgressChart.args
};