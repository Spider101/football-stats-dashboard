import { action } from '@storybook/addon-actions';
import { LookupDataContextProvider } from '../context/LookupDataProvider';
import { getLookupDataHandlers } from '../mocks/handlers';

import AddPlayerForm from '../widgets/AddPlayerForm';

export default {
    component: AddPlayerForm,
    title: 'Widgets/SquadHubView/AddPlayerForm',
    decorators: [
        Story => (
            <LookupDataContextProvider>
                <Story />
            </LookupDataContextProvider>
        )
    ]
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

Default.parameters = {
    msw: {
        handlers: getLookupDataHandlers()
    }
};