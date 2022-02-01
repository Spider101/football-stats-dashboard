import PropTypes from 'prop-types';

import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';

export default function MenuItem({ text, icon, clsName, selectedItem, menuItemIndex, handleMenuItemClick, componentType,
    routePath, isDisabled = false }) {

    return (
        <ListItem
            button
            disabled={ isDisabled }
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
    icon: PropTypes.element,
    clsName: PropTypes.string,
    isDisabled: PropTypes.bool,
    selectedItem: PropTypes.number,
    menuItemIndex: PropTypes.number,
    handleMenuItemClick: PropTypes.func,
    componentType: PropTypes.elementType,
    routePath: PropTypes.string
};
