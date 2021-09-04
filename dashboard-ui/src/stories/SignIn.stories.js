import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';

import SignIn from '../components/SignIn';
import { mockHandleChange, mockSubmit } from './utils/storyMocks';

export default {
    component: SignIn,
    title: 'Components/UserAuth/SignIn',
    parameters: {
        docs: {
            description: {
                component: 'UI Component for displaying a Sign In form. It consists of an _Email_ and _Password_ field.'
                + ' Other than the default, it can exist in other states such as _submitting_ the form, having'
                + ' _submitted_ the form, when one of the input fields fail client-side validations and when after'
                + ' submitting the form, the server returns a validation.'
            }
        }
    },
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

const Template = args => <SignIn {...args} />;

export const Default = Template.bind({});
Default.args = {
    values: { email: '', password: '' },
    handleChange: mockHandleChange,
    handleSubmit: mockSubmit,
    validations: { email: null, password: null, form: null },
    submitStatus: null
};

export const Submitting = Template.bind({});
Submitting.args = {
    ...Default.args,
    values: { email: 'fake@test.email', password: 'fakepassword' },
    submitStatus: 'SUBMITTING'
};

export const Submitted = Template.bind({});
Submitted.args = {
    ...Default.args,
    submitStatus: 'SUBMITTED'
};

export const FailedInput = Template.bind({});
FailedInput.args = {
    ...Default.args,
    validations: {
        ...Default.args.validations,
        email: 'Email address is not in correct format!',
        password: 'Password is required'
    }
};

export const FailedSubmit = Template.bind({});
FailedSubmit.args = {
    ...Default.args,
    validations: { ...Default.args.validations, form: 'Email/Password does not match' }
};
