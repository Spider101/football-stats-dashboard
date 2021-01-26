import React from 'react';
import PropTypes from 'prop-types';
import { Link as RouterLink, Route, Switch, useParams, useRouteMatch  } from 'react-router-dom';

import CircularProgress from '@material-ui/core/CircularProgress';
import Grid from '@material-ui/core/Grid';
import Link from '@material-ui/core/Link';
import { fade, makeStyles } from '@material-ui/core/styles';

import { getPlayerData, getPlayerPerformanceData } from '../clients/DashboardClient';
import PlayerProgressionView from '../views/PlayerProgressionView';
import MatchPerformanceView from '../views/MatchPerformanceView';

const useStyles = makeStyles((theme) => ({
    loadingCircle: {
        width: '200px !important',
        height: '200px !important',
        alignSelf: 'center',
        margin: '25vh'
    },
    topMenu: {
        borderBottomColor: fade(theme.palette.common.white, 0.25),
        borderBottomWidth: 1,
        borderBottomStyle: 'solid',
        marginBottom: theme.spacing(2),
        '& a': {
            color: theme.palette.common.white,
            textDecoration: 'none !important'
        }
    }
}));

const PAGE_STATUS = {
    LOADING: 'loading',
    READY: 'ready'
};

export default function Player() {
    const classes = useStyles();
    const { playerId } = useParams();
    const { path, url } = useRouteMatch();

    return (
        <>
            <Grid container spacing={2} className={ classes.topMenu }>
                <Grid item>
                    <Link component={ RouterLink } to={ url }>
                        Player Progress
                    </Link>
                </Grid>
                <Grid item>
                    <Link component={ RouterLink } to={ `${url}/compare` }>
                        Player Comparison
                    </Link>
                </Grid>
                <Grid item >
                    <Link component={ RouterLink } to={ `${url}/performance` }>
                        Match Performance
                    </Link>
                </Grid>
            </Grid>

            <Switch>
                <Route exact path={ path }>
                    <PlayerProgressionContainer playerId={ playerId } classes={ classes } />
                </Route>
                <Route path={ `${path}/performance` }>
                    <PlayerPerformanceContainer playerId={ playerId } classes={ classes } />
                </Route>
            </Switch>
        </>
    );
}

const PlayerProgressionContainer = ({ playerId, classes }) => {
    const [pageStatus, setPageStatus] = React.useState(PAGE_STATUS.LOADING);
    const [playerProgressViewData, setPlayerProgressViewData] = React.useState({});

    React.useEffect(() => {
        const getPlayerViewData = async () => {
            const { metadata, roles, ability, attributes } = await getPlayerData(playerId);

            setPlayerProgressViewData({
                isSelected: true,
                orientation: '',
                playerMetadata: metadata,
                playerRoles: roles,
                playerOverall: {
                    currentValue: ability.current,
                    history: ability.history
                },
                playerAttributes: attributes
            });

            setPageStatus(PAGE_STATUS.READY);
        };

        getPlayerViewData();
    }, [playerId]);

    return (
        <>
            {
                pageStatus === PAGE_STATUS.LOADING
                    ? <CircularProgress className={ classes.loadingCircle }/>
                    : pageStatus === PAGE_STATUS.READY
                        ? <PlayerProgressionView { ...playerProgressViewData } />
                        : null
            }
        </>
    );
};

const PlayerPerformanceContainer = ({ playerId, classes }) => {
    const [pageStatus, setPageStatus] = React.useState(PAGE_STATUS.LOADING);
    const [playerPerformanceViewData, setPlayerPerformanceViewData] = React.useState({});

    React.useEffect(() => {
        const getPlayerPerformanceViewData = async () => {
            const playerPerformanceViewData = await getPlayerPerformanceData(playerId);

            setPlayerPerformanceViewData({
                playerPerformance: {
                    competitions: playerPerformanceViewData
                }
            });

            setPageStatus(PAGE_STATUS.READY);
        };

        getPlayerPerformanceViewData();
    }, [playerId]);

    return (
        <>
            {
                pageStatus === PAGE_STATUS.LOADING
                    ? <CircularProgress className={ classes.loadingCircle }/>
                    : pageStatus === PAGE_STATUS.READY
                        ? <MatchPerformanceView { ...playerPerformanceViewData } />
                        : null
            }
        </>
    );
};

PlayerProgressionContainer.propTypes = {
    playerId: PropTypes.number,
    classes: PropTypes.object,
};

PlayerPerformanceContainer.propTypes = {
    ...PlayerProgressionContainer.propTypes
};