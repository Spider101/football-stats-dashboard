import React from 'react';
import { render, wait, fireEvent } from '@testing-library/react';
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
    const { queryByRole } = render(
        <MemoryRouter>
            {createQueryWrapper(
                <AuthContextProvider>
                    <Home />
                </AuthContextProvider>
            )}
        </MemoryRouter>
    );

    // verify that a listItem (role=button) is rendered with the club name as fetched from the backend
    await wait(() => expect(queryByRole('button', { name: 'Chelsea F.C' })).toBeInTheDocument());
});

it('should render success message when add new club form is submitted', async () => {
    const { getByRole, queryByRole, getByLabelText, findByRole, findByText } = render(
        <MemoryRouter>
            {createQueryWrapper(
                <AuthContextProvider>
                    <Home />
                </AuthContextProvider>
            )}
        </MemoryRouter>
    );

    const addClubButton = await findByRole('button', { name: 'add'});
    fireEvent.click(addClubButton);

    expect(queryByRole('dialog')).toBeInTheDocument();

    const inputValue = 'Aston Villa FC';
    fireEvent.change(getByLabelText(/club name/i), { target: { value: inputValue } });
    const submitButton = getByRole('button', { name: 'Submit' });
    fireEvent.click(submitButton);

    await findByText('New Club Added Successfully!');
});