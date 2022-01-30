import ClubPageView from '../views/ClubPageView';

import { getClubData } from './utils/storyDataGenerators';

export default {
    component: ClubPageView,
    title: 'Views/ClubPageView',
    parameters: {
        docs: {
            description: {
                component: 'View representing what the user will see when landing on the __Home Page__ of the'
                + ' currently selected club. It is designed in the way of a dashboard, with important facets of the'
                + ' club encapsulated in the different tiles of the dashboard.'
            }
        }
    }
};

const Template = args => <ClubPageView {...args} />;
export const Default = Template.bind({});
Default.args = {
    club: getClubData()
};