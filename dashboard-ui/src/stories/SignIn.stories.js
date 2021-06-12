import React from 'react';
import SignIn from '../components/SignIn';
import { mockHandleChange, mockSubmit } from './utils/storyMocks';

export default {
    component: SignIn,
    title: 'Components/UserAuth/SignIn'
};

const defaultArgs = {
    values: { email: '', password: '' },
    handleChange: mockHandleChange,
    handleSubmit: mockSubmit,
    validations: { email: null, password: null, form: null },
    submitStatus: null
};
const Template = args => <SignIn { ...args }/>;

export const Default = Template.bind({});
Default.args = defaultArgs;

export const Submitting = Template.bind({});
Submitting.args = {
    ...defaultArgs,
    values: { email: 'fake@test.email', password: 'fakepassword'},
    submitStatus: 'SUBMITTING'
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
        email: 'Email address is not in correct format!',
        password: 'Password is required'
    }
};

export const FailedSubmit = Template.bind({});
FailedSubmit.args = {
    ...defaultArgs,
    validations: { ...defaultArgs.validations, form: 'Email/Password does not match' }
};
