import { MemoryRouter } from 'react-router-dom';

import Signup from '../components/Signup';
import { mockHandleChange, mockSubmit } from './utils/storyMocks';

export default {
    component: Signup,
    title: 'Components/UserAuth/Signup',
    parameters: {
        docs: {
            description: {
                component: 'UI Component for displaying a Sign In form. It consists of a _FirstName_, a _LastName_,'
                + ' an _Email_, a _New Password_ and a _Confirm Password_ field.'
                + ' Other than the default, it can exist in other states such as _submitting_ the form, having'
                + ' _submitted_ the form, when one of the input fields fail client-side validations and when after'
                + ' submitting the form, the server returns a validation.'
            }
        }
    },
    decorators: [
        Story => (
            <MemoryRouter>
                <div style={{ display: 'flex', justifyContent: 'center' }}>
                    <Story />
                </div>
            </MemoryRouter>
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
