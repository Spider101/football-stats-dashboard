import TextField from '@material-ui/core/TextField';

import DialogForm from '../components/DialogForm';
import { action } from '@storybook/addon-actions';

export default {
    component: DialogForm,
    title: 'Components/Globals/DialogForm',
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

export const SingleStep = Template.bind({});
SingleStep.args = {
    isOpen: true,
    dialogTitle: 'Generic Form In Dialog',
    handleSubmit: action('Form Submit Handler'),
    handleClose: action('Dialog Close Handler')
};

export const MultiStepFirstStep = Template.bind({});
MultiStepFirstStep.args = {
    ...SingleStep.args,
    handleNext: action('Form Next Step Handler'),
    handleBack: action('Form Previous Step Handler'),
    numSteps: 2
};

export const MultiStepLastStep = Template.bind({});
MultiStepLastStep.args = {
    ...MultiStepFirstStep.args,
    activeStep: 1
};

export const MultiStepIntermediateStep = Template.bind({});
MultiStepIntermediateStep.args = {
    ...MultiStepFirstStep.args,
    numSteps: 3,
    activeStep: 1
};