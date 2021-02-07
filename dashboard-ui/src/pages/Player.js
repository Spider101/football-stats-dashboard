import React from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';
import { Link as RouterLink, Route, Switch, useParams, useRouteMatch  } from 'react-router-dom';

import CircularProgress from '@material-ui/core/CircularProgress';
import Grid from '@material-ui/core/Grid';
import Link from '@material-ui/core/Link';
import { fade, makeStyles } from '@material-ui/core/styles';

import { fetchPlayerData, fetchPlayerPerformanceData, fetchSquadHubData } from '../clients/DashboardClient';
import PlayerProgressionView from '../views/PlayerProgressionView';
import MatchPerformanceView from '../views/MatchPerformanceView';
import PlayerComparisonView from '../views/PlayerComparisonView';
import FilterControl from '../components/FilterControl';

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
    const [squadPlayers, setSquadPlayers] = React.useState([]);
    const [currentPlayerId, setCurrentPlayerId] = React.useState(-1);
    const [comparedPlayer, setComparedPlayer] = React.useState(null);

    const handlePlayerChange = (event) => {
        setCurrentPlayerId(event.target.value);
    };

    const filterControlProps = {
        currentValue: currentPlayerId,
        allPossibleValues: squadPlayers,
        handleChangeFn: handlePlayerChange,
        labelIdFragment: 'players',
        inputLabelText: 'players',
        helperText: 'Choose player to compare against'
    };

    const filterControl = <FilterControl { ...filterControlProps } />;

    React.useEffect(() => {
        const getSquadHubViewData = async () => {
            const squadHubData = await fetchSquadHubData();
            setSquadPlayers([
                ...squadHubData.map(squadPlayer => ({ id: squadPlayer.playerId, text: squadPlayer.name }))
            ]);
        };

        if (!_.isEmpty(playerData)) {
            setPageStatus(PAGE_STATUS.READY);
            getSquadHubViewData();
        }
    }, [playerData]);

    React.useEffect(() => {
        const getComparedPlayerData = async () => {
            const { metadata, roles, ability, attributes } = await fetchPlayerData(currentPlayerId);
            setComparedPlayer({
                playerMetadata: metadata,
                playerRoles: roles,
                playerOverall: {
                    currentValue: ability.current,
                    history: ability.history
                },
                playerAttributes: attributes
            });
        };

        currentPlayerId !== -1 && getComparedPlayerData();
    }, [currentPlayerId]);

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
            comparedPlayer,
            filterControl
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
    playerId: PropTypes.string,
    classes: PropTypes.object
};