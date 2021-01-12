import React from 'react';

// import { createMuiTheme, ThemeProvider } from '@material-ui/core';
// import {
//     orange,
//     lightBlue,
//     deepPurple,
//     deepOrange
// } from '@material-ui/core/colors';

// import AppbarMenu from './components/AppBarMenu';

import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';

import { HomeRoute, SquadHubRoute } from './routes';
import Home from './pages/Home';
import SquadHub from './pages/SquadHub';
import Navigator from './Navigator';

function App() {
    // const [darkState, setDarkState] = React.useState(false);
    // const palleteType = 'dark';
    // const mainPrimaryColor = darkState ? orange[500] : lightBlue[500];
    // const mainSecondaryColor = darkState ? deepOrange[900] : deepPurple[500];

    // const darkTheme = createMuiTheme({
    //     pallete: {
    //         type: palleteType,
    //         primary: {
    //             main: mainPrimaryColor
    //         },
    //         secondary: {
    //             main: mainSecondaryColor
    //         }
    //     }
    // });

    return (
        <div className="App">
            <Router>
                <Navigator />
                <Switch>
                    <Route exact path={ HomeRoute } component={ Home }/>
                    <Route path={ SquadHubRoute} component={ SquadHub } />
                </Switch>
            </Router>
        </div>
    );
}

export default App;
