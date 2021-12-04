import { rest } from 'msw';

const baseUrl = `http://localhost${process.env.REACT_APP_SERVER_ENDPOINT}`;

export const getUserHandlers =  (fakeUserId) => [
    rest.get(`${baseUrl}/users/${fakeUserId}`, (req, res, ctx) => {
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

export const getClubHandlers = () => [
    rest.get(`${baseUrl}/club/all`, (req, res, ctx) => {
        return res(
            ctx.status(200),
            ctx.json([
                {
                    id: 'd7b2772f-699b-408b-bead-bb21e7761115',
                    name: 'Chelsea F.C',
                    transferBudget: 5000000,
                    wageBudget: 300000,
                    income: 10000000,
                    expenditure: 100000
                }
            ])
        );
    }),
    rest.post(`${baseUrl}/club`, (req, res, ctx) => {
        return res(ctx.status(201), ctx.json({ id: 'new-club-id', ...req.body }));
    })
];