import React from 'react';

import Alert from '../components/Alert';

export default {
    component: Alert,
    title: 'Components/Globals/Alert'
};

const Template = args => <Alert { ...args } />;

export const Success = Template.bind({});
Success.args = {
    severity: 'success',
    text: 'This is a success message!'
};

export const Error = Template.bind({});
Error.args = {
    severity: 'error',
    text: 'This is an error message!'
};