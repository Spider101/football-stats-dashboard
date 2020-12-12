import React from 'react';
import AttributeProgressChart from '../components/AttributeProgressChart';

import { getAttributeLineData } from './utils/storyDataGenerators';

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
    ...getAttributeLineData(10)
};