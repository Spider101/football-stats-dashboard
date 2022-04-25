import { setupWorker } from 'msw';
import {
    getBoardObjectiveHandlers,
    getClubHandlers,
    getFileUploadHandlers,
    getLookupDataHandlers,
    getPlayerHandlers,
    getUserHandlers
} from './handlers';

const handlers = [
    ...getUserHandlers(),
    ...getClubHandlers(),
    ...getBoardObjectiveHandlers(),
    ...getPlayerHandlers(),
    ...getLookupDataHandlers(),
    ...getFileUploadHandlers()
];
export const worker = setupWorker(...handlers);