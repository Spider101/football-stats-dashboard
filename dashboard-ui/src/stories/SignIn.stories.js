import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';

import SignIn from '../components/SignIn';
import { mockHandleChange, mockSubmit } from './utils/storyMocks';

export default {
    component: SignIn,
    title: 'Components/UserAuth/SignIn',
    decorators: [
        Story => (
            <Router>
                <div style={{ display: 'flex', justifyContent: 'center' }}>
                    <Story />
                </div>
            </Router>
        )
    ]
};

const defaultArgs = {
    values: { email: '', password: '' },
    handleChange: mockHandleChange,
    handleSubmit: mockSubmit,
    validations: { email: null, password: null, form: null },
    submitStatus: null
};
const Template = args => <SignIn {...args} />;

export const Default = Template.bind({});
Default.args = defaultArgs;

export const Submitting = Template.bind({});
Submitting.args = {
    ...defaultArgs,
    values: { email: 'fake@test.email', password: 'fakepassword' },
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
