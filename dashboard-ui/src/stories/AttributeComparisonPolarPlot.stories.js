import AttributeComparisonPolarPlot from '../components/AttributeComparisonPolarPlot';
import { faker } from '@faker-js/faker';

import { getAttrGroupData } from './utils/storyDataGenerators';

export default {
    component: AttributeComparisonPolarPlot,
    title: 'Components/PlayerComparisonView/AttributeComparisonPolarPlot',
    parameters: {
        docs: {
            description: {
                component: 'UI Component for displaying a _polar plot_ comparing the attributes of two players'
                + ' when grouped into specific categories.'
            }
        }
    }
};

const Template = args => <AttributeComparisonPolarPlot { ...args } />;

export const Default = Template.bind({});
Default.args = {
    playersWithAttributes: [{
        name: faker.name.lastName(),
        attributes: getAttrGroupData(5)
    }, {
        name: faker.name.lastName(),
        attributes: getAttrGroupData(5)
    }]
};

export const SinglePlayer = Template.bind({});
SinglePlayer.args = {
    playersWithAttributes: [{
        name: faker.name.lastName(),
        attributes: getAttrGroupData(5)
    }]
};