import MatchPerformanceView from '../views/MatchPerformanceView';

import { getMatchPerformanceBreakDown } from './utils/storyDataGenerators';

export default {
    component: MatchPerformanceView,
    title: 'Views/MatchPerformanceView',
    parameters: {
        docs: {
            description: {
                component: 'View containing all the information relevant to a player\'s performance in recent matches.'
            }
        }
    }
};

const Template = args => <MatchPerformanceView { ...args } />;
export const Default = Template.bind({});
Default.args = {
    playerPerformance: getMatchPerformanceBreakDown(5, 10)
};