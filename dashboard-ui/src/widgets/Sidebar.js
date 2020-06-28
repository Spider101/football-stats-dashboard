import React from "react";
import PropTypes from "prop-types";
import clsx from "clsx";

import MenuItemGroup from "./MenuItemGroup";
import MenuItem from "../components/MenuItem";
import Divider from "@material-ui/core/Divider";
import { makeStyles } from "@material-ui/core/styles";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import List from "@material-ui/core/List";
import Drawer from "@material-ui/core/Drawer";
import CssBaseline from "@material-ui/core/CssBaseline";
import IconButton from "@material-ui/core/IconButton";
import SettingsIcon from '@material-ui/icons/Settings';
import ChevronLeftIcon from "@material-ui/icons/ChevronLeft";
import ChevronRightIcon from "@material-ui/icons/ChevronRight";

const drawerWidth = 240;
const useStyles = makeStyles((theme) => ({
    settingsRoot: {
        display: "flex"
    },
    toolbar: {
        padding: theme.spacing(0, 1),
        display: "flex",
        alignItems: "center",
        justifyContent: "flex-end"
    },
    drawer: {
        width: drawerWidth,
        flexShrink: 0,
        whiteSpace: "nowrap"
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

    const handleMenuGroupToggle = (id, shouldCollapseAll = false) => {
        const updatedSideBarItems = sideBarItems.map(sideBarItem => {
            return sideBarItem.isGroup && (shouldCollapseAll || sideBarItem.listItem.id === id) ?
                {
                    ...sideBarItem,
                    listItem: {
                        ...sideBarItem.listItem,
                        isCollapsed: !sideBarItem.listItem.isCollapsed
                    }
                } : sideBarItem;
        });
        updateSideBarItems(updatedSideBarItems);
    };

    const handleDrawerToggle = () => {
        setOpen(!open);
        handleMenuGroupToggle(null, true);
    };

    return (
        <div className={ classes.settingsRoot }>
            <CssBaseline />
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
                { sideBarItems.map(sideBarItem => (sideBarItem.isGroup ?
                    <MenuItemGroup
                        menuGroup={ sideBarItem.listItem }
                        onCollapseMenuItemGroup={ handleMenuGroupToggle }
                    /> : <MenuItem { ...sideBarItem.item }/> )) }
                <Divider />
                <List className={ classes.settingsRoot }>
                    <ListItem button>
                        <ListItemIcon>
                            <SettingsIcon />
                        </ListItemIcon>
                        <ListItemText primary="Settings"/>
                    </ListItem>
                </List>
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
