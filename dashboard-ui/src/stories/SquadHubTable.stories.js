import React from 'react';
import MoodIcon from '@material-ui/icons/Mood';
import MoodBadIcon from '@material-ui/icons/MoodBad';

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
            {
                id: 'nationality',
                type: 'image',
                data: 'https://assets.stickpng.com/images/580b585b2edbce24c47b2833.png',
                metadata: {
                    sortValue: 'England'
                }
            },
            { id: 'role', type: 'string', data: 'Midfielder' },
            { id: 'wages', type: 'string', data: '$200K' },
            {
                id: 'form',
                type: 'chart',
                data: {
                    type: 'bar',
                    series: [{
                        name: 'form',
                        data: [ ...Array(5) ].map(() => (Math.random() * 10).toFixed(2) + 1)
                    }],
                },
                metadata: {
                    sortValue: 9
                }
            },
            {
                id: 'morale',
                type: 'icon',
                data: <MoodIcon />,
                metadata: {
                    sortValue: 'Happy'
                }
            },
            { id: 'current ability', type: 'number', data: 85 }
        ], [
            { id: 'name', type: 'string', data: 'Todibo' },
            {
                id: 'nationality',
                type: 'image',
                data: 'https://upload.wikimedia.org/wikipedia/commons/6/62/Flag_of_France.png',
                metadata: {
                    sortValue: 'France'
                }
            },
            { id: 'role', type: 'string', data: 'Defender' },
            { id: 'wages', type: 'string', data: '$100K' },
            {
                id: 'form',
                type: 'chart',
                data: {
                    type: 'bar',
                    series: [{
                        name: 'form',
                        data: [ ...Array(5) ].map(() => (Math.random() * 10).toFixed(2) + 1)
                    }],
                },
                metadata: {
                    sortValue: 8
                }
            },
            {
                id: 'morale',
                type: 'icon',
                data: <MoodBadIcon />,
                metadata: {
                    sortValue: 'Angry'
                }
            },
            { id: 'current ability', type: 'number', data: 65 }
        ]
    ]
};

export const Default = () => <SquadHubTable { ...squadHubTableData } />;