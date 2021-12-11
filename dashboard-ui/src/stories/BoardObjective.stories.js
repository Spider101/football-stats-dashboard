import { action } from '@storybook/addon-actions';

import BoardObjective from '../components/BoardObjective';
import { getBoardObjectives } from './utils/storyDataGenerators';

export default {
    component: BoardObjective,
    title: 'Components/ClubPageView/BoardObjective'
};

const Template = args => <BoardObjective {...args} />;

export const Default = Template.bind({});
Default.args = {
    objective: getBoardObjectives(1)[0],
    hasDivider: false,
    handleClickFn: action('Board Objective was clicked!')
};

export const Completed = Template.bind({});
Completed.args = {
    ...Default.args,
    objective: {
        ...Default.args.objective,
        isCompleted: true
    }
};

export const WithDivider = Template.bind({});
WithDivider.args = {
    ...Default.args,
    hasDivider: true
};