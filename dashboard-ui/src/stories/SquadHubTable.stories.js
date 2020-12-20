import React from 'react';
import SquadHubTable from '../widgets/SquadHubTable';

export default {
    component: SquadHubTable,
    title: 'Widgets/SquadHubView/SquadHubTable',
    excludeStories: /.*Data$/
};

const squadHubTableData = {
    headers: [
        { id: 'name', type: 'string' },
        { id: 'nationality', type: 'image' },
        { id: 'role', type: 'string' },
        { id: 'wages', type: 'string' },
        { id: 'form', type: 'chart' },
        { id: 'morale', type: 'icon' },
        { id: 'current ability', type: 'number' }
    ],
    rows: [
        [
            { id: 'name', type: 'string', data: 'Phil Foden' },
            { id: 'nationality', type: 'string', data: 'England' },
            { id: 'role', type: 'string', data: 'Midfielder' },
            { id: 'wages', type: 'string', data: '$200K' },
            { id: 'form', type: 'string', data: 'Good' },
            { id: 'morale', type: 'string', data: 'Happy' },
            { id: 'current ability', type: 'number', data: 85 }
        ], [
            { id: 'name', type: 'string', data: 'Phil Jones' },
            { id: 'nationality', type: 'string', data: 'England' },
            { id: 'role', type: 'string', data: 'Defender' },
            { id: 'wages', type: 'string', data: '$100K' },
            { id: 'form', type: 'string', data: 'Bad' },
            { id: 'morale', type: 'string', data: 'Angry' },
            { id: 'current ability', type: 'number', data: 65 }
        ]
    ]
};

export const Default = () => <SquadHubTable { ...squadHubTableData } />;