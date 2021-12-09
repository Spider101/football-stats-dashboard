import { StrictMode } from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import { ThemePreferenceProvider } from './context/themePreferenceProvider';
import { worker } from './mocks/browser';

if (process.env.NODE_ENV === 'development'){
    worker.start();
}

ReactDOM.render(
    <StrictMode>
        <ThemePreferenceProvider>
            <App />
        </ThemePreferenceProvider>
    </StrictMode>,
    document.getElementById('root')
);