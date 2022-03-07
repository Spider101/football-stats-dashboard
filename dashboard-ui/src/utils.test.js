import { faker } from '@faker-js/faker';
import * as _ from 'lodash';

import { transformIntoTabularData } from './utils';

it('creates tabular data with correct number of rows and columns', () => {
    // setup
    const headerNames = [...Array(3)].map(() => faker.hacker.noun());
    const testData = [...Array(3)].map(() => ({ id: faker.datatype.uuid(), type: _.sample(headerNames) }));

    // execute
    const { headers: columnNames, rows } = transformIntoTabularData(
        testData,
        headerNames,
        (x, _) => x,
        x => x
    );

    // assert
    expect(columnNames).toEqual(headerNames);

    expect(rows.length).toBeGreaterThan(0);
    expect(rows[0].length).toBe(columnNames.length);

    // since the filter defined in the function call is just an identity function,
    // the number of rows returned should be the number of entries in the test data collection
    expect(rows.length).toBe(testData.length);
});