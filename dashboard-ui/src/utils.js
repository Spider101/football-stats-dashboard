export const capitalizeLabel = (label) => {
    return label.split(' ')
        .map(word => word[0].toUpperCase() + word.slice(1))
        .join(' ');
};

const iconMetadataComparator = (x, y) =>
    x.metadata.sortValue < y.metadata.sortValue ? -1 : x.metadata.sortValue > y.metadata.sortValue ? 1 : 0;
const imageMetadataComparator = (x, y) =>
    x.metadata.sortValue < y.metadata.sortValue ? -1 : x.metadata.sortValue > y.metadata.sortValue ? 1 : 0;
const chartMetadataComparator = (x, y) =>
    x.metadata.sortValue < y.metadata.sortValue ? -1 : x.metadata.sortValue > y.metadata.sortValue ? 1 : 0;
const defaultComparator = (x, y) => x.data < y.data ? -1 : x.data > y.data ? 1 : 0;

const compare = (row1, row2, sortOrder, cellLabel) => {
    const cell1 = row1.find(cell => cell.id === cellLabel);
    const cell2 = row2.find(cell => cell.id === cellLabel);

    let comparator = null;

    // we can assume that the two cells are from the same column
    // so we can just take the comparator method from one of them
    switch(cell1.type) {
    case 'icon':
        comparator = iconMetadataComparator;
        break;
    case 'image':
        comparator = imageMetadataComparator;
        break;
    case 'chart':
        comparator = chartMetadataComparator;
        break;
    default:
        comparator = defaultComparator;
    }

    return sortOrder === 'asc' ? comparator(cell1, cell2) : -comparator(cell1, cell2);
};

export const stableSortList = (array, sortOrder, columnNameToOrderBy) => {
    const orderedArray = array.map((el, _idx) => [ el, _idx]);

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