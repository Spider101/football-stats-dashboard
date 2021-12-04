import PlayerComparison from '../widgets/PlayerComparison';
import { getAttributeNamesList, getPlayerData } from './utils/storyDataGenerators';

export default {
    component: PlayerComparison,
    title: 'Widgets/PlayerComparisonView/PlayerComparison',
    parameters: {
        docs: {
            description: {
                component: 'Widget containing tools for comparing the _attributes_ of two players. It combines'
                + ' components like `AttributeComparisonTable` and `AttributeComparisonPolarPlot` in a tabbed view'
                + ' to achieve this.'
            }
        }
    }
};

const Template = args => <PlayerComparison { ...args } />;

export const Default = Template.bind({});
Default.args = {
    players: [
        getPlayerData(getAttributeNamesList(3 * 10)),
        getPlayerData(getAttributeNamesList(3 * 10))
    ]
};

export const SinglePlayer = Template.bind({});
SinglePlayer.args = {
    players: [ getPlayerData(getAttributeNamesList(3 * 10)) ]
};