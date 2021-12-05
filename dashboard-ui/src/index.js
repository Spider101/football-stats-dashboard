import { StrictMode } from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import { ThemePreferenceProvider } from './context/themePreferenceProvider';

ReactDOM.render(
    <StrictMode>
        <ThemePreferenceProvider>
            <App />
        </ThemePreferenceProvider>
    </StrictMode>,
    document.getElementById('root')
);
