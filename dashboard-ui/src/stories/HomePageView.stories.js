import React from 'react';
import { MemoryRouter } from 'react-router';

import HomePageView from '../views/HomePageView';
import { getClubsData } from './utils/storyDataGenerators';

export default {
    component: HomePageView,
    title: 'Views/HomePageView',
    parameters: {
        docs: {
            description: {
                component: 'View representing what the user will see when landing on the __Home Page__ of the'
                + ' application.'
            }
        }
    },
    decorators: [
        Story => (
            <MemoryRouter>
                <Story />
            </MemoryRouter>
        )
    ]
};

const Template = args => <HomePageView {...args} />;

export const Default = Template.bind({});
Default.args = {
    clubs: getClubsData(5)
};

export const NoClubs = Template.bind({});
NoClubs.args = {
    clubs: []
};