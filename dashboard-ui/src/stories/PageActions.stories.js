import React from 'react';

import { action } from '@storybook/addon-actions';

import FileCopyIcon from '@material-ui/icons/FileCopyOutlined';
import SaveIcon from '@material-ui/icons/Save';
import PrintIcon from '@material-ui/icons/Print';
import ShareIcon from '@material-ui/icons/Share';
import FavoriteIcon from '@material-ui/icons/Favorite';
import PageActions from '../components/PageActions';


export default {
    title: 'Components/Globals/PageActions',
    component: PageActions
};

const actions = [
    { icon: <FileCopyIcon />, name: 'Copy', clickHandler: action('click') },
    { icon: <SaveIcon />, name: 'Save', clickHandler: action('click') },
    { icon: <PrintIcon />, name: 'Print', clickHandler: action('click') },
    { icon: <ShareIcon />, name: 'Share', clickHandler: action('click') },
    { icon: <FavoriteIcon />, name: 'Like', clickHandler: action('click') },
];

const Template = args => <PageActions { ... args } />;

export const Default = Template.bind({});
Default.args = {
    actions
};