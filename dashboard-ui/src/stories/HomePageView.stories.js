import React from 'react';
import HomePageView from '../views/HomePageView';

export default {
    component: HomePageView,
    title: 'Views/HomePageView'
};

const Template = args => <HomePageView {...args} />;
export const Default = Template.bind({});
Default.args = {};