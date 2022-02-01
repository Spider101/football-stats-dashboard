import { MemoryRouter } from 'react-router-dom';

import SignIn from '../components/SignIn';
import { formSubmission } from '../constants';
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
            <MemoryRouter>
                <div style={{ display: 'flex', justifyContent: 'center' }}>
                    <Story />
                </div>
            </MemoryRouter>
        )
    ]
};

const Template = args => <SignIn {...args} />;

export const Default = Template.bind({});
Default.args = {
    values: { email: '', password: '' },
    handleChange: mockHandleChange,
    handleSubmit: mockSubmit,
    validations: {},
    submitStatus: formSubmission.NOT_READY 
};

export const ReadyToSubmit = Template.bind({});
ReadyToSubmit.args = {
    ...Default.args,
    values: { email: 'fake@test.email', password: 'fakepassword' },
    submitStatus: formSubmission.READY
};

export const Submitting = Template.bind({});
Submitting.args = {
    ...ReadyToSubmit.args,
    submitStatus: formSubmission.INPROGRESS
};

export const Submitted = Template.bind({});
Submitted.args = {
    ...Default.args,
    submitStatus: formSubmission.COMPLETE
};

export const FailedInput = Template.bind({});
FailedInput.args = {
    ...Default.args,
    values: { email: 'fake@test', password: '' },
    validations: {
        email: 'Email address is not in correct format!',
        password: 'Password is required'
    }
};

export const FailedSubmit = Template.bind({});
FailedSubmit.args = {
    ...ReadyToSubmit.args,
    validations: { ...Default.args.validations, form: 'Email/Password does not match' }
};
