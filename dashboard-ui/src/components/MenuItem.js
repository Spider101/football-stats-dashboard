import React from 'react';
import PropTypes from 'prop-types';

import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';

export default function MenuItem({ text, icon, clsName, menuItemIndex, componentType, routePath }) {
    const [selectedItem, setSelectedItem] = React.useState(0);

    const handleMenuItemClick = (event, itemIndex) => {
        setSelectedItem(itemIndex);
    };

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
    icon: PropTypes.elementType,
    clsName: PropTypes.string,
    menuItemIndex: PropTypes.number,
    componentType: PropTypes.element,
    routePath: PropTypes.string
};
