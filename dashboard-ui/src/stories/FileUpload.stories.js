import { faker } from '@faker-js/faker';
import { action } from '@storybook/addon-actions';

import FileUpload from '../components/FileUpload';
import { caseFormat } from '../constants';
import { capitalizeLabel } from '../utils';

export default {
    component: FileUpload,
    title: 'Components/Globals/FileUpload'
};

const Template = args => <FileUpload {...args} />;

const TextFieldProps = {
    name: faker.hacker.noun(),
    id: faker.hacker.noun(),
    label: capitalizeLabel(faker.hacker.noun(), caseFormat.CAMEL_CASE)
};

export const Initial = Template.bind({});
Initial.args = {
    TextFieldProps,
    progress: 0,
    fileKey: null,
    errorMessage: null,
    handleChangeFn: action('invoked change handler')
};

export const MidUpload = Template.bind({});
MidUpload.args = {
    TextFieldProps,
    progress: 44,
    fileKey: null,
    errorMessage: null,
    handleChangeFn: action('invoked change handler')
};

export const CompletedUpload = Template.bind({});
CompletedUpload.args = {
    TextFieldProps,
    progress: 100,
    fileKey: 'Sample.png',
    errorMessage: null,
    handleChangeFn: action('invoked change handler')
};

export const FailedUpload = Template.bind({});
FailedUpload.args = {
    TextFieldProps,
    progress: 0,
    fileKey: null,
    errorMessage: 'Something went wrong in uploading the file!',
    handleChangeFn: action('invoked change handler')
};