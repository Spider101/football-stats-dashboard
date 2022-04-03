import { httpStatus } from '../constants';
import makeRequestToEndpoint from './utils';
import UnauthorizedError from '../errors/UnauthorizedError';

/**
 * accepts the user's credentials and authenticates their request to login
 * @param {string} userCredentials
 * @param {string} userCredentials.username
 * @param {string} userCredentials.password
 * @returns auth data containing bearer token, userId and refresh information
 */
export const authenticateUser = async ({ username, password }) => {
    const res = await makeRequestToEndpoint('users/authenticate', 'POST', {}, { email: username, password });
    if (res.ok) {
        return await res.json();
    } else if (res.status === httpStatus.BAD_REQUEST) {
        throw new Error('Incorrect username/password provided!');
    } else {
        throw new Error(`Something went wrong trying to log in user with username: ${username}`);
    }
};

/**
 * fetches user information on the basis of the user id stored in the auth data (localStorage)
 * @param {*} queryData
 * @param {string} queryData.queryKey
 * @returns user data containing userId, email, firstName, lastName and encrypted password
 */
export const fetchUser = async ({ meta: { authData } }) => {
    const res = await makeRequestToEndpoint(`users/${authData.userId}`, 'GET', {
        Authorization: `BEARER ${authData.id}`
    });
    if (res.ok) {
        return await res.json();
    } else if (res.status === httpStatus.UNAUTHORIZED) {
        throw new UnauthorizedError(res, 'User is unauthorized to view this page. Please login!');
    } else {
        throw new Error(`No user found with given auth token: ${authData}`);
    }
};

/**
 * creates a new user with the data passed in
 * @param {object} newUserData
 * @returns newly created user with encrypted password and userId
 */
export const createUser = async newUserData => {
    const { email } = newUserData;

    const res = await makeRequestToEndpoint('users', 'POST', {}, newUserData);
    if (res.ok) {
        return await res.json();
    } else if (res.status === httpStatus.CONFLICT) {
        throw new Error(`User with email address: ${email}, already exists!`);
    } else {
        throw new Error('Something went wrong in creating new user! Please try again');
    }
};