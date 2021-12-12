import { useState } from 'react';
import PropTypes from 'prop-types';
import { NavLink as RouterLink, Route, Switch, useParams, useRouteMatch  } from 'react-router-dom';

import Grid from '@material-ui/core/Grid';
import Link from '@material-ui/core/Link';
import { alpha, makeStyles } from '@material-ui/core/styles';

import PlayerProgressionView from '../views/PlayerProgressionView';
import MatchPerformanceView from '../views/MatchPerformanceView';
import PlayerComparisonView from '../views/PlayerComparisonView';

import FilterControl from '../components/FilterControl';
import StyledLoadingCircle from '../components/StyledLoadingCircle';

import useSquadHub from '../hooks/useSquadHubData';
import usePlayerData from '../hooks/usePlayerData';
import usePlayerPerfData from '../hooks/usePlayerPerfData';
import { queryKeys } from '../utils';

const useStyles = makeStyles((theme) => ({
    topMenu: {
        borderBottomColor: alpha(theme.palette.common.white, 0.25),
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
                    <PlayerProgressionContainer playerId={ playerId } />
                </Route>
                <Route path={ `${path}/compare` }>
                    <PlayerComparisonContainer playerId={ playerId } />
                </Route>
                <Route path={ `${path}/performance` }>
                    <PlayerPerformanceContainer playerId={ playerId } />
                </Route>
            </Switch>
        </>
    );
}

const PlayerProgressionContainer = ({ playerId }) => {
    const { isLoading, data: playerProgressViewData } = usePlayerData(queryKeys.PLAYER_DATA, playerId);

    if (isLoading) {
        return <StyledLoadingCircle />;
    }

    return <PlayerProgressionView { ...playerProgressViewData } />;
};

const PlayerPerformanceContainer = ({ playerId }) => {
    const { isLoading, data: playerPerformanceData } = usePlayerPerfData(playerId);

    if (isLoading) {
        return <StyledLoadingCircle />;
    }

    const playerPerformanceViewData = {
        playerPerformance: {
            competitions: playerPerformanceData
        }
    };

    return <MatchPerformanceView { ...playerPerformanceViewData } />;
};

const PlayerComparisonContainer = ({ playerId }) => {
    const [comparedPlayerId, setCurrentPlayerId] = useState(-1);

    const handlePlayerChange = (event) => {
        setCurrentPlayerId(event.target.value);
    };

    const squadDataQuery = useSquadHub();
    const squadPlayers = squadDataQuery.data.filter(d => d.playerId !== playerId)
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
    const { data: comparedPlayer } = usePlayerData(queryKeys.COMPARED_PLAYER_DATA, comparedPlayerId);

    // fetch the data for the current player (from cache or server) to be passed into the player comparison view
    const basePlayerDataQuery = usePlayerData(queryKeys.PLAYER_DATA, playerId);
    const playerComparisonViewData = {
        basePlayer: basePlayerDataQuery.data,
        comparedPlayer,
        filterControl
    };

    if (squadDataQuery.isLoading || basePlayerDataQuery.isLoading) {
        return <StyledLoadingCircle />;
    }

    return <PlayerComparisonView { ...playerComparisonViewData } />;
};

PlayerProgressionContainer.propTypes = {
    playerId: PropTypes.string,
};

PlayerComparisonContainer.propTypes = {
    ...PlayerProgressionContainer.propTypes
};

PlayerPerformanceContainer.propTypes = {
    ...PlayerProgressionContainer.propTypes
};