import React from 'react';
import EnhancedTableHeader from '../components/EnhancedTableHeader';

import { action } from '@storybook/addon-actions';

export default {
    component: EnhancedTableHeader,
    title: 'Components/SquadHubView/EnhancedTableHeader',
    excludeStories: /.*Data$/
};

const defaultData = {
    headerCells: [
        {
            id: 'left aligned cell',
            label: 'Left-Aligned Table Header Cell',
            alignment: 'left'
        },
        {
            id: 'center aligned cell',
            label: 'Center-Aligned Table Header Cell',
            alignment: 'center'
        },
        {
            id: 'right aligned cell',
            label: 'Right-Aligned Table Header Cell',
            alignment: 'right'
        }
    ],
    order: 'asc',
    orderBy: '', // this ensures none of the provided table header cells get sorted
    onRequestSort: action('sort requested on this column')
};

const sortedAscData = {
    headerCells: [
        {
            id: 'table header cell to sort asc',
            label: 'Table Header Cell to Sort Ascending',
            alignment: 'left'
        },
        {
            id: 'table header cell with no sorting',
            label: 'Table Header Cell with No Sorting',
            alignment: 'right'
        }
    ],
    order: 'asc',
    orderBy: 'table header cell to sort asc',
    onRequestSort: action('sort requested on this column')
};

const sortedDescData = {
    headerCells: [
        {
            id: 'table header cell to sort desc',
            label: 'Table Header Cell to Sort Descending',
            alignment: 'left'
        },
        {
            id: 'table header cell with no sorting',
            label: 'Table Header Cell with No Sorting',
            alignment: 'right'
        }
    ],
    order: 'desc',
    orderBy: 'table header cell to sort desc',
    onRequestSort: action('sort requested on this column')
};

export const Default = () => (
    <table>
        <EnhancedTableHeader {...defaultData} />
    </table>
);

export const AscendingSortedCell = () => (
    <table>
        <EnhancedTableHeader {...sortedAscData} />
    </table>
);

export const DescendingSortedCell = () => (
    <table>
        <EnhancedTableHeader {...sortedDescData} />
    </table>
);