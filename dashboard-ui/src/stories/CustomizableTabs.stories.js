import React from 'react';
import { action } from '@storybook/addon-actions';
import CustomizableTabs, { TabPanel } from '../components/CustomizableTabs';

export default {
    component: CustomizableTabs,
    title: 'Components/Globals/CustomizableTabs',
    excludeStories: /.*Data$/
};

export const Default = () => (
    <CustomizableTabs
        tabValue={0}
        onTabChange={ action('Tab Change Handler') }
        ariaLabel="Basic Tab"
        isFullWidth={ false }
        tabLabels={['Basic Tab']}
    >
        <TabPanel value={0} index={0}>
            Item 1
        </TabPanel>
    </CustomizableTabs>
);

export const FixedWidth = () => (
    <CustomizableTabs
        tabValue={0}
        onTabChange={ action('Tab Change Handler') }
        ariaLabel="Simple Fixed Width Tab"
        isFullWidth={ true }
        tabLabels={['Simple Fixed Width Tab']}
    >
        <TabPanel value={0} index={0}>
            Item 1
        </TabPanel>
    </CustomizableTabs>
);