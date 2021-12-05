import { render, screen } from '@testing-library/react';

import { WithDisabledItems } from '../stories/Sidebar.stories';
import { MemoryRouter } from 'react-router-dom';

it('should render menu item as disabled', () => {
    render(
        <MemoryRouter initialEntries={['/']}>
            <WithDisabledItems {...WithDisabledItems.args} />
        </MemoryRouter>
    );
    const disabledMenuItemName = 'Disabled Menu Item';
    const disabledMenuItem = screen.getByRole('button', { name: disabledMenuItemName });
    expect(disabledMenuItem).toHaveAttribute('aria-disabled', 'true');
});