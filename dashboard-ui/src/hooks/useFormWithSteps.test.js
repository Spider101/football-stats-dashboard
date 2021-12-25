import { renderHook } from '@testing-library/react-hooks';
import { act } from 'react-dom/test-utils';

import useFormWithSteps from './useFormWithSteps';
import { formSubmission } from '../utils';

describe('useFormWithSteps hook -', () => {
    const testSchema = {};
    describe('handle next button click', () => {
        beforeEach(() => {
            testSchema.firstStep = { firstField: '', secondField: '' };
            testSchema.secondStep = { firstField: '', secondField: '' };
            jest.resetAllMocks();
        });

        it('does not update global state or activeStep if current form data has validations', () => {
            const { result } = renderHook(() => useFormWithSteps(testSchema, jest.fn()));
            const { handleNextFn, activeStep: initialActiveStep, getSubmitStatusAtStep } = result.current;
            expect(initialActiveStep).toBe(0);
            expect(getSubmitStatusAtStep(initialActiveStep)).toBe(formSubmission.NOT_READY);

            act(() => handleNextFn());
            const { activeStep: activeStepAfterClick } = result.current;
            expect(activeStepAfterClick).toBe(initialActiveStep);
        });

        it('updates global state and increments activeStep if current form data has no validations', () => {
            const { result } = renderHook(() => useFormWithSteps(testSchema, jest.fn()));
            const {
                activeStep: initialActiveStep,
                getSubmitStatusAtStep,
            } = result.current;
            expect(initialActiveStep).toBe(0);
            expect(getSubmitStatusAtStep(initialActiveStep)).toBe(formSubmission.NOT_READY);

            // fill the form till the first step
            fillFormTillStep(1, result);

            // simulate click on the Next button
            const { handleNextFn } = result.current;
            act(() => handleNextFn());

            // verify activeStep has been incremented, indicating we have moved to the next step
            const { activeStep: activeStepAfterClick } = result.current;
            expect(activeStepAfterClick).toBe(initialActiveStep + 1);
        });
    });

    it('handle back button click decrements activeStep', () => {
        const { result } = renderHook(() => useFormWithSteps(testSchema, jest.fn()));
        const {
            handleBackFn,
            activeStep: initialActiveStep,
            getSubmitStatusAtStep,
        } = result.current;
        expect(initialActiveStep).toBe(0);
        expect(getSubmitStatusAtStep(initialActiveStep)).toBe(formSubmission.NOT_READY);

        act(() => handleBackFn());
        const { activeStep: activeStepAfterClick } = result.current;
        expect(activeStepAfterClick).toBe(initialActiveStep - 1);
    });

    describe('handle form submission', () => {
        beforeEach(() => {
            testSchema.firstStep = { firstField: '', secondField: '' };
            testSchema.secondStep = { firstField: '', secondField: '' };
            jest.resetAllMocks();
        });

        it('does not run form submission logic if current form data has validations', () => {
            const mockCallback = jest.fn();
            const { result } = renderHook(() => useFormWithSteps(testSchema, mockCallback));
            const {
                activeStep: initialActiveStep,
                getSubmitStatusAtStep,
            } = result.current;
            expect(initialActiveStep).toBe(0);
            expect(getSubmitStatusAtStep(initialActiveStep)).toBe(formSubmission.NOT_READY);

            // fill the form till the first step
            fillFormTillStep(1, result);

            // simulate click on the Next button
            const { handleNextFn } = result.current;
            act(() => handleNextFn());

            // verify activeStep has been incremented, indicating we have moved to the next step
            const { activeStep: activeStepAfterClick } = result.current;
            expect(activeStepAfterClick).toBe(initialActiveStep + 1);

            const { handleSubmitFn } = result.current;
            act(() => handleSubmitFn());

            expect(mockCallback).not.toHaveBeenCalled();
        });

        it('callback is invoked if current form data has no validations', async () => {
            const mockCallback = jest.fn();
            const { result, waitForNextUpdate } = renderHook(() => useFormWithSteps(testSchema, mockCallback));
            const {
                activeStep: initialActiveStep,
                getSubmitStatusAtStep,
            } = result.current;
            expect(initialActiveStep).toBe(0);
            expect(getSubmitStatusAtStep(initialActiveStep)).toBe(formSubmission.NOT_READY);

            // fill the form till the second step
            fillFormTillStep(2, result);

            // verify all fields are validated successfully on last step and form is ready to be submitted
            const { activeStep: activeStepBeforeFormSubmit } = result.current;
            expect(result.current.getSubmitStatusAtStep(activeStepBeforeFormSubmit)).toBe(formSubmission.READY);

            const { handleSubmitFn } = result.current;
            act(() => handleSubmitFn());

            await waitForNextUpdate();
            expect(mockCallback).toHaveBeenCalledWith({
                firstStep: {
                    firstField: 'fake field value',
                    secondField: 'fake field value',
                },
                secondStep: {
                    firstField: 'fake field value',
                    secondField: 'fake field value'
                }
            });

            const { formSubmissionResponse } = result.current;
            expect(formSubmissionResponse.severity).toBe('success');
        });

        it('error state is set if callback invoked returns error message', async () => {
            const errorMessage = 'Something went wrong!';
            const mockCallback = jest.fn();
            mockCallback.mockResolvedValue(errorMessage);

            const { result, waitForNextUpdate } = renderHook(() => useFormWithSteps(testSchema, mockCallback));
            const {
                activeStep: initialActiveStep,
                getSubmitStatusAtStep,
            } = result.current;
            expect(initialActiveStep).toBe(0);
            expect(getSubmitStatusAtStep(initialActiveStep)).toBe(formSubmission.NOT_READY);

            // fill the form till the second step
            fillFormTillStep(2, result);

            // verify all fields are validated successfully on last step and form is ready to be submitted
            const { activeStep: activeStepBeforeFormSubmit } = result.current;
            expect(result.current.getSubmitStatusAtStep(activeStepBeforeFormSubmit)).toBe(formSubmission.READY);

            const { handleSubmitFn } = result.current;
            act(() => handleSubmitFn());

            await waitForNextUpdate();
            expect(mockCallback).toHaveBeenCalledTimes(1);

            const { formSubmissionResponse } = result.current;
            expect(formSubmissionResponse).toEqual({ severity: 'error', message: errorMessage });
        });
    });
});

/**
 * iteratively fill fields till a given step on the form.
 * @param {number} numSteps - number of steps of the form to process
 * @param {Object} resultRef - reference to the result object representing the current state of the hook
 */
const fillFormTillStep = (numSteps, resultRef) => {
    for (let i = 0; i < numSteps; i ++) {
        if (i !== 0) {
            const { handleNextFn } = resultRef.current;
            act(() => handleNextFn());
        }
        const { getFormMetadataAtStep, activeStep } = resultRef.current;
        const { formData } = getFormMetadataAtStep(activeStep);
        fillFieldsOnStep(Object.keys(formData), activeStep, resultRef);
    }
};

/**
 * iteratively fill all the fields present on a given step in the form
 * @param {String[]} fieldNames - list of field names to iterate over
 * @param {number} activeStep - the current step of the form being processed
 * @param {Object} resultRef - reference to the result object representing the current state of the hook
 */
const fillFieldsOnStep = (fieldNames, activeStep, resultRef) => {
    fieldNames.forEach(key => {
        const { getFormMetadataAtStep } = resultRef.current;
        const { handleChangeFn } = getFormMetadataAtStep(activeStep);
        act(() => handleChangeFn({ target: { name: key, value: 'fake field value' } }));
    });
};