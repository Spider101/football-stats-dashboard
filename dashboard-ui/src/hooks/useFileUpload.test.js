import { act, renderHook } from '@testing-library/react-hooks';

import useFileUpload from './useFileUpload';

describe('useFileUpload hook - handle file selection change', () => {
    const fileName = 'test-file.png';
    const errorMessage = 'sample error message';
    const file = new File(['foo'], fileName, {
        type: 'image/png'
    });

    beforeEach(() => {
        jest.resetAllMocks();
    });

    it('successfully upload file selected', async () => {
        const mockCallback = jest.fn().mockResolvedValue({ fileKey: file.name, error: null });
        const mockFileKeyChangeHandler = jest.fn();
        const { result, waitForNextUpdate } = renderHook(() => useFileUpload(mockFileKeyChangeHandler, mockCallback));
        const { handleChangeFn } = result.current;

        act(() => {
            handleChangeFn({ target: { files: [file] } });
        });
        await waitForNextUpdate();


        const { fileKey, fileUploadError } = result.current;
        expect(fileKey).toBe(file.name);
        expect(fileUploadError).toBeNull();
        expect(mockFileKeyChangeHandler).toHaveBeenCalledWith(fileKey);
    });

    it('returns error if file upload is unsuccessful', async () => {
        const mockCallback = jest.fn().mockResolvedValue({ fileKey: null, error: errorMessage });
        const mockFileKeyChangeHandler = jest.fn();
        const { result, waitForNextUpdate } = renderHook(() => useFileUpload(mockFileKeyChangeHandler, mockCallback));
        const { handleChangeFn } = result.current;

        act(() => {
            handleChangeFn({ target: { files: [file] } });
        });

        await waitForNextUpdate();

        const { fileKey, fileUploadError } = result.current;
        expect(fileKey).toBeNull();
        expect(fileUploadError).toBe(errorMessage);
        expect(mockFileKeyChangeHandler).not.toHaveBeenCalled();
    });

    it('file upload logic is not run on hook initialization', () => {
        const mockCallback = jest.fn();
        const mockFileKeyChangeHandler = jest.fn();
        const { result } = renderHook(() => useFileUpload(mockFileKeyChangeHandler, mockCallback));
        const { fileKey, fileUploadError } = result.current;

        expect(fileKey).toBeNull();
        expect(fileUploadError).toBeNull();
        expect(mockCallback).not.toHaveBeenCalled();
        expect(mockFileKeyChangeHandler).not.toHaveBeenCalled();
    });

    it( 'skips file upload if no files have been selected', () => {
        const mockCallback = jest.fn();
        const mockFileKeyChangeHandler = jest.fn();
        const { result } = renderHook(() => useFileUpload(mockFileKeyChangeHandler, mockCallback));
        const { handleChangeFn } = result.current;

        act(() => {
            handleChangeFn({ target: { files: [] } });
        });

        const { fileKey, fileUploadError } = result.current;
        expect(fileKey).toBeNull();
        expect(fileUploadError).toBeNull();
        expect(mockCallback).not.toHaveBeenCalled();
        expect(mockFileKeyChangeHandler).not.toHaveBeenCalled();
    });

    it('reflects the key of the latest file uploaded when invoked multiple times', async () => {
        const newFileName = 'newFiles.jpeg';
        const newFile = new File(['bar'], newFileName, {
            type: 'image/jpeg'
        });
        const mockCallback = jest.fn()
            .mockResolvedValueOnce({ fileKey: file.name, error: null })
            .mockResolvedValueOnce({ fileKey: newFile.name, error: null });
        const mockFileKeyChangeHandler = jest.fn();
        const { result, waitForNextUpdate } = renderHook(() => useFileUpload(mockFileKeyChangeHandler, mockCallback));
        const { handleChangeFn } = result.current;

        act(() => {
            handleChangeFn({ target: { files: [file] } });
        });
        await waitForNextUpdate();

        const { fileKey, fileUploadError } = result.current;
        expect(fileKey).toBe(file.name);
        expect(fileUploadError).toBeNull();
        expect(mockFileKeyChangeHandler).toHaveBeenCalledWith(fileKey);

        // simulate second file upload by calling handleChangeFn a second time with the new file
        const { handleChangeFn: handleChangeAfterFirstUploadFn } = result.current;
        act(() => {
            handleChangeAfterFirstUploadFn({ target: { files: [newFile] } });
        });

        await waitForNextUpdate();

        const { fileKey: updatedFileKey, fileUploadError: updatedFileUploadError } = result.current;
        expect(updatedFileKey).toBe(newFile.name);
        expect(updatedFileUploadError).toBeNull();
        expect(mockFileKeyChangeHandler).toHaveBeenLastCalledWith(updatedFileKey);
    });
});