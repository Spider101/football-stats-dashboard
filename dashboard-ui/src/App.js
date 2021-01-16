import React from 'react';

// import { createMuiTheme, ThemeProvider } from '@material-ui/core';
// import {
//     orange,
//     lightBlue,
//     deepPurple,
//     deepOrange
// } from '@material-ui/core/colors';

import { BrowserRouter as Router } from 'react-router-dom';

import Layout from './Layout';

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
                <Layout />
            </Router>
        </div>
    );
}

export default App;
