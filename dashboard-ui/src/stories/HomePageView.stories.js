import { MemoryRouter } from 'react-router-dom';

import { Success } from './AddClub.stories';
import HomePageView from '../views/HomePageView';
import { getClubSummaryData } from './utils/storyDataGenerators';

export default {
    component: HomePageView,
    title: 'Views/HomePageView',
    argTypes: {
        addClubWidget: { control: { disable: true } }
    },
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
    clubSummaries: getClubSummaryData(5),
    addClubWidget: <Success {...Success.args} />
};

export const NoClubs = Template.bind({});
NoClubs.args = {
    clubSummaries: [],
    addClubWidget: <Success {...Success.args} />
};