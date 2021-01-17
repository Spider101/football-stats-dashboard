import React from 'react';

import CssBaseline from '@material-ui/core/CssBaseline';

import AppBarMenu from './components/AppBarMenu';
import Sidebar from './widgets/Sidebar';

import sideBarData from './sideBarData';
import { Link, Route, Switch } from 'react-router-dom';
import { makeStyles } from '@material-ui/core';

const useStyles = makeStyles((theme) => ({
    root: {
        display: 'flex'
    },
    content: {
        flexGrow: 1,
        padding: theme.spacing(3)
    },
    view: {
        display: 'flex',
        alignItems: 'flex-end',
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

    const sideBarItems = sideBarData.map((sidebarItemData, _idx) => ({
        isGroup: false,
        listItem: {
            text: sidebarItemData.text,
            icon: sidebarItemData.icon,
            menuItemIndex: _idx,
            componentType: Link,
            routePath: sidebarItemData.routePath
        }
    }));

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
                            sideBarData.map((sidebarItemData, _idx) => (
                                <Route exact
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