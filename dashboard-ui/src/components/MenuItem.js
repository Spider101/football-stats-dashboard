import React from 'react';
import PropTypes from 'prop-types';

import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';

export default function MenuItem({ text, icon, clsName, onSelectMenuItem, componentType, routePath }) {
    return (
        <ListItem button
            className={ clsName }
            onClick={() => onSelectMenuItem()}
            component={ componentType }
            to={ routePath }
        >
            <ListItemIcon>
                { icon }
            </ListItemIcon>
            <ListItemText primary={text} />
        </ListItem>
    );
}

MenuItem.propTypes = {
    text: PropTypes.string,
    icon: PropTypes.elementType,
    clsName: PropTypes.string,
    onSelectMenuItem: PropTypes.func,
    componentType: PropTypes.element,
    routePath: PropTypes.string
};
