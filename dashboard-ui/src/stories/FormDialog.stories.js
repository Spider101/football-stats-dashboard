import React from 'react';
import FormDialog from '../widgets/FormDialog';
import { action } from '@storybook/addon-actions';

import TextField from '@material-ui/core/TextField';
import DialogContentText from '@material-ui/core/DialogContentText';

export default {
    component: FormDialog,
    title: 'Widgets/Globals/FormDialog'
};

const form = (
    <>
        <DialogContentText>
            To subscribe to this website, please enter your email address here. We will send updates
            occasionally.
        </DialogContentText>
        <TextField
            autoFocus
            margin="dense"
            id="name"
            label="Email Address"
            type="email"
            fullWidth
        />
    </>
);
const formBuilder = (...args) => ({
    form,
    handleSubmit: action('submit'),
    formSubmitStatus: ''
});

const Template = args => <FormDialog { ...args } />;

export const Default = Template.bind({});
Default.args = {
    open: true,
    handleClose: action('close'),
    dialogTitle: 'Form Dialog Title',
    formData: {},
    useFormBuilder: formBuilder
};