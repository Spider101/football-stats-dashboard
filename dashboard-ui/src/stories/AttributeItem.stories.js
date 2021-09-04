import React from 'react';
import faker from 'faker';

import AttributeItem from '../components/AttributeItem';

import { getAttributeItemData } from './utils/storyDataGenerators';

export default {
    component: AttributeItem,
    title: 'Components/PlayerAttributeTable/AttributeItem',
    parameters: {
        docs: {
            description: {
                component: 'UI Component for the smallest functional block used in composing the'
                + ' `PlayerAttributeTable` widget. It consists of the attribute name, an icon depicting the direction'
                + ' of the attribute\'s growth and the attribute value.'
            }
        }
    }
};

const highlightedAttribute = faker.hacker.noun();

const Template = args => <AttributeItem { ...args } />;

export const Default = Template.bind({});
Default.args = getAttributeItemData(faker.hacker.noun());

export const Highlighted = Template.bind({});
Highlighted.args = getAttributeItemData(highlightedAttribute, [ highlightedAttribute] );