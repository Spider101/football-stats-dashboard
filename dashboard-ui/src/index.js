import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import { ThemePreferenceProvider } from './context/themePreferenceProvider';

ReactDOM.render(
    <React.StrictMode>
        <ThemePreferenceProvider>
            <App />
        </ThemePreferenceProvider>
    </React.StrictMode>,
    document.getElementById('root')
);
