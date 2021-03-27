import React from 'react';

import CssBaseline from '@material-ui/core/CssBaseline';

import AppBarMenu from './components/AppBarMenu';
import Sidebar from './widgets/Sidebar';

import routingData from './routingData';
import { Link, Route, Switch } from 'react-router-dom';
import { makeStyles } from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
    root: {
        display: 'flex'
    },
    content: {
        flexGrow: 1,
        padding: theme.spacing(10, 3)
    },
    view: {
        display: 'flex',
        flexDirection: 'column',
        padding: theme.spacing(0, 1),
        // necessary for content to be below app bar
        ...theme.mixins.toolbar
    }
}));

export default function Layout() {
    const classes = useStyles();

    const [open, setOpen] = React.useState(false);

    const handleDrawerOpen = () => {
        setOpen(true);
    };

    const handleDrawerClose = () => {
        setOpen(false);
    };

    const sideBarItems = routingData.filter(sidebarItem => sidebarItem.showInSidebar)
        .map((sidebarItemData, _idx) => ({
            isGroup: false,
            listItem: {
                text: sidebarItemData.text,
                icon: sidebarItemData.icon,
                menuItemIndex: _idx,
                componentType: Link,
                routePath: sidebarItemData.routePath
            }
        }));

    // TODO: this should be returned from backend; remove it when ready
    const menuData = {
        title: 'Dummy App Bar Menu Title',
        teamColor: 'red'
    };

    return (
        <div className={ classes.root }>
            <CssBaseline />
            <AppBarMenu
                menu={{ ...menuData }}
                isOpen={ open }
                onClickHandler={ handleDrawerOpen }
            />
            <Sidebar
                sideBarItems={ sideBarItems }
                isOpen={ open }
                onClickHandler={ handleDrawerClose }
            />
            <main className={ classes.content }>
                <div className={ classes.view }>
                    <Switch>
                        {
                            routingData.map((sidebarItemData, _idx) => (
                                <Route exact={ sidebarItemData.isExact }
                                    key={ _idx }
                                    path={ sidebarItemData.routePath }
                                    component={ sidebarItemData.component }
                                />
                            ))
                        }
                    </Switch>
                </div>
            </main>
        </div>
    );

}