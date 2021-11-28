import React from 'react';

import { Default as DialogForm } from './DialogForm.stories';

import PageAction from '../components/PageAction';

export default {
    component: PageAction,
    title: 'Components/Globals/PageAction',
    argTypes: {
        dialog: { control: '' }
    },
    parameters: {
        docs: {
            description: {
                component: 'UI Component for representing actions user can perform on a page.'
            }
        }
    }
};

const Template = args => <PageAction {...args} />;

export const EditAction = Template.bind({});
EditAction.args = {
    actionType: 'edit',
    dialog: <DialogForm {...DialogForm.args} />
};

export const AddAction = Template.bind({});
AddAction.args = {
    actionType: 'add',
    dialog: <DialogForm {...DialogForm.args} />
};

export const DeleteAction = Template.bind({});
DeleteAction.args = {
    actionType: 'delete',
    dialog: <DialogForm {...DialogForm.args} />
};