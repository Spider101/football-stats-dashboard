import React from 'react';
import PropTypes from 'prop-types';
import { Link, Route, Switch } from 'react-router-dom';

import CssBaseline from '@material-ui/core/CssBaseline';
import CircularProgress from '@material-ui/core/CircularProgress';
import { makeStyles } from '@material-ui/core/styles';

import AppBarMenu from './components/AppBarMenu';
import Sidebar from './widgets/Sidebar';
import UserAuth from './pages/UserAuth';
import routingData from './routingData';
import { useUserAuth } from './context/authProvider';
import useUserData from './hooks/useUserData';

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
    loadingCircleRoot: {
        display: 'flex',
        flexDirection: 'column',
        flexGrow: 1,
        alignItems: 'center'
    },
    loadingCircle: {
        width: '200px !important',
        height: '200px !important',
        margin: '35vh'
    },
    formContainer: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center'
    }
}));

const AppContainer = ({ classes }) => {
    const [open, setOpen] = React.useState(false);

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
                        {routingData.map((sidebarItemData, _idx) => (
                            <Route
                                exact={sidebarItemData.isExact}
                                key={_idx}
                                path={sidebarItemData.routePath}
                                component={sidebarItemData.component}
                            />
                        ))}
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

    const { authToken } = useUserAuth();
    const { isLoading, isLoggedIn } = useUserData(authToken);

    return (
        <div className={classes.root}>
            <CssBaseline />
            {isLoading ? (
                <div className={classes.loadingCircleRoot}>
                    <CircularProgress className={classes.loadingCircle} />
                </div>
            ) : isLoggedIn ? (
                <AppContainer classes={classes} />
            ) : (
                <UserAuth classes={classes} />
            )}
        </div>
    );
}