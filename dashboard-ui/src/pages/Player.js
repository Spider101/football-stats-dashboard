import React from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';
import { Link as RouterLink, Route, Switch, useParams, useRouteMatch  } from 'react-router-dom';

import CircularProgress from '@material-ui/core/CircularProgress';
import Grid from '@material-ui/core/Grid';
import Link from '@material-ui/core/Link';
import { fade, makeStyles } from '@material-ui/core/styles';

import { fetchPlayerData, fetchPlayerPerformanceData } from '../clients/DashboardClient';
import PlayerProgressionView from '../views/PlayerProgressionView';
import MatchPerformanceView from '../views/MatchPerformanceView';
import CardWithFilter from '../widgets/CardWithFilter';
import PlayerComparisonView from '../views/PlayerComparisonView';

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
    const [playerData, setPlayerData] = React.useState({});

    React.useEffect(() => {
        const getPlayerData = async () => {
            const playerData = await fetchPlayerData(playerId);
            setPlayerData({ ...playerData });
        };

        getPlayerData();
    }, [playerId]);

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
                    <PlayerProgressionContainer
                        playerData={ playerData }
                        classes={ classes }
                    />
                </Route>
                <Route path={ `${path}/compare` }>
                    <PlayerComparisonContainer
                        playerData={ playerData }
                        classes={ classes }
                    />
                </Route>
                <Route path={ `${path}/performance` }>
                    <PlayerPerformanceContainer playerId={ playerId } classes={ classes } />
                </Route>
            </Switch>
        </>
    );
}

const PlayerProgressionContainer = ({ playerData, classes }) => {
    const [pageStatus, setPageStatus] = React.useState(PAGE_STATUS.LOADING);

    React.useEffect(() => {
        !_.isEmpty(playerData) && setPageStatus(PAGE_STATUS.READY);
    }, [playerData]);

    const playerProgressViewData = _.isEmpty(playerData) ? {} :
        {
            playerMetadata: playerData.metadata,
            playerRoles: playerData.roles,
            playerOverall: {
                currentValue: playerData.ability.current,
                history: playerData.ability.history
            },
            playerAttributes: playerData.attributes
        };

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
            const playerPerformanceViewData = await fetchPlayerPerformanceData(playerId);

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

const PlayerComparisonContainer = ({ playerData, classes }) => {
    const [pageStatus, setPageStatus] = React.useState(PAGE_STATUS.LOADING);

    const cardWithFilterProps = {
        currentValue: '',
        allPossibleValues: [],
        handleChangeFn: x => x,
        labelIdFragment: 'players',
        inputLabelText: 'players',
        helperText: 'Choose player to compare against'
    };

    const cardWithFilter = (
        <CardWithFilter filterControl={ cardWithFilterProps } />
    );

    React.useEffect(() => {
        !_.isEmpty(playerData) && setPageStatus(PAGE_STATUS.READY);
    }, [playerData]);

    const playerComparisonViewData = _.isEmpty(playerData) ? {} :
        {
            basePlayer: {
                playerMetadata: playerData.metadata,
                playerRoles: playerData.roles,
                playerOverall: {
                    currentValue: playerData.ability.current,
                    history: playerData.ability.history
                },
                playerAttributes: playerData.attributes
            },
            comparedPlayer: null,
            cardWithFilter
        };

    return (
        <>
            {
                pageStatus === PAGE_STATUS.LOADING
                    ? <CircularProgress className={ classes.loadingCircle }/>
                    : pageStatus === PAGE_STATUS.READY
                        ? <PlayerComparisonView { ...playerComparisonViewData } />
                        : null
            }
        </>
    );
};

PlayerProgressionContainer.propTypes = {
    playerData: PropTypes.object,
    classes: PropTypes.object
};

PlayerComparisonContainer.propTypes = {
    ...PlayerProgressionContainer.propTypes
};

PlayerPerformanceContainer.propTypes = {
    playerId: PropTypes.number,
    classes: PropTypes.object
};