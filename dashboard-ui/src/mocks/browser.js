import { setupWorker } from 'msw';
import { getClubHandlers, getLookupDataHandlers, getPlayerHandlers, getUserHandlers } from './handlers';

const handlers = [ ...getUserHandlers(), ...getClubHandlers(), ...getPlayerHandlers(), ...getLookupDataHandlers()];
export const worker = setupWorker(...handlers);