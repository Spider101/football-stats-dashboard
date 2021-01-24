const { formatMs } = require('@material-ui/core');
var faker = require('faker');
var _ = require('lodash');

const nations = ['France', 'Germany', 'Spain', 'Netherlands'];
const moraleList = ['Angry', 'Happy'];

const MAX_ATTR_VALUE = 20;
const MAX_OVERALL_VALUE = 100;

function getRandomNumberInRange(upper, lower = 0) {
    return Math.round(Math.random() * upper) + lower;
}

function getSquadHubPlayerData(numPlayers, nationsList, moraleList) {
    const playerData = [];
    for (let i = 0; i < numPlayers; i++) {
        const formList = [];
        for (let j = 0; j < 5; j++) {
            formList.push(getRandomNumberInRange(MAX_ATTR_VALUE));
        }

        const player = {
            playerId: i,
            name: faker.name.findName(),
            nationality: _.sample(nationsList),
            role: faker.hacker.noun(),
            wages: getRandomNumberInRange(1000, 100),
            form: formList,
            morale: _.sample(moraleList),
            current_ability: getRandomNumberInRange(MAX_OVERALL_VALUE, 1)
        }
        playerData.push(player);
    }
    return playerData;
}

module.exports = () => {
    const playerData = getSquadHubPlayerData(10, nations, moraleList)
    const data = { users: [], players: playerData }
    // Create 1000 users
    for (let i = 0; i < 1000; i++) {
        data.users.push({ id: i, name: faker.hacker.noun() })
    }
    return data
}