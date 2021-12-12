import { useState } from 'react';
import PropTypes from 'prop-types';
import { Link, Route, Switch } from 'react-router-dom';

import CssBaseline from '@material-ui/core/CssBaseline';
import { makeStyles } from '@material-ui/core/styles';

import AppBarMenu from './components/AppBarMenu';
import StyledLoadingCircle from './components/StyledLoadingCircle';
import Sidebar from './widgets/Sidebar';
import UserAuth from './pages/UserAuth';
import routingData from './routing/routingData';
import useUserData from './hooks/useUserData';
import PrivateRoute from './routing/PrivateRoute';

const useStyles = makeStyles(theme => ({
    root: {
        display: 'flex'
    },
    content: {
        flexGrow: 1,
        padding: theme.spacing(10, 3)
    },
    view: {
        padding: theme.spacing(0, 1),
        // necessary for content to be below app bar
        ...theme.mixins.toolbar
    },
    formContainer: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center'
    }
}));

const AppContainer = ({ classes }) => {
    const [open, setOpen] = useState(false);

    const handleDrawerOpen = () => {
        setOpen(true);
    };

    const handleDrawerClose = () => {
        setOpen(false);
    };

    const sideBarItems = routingData
        .filter(sidebarItem => sidebarItem.showInSidebar)
        .map((sidebarItemData, _idx) => ({
            isGroup: false,
            listItem: {
                text: sidebarItemData.text,
                icon: sidebarItemData.icon,
                menuItemIndex: _idx,
                componentType: Link,
                disabledPaths: sidebarItemData.disabledPaths,
                routePath: sidebarItemData.routePath
            }
        }));

    // TODO: this should be returned from backend; remove it when ready
    const menuData = {
        title: 'Dummy App Bar Menu Title',
        teamColor: 'red'
    };
    return (
        <>
            <AppBarMenu menu={{ ...menuData }} isOpen={open} onClickHandler={handleDrawerOpen} />
            <Sidebar sideBarItems={sideBarItems} isOpen={open} onClickHandler={handleDrawerClose} />
            <main className={classes.content}>
                <div className={classes.view}>
                    <Switch>
                        {routingData.map((sidebarItemData, _idx) => {
                            return sidebarItemData.disabledPaths ? (
                                <PrivateRoute path={sidebarItemData.routePath} component={sidebarItemData.component} />
                            ) : (
                                <Route
                                    exact={sidebarItemData.isExact}
                                    key={_idx}
                                    path={sidebarItemData.routePath}
                                    component={sidebarItemData.component}
                                />
                            );
                        })}
                    </Switch>
                </div>
            </main>
        </>
    );
};

AppContainer.propTypes = {
    classes: PropTypes.object
};

export default function Layout() {
    const classes = useStyles();

    const { isLoading, isLoggedIn } = useUserData();

    return (
        <div className={classes.root}>
            <CssBaseline />
            {isLoading ? (
                <StyledLoadingCircle />
            ) : isLoggedIn ? (
                <AppContainer classes={classes} />
            ) : (
                <UserAuth classes={classes} />
            )}
        </div>
    );
}