import React from 'react';
import faker from 'faker';
import _ from 'lodash';

import CardWithFilter from '../widgets/CardWithFilter';
import FilterControl from '../components/FilterControl';

export default {
    component: CardWithFilter,
    title: 'Widgets/PlayerComparisonView/CardWithFilter',
    // exclude stories starting with lower case letter
    excludeStories: /^[a-z].*/
};

const filterControlProps = {
    allPossibleValues: [ ...Array(10) ].map((_, _idx) => ({ id: _idx, text: faker.hacker.noun()})),
    currentValue: -1,
    handleChangeFn: x => x,
    labelIdFragment: 'players',
    inputLabelText: 'players',
    helperText: 'Choose player to compare against'
};

const filterControlPropsWithSelectedValue = {
    ...filterControlProps,
    currentValue: _.sample(filterControlProps.allPossibleValues).id
};

export const filterControl = <FilterControl { ...filterControlProps } />;
const filterControlWithSelectedValue = <FilterControl { ...filterControlPropsWithSelectedValue } />;

const Template = args => <CardWithFilter { ...args } />;

export const Default = Template.bind({});
Default.args = {
    filterControl
};

export const Selected = Template.bind({});
Selected.args = {
    filterControl: filterControlWithSelectedValue
};