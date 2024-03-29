import { caseFormat } from './constants';

export const capitalizeLabel = (label, format = caseFormat.SNAKE_CASE) => {
    let tokens;
    if (format === caseFormat.CAMEL_CASE) {
        tokens = label.replace(/([a-z])([A-Z])/g, '$1 $2').toLowerCase().split(' ');
    } else {
        tokens = label.split('_');
    }
    return tokens
        .map(word => word[0].toUpperCase() + word.slice(1))
        .join(' ');
};

const metadataComparator = (x, y) =>
    x.metadata.sortValue < y.metadata.sortValue ? -1 : x.metadata.sortValue > y.metadata.sortValue ? 1 : 0;
const defaultComparator = (x, y) => (x.data < y.data ? -1 : x.data > y.data ? 1 : 0);

const compare = (row1, row2, sortOrder, cellLabel) => {
    const cell1 = row1.find(cell => cell.id === cellLabel);
    const cell2 = row2.find(cell => cell.id === cellLabel);

    let comparator = null;

    // we can assume that the two cells are from the same column
    // so we can just take the comparator method from one of them
    switch (cell1.type) {
    case 'icon':
    case 'image':
    case 'chart':
        comparator = metadataComparator;
        break;
    default:
        comparator = defaultComparator;
    }

    return sortOrder === 'asc' ? comparator(cell1, cell2) : -comparator(cell1, cell2);
};

export const stableSortList = (array, sortOrder, columnNameToOrderBy) => {
    // no need to sort if no column has been chosen to sort on
    if (columnNameToOrderBy === '') {
        return array;
    }

    const orderedArray = array.map((el, _idx) => [el, _idx]);

    orderedArray.sort((a, b) => {
        const order = compare(a[0], b[0], sortOrder, columnNameToOrderBy);

        // if the elements are not equal, return if one was bigger than the other
        if (order !== 0) return order;

        // if they are equal, use the position/order to sort the elements
        return a[1] - b[1];
    });

    // return the array with the order information
    // eslint-disable-next-line no-unused-vars
    return orderedArray.map(([el, _]) => el);
};

export const convertCamelCaseToSnakeCase = camelCaseString =>
    camelCaseString.replace(/([a-z])([A-Z])/g, '$1_$2').toLowerCase();

export const formatNumberWithCommas = number =>
    number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');

export const transformIntoTabularData = (rawData, headers, filterByColumnNameFn, tranformToRowDataFn) => {
    const tabularData = headers.map(columnName =>
        rawData.filter(item => filterByColumnNameFn(item, columnName))
            .map(childData => tranformToRowDataFn(childData))
    );

    const maxRows = Math.max(...tabularData.map(row => row.length));

    const rows = [...Array(maxRows)].map((_, i) =>
        [...Array(headers.length)].map((_, j) =>
            i >= tabularData[j].length ? null : tabularData[j][i]
        )
    );

    return {
        headers,
        rows
    };
};

export const buildChartPalette = (theme= null, themePreference = 'light') => {
    const lightPalette = ['#003f5c', '#2f4b7c', '#665191', '#a05195', '#d45087', '#f95d6a', '#ff7c43', '#ffa600'];
    const darkPalette = [
        '#ebff93',
        '#f2e2f6',
        '#ffdfcb',
        '#c2fe90',
        '#feafca',
        '#c3d9fd',
        '#74fd8a',
        '#76c5fc',
        '#fd82c7'
    ];

    let chartPalette;
    if (theme != null) {
        chartPalette =[ theme.palette.primary.main, theme.palette.secondary.main];
    } else {
        chartPalette = themePreference === 'dark' ? darkPalette : lightPalette;
    }

    return {
        getPaletteColor: idx => chartPalette[idx % chartPalette.length]
    };
};