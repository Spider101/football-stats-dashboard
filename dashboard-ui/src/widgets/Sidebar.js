import React from 'react';
import PropTypes from 'prop-types';
import clsx from 'clsx';

import MenuItemGroup from './MenuItemGroup';
import MenuItem from '../components/MenuItem';
import Divider from '@material-ui/core/Divider';
import { makeStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import Drawer from '@material-ui/core/Drawer';
import IconButton from '@material-ui/core/IconButton';
import SettingsIcon from '@material-ui/icons/Settings';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';

const drawerWidth = 240;
const useStyles = makeStyles((theme) => ({
    settingsRoot: {
        marginTop: 'auto'
    },
    toolbar: {
        padding: theme.spacing(0, 1),
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-end'
    },
    drawer: {
        width: drawerWidth,
        flexShrink: 0,
        whiteSpace: 'nowrap'
    },
    drawerOpen: {
        width: drawerWidth,
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

export default function Sidebar({ sideBarItems: initialSideBarItems }) {
    const classes = useStyles();
    const [ sideBarItems, updateSideBarItems ] = React.useState(initialSideBarItems);
    const [ open, setOpen ] = React.useState(true);

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
                        isItemTextWrapped: shouldToggleAll === !open
                    }
                } : sideBarItem;
        });
        updateSideBarItems(updatedSideBarItems);
    };

    const handleDrawerToggle = () => {
        setOpen(!open);
        handleMenuGroupToggle(null, true);
    };

    const settingsMenuItemData = {
        text: 'Settings',
        icon: <SettingsIcon />,
        onSelectMenuItem: x => x
        // TODO: add router specific props when settings page is ready
    };

    return (
        <div className={ classes.settingsRoot }>
            <Drawer
                variant="permanent"
                className={ clsx(classes.drawer, {
                    [classes.drawerOpen]: open,
                    [classes.drawerClose]: !open
                })}
                classes={{
                    paper: clsx({
                        [classes.drawerOpen]: open,
                        [classes.drawerClose]: !open
                    })
                }}
            >
                <div className={classes.toolbar}>
                    <IconButton onClick={ () => handleDrawerToggle() }>
                        { !open ? <ChevronRightIcon /> : <ChevronLeftIcon />}
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
                            : <MenuItem key={ _idx } { ...sideBarItem.listItem }/> )
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
        </div>
    );
}

Sidebar.propTypes = {
    sideBarItems: PropTypes.arrayOf(PropTypes.shape({
        isGroup: PropTypes.bool,
        listItem: PropTypes.object
    }))
};
