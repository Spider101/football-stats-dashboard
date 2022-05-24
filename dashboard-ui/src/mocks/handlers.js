import { rest } from 'msw';

import { AUTH_DATA_LS_KEY } from '../constants';
import { countryFlagMetadata } from '../stories/utils/storyDataGenerators';

import dummyImage from './dummyImage.jpg';

const mockAuthData = {
    id: 'fakeAuthToken',
    userId: 'fakeUserId'
};

const dummyPlayer = {
    id: 'fd4ba2dc-d6bf-49c0-92cf-d6165e6c2b9e',
    metadata: {
        name: 'Sander Gard Bolin Berge',
        country: 'Norway',
        club: 'Boehm, Braun and Stokes',
        countryLogo: 'https://flagcdn.com/w40/no.png',
        age: 23
    },
    roles: [
        {
            name: 'defensive central midfielder',
            associatedAttributes: [
                'interceptions',
                'defensive awareness',
                'aggression',
                'stamina',
                'stand tackle',
                'slide tackle'
            ]
        },
        {
            name: 'central midfielder',
            associatedAttributes: ['long passing', 'vision', 'short passing']
        }
    ],
    ability: {
        current: 11,
        history: [11]
    },
    attributes: [
        {
            name: 'freekickAccuracy',
            category: 'Technical',
            group: 'Attacking',
            value: 2,
            history: [2]
        },
        {
            name: 'penalties',
            category: 'Technical',
            group: 'Attacking',
            value: 9,
            history: [9]
        },
        {
            name: 'headingAccuracy',
            category: 'Technical',
            group: 'Aerial',
            value: 3,
            history: [3]
        },
        {
            name: 'crossing',
            category: 'Technical',
            group: 'Attacking',
            value: 1,
            history: [1]
        },
        {
            name: 'shortPassing',
            category: 'Technical',
            group: 'Vision',
            value: 16,
            history: [16]
        },
        {
            name: 'longPassing',
            category: 'Technical',
            group: 'Vision',
            value: 19,
            history: [19]
        },
        {
            name: 'longShots',
            category: 'Technical',
            group: 'Attacking',
            value: 16,
            history: [16]
        },
        {
            name: 'finishing',
            category: 'Technical',
            group: 'Attacking',
            value: 2,
            history: [2]
        },
        {
            name: 'volleys',
            category: 'Technical',
            group: 'Attacking',
            value: 18,
            history: [18]
        },
        {
            name: 'ballControl',
            category: 'Technical',
            group: 'Attacking',
            value: 9,
            history: [9]
        },
        {
            name: 'standingTackle',
            category: 'Technical',
            group: 'Defending',
            value: 8,
            history: [8]
        },
        {
            name: 'slidingTackle',
            category: 'Technical',
            group: 'Defending',
            value: 3,
            history: [3]
        },
        {
            name: 'dribbling',
            category: 'Technical',
            group: 'Attacking',
            value: 7,
            history: [7]
        },
        {
            name: 'curve',
            category: 'Technical',
            group: 'Attacking',
            value: 19,
            history: [19]
        },
        {
            name: 'stamina',
            category: 'Physical',
            group: 'Defending',
            value: 19,
            history: [19]
        },
        {
            name: 'jumping',
            category: 'Physical',
            group: 'Aerial',
            value: 14,
            history: [14]
        },
        {
            name: 'strength',
            category: 'Physical',
            group: 'Defending',
            value: 17,
            history: [17]
        },
        {
            name: 'sprintSpeed',
            category: 'Physical',
            group: 'Speed',
            value: 8,
            history: [8]
        },
        {
            name: 'acceleration',
            category: 'Physical',
            group: 'Speed',
            value: 5,
            history: [5]
        },
        {
            name: 'agility',
            category: 'Physical',
            group: 'Speed',
            value: 17,
            history: [17]
        },
        {
            name: 'balance',
            category: 'Physical',
            group: 'Speed',
            value: 12,
            history: [12]
        },
        {
            name: 'aggression',
            category: 'Mental',
            group: 'Attacking',
            value: 0,
            history: [0]
        },
        {
            name: 'vision',
            category: 'Mental',
            group: 'Vision',
            value: 19,
            history: [19]
        },
        {
            name: 'composure',
            category: 'Mental',
            group: 'Attacking',
            value: 10,
            history: [10]
        },
        {
            name: 'defensiveAwareness',
            category: 'Mental',
            group: 'Defending',
            value: 11,
            history: [11]
        },
        {
            name: 'attackingPosition',
            category: 'Mental',
            group: 'Attacking',
            value: 14,
            history: [14]
        }
    ]
};

const squadPlayers = [
    {
        name: dummyPlayer.metadata.name,
        country: dummyPlayer.metadata.country,
        countryFlag: dummyPlayer.metadata.countryFlagUrl,
        role: dummyPlayer.roles[0].name,
        currentAbility: dummyPlayer.ability.current,
        recentForm: [],
        playerId: dummyPlayer.id
    }
];

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
        logo: 'fake_club_logo.jpeg',
        name: 'Chelsea F.C',
        transferBudget: 5000000,
        wageBudget: 300000,
        income: 10000000,
        expenditure: 100000,
        createdDate: '2021-02-20'
    };

    const clubSummaries = [];
    clubSummaries.push({
        clubId: dummyClub.id,
        name: dummyClub.name,
        logo: dummyClub.logo,
        createdDate: dummyClub.createdDate
    });

    return [
        rest.get(`${baseUrl}/club/all`, (req, res, ctx) => {
            return res(ctx.status(200), ctx.json(clubSummaries));
        }),
        rest.get(`${baseUrl}/club/${clubIdFragment}`, (req, res, ctx) => {
            return res(ctx.status(200), ctx.json(dummyClub));
        }),
        rest.post(`${baseUrl}/club`, (req, res, ctx) => {
            const newClub = { id: 'new-club-id', createdDate: '2022-04-28', ...req.body };

            clubSummaries.push({
                id: newClub.id,
                name: newClub.name,
                logo: newClub.logo,
                createdDate: newClub.createdDate
            });

            return res(ctx.status(201), ctx.json(newClub));
        }),
        rest.get(`${baseUrl}/club/${clubIdFragment}/squadPlayers`, (req, res, ctx) => {
            return res(
                ctx.status(200),
                ctx.json(squadPlayers)
            );
        })
    ];
};

export const getBoardObjectiveHandlers = (baseUrl = '*', clubIdFragment = ':clubId') => {
    const allBoardObjectives = [];
    return [
        rest.post(`${baseUrl}/club/${clubIdFragment}/board-objective`, (req, res, ctx) => {
            const newBoardObjective = { id: 'new-board-objective-id', ...req.body };
            allBoardObjectives.push(newBoardObjective);
            return res(ctx.status(201), ctx.json(newBoardObjective));
        }),
        rest.get(`${baseUrl}/club/${clubIdFragment}/board-objective/all`, (req, res, ctx) => {
            return res(ctx.status(200), ctx.json(allBoardObjectives));
        })
    ];
};

export const getPlayerHandlers = (baseUrl = '*') => {
    return [
        rest.post(`${baseUrl}/players`, (req, res, ctx) => {
            const newPlayer = { id: 'new-player-id', ...req.body };

            const countryFlagUrl = countryFlagMetadata.find(
                metadata => metadata.countryName === newPlayer.metadata.country
            ).countryFlagUrl;
            const currentAbility =
                newPlayer.attributes.reduce((sum, attribute) => sum + attribute.value) / newPlayer.attributes.length;
            squadPlayers.push({
                name: newPlayer.metadata.name,
                country: newPlayer.metadata.country,
                countryFlag: countryFlagUrl,
                role: newPlayer.roles[0].name,
                currentAbility,
                recentForm: [],
                playerId: newPlayer.id
            });

            return res(ctx.status(201), ctx.json(newPlayer));
        }),

        rest.get(`${baseUrl}/players/*`, (req, res, ctx) => {
            return res(
                ctx.status(200),
                ctx.json(dummyPlayer)
            );
        })
    ];
};

export const getLookupDataHandlers = (baseUrl = '*') => {
    return [
        rest.get(`${baseUrl}/lookup/countryFlags`, (req, res, ctx) => {
            return res(ctx.status(200), ctx.json(countryFlagMetadata));
        })
    ];
};

export const getFileUploadHandlers = (baseUrl = '*') => {
    return [
        rest.post(`${baseUrl}/file-storage/image/upload`, (req, res, ctx) => {
            return res(ctx.status(201), ctx.json({ fileKey: 'sample file.png' }));
        }),
        rest.get(`${baseUrl}/file-storage/image/*`, async (req, res, ctx) => {
            const imageBuffer = await fetch(dummyImage).then(res => res.arrayBuffer());
            return res(
                ctx.status(200),
                ctx.set('Content-Type', 'image/jpeg'),
                ctx.set('Content-Length', imageBuffer.byteLength.toString()),
                ctx.body(imageBuffer)
            );
        })
    ];
};