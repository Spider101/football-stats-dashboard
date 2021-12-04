import { render } from '@testing-library/react';

import { WithDisabledItems } from '../stories/Sidebar.stories';
import { MemoryRouter } from 'react-router';

it('should render menu item as disabled', () => {
    const { getByRole } = render(
        <MemoryRouter initialEntries={['/']}>
            <WithDisabledItems {...WithDisabledItems.args} />
        </MemoryRouter>
    );
    const disabledMenuItemName = 'Disabled Menu Item';
    const disabledMenuItem = getByRole('button', { name: disabledMenuItemName });
    expect(disabledMenuItem).toHaveAttribute('aria-disabled', 'true');
});