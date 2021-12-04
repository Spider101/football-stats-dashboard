import { render, screen } from '@testing-library/react';

import { Disabled } from '../stories/MenuItem.stories';

it('should render item as disabled if isDisabled prop is true', () => {
    render(<Disabled {...Disabled.args} />);
    const menuItem = screen.getByRole('button');
    expect(menuItem).toHaveAttribute('aria-disabled', 'true');
});