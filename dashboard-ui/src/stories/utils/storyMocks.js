import { action } from '@storybook/addon-actions';

export const mockSubmit = (e) => {
    e.preventDefault();
    const submitAction = action('Submitted Sign In Form');
    submitAction(e);
};
export const mockHandleChange = (e) => {
    const handleChangeAction = action('typing ...');
    handleChangeAction(e.target.value);
};