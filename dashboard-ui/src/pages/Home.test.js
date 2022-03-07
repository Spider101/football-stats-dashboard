import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { MemoryRouter } from 'react-router-dom';

import { AuthContextProvider } from '../context/authProvider';
import { createQueryWrapper } from '../testUtils';
import Home from './Home';

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

    // verify that a listItem is rendered with the club name as fetched from the backend
    const listItem = await screen.findByRole('listitem');
    expect(listItem).toHaveTextContent(/chelsea f.c/i);
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
    const clubLogoToUpload = new File(['club logo'], 'clubLogo.png', { type: 'image/png' });

    userEvent.type(screen.getByLabelText(/club name/i), inputValue);

    await act(async () => {
        // running this inside act because file upload updates the state by setting the `fileKey` property
        userEvent.upload(screen.getByLabelText(/club logo/i), clubLogoToUpload);
    });
    const fileInput = screen.getByLabelText(/club logo/i);
    expect(fileInput.files.length).toBe(1);
    expect(fileInput.files[0]).toEqual(clubLogoToUpload);

    userEvent.type(screen.getByLabelText(/manager funds/i), '10');

    // shift focus to the slider and hit left arrow key to set transfer and wage budget values
    const slider = screen.getByRole('slider');
    slider.focus();
    // userEvent does not support keyboard events in this version of react-testing-library
    // so using fireEvent instead
    fireEvent.keyDown(document.activeElement, { key: 'ArrowLeft' });

    userEvent.type(screen.getByLabelText(/income/i), '1');
    userEvent.type(screen.getByLabelText(/expenditure/i), '1');

    const submitButton = screen.getByRole('button', { name: 'Submit' });
    expect(submitButton).not.toBeDisabled();
    userEvent.click(submitButton);

    await waitFor(() => expect(screen.queryByText('New Club Added Successfully!')).toBeInTheDocument());
});