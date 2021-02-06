import React from 'react';
import faker from 'faker';
import _ from 'lodash';

import CardWithFilter from '../widgets/CardWithFilter';

export default {
    component: CardWithFilter,
    title: 'Widgets/PlayerComparisonView/CardWithFilter'
};

const defaultData = {
    allPossibleValues: [ ...Array(10) ].map(() => faker.hacker.noun()),
    currentValue: faker.hacker.noun(),
    handleChangeFn: x => x,
    labelIdFragment: 'players',
    inputLabelText: 'players',
    helperText: 'Choose player to compare against'
};

const Template = args => <CardWithFilter { ...args } />;

export const Default = Template.bind({});
Default.args = {
    filterControl: defaultData
};

export const Selected = Template.bind({});
Selected.args = {
    filterControl: {
        ...defaultData,
        currentValue: _.sample(defaultData.allPossibleValues)
    }
};