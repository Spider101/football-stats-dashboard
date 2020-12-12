import React from 'react';
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
    }
};

export const Default = (args) => <AttributeProgressChart { ...args } />;

Default.args = {
    attributeData: getPlayerProgressionData(10, null, MAX_ATTR_VALUE)
};