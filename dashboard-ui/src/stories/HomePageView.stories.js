import React from 'react';
import HomePageView from '../views/HomePageView';

export default {
    component: HomePageView,
    title: 'Views/HomePageView',
    parameters: {
        docs: {
            description: {
                component: 'View representing what the user will see when landing on the __Home Page__ of the'
                + ' application. It is designed in the way of a dashboard, with important facets of the club'
                + ' encapsulated in the different tiles of the dashboard.'
            }
        }
    }
};

const Template = args => <HomePageView {...args} />;
export const Default = Template.bind({});