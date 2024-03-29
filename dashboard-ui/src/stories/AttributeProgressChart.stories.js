import AttributeProgressChart from '../components/AttributeProgressChart';

import { getPlayerProgressionData, MAX_ATTR_VALUE } from './utils/storyDataGenerators';

export default {
    component: AttributeProgressChart,
    title: 'Components/PlayerProgressionView/AttributeProgressChart',
    excludeStories: /.*Data$/,
    argTypes: {
        attributeData: {
            name: 'Attribute Progression Data',
            control: { type: 'object' }
        }
    },
    parameters: {
        docs: {
            description: {
                component: 'UI Component for displaying a line chart with each line tracking the historical data of the'
                + ' attribute which is passed into the component.'
            }
        }
    }
};

const Template = args => <AttributeProgressChart { ...args } />;
export const MultipleDataPoints = Template.bind({});
MultipleDataPoints.args = {
    attributeData: getPlayerProgressionData(10, MAX_ATTR_VALUE)
};

export const SingleDataPoint = Template.bind({});
SingleDataPoint.args = {
    attributeData: getPlayerProgressionData(10, MAX_ATTR_VALUE, 1)
};