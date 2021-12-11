import { setupWorker } from 'msw';
import { getClubHandlers, getUserHandlers } from './handlers';

const handlers = [ ...getUserHandlers(), ...getClubHandlers()];
export const worker = setupWorker(...handlers);