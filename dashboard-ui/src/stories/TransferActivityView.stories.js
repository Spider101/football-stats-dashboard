import React from 'react';
import TransferActivityView from '../views/TransferActivityView';
import { getTransferActivityData } from './utils/storyDataGenerators';

export default {
    component: TransferActivityView,
    title: 'Views/TranferActivityView'
};

const Template = args => <TransferActivityView { ...args } />;
export const Default = Template.bind({});
Default.args = {
    ...getTransferActivityData(10)
};