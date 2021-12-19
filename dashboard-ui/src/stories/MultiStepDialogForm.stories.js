import TextField from '@material-ui/core/TextField';

import DialogForm from '../components/DialogForm';
import { action } from '@storybook/addon-actions';

export default {
    component: DialogForm,
    title: 'Components/Globals/DialogForm/MultiStep',
    argTypes: {
        children: { table: { disable: true } }
    },
    parameters: {
        docs: {
            description: {
                component:
                    'UI Component for displaying a custom multi-step form with a title and submit and cancel buttons'
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

export const FirstStep = Template.bind({});
FirstStep.args = {
    isOpen: true,
    dialogTitle: 'Generic Form In Dialog',
    handleSubmit: action('Form Submit Handler'),
    handleClose: action('Dialog Close Handler'),
    handleNext: action('Form Next Step Handler'),
    handleBack: action('Form Previous Step Handler'),
    numSteps: 2
};

export const LastStep = Template.bind({});
LastStep.args = {
    ...FirstStep.args,
    activeStep: 1
};

export const IntermediateStep = Template.bind({});
IntermediateStep.args = {
    ...FirstStep.args,
    numSteps: 3,
    activeStep: 1
};