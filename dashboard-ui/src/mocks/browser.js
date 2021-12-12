import { setupWorker } from 'msw';
import { getClubHandlers, getPlayerHandlers, getUserHandlers } from './handlers';

const handlers = [ ...getUserHandlers(), ...getClubHandlers(), ...getPlayerHandlers()];
export const worker = setupWorker(...handlers);