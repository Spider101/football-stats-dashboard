import React from 'react';
import PropTypes from 'prop-types';

import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';

export default function MenuItem({ text, icon, clsName, selectedItem, menuItemIndex, handleMenuItemClick, componentType,
    routePath }) {

    return (
        <ListItem
            button
            className={ clsName }
            selected={ selectedItem === menuItemIndex }
            onClick={(event) => handleMenuItemClick(event, menuItemIndex)}
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
    icon: PropTypes.node,
    clsName: PropTypes.string,
    selectedItem: PropTypes.number,
    menuItemIndex: PropTypes.number,
    handleMenuItemClick: PropTypes.func,
    componentType: PropTypes.elementType,
    routePath: PropTypes.string
};
