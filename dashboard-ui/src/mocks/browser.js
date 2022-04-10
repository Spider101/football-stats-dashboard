import { setupWorker } from 'msw';
import {
    getClubHandlers,
    getFileUploadHandlers,
    getLookupDataHandlers,
    getPlayerHandlers,
    getUserHandlers
} from './handlers';

const handlers = [
    ...getUserHandlers(),
    ...getClubHandlers(),
    ...getPlayerHandlers(),
    ...getLookupDataHandlers(),
    ...getFileUploadHandlers()
];
export const worker = setupWorker(...handlers);