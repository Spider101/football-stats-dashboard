import BoardObjectives from '../widgets/BoardObjectives';
import { getBoardObjectives } from './utils/storyDataGenerators';

export default {
    component: BoardObjectives,
    title: 'Widgets/ClubPageView/BoardObjectives'
};

const Template = args => <BoardObjectives {...args} />;

export const Default = Template.bind({});
Default.args = {
    objectives: getBoardObjectives(4)
};

export const FullObjectives = Template.bind({});
FullObjectives.args = {
    objectives: getBoardObjectives(5)
};

export const NoObjectives = Template.bind({});
NoObjectives.args = {
    objectives: []
};