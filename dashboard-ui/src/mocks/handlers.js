import { rest } from 'msw';

import { AUTH_DATA_LS_KEY } from '../constants';
import { countryFlagMetadata } from '../stories/utils/storyDataGenerators';

const mockAuthData = {
    id: 'fakeAuthToken',
    userId: 'fakeUserId'
};

export const getUserHandlers = (baseUrl = '*', userIdFragment = ':userId', isForServer = false) => {
    if (isForServer && localStorage.getItem(AUTH_DATA_LS_KEY) === null) {
        localStorage.setItem(AUTH_DATA_LS_KEY, JSON.stringify(mockAuthData));
    }
    return [
        rest.post(`${baseUrl}/users/authenticate`, (req, res, ctx) => {
            localStorage.setItem(AUTH_DATA_LS_KEY, JSON.stringify(mockAuthData));
            return res(ctx.status(200), ctx.json(mockAuthData));
        }),
        rest.get(`${baseUrl}/users/${userIdFragment}`, (req, res, ctx) => {
            return res(
                ctx.status(200),
                ctx.json({
                    firstName: 'fake first name',
                    lastName: 'fake last name',
                    email: 'fake email address'
                })
            );
        })
    ];
};

export const getClubHandlers = (baseUrl = '*', clubIdFragment = ':clubId') => {
    const dummyClub = {
        id: 'd7b2772f-699b-408b-bead-bb21e7761115',
        name: 'Chelsea F.C',
        transferBudget: 5000000,
        wageBudget: 300000,
        income: 10000000,
        expenditure: 100000,
        createdDate: '2021-02-20'
    };
    const dummyClubSummary = {
        clubId: dummyClub.id,
        name: dummyClub.name,
        createdDate: dummyClub.createdDate
    };
    return [
        rest.get(`${baseUrl}/club/all`, (req, res, ctx) => {
            return res(ctx.status(200), ctx.json([dummyClubSummary]));
        }),
        rest.get(`${baseUrl}/club/${clubIdFragment}`, (req, res, ctx) => {
            return res(ctx.status(200), ctx.json(dummyClub));
        }),
        rest.post(`${baseUrl}/club`, (req, res, ctx) => {
            return res(ctx.status(201), ctx.json({ id: 'new-club-id', ...req.body }));
        }),
        rest.get(`${baseUrl}/club/${clubIdFragment}/squadPlayers`, (req, res, ctx) => {
            return res(
                ctx.status(200),
                ctx.json([
                    {
                        name: 'Sander Gard Bolin Berge',
                        country: 'Norway',
                        countryFlag: 'https://flagcdn.com/w40/no.png',
                        role: 'Defensive Central Midfielder',
                        currentAbility: 97,
                        recentForm: [],
                        playerId: '03b3d2be-0e71-4d25-8ed6-f7a204779dbb'
                    }
                ])
            );
        })
    ];
};

export const getPlayerHandlers = (baseUrl = '*') => {
    return [
        rest.post(`${baseUrl}/players`, (req, res, ctx) => {
            return res(ctx.status(201), ctx.json({ id: 'new-player-id', ...req.body }));
        })
    ];
};

export const getLookupDataHandlers = (baseUrl = '*') => {
    return [
        rest.get(`${baseUrl}/lookup/countryFlags`, (req, res, ctx) => {
            return res(
                ctx.status(200),
                ctx.json(countryFlagMetadata)
            );
        })
    ];
};