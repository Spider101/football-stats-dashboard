import { action } from '@storybook/addon-actions';

import AddPlayerForm from '../widgets/AddPlayerForm';

export default {
    component: AddPlayerForm,
    title: 'Widgets/SquadHubView/AddPlayerForm'
};

const getFormMetadataAtStep = () => ({
    formData: { name: '', age: '', country: '' },
    formValidations: {},
    handleChangeFn: action('field was changed')
});

const Template = args => <AddPlayerForm {...args} />;
export const Default = Template.bind({});
Default.args = {
    getFormMetadataAtStep,
    stepIdx: 0
};