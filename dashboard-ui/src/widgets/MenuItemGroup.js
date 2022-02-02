import PropTypes from 'prop-types';

import MenuItem from '../components/MenuItem';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Collapse from '@material-ui/core/Collapse';
import ExpandLess from '@material-ui/icons/ExpandLess';
import ExpandMore from '@material-ui/icons/ExpandMore';
import { makeStyles } from '@material-ui/core/styles';
import ListItemIcon from '@material-ui/core/ListItemIcon';

const useStyles = makeStyles((theme) => ({
    listGroupRoot: {
        width: '100%',
        backgroundColor: theme.palette.background.paper
    },
    nestedListItems: {
        paddingLeft: theme.spacing(4),
    },
}));

export default function MenuItemGroup({ menuGroup: { id, menuItems, groupTitle, groupIcon, isCollapsed,
    isItemTextWrapped = true }, onCollapseMenuItemGroup}) {
    const classes = useStyles();
    const whiteSpaceStyle = !isItemTextWrapped ? 'nowrap' : 'normal';
    return (
        <List key={ id } className={ classes.listGroupRoot } component="nav">
            <ListItem button onClick={ () => onCollapseMenuItemGroup(id) }>
                <ListItemIcon> { groupIcon } </ListItemIcon>
                <ListItemText primary={ groupTitle } style={{ whiteSpace: whiteSpaceStyle }}/>
                { isCollapsed ? <ExpandLess /> : <ExpandMore />}
            </ListItem>
            <Collapse in={ !isCollapsed } timeout="auto" unmountOnExit>
                <List component="div" disablePadding>
                    { menuItems.map((menuItem, i) =>
                        <MenuItem key={ i } { ...menuItem }  clsName={ classes.nestedListItems } /> ) }
                </List>
            </Collapse>
        </List>
    );
}

MenuItemGroup.propTypes = {
    menuGroup: PropTypes.shape({
        id: PropTypes.string,
        menuItems: PropTypes.arrayOf(PropTypes.shape(MenuItem.propTypes)),
        groupTitle: PropTypes.string,
        groupIcon: PropTypes.element,
        isCollapsed: PropTypes.bool,
        isItemTextWrapped: PropTypes.bool,
    }),
    onCollapseMenuItemGroup: PropTypes.func
};
