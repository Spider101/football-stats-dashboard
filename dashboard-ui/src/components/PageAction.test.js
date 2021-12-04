import { render, screen, waitForElementToBeRemoved } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { AddAction } from '../stories/PageAction.stories';

it('should open dialog when floating action button (fab) is clicked', () => {
    render(<AddAction {...AddAction.args} />);
    const fab = screen.getByRole('button');

    // verify that by default dialog is not present in the DOM
    expect(screen.queryByRole('dialog')).not.toBeInTheDocument();

    // verify that the dialog is now present after clicking on the `fab` button
    userEvent.click(fab);
    expect(screen.queryByRole('dialog')).toBeInTheDocument();
});

it('should close dialog when cancel button in dialog actions section is clicked', async () => {
    render(<AddAction {...AddAction.args} />);
    const fab = screen.getByRole('button');
    userEvent.click(fab);
    expect(screen.queryByRole('dialog')).toBeInTheDocument();

    // verify dialog is removed from DOM after clicking on Cancel button on the dialog
    const cancelButton = screen.getByRole('button', { name: 'Cancel' });
    userEvent.click(cancelButton);
    await waitForElementToBeRemoved(() => screen.queryByRole('dialog'));
});