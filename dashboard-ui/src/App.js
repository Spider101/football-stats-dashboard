import React from 'react';
import './App.css';

import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';

import { HomeRoute, SquadHubRoute } from './routes';
import Home from './pages/Home';
import SquadHub from './pages/SquadHub';
import Navigator from './Navigator';

function App() {
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
