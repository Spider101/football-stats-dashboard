import React from 'react';
import faker from 'faker';
import _ from 'lodash';

import CardWithFilter from '../widgets/CardWithFilter';

export default {
    component: CardWithFilter,
    title: 'Widgets/PlayerComparisonView/CardWithFilter'
};

const Template = args => <CardWithFilter { ...args } />;

export const Default = Template.bind({});
Default.args = {
    filterControl: {
        allPossibleValues: [ ...Array(10) ].map((_, _idx) => ({ id: _idx, text: faker.hacker.noun()})),
        currentValue: { id: -1, name: '' },
        handleChangeFn: x => x,
        labelIdFragment: 'players',
        inputLabelText: 'players',
        helperText: 'Choose player to compare against'
    }
};

export const Selected = Template.bind({});
Selected.args = {
    filterControl: {
        ...Default.args.filterControl,
        currentValue: _.sample(Default.args.filterControl.allPossibleValues)
    }
};