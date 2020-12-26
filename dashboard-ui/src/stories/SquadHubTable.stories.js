import React from 'react';
import MoodIcon from '@material-ui/icons/Mood';
import MoodBadIcon from '@material-ui/icons/MoodBad';

import SquadHubTable from '../widgets/SquadHubTable';

import { getSquadHubTableData } from './utils/storyDataGenerators';

export default {
    component: SquadHubTable,
    title: 'Widgets/SquadHubView/SquadHubTable',
    excludeStories: /.*Data$/
};

const nationalityFlagMap = [
    { nationality: 'France', flag: 'https://upload.wikimedia.org/wikipedia/commons/6/62/Flag_of_France.png' },
    { nationality: 'Germany', flag: 'https://freepngimg.com/thumb/germany_flag/1-2-germany-flag-picture.png' },
    { nationality: 'England', flag: 'https://assets.stickpng.com/images/580b585b2edbce24c47b2833.png' },
    { nationality: 'Spain', flag: 'https://freepngimg.com/thumb/spain/5-2-spain-flag-picture.png' },
    { nationality: 'Netherlands', flag: 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/20/'
        + 'Flag_of_the_Netherlands.svg/125px-Flag_of_the_Netherlands.svg.png' },
];

const moraleIconsMap = [
    { morale: 'Angry', icon: <MoodBadIcon /> },
    { morale: 'Happy', icon: <MoodIcon /> }
];

export const Default = () => <SquadHubTable { ...getSquadHubTableData(10, nationalityFlagMap, moraleIconsMap) } />;