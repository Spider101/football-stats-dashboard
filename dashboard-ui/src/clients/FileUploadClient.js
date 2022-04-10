import makeRequestToEndpoint from './utils';

export const uploadImageFile = async (fileData) => {
    const formData = new FormData();
    formData.append('image', fileData);
    const res = await makeRequestToEndpoint('upload/image', 'POST', {}, formData);
    if (res.ok) {
        return await res.json();
    } else {
        const { message: errorMessage } = await res.json();
        throw new Error(errorMessage);
    }
};