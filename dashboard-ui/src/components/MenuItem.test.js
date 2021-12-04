import { render } from '@testing-library/react';

import { Disabled } from '../stories/MenuItem.stories';

it('should render item as disabled if isDisabled prop is true', () => {
    const { getByRole } = render(<Disabled {...Disabled.args} />);
    const menuItem = getByRole('button');
    expect(menuItem).toHaveAttribute('aria-disabled', 'true');
});