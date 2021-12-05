import { hacker } from 'faker';
import _ from 'lodash';

import CardWithFilter from '../widgets/CardWithFilter';
import FilterControl from '../components/FilterControl';

export default {
    component: CardWithFilter,
    title: 'Widgets/PlayerComparisonView/CardWithFilter',
    argTypes: {
        filterControl: { table: { disable: true } }
    },
    parameters: {
        docs: {
            description: {
                component: 'Widget for housing a filter control element. The filter control element is dynamically' 
                + ' passed into the widget.'
            }
        }
    }
};


const Template = (args) => (
    <CardWithFilter filterControl={<FilterControl {...args} />} />
);

export const Default = Template.bind({});
Default.args = {
    allPossibleValues: [...Array(10)].map((_, _idx) => ({ id: _idx, text: hacker.noun() })),
    currentValue: -1,
    handleChangeFn: x => x,
    labelIdFragment: 'players',
    inputLabelText: 'players',
    helperText: 'Choose player to compare against'
};

export const Selected = Template.bind({});
Selected.args = {
    ...Default.args,
    currentValue: _.sample(Default.args.allPossibleValues).id
};