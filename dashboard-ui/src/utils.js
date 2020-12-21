const ascComparator = (a, b, propName) => {
    const x = a.find(el => el.id === propName);
    const y = b.find(el => el.id === propName);

    return x.data < y.data ? -1 : x.data > y.data ? 1 : 0;
};

export const capitalizeLabel = (label) => {
    return label.split(' ')
        .map(word => word[0].toUpperCase() + word.slice(1))
        .join(' ');
};

export const getComparator = (order, orderBy) => {
    return order === 'asc'
        ? (a, b) => ascComparator(a, b, orderBy)
        : (a, b) => -ascComparator(a, b, orderBy);
};

export const stableSortList = (array, comparator) => {
    const orderedArray = array.map((el, _idx) => [ el, _idx]);

    orderedArray.sort((a, b) => {
        const order = comparator(a[0], b[0]);

        // if the elements are not equal, return if one was bigger than the other
        if (order !== 0) return order;

        // if they are equal, use the position/order to sort the elements
        return a[1] - b[1];
    });

    // return the array with the order information
    // eslint-disable-next-line no-unused-vars
    return orderedArray.map(([el, _]) => el);
};