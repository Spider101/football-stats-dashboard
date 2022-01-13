import { fireEvent, render, screen } from '@testing-library/react';
import CustomSlider from './CustomSlider';

const defaultProps = {
    sliderTitle: 'fake slider title',
    splitMetadata: {
        valueToSplit: 1000,
        entitiesToSplit: [{
            name: 'entityA',
            handleChange: jest.fn()
        }, {
            name: 'entityB',
            handleChange: jest.fn()
        }]
    }
};

beforeEach(() => {
    jest.resetAllMocks();
});

it('renders successfully', () => {
    render(<CustomSlider {...defaultProps}/>);
    const slider = screen.getByRole('slider');
    expect(slider).toHaveAttribute('aria-valuenow', '100');

    // verify the change handlers for the components is not invoked on mount
    const { splitMetadata: { entitiesToSplit } } = defaultProps;
    entitiesToSplit.forEach(entity => expect(entity.handleChange).not.toHaveBeenCalled());
});

it('updating the slider calls the change handlers for each component', () => {
    render(<CustomSlider {...defaultProps} />);
    const slider = screen.getByRole('slider');
    expect(slider).toHaveAttribute('aria-valuenow', '100');

    // verify the change handlers for the components is not invoked on mount
    const { splitMetadata: { entitiesToSplit } } = defaultProps;
    entitiesToSplit.forEach(entity => expect(entity.handleChange).not.toHaveBeenCalled());

    // shift focus to the slider and hit left arrow key to set transfer and wage budget values
    slider.focus();
    // userEvent does not support keyboard events in this version of react-testing-library
    // so using fireEvent instead
    fireEvent.keyDown(document.activeElement, { key: 'ArrowLeft' });
    expect(slider).toHaveAttribute('aria-valuenow', '99');

    // verify the change handlers are now called
    entitiesToSplit.forEach(entity => expect(entity.handleChange).toHaveBeenCalled());
});

it('rerendering with different prop values updates the internal state', () => {
    const { rerender } = render(<CustomSlider {...defaultProps} />);

    const updatedValueToSplit = 200;
    const updatedProps = {
        ...defaultProps,
        splitMetadata: {
            ...defaultProps.splitMetadata,
            valueToSplit: updatedValueToSplit
        }
    };
    rerender(<CustomSlider {...updatedProps} />);

    // since the initial percentage is 100% and has not changed, the first component's change handler should be called
    // with a value equal to the value to split passed in the props
    const { splitMetadata: { entitiesToSplit } } = updatedProps;
    entitiesToSplit.forEach(entity => expect(entity.handleChange).toHaveBeenCalled());
    expect(entitiesToSplit[0].handleChange).toHaveBeenCalledWith({
        target: { name: entitiesToSplit[0].name, value: updatedValueToSplit }
    });
});