import React from 'react';
import PropTypes from 'prop-types';
import clsx from 'clsx';

import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import InputBase from '@material-ui/core/InputBase';

import MenuIcon from '@material-ui/icons/Menu';
import AccountCircleSharp from '@material-ui/icons/AccountCircleSharp';
import SearchIcon from '@material-ui/icons/Search';

import { fade, makeStyles } from '@material-ui/core/styles';

import { DRAWER_WIDTH } from '../utils';
import { useUserAuth } from '../context/authProvider';

const useStyles = makeStyles((theme) => ({
    grow: {
        flexGrow: 1
    },
    appBar: {
        zIndex: theme.zIndex.drawer + 1,
        transition: theme.transitions.create(['width', 'margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen
        })
    },
    appBarShift: {
        marginLeft: DRAWER_WIDTH,
        width: `calc(100% - ${DRAWER_WIDTH}px)`,
        transition: theme.transitions.create(['width', 'margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen
        })
    },
    hide: {
        display: 'none'
    },
    menuButton: {
        marginRight: theme.spacing(2)
    },
    title: {
        display: 'none',
        [theme.breakpoints.up('sm')]: {
            display: 'block'
        }
    },
    search: {
        position: 'relative',
        borderRadius: theme.shape.borderRadius,
        marginRight: theme.spacing(2),
        marginLeft: 0,
        width: '100%',
        backgroundColor: fade(theme.palette.common.white, 0.15),
        '&:hover': {
            backgroundColor: fade(theme.palette.common.white, 0.25)
        },
        [theme.breakpoints.up('sm')]: {
            marginLeft: theme.spacing(3),
            width: 'auto'
        }
    },
    searchIcon: {
        padding: theme.spacing(0, 2),
        height: '100%',
        position: 'absolute',
        pointerEvents: 'none',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center'
    },
    inputRoot: {
        color: 'inherit'
    },
    inputInput: {
        padding: theme.spacing(1, 1, 1, 0),
        // vertical padding + font size from searchIcon
        paddingLeft: `calc(1em + ${theme.spacing(4)}px)`,
        transition: theme.transitions.create('width'),
        width: '100%',
        [theme.breakpoints.up('md')]: {
            width: '20ch'
        }
    }
}));

export default function AppBarMenu({ menu: { title, teamColor }, onClickHandler, isOpen }) {
    const classes = useStyles();
    const [menuAnchor, setMenuAnchor] = React.useState(null);
    const isMenuOpen = Boolean(menuAnchor);
    const { logOut } = useUserAuth();

    const handleMenuOpen = e => {
        setMenuAnchor(e.currentTarget);
    };

    const handleMenuClose = () => {
        setMenuAnchor(null);
    };

    const renderUserMenu = (
        <Menu
            keepMounted
            anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
            transformOrigin={{ vertical: 'top', horizontal: 'right' }}
            open={isMenuOpen}
            anchorEl={menuAnchor}
            onClose={handleMenuClose}
        >
            <MenuItem onClick={handleMenuClose}>Profile</MenuItem>
            <MenuItem onClick={logOut}>Log Out</MenuItem>
        </Menu>
    );

    return (
        <>
            <AppBar
                style={{ backgroundColor: teamColor }}
                className={clsx(classes.appBar, {
                    [classes.appBarShift]: isOpen
                })}
                position='fixed'
            >
                <Toolbar>
                    <IconButton
                        edge='start'
                        className={clsx(classes.menuButton, {
                            [classes.hide]: isOpen
                        })}
                        color='inherit'
                        onClick={onClickHandler}
                    >
                        <MenuIcon />
                    </IconButton>
                    <Typography variant='h6' className={classes.title} noWrap>
                        {title}
                    </Typography>
                    <div className={classes.search}>
                        <div className={classes.searchIcon}>
                            <SearchIcon />
                        </div>
                        <InputBase
                            placeholder='Search players ...'
                            classes={{
                                root: classes.inputRoot,
                                input: classes.inputInput
                            }}
                        />
                    </div>
                    <div style={{ flexGrow: 1 }} />
                    <div style={{ display: 'flex' }}>
                        <IconButton color='inherit' onClick={handleMenuOpen}>
                            <AccountCircleSharp />
                        </IconButton>
                    </div>
                </Toolbar>
            </AppBar>
            {renderUserMenu}
        </>
    );
}

AppBarMenu.propTypes = {
    menu: PropTypes.shape({
        title: PropTypes.string,
        teamColor: PropTypes.string
    }),
    onClickHandler: PropTypes.func,
    isOpen: PropTypes.bool
};
