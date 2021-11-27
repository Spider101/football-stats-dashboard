import React from 'react';

import TextField from '@material-ui/core/TextField';

import DialogForm from '../components/DialogForm';
import { action } from '@storybook/addon-actions';

export default {
    component: DialogForm,
    title: 'Components/Globals/DialogForm',
    argTypes: {
        children: { control: '' }
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
            id="textInput"
            name="textInput"
            fullWidth
            variant="outlined"
            margin="normal"
            placeholder="Enter any text ..."
        />
        <TextField
            id="numberInput"
            name="numberInput"
            fullWidth
            type="number"
            variant="outlined"
            margin="normal"
            placeholder="Enter any number ..."
        />
        <TextField
            id="emailInput"
            name="emailInput"
            fullWidth
            type="email"
            variant="outlined"
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
    handleClose: action('Dialog Close Handler')
};