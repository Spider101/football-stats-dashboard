import { useCallback, useEffect, useState } from 'react';

// TODO: 03/06/22 implement progress tracking once chunked file upload endpoint is ready
// const CHUNK_SIZE = 10 * 1024;

const useFileUpload = (handleFileKeyChangeFn, callback) => {
    // TODO: 03/06/22 implement progress tracking once chunked file upload endpoint is ready
    // const [progress, setProgress] = useState(0);

    const [fileKey, setFileKey] = useState(null);
    const [fileUploadError, setFileUploadError] = useState(null);
    const [fileToUpload, setFileToUpload] = useState(null);

    const handleChangeFn = e => {
        const { files } = e.target;
        if (files.length == 0) return;
        setFileToUpload(files[0]);
    };

    const uploadFile = useCallback(
        async fileToUpload => {
            const { fileKey, error } = await callback(fileToUpload);
            if (error != null) {
                setFileUploadError(error);
            } else {
                setFileKey(fileKey);
            }
        },
        [fileToUpload]
    );

    useEffect(() => {
        if (fileToUpload !== null) {
            uploadFile(fileToUpload);
        }
    }, [fileToUpload, uploadFile]);

    useEffect(() => {
        if (fileKey != null) {
            handleFileKeyChangeFn(fileKey);
        }
    }, [fileKey]);

    return {
        // TODO: 03/06/22 implement progress tracking once chunked file upload endpoint is ready
        progress: 0,
        fileKey,
        fileUploadError,
        handleChangeFn
    };
};

export default useFileUpload;