import CustomSlider from '../components/CustomSlider';
import { action } from '@storybook/addon-actions';

export default {
    component: CustomSlider,
    title: 'Components/Globals/CustomSlider'
};

const Template = args => <CustomSlider {...args}/>;

export const Default = Template.bind({});
Default.args = {
    sliderTitle: 'Slider Title',
    splitMetadata: {
        valueToSplit: 2000000,
        entitiesToSplit: [{
            name: 'transferBudget',
            handleChange: action('Update Transfer Budget')
        }, {
            name: 'wageBudget',
            handleChange: action('Update Wage Budget')
        }]
    }
};