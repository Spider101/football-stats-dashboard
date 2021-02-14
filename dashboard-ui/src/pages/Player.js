import React from 'react';
import PropTypes from 'prop-types';
import { NavLink as RouterLink, Route, Switch, useParams, useRouteMatch  } from 'react-router-dom';

import CircularProgress from '@material-ui/core/CircularProgress';
import Grid from '@material-ui/core/Grid';
import Link from '@material-ui/core/Link';
import { fade, makeStyles } from '@material-ui/core/styles';

import { fetchPlayerData, fetchPlayerPerformanceData, fetchSquadHubData } from '../clients/DashboardClient';
import PlayerProgressionView from '../views/PlayerProgressionView';
import MatchPerformanceView from '../views/MatchPerformanceView';
import PlayerComparisonView from '../views/PlayerComparisonView';
import FilterControl from '../components/FilterControl';
import { useQuery, useQueryClient } from 'react-query';

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
        '& div': {
            paddingLeft: '0px !important',
            paddingRight: `${theme.spacing(2)}px !important`
        },
        '& a': {
            color: theme.palette.common.white
        }
    },
    selectedPage: {
        borderBottomWidth: 1,
        borderBottomStyle: 'solid',
        padding: `${(theme.spacing(1) + 1)}px 0px`
    }
}));

export default function Player() {
    const classes = useStyles();
    const { playerId } = useParams();
    const { path, url } = useRouteMatch();

    return (
        <>
            <Grid container spacing={2} className={ classes.topMenu }>
                <Grid item>
                    <Link
                        exact
                        component={ RouterLink }
                        to={ url }
                        underline='none'
                        activeClassName={ classes.selectedPage }
                    >
                        Player Progress
                    </Link>
                </Grid>
                <Grid item>
                    <Link
                        component={ RouterLink }
                        to={ `${url}/compare` }
                        underline='none'
                        activeClassName={ classes.selectedPage }
                    >
                        Player Comparison
                    </Link>
                </Grid>
                <Grid item>
                    <Link
                        component={ RouterLink }
                        to={ `${url}/performance` }
                        underline='none'
                        activeClassName={ classes.selectedPage }
                    >
                        Match Performance
                    </Link>
                </Grid>
            </Grid>

            <Switch>
                <Route exact path={ path }>
                    <PlayerProgressionContainer playerId={ playerId } classes={ classes } />
                </Route>
                <Route path={ `${path}/compare` }>
                    <PlayerComparisonContainer playerId={ playerId } classes={ classes } />
                </Route>
                <Route path={ `${path}/performance` }>
                    <PlayerPerformanceContainer playerId={ playerId } classes={ classes } />
                </Route>
            </Switch>
        </>
    );
}

const PlayerProgressionContainer = ({ playerId, classes }) => {
    const { isLoading, data: playerData } = useQuery(['player', { playerId }], fetchPlayerData);

    const playerProgressViewData = isLoading ? {} :
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
                isLoading ? <CircularProgress className={ classes.loadingCircle }/>
                    : <PlayerProgressionView { ...playerProgressViewData } />
            }
        </>
    );
};

const PlayerPerformanceContainer = ({ playerId, classes }) => {
    const { isLoading, data: playerPerformanceData } =
        useQuery(['playerPerformance', { playerId }], fetchPlayerPerformanceData);

    const playerPerformanceViewData = {
        playerPerformance: {
            competitions: playerPerformanceData
        }
    };

    return (
        <>
            {
                isLoading ? <CircularProgress className={ classes.loadingCircle }/>
                    : <MatchPerformanceView { ...playerPerformanceViewData } />
            }
        </>
    );
};

const PlayerComparisonContainer = ({ playerId, classes }) => {
    const [comparedPlayerId, setCurrentPlayerId] = React.useState(-1);
    const queryClient = useQueryClient();

    const handlePlayerChange = (event) => {
        setCurrentPlayerId(event.target.value);
    };

    // fetch the squad players data (from cache or the server)
    // to populate the list of players that the current player can be compared against
    const squadDataQuery = useQuery(
        'squadData',
        fetchSquadHubData, {
            initialData: () => {
                return queryClient.getQueryData('squadData')
            },
            staleTime: 60 * 1000,
            initialDataUpdatedAt: queryClient.getQueryState('squadData')?.dataUpdatedAt
        });
    const squadPlayers = squadDataQuery.isLoading ? []
        : squadDataQuery.data.filter(d => d.playerId !== playerId)
            .map(d => ({ id: d.playerId, text: d.name }));

    const filterControlProps = {
        currentValue: comparedPlayerId,
        allPossibleValues: squadPlayers,
        handleChangeFn: handlePlayerChange,
        labelIdFragment: 'players',
        inputLabelText: 'players',
        helperText: 'Choose player to compare against'
    };
    const filterControl = <FilterControl { ...filterControlProps } />;

    // fetch the data for the player to be compared against
    // (run the query only when the comparedPlayerId's value changes from the default of -1)
    const comparedPlayerQuery = useQuery(['comparedPlayer', { playerId: comparedPlayerId }], fetchPlayerData, {
        enabled: comparedPlayerId !== -1
    });
    const comparedPlayer = comparedPlayerQuery.isLoading || comparedPlayerQuery.isIdle ? null : {
        playerMetadata: comparedPlayerQuery.data.metadata,
        playerRoles: comparedPlayerQuery.data.roles,
        playerOverall: {
            currentValue: comparedPlayerQuery.data.ability.current,
            history: comparedPlayerQuery.data.ability.history
        },
        playerAttributes: comparedPlayerQuery.data.attributes
    };

    // fetch the data for the current player (from cache or server) to be passed into the player comparison view
    const playerDataQuery = useQuery(['player', { playerId }], fetchPlayerData);
    const playerComparisonViewData = playerDataQuery.isLoading ? {} :
        {
            basePlayer: {
                playerMetadata: playerDataQuery.data.metadata,
                playerRoles: playerDataQuery.data.roles,
                playerOverall: {
                    currentValue: playerDataQuery.data.ability.current,
                    history: playerDataQuery.data.ability.history
                },
                playerAttributes: playerDataQuery.data.attributes
            },
            comparedPlayer,
            filterControl
        };

    return (
        <>
            {
                squadDataQuery.isLoading || playerDataQuery.isLoading
                ? <CircularProgress className={ classes.loadingCircle }/>
                    : <PlayerComparisonView { ...playerComparisonViewData } />
            }
        </>
    );
};

PlayerProgressionContainer.propTypes = {
    playerId: PropTypes.string,
    classes: PropTypes.object
};

PlayerComparisonContainer.propTypes = {
    ...PlayerProgressionContainer.propTypes
};

PlayerPerformanceContainer.propTypes = {
    ...PlayerProgressionContainer.propTypes
};