import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { QueryClient, QueryClientProvider } from 'react-query';
import { MemoryRouter } from 'react-router';

import { AuthContextProvider } from '../context/authProvider';
import Home from './Home';

const createQueryWrapper = children => {
    // creates a new QueryClient instance for each test
    const queryClient = new QueryClient({
        defaultOptions: {
            queries: {
                retry: false
            }
        }
    });
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
};

it('should render view with club data fetched from backend', async () => {
    render(
        <MemoryRouter>
            {createQueryWrapper(
                <AuthContextProvider>
                    <Home />
                </AuthContextProvider>
            )}
        </MemoryRouter>
    );

    // verify that a listItem (role=button) is rendered with the club name as fetched from the backend
    await waitFor(() => expect(screen.queryByRole('button', { name: 'Chelsea F.C' })).toBeInTheDocument());
});

it('should render success message when add new club form is submitted', async () => {
    render(
        <MemoryRouter>
            {createQueryWrapper(
                <AuthContextProvider>
                    <Home />
                </AuthContextProvider>
            )}
        </MemoryRouter>
    );

    const addClubButton = await screen.findByRole('button', { name: 'add' });
    userEvent.click(addClubButton);

    expect(screen.queryByRole('dialog')).toBeInTheDocument();

    const inputValue = 'Aston Villa FC';
    userEvent.type(screen.getByLabelText(/club name/i), inputValue);
    const submitButton = screen.getByRole('button', { name: 'Submit' });
    userEvent.click(submitButton);

    await screen.findByText('New Club Added Successfully!');
});