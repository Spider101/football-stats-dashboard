import { renderHook, act } from '@testing-library/react-hooks';

import { formSubmission } from '../utils';
import useForm from './useForm';

describe('useForm hook -', () => {
    describe('handle form input change', () => {
        it('validates the input being changed', async () => {
            const { result } = renderHook(() => useForm({ firstName: '' }, jest.fn()));
            const { handleChangeFn, formValidations } = result.current;
            expect(formValidations.firstName).toBeUndefined();

            act(() => {
                handleChangeFn({ target: { name: 'firstName', value: '' } });
            });
            const {
                formValidations: { firstName: firstNameEmptyValidation }
            } = result.current;
            expect(firstNameEmptyValidation).toBeDefined();
            expect(firstNameEmptyValidation).toEqual('First Name cannot be empty!');

            // the validations are reset when correct values are passed in.
            act(() => {
                handleChangeFn({ target: { name: 'firstName', value: 'fake first name' } });
            });
            const {
                formValidations: { firstName: firstNameValidation },
                submitStatus
            } = result.current;
            expect(firstNameValidation).toBeNull();
            expect(submitStatus).toEqual(formSubmission.READY);
        });

        it('validates email format', async () => {
            const { result } = renderHook(() => useForm({ email: '' }, jest.fn()));
            const { handleChangeFn, formValidations } = result.current;
            expect(formValidations.email).toBeUndefined();

            act(() => {
                handleChangeFn({ target: { name: 'email', value: 'ab@ab' } });
            });
            const {
                formValidations: { email: emailFormatValidation }
            } = result.current;
            expect(emailFormatValidation).toBeDefined();
            expect(emailFormatValidation).toEqual('Email format is incorrect!');

            // the validations are reset when correct values are passed in.
            act(() => {
                handleChangeFn({ target: { name: 'email', value: 'fake@email.com' } });
            });
            const {
                formValidations: { email: emailValidation }
            } = result.current;
            expect(emailValidation).toBeNull();
        });

        it('validates password length', async () => {
            const { result } = renderHook(() => useForm({ newPassword: '' }, jest.fn()));
            const { handleChangeFn, formValidations } = result.current;
            expect(formValidations.newPassword).toBeUndefined();

            act(() => {
                handleChangeFn({ target: { name: 'newPassword', value: '12345' } });
            });

            const {
                formValidations: { newPassword: passwordLengthValidation }
            } = result.current;
            expect(passwordLengthValidation).toBeDefined();
            expect(passwordLengthValidation).toEqual('Password must be between 6 and 12 characters');

            // the validations are reset when correct values are passed in.
            act(() => {
                handleChangeFn({ target: { name: 'newPassword', value: '123456' } });
            });
            const {
                formValidations: { newPassword: passwordValidation }
            } = result.current;
            expect(passwordValidation).toBeNull();
        });

        it('validates that passwords are matching', async () => {
            const { result } = renderHook(() =>
                useForm({ newPassword: '123456', confirmedPassword: '' }, jest.fn())
            );
            const { handleChangeFn, formValidations } = result.current;
            expect(formValidations.confirmedPassword).toBeUndefined();

            act(() => {
                handleChangeFn({ target: { name: 'confirmedPassword', value: '123457' } });
            });

            const {
                formValidations: { confirmedPassword: passwordNotMatchingValidation }
            } = result.current;
            expect(passwordNotMatchingValidation).toBeDefined();
            expect(passwordNotMatchingValidation).toEqual('Passwords must match!');

            // the validations are reset when correct values are passed in.
            act(() => {
                handleChangeFn({ target: { name: 'confirmedPassword', value: '123456' } });
            });
            const {
                formValidations: { confirmedPassword: passwordMatchingValidation }
            } = result.current;
            expect(passwordMatchingValidation).toBeNull();
        });

        it('validates form data with multiple fields', async () => {
            const { result } = renderHook(() => useForm({ firstName: '', lastName: '' }, jest.fn()));
            const { handleChangeFn, formValidations } = result.current;
            expect(formValidations.firstName).toBeUndefined();
            expect(formValidations.lastName).toBeUndefined();

            act(() => {
                handleChangeFn({ target: { name: 'firstName', value: 'fake first name' } });
            });
            const {
                formValidations: { firstName: firstNameValidation }
            } = result.current;
            expect(firstNameValidation).toBeDefined();
            expect(result.current.submitStatus).toBe(formSubmission.NOT_READY);

            // updating the same field with valid input should result in the same state as before
            const { handleChangeFn: handleChangeOnFirstNameUpdate } = result.current;
            act(() => {
                handleChangeOnFirstNameUpdate({ target: { name: 'firstName', value: 'updated fake first name' } });
            });
            const {
                formValidations: { firstName: updatedFirstNameValidation }
            } = result.current;
            expect(updatedFirstNameValidation).toBeDefined();
            expect(result.current.submitStatus).toBe(formSubmission.NOT_READY);

            // updating the last field in the form with valid input should result in the form submit status to change
            // to READY
            const { handleChangeFn: handleChangeOnLastNameInput } = result.current;
            act(() => {
                handleChangeOnLastNameInput({ target: { name: 'lastName', value: 'fake last name' } });
            });
            const {
                formValidations: { lastName: lastNameValidation }
            } = result.current;
            expect(lastNameValidation).toBeDefined();
            expect(result.current.submitStatus).toBe(formSubmission.READY);
        });
    });

    describe('handle form submission', () => {
        beforeEach(() => {
            jest.resetAllMocks();
        });

        it('client call is not made if there are existing field validations', () => {
            const mockEvent = {
                preventDefault: jest.fn()
            };
            const mockCallback = jest.fn();
            const { result } = renderHook(() => useForm({ firstName: '' }, mockCallback));
            const { handleChangeFn } = result.current;

            act(() => {
                handleChangeFn({ target: { name: 'firstName', value: ' ' } });
            });

            const { handleSubmitFn } = result.current;
            act(() => {
                handleSubmitFn(mockEvent);
            });
            expect(mockEvent.preventDefault).toBeCalledTimes(1);
            expect(mockCallback).not.toBeCalled();
        });

        it('client call is made if there are no validations', async () => {
            const mockCallback = jest.fn();
            mockCallback.mockResolvedValue(null);

            const { result, waitForNextUpdate } = renderHook(() => useForm({ firstName: '' }, mockCallback));
            const { handleChangeFn, submitStatus } = result.current;
            expect(submitStatus).toEqual(formSubmission.NOT_READY);

            act(() => {
                handleChangeFn({ target: { name: 'firstName', value: 'fake first name' } });
            });

            const { handleSubmitFn, submitStatus: submitStatusAfterFieldUpdate } = result.current;
            expect(submitStatusAfterFieldUpdate).toEqual(formSubmission.READY);

            act(() => {
                handleSubmitFn({ preventDefault: jest.fn() });
            });
            await waitForNextUpdate();
            expect(mockCallback).toBeCalledTimes(1);

            const { submitStatus: submitStatusAfterClientCall } = result.current;
            expect(submitStatusAfterClientCall).toEqual(formSubmission.COMPLETE);
        });

        it('form submit status is not changed to COMPLETE if client returns error', async () => {
            const mockCallback = jest.fn();
            mockCallback.mockResolvedValue('Something went wrong!');

            const { result, waitForNextUpdate } = renderHook(() => useForm({ firstName: '' }, mockCallback));
            const { handleChangeFn, submitStatus } = result.current;
            expect(submitStatus).toEqual(formSubmission.NOT_READY);

            act(() => {
                handleChangeFn({ target: { name: 'firstName', value: 'fake first name' } });
            });

            const { handleSubmitFn } = result.current;
            act(() => {
                handleSubmitFn({ preventDefault: jest.fn() });
            });
            await waitForNextUpdate();
            expect(mockCallback).toBeCalledTimes(1);

            const { formValidations, submitStatus: submitStatusAfterClientCall } = result.current;
            expect(formValidations.form).toBeDefined();
            expect(formValidations.form).toEqual('Something went wrong!');

            // submit status is changed from INPROGRESS to null again because there is an error on the form now
            expect(submitStatusAfterClientCall).not.toBe(formSubmission.COMPLETE);
        });
    });
});