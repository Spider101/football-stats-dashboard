import { faker } from '@faker-js/faker';
import _ from 'lodash';

import CardWithFilter from '../widgets/CardWithFilter';
import FilterControl from '../components/FilterControl';

export default {
    component: CardWithFilter,
    title: 'Widgets/PlayerComparisonView/CardWithFilter',
    argTypes: {
        filterControl: { control: { disable: true } }
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
    allPossibleValues: [...Array(10)].map(() => ({ id: faker.datatype.uuid(), text: faker.name.findName() })),
    currentValue: '-1',
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