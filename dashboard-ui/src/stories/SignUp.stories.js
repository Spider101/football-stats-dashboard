import React from 'react';
import Signup from '../components/Signup';
import { mockHandleChange, mockSubmit } from './utils/storyMocks';

export default {
    component: Signup,
    title: 'Components/UserAuth/Signup',
};

const defaultArgs = {
    values: { firstName: '', lastName: '', email: '', newPassword: '', confirmedPassword: '' },
    handleChange: mockHandleChange,
    handleSubmit: mockSubmit,
    validations: { firstName: null, lastName: null, email: null, newPassword: null, confirmedPassword: null },
    submitStatus: null
};

const Template = args => <Signup { ...args }/>;
export const Default = Template.bind({});
Default.args = defaultArgs;

export const Submitting = Template.bind({});
Submitting.args = {
    ...defaultArgs,
    values: {
        firstName: 'fake first name',
        lastName: 'fake last name',
        email: 'fake@test.email',
        newPassword: 'fake new password',
        confirmedPassword: 'fake confirmed password'
    },
    submitStatus: 'SUBMITTING',
};

export const Submitted = Template.bind({});
Submitted.args = {
    ...defaultArgs,
    submitStatus: 'SUBMITTED'
};

export const FailedInput = Template.bind({});
FailedInput.args = {
    ...defaultArgs,
    validations: {
        ...defaultArgs.validations,
        firstName: 'First Name is required!',
        lastName: 'LastName Name is required!',
        email: 'Email is required!',
        newPassword: 'Password is required!',
        confirmedPassword: 'Passwords do not match!'
    }
};

export const FailedSubmit = Template.bind({});
FailedSubmit.args = {
    ...defaultArgs,
    validations: {
        ...defaultArgs.validations,
        form: 'Unable to create account!'
    }
};
