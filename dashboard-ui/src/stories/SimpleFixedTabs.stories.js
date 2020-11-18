import React from 'react';
import SimpleFixedTabs from '../components/SimpleFixedTabs';
import { action } from '@storybook/addon-actions';

export default {
    component: SimpleFixedTabs,
    title: 'Components/PlayerComparisonView/SimpleFixedTabs',
    excludeStories: /.*Data$/
};

export const Default = () => (
    <SimpleFixedTabs tabValue={0} onTabChange={ action('Tab Change Handler') }>
        <div>Nothing to see here. Move alongg!</div>
    </SimpleFixedTabs>
);