import { faker } from '@faker-js/faker';
import { action } from '@storybook/addon-actions';
import FileUpload from '../components/FileUpload';

export default {
    component: FileUpload,
    title: 'Components/Globals/FileUpload'
};

const Template = args => <FileUpload {...args} />;

export const Initial = Template.bind({});
Initial.args = {
    name: faker.hacker.noun(),
    id: faker.hacker.noun(),
    progress: 0,
    fileKey: null,
    errorMessage: null,
    handleChangeFn: action('invoked change handler')
};

export const MidUpload = Template.bind({});
MidUpload.args = {
    name: faker.hacker.noun(),
    id: faker.hacker.noun(),
    progress: 44,
    fileKey: null,
    errorMessage: null,
    handleChangeFn: action('invoked change handler')
};

export const CompletedUpload = Template.bind({});
CompletedUpload.args = {
    name: faker.hacker.noun(),
    id: faker.hacker.noun(),
    progress: 100,
    fileKey: 'Sample.png',
    errorMessage: null,
    handleChangeFn: action('invoked change handler')
};

export const FailedUpload = Template.bind({});
FailedUpload.args = {
    name: faker.hacker.noun(),
    id: faker.hacker.noun(),
    progress: 0,
    fileKey: null,
    errorMessage: 'Something went wrong in uploading the file!',
    handleChangeFn: action('invoked change handler')
};