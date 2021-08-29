import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';

import Signup from '../components/Signup';
import { mockHandleChange, mockSubmit } from './utils/storyMocks';

export default {
    component: Signup,
    title: 'Components/UserAuth/Signup',
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

const Template = args => <Signup { ...args }/>;

export const Default = Template.bind({});
Default.args = {
    values: { firstName: '', lastName: '', email: '', newPassword: '', confirmedPassword: '' },
    handleChange: mockHandleChange,
    handleSubmit: mockSubmit,
    validations: { firstName: null, lastName: null, email: null, newPassword: null, confirmedPassword: null },
    submitStatus: null
};

export const Submitting = Template.bind({});
Submitting.args = {
    ...Default.args,
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
    ...Default.args,
    submitStatus: 'SUBMITTED'
};

export const FailedInput = Template.bind({});
FailedInput.args = {
    ...Default.args,
    validations: {
        ...Default.args.validations,
        firstName: 'First Name is required!',
        lastName: 'LastName Name is required!',
        email: 'Email is required!',
        newPassword: 'Password is required!',
        confirmedPassword: 'Passwords do not match!'
    }
};

export const FailedSubmit = Template.bind({});
FailedSubmit.args = {
    ...Default.args,
    validations: {
        ...Default.args.validations,
        form: 'Unable to create account!'
    }
};
