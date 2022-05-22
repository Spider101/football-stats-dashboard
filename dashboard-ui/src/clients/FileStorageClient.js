import makeRequestToEndpoint, { getBaseUrl } from './utils';

export const uploadImageFile = async (fileData) => {
    const formData = new FormData();
    formData.append('image', fileData);
    const res = await makeRequestToEndpoint('file-storage/image/upload', 'POST', {}, formData);
    if (res.ok) {
        return await res.json();
    } else {
        const { message: errorMessage } = await res.json();
        throw new Error(errorMessage);
    }
};

export const getImageDownloadURI = (fileKey) => `${getBaseUrl()}/file-storage/image/${fileKey}`;