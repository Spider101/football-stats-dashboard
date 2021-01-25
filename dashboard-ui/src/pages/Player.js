import React from 'react';
import { Link as RouterLink, Route, Switch, useParams, useRouteMatch  } from 'react-router-dom';

import CircularProgress from '@material-ui/core/CircularProgress';
import Grid from '@material-ui/core/Grid';
import Link from '@material-ui/core/Link';
import { fade, makeStyles } from '@material-ui/core/styles';

import { getPlayerData } from '../clients/DashboardClient';
import PlayerProgressionView from '../views/PlayerProgressionView';

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

export default function Player() {
    const classes = useStyles();
    const { playerId } = useParams();
    const { path, url } = useRouteMatch();

    console.log(playerId, path, url);

    const [pageStatus, setPageStatus] = React.useState('loading');
    const [playerViewData, setPlayerViewData] = React.useState({});

    React.useEffect(() => {
        const getPlayerViewData = async () => {
            const { metadata, roles, ability, attributes } = await getPlayerData(playerId);

            setPlayerViewData({
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

            setPageStatus('ready');
        };

        getPlayerViewData();
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
            { pageStatus === 'loading' && <CircularProgress className={ classes.loadingCircle }/> }

            <Switch>
                <Route exact path={ path }>
                    { pageStatus === 'ready' && <PlayerProgressionView { ...playerViewData } /> }
                </Route>
            </Switch>
        </>
    );
}