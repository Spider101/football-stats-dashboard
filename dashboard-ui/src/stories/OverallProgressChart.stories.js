import OverallProgressChart from '../components/OverallProgressChart';
import { getPlayerOverallData } from './utils/storyDataGenerators';

export default {
    component: OverallProgressChart,
    title: 'Components/PlayerProgressionView/OverallProgressChart',
    excludeStories: /.*Data$/,
    argTypes: {
        overallData: {
            name: 'Overall Progression Data',
            control: { type: 'object' }
        }
    },
    parameters: {
        docs: {
            description: {
                component: 'UI Component for displaying a bar chart documenting a player\'s overall progress over the'
                + ' last _N_ months.'
            }
        }
    }
};

export const Default = (args) => <OverallProgressChart { ...args } />;

Default.args = {
    overallData: getPlayerOverallData()
};