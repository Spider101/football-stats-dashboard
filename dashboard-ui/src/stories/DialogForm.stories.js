import { action } from '@storybook/addon-actions';
import TextField from '@material-ui/core/TextField';

import DialogForm from '../components/DialogForm';
import { formSubmission } from '../constants';

export default {
    component: DialogForm,
    title: 'Components/Globals/DialogForm/SingleStep',
    argTypes: {
        children: { table: { disable: true } }
    },
    parameters: {
        docs: {
            description: {
                component: 'UI Component for displaying a custom form with a title and submit and cancel buttons'
            }
        }
    }
};

const Template = args => (
    <DialogForm {...args}>
        <TextField
            label='Text Input'
            autoFocus
            id="textInput"
            name="textInput"
            fullWidth
            margin="normal"
            placeholder="Enter any text ..."
        />
        <TextField
            label='Number Input'
            id="numberInput"
            name="numberInput"
            fullWidth
            type="number"
            margin="normal"
            placeholder="Enter any number ..."
        />
        <TextField
            label='Email Input'
            id="emailInput"
            name="emailInput"
            fullWidth
            type="email"
            margin="normal"
            placeholder="Enter any email ..."
        />
    </DialogForm>
);

export const Default = Template.bind({});
Default.args = {
    isOpen: true,
    dialogTitle: 'Generic Form In Dialog',
    handleSubmit: action('Form Submit Handler'),
    handleClose: action('Dialog Close Handler'),
    submitStatus: formSubmission.NOT_READY
};

export const ReadyToSubmit = Template.bind({});
ReadyToSubmit.args = {
    ...Default.args,
    submitStatus: formSubmission.READY
};