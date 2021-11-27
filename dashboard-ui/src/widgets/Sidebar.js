import React from 'react';
import PropTypes from 'prop-types';
import clsx from 'clsx';
import { useLocation } from 'react-router';

import { makeStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import Divider from '@material-ui/core/Divider';
import Drawer from '@material-ui/core/Drawer';
import IconButton from '@material-ui/core/IconButton';
import SettingsIcon from '@material-ui/icons/Settings';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';

import MenuItemGroup from './MenuItemGroup';
import MenuItem from '../components/MenuItem';
import { DRAWER_WIDTH } from '../utils';

const useStyles = makeStyles((theme) => ({
    settingsRoot: {
        marginTop: 'auto'
    },
    toolbar: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-end',
        padding: theme.spacing(0, 1),
        // necessary for content to be below app bar
        ...theme.mixins.toolbar
    },
    drawer: {
        width: DRAWER_WIDTH,
        flexShrink: 0,
        whiteSpace: 'nowrap'
    },
    drawerOpen: {
        width: DRAWER_WIDTH,
        transition: theme.transitions.create('width', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
        }),
    },
    drawerClose: {
        transition: theme.transitions.create('width', {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
        }),
        overflowX: 'hidden',
        width: theme.spacing(7) + 1,
        [theme.breakpoints.up('sm')]: {
            width: theme.spacing(9) + 1,
        },
    },
}));

export default function Sidebar({ sideBarItems: initialSideBarItems, onClickHandler, isOpen }) {
    const classes = useStyles();
    const [ sideBarItems, updateSideBarItems ] = React.useState(initialSideBarItems);
    const [selectedItem, setSelectedItem] = React.useState(-1);
    const location = useLocation();

    const handleClick = (event, itemIndex) => {
        setSelectedItem(itemIndex);
    };

    const handleMenuGroupToggle = (id, shouldToggleAll = false) => {
        const updatedSideBarItems = sideBarItems.map(sideBarItem => {
            return sideBarItem.isGroup && (shouldToggleAll || sideBarItem.listItem.id === id) ?
                {
                    ...sideBarItem,
                    listItem: {
                        ...sideBarItem.listItem,
                        isCollapsed: !sideBarItem.listItem.isCollapsed,
                        // the shouldToggleAll flag indicates if this method is being called from the child (false) or
                        // parent (false) component. If it is called from the child component, the `open` flag has
                        // already been set so we can use it's value directly, otherwise toggle it to reflect the value
                        // it is going to be
                        isItemTextWrapped: shouldToggleAll === !isOpen
                    }
                } : sideBarItem;
        });
        updateSideBarItems(updatedSideBarItems);
    };

    const settingsMenuItemData = {
        text: 'Settings',
        icon: <SettingsIcon />,
        selectedItem,
        handleMenuItemClick: handleClick,
        menuItemIndex: sideBarItems.length
        // TODO: add router specific props when settings page is ready
    };

    return (
        <Drawer
            variant="permanent"
            className={ clsx(classes.drawer, {
                [classes.drawerOpen]: isOpen,
                [classes.drawerClose]: !isOpen
            })}
            classes={{
                paper: clsx({
                    [classes.drawerOpen]: isOpen,
                    [classes.drawerClose]: !isOpen
                })
            }}
        >
            <div className={classes.toolbar}>
                <IconButton onClick={ onClickHandler }>
                    { !isOpen ? <ChevronRightIcon /> : <ChevronLeftIcon />}
                </IconButton>
            </div>
            <Divider />
            <List>
                {
                    sideBarItems.map((sideBarItem, _idx) => (sideBarItem.isGroup
                        ? <MenuItemGroup
                            key={ _idx }
                            menuGroup={ sideBarItem.listItem }
                            onCollapseMenuItemGroup={ handleMenuGroupToggle }
                        />
                        : <MenuItem
                            key={ _idx }
                            isDisabled={ (sideBarItem.listItem?.disabledPaths || []).includes(location.pathname) }
                            selectedItem={ selectedItem }
                            handleMenuItemClick={ handleClick }
                            { ...sideBarItem.listItem }
                        />)
                    )
                }
            </List>
            <div className={ classes.settingsRoot }>
                <Divider />
                <List>
                    <MenuItem { ...settingsMenuItemData } />
                </List>
            </div>
        </Drawer>
    );
}

Sidebar.propTypes = {
    sideBarItems: PropTypes.arrayOf(PropTypes.shape({
        isGroup: PropTypes.bool,
        listItem: PropTypes.object
    })),
    onClickHandler: PropTypes.func,
    isOpen: PropTypes.bool
};