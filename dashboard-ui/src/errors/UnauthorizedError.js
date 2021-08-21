class UnauthorizedError extends Error {
    constructor(response, message) {
        super(message);
        this.response = response;
        this.name = 'Unauthorized Error';
    }
}

export default UnauthorizedError;