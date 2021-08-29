import React from 'react';
import { action } from '@storybook/addon-actions';
import CustomizableTabs, { TabPanel } from '../components/CustomizableTabs';

export default {
    component: CustomizableTabs,
    title: 'Components/Globals/CustomizableTabs'
};

const Template = args => (
    <CustomizableTabs {...args}>
        <TabPanel value={0} index={0}>
            Item 1
        </TabPanel>
    </CustomizableTabs>
);

export const Default = Template.bind({});
Default.args = {
    tabValue: 0,
    onTabChange: action('Tab Change Handler'),
    ariaLabel: 'Basic Tab',
    isFullWidth: true,
    tabLabels: ['Basic Tab']
};

export const FixedWidth = Template.bind({});
FixedWidth.args = {
    ...Default.args,
    isFullWidth: false
};