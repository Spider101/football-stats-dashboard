import { render, fireEvent, waitForElementToBeRemoved } from '@testing-library/react';

import { AddAction } from '../stories/PageAction.stories';

it('should open dialog when floating action button (fab) is clicked', () => {
    const { queryByRole, getByRole } = render(<AddAction {...AddAction.args} />);
    const fab = getByRole('button');

    // verify that by default dialog is not present in the DOM
    expect(queryByRole('dialog')).not.toBeInTheDocument();

    // verify that the dialog is now present after clicking on the `fab` button
    fireEvent.click(fab);
    expect(queryByRole('dialog')).toBeInTheDocument();
});

it('should close dialog when cancel button in dialog actions section is clicked', async () => {
    const { getByRole, queryByRole } = render(<AddAction {...AddAction.args} />);
    const fab = getByRole('button');
    fireEvent.click(fab);
    expect(queryByRole('dialog')).toBeInTheDocument();

    // verify dialog is removed from DOM after clicking on Cancel button on the dialog
    const cancelButton = getByRole('button', { name: 'Cancel'});
    fireEvent.click(cancelButton);
    await waitForElementToBeRemoved(() => queryByRole('dialog'));
});