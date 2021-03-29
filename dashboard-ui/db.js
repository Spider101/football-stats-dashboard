var faker = require('faker');
var _ = require('lodash');

const nations = ['France', 'Germany', 'Spain', 'Netherlands'];
const moraleList = ['Angry', 'Happy'];

const MAX_ATTR_VALUE = 20;
const MAX_OVERALL_VALUE = 100;
const NUM_MONTHS = 6;

function getRandomNumberInRange(upper, lower = 0) {
    return Math.round(Math.random() * upper) + lower;
}

function getSquadHubPlayerData(playerData, numPlayersInSquad, moraleList) {
    const playersInSquad = _.sampleSize(playerData, numPlayersInSquad);
    const squadPlayerData = [];
    for (let i = 0; i < numPlayersInSquad; i++) {
        const formList = [];
        for (let j = 0; j < 5; j++) {
            formList.push(getRandomNumberInRange(MAX_ATTR_VALUE));
        }

        const squadPlayer = {
            playerId: playersInSquad[i].id,
            name: playersInSquad[i].metadata.name,
            nationality: playersInSquad[i].metadata.country,
            role: faker.hacker.noun(),
            wages: getRandomNumberInRange(1000, 100),
            form: formList,
            morale: _.sample(moraleList),
            currentAbility: playersInSquad[i].ability.current
        };
        squadPlayerData.push(squadPlayer);
    }
    return squadPlayerData;
}

function getPlayerAttributeGroupData(numAttributes) {
    const attributeGroupData = [];

    const defendingAttributes = [];
    const speedAttributes = [];
    const visionAttributes = [];
    const attackingAttributes = [];
    const aerialAttributes = [];

    for (let i = 0; i < numAttributes; i++) {
        defendingAttributes.push(getRandomNumberInRange(MAX_ATTR_VALUE));
        speedAttributes.push(getRandomNumberInRange(MAX_ATTR_VALUE));
        visionAttributes.push(getRandomNumberInRange(MAX_ATTR_VALUE));
        attackingAttributes.push(getRandomNumberInRange(MAX_ATTR_VALUE));
        aerialAttributes.push(getRandomNumberInRange(MAX_ATTR_VALUE));
    }
    attributeGroupData.push({
        groupName: 'Defending',
        attributesInGroup: defendingAttributes
    }, {
        groupName: 'Speed',
        attributesInGroup: speedAttributes
    }, {
        groupName: 'Vision',
        attributesInGroup: visionAttributes
    }, {
        groupName: 'Attacking',
        attributesInGroup: attackingAttributes
    }, {
        groupName: 'Aerial',
        attributesInGroup: aerialAttributes
    });

    return attributeGroupData;

}

function getPlayerAttributeCategoryData(attributeNamesList, hasHistory) {
    const attributeCategoryData = [];

    let technicalAttributes = [];
    let physicalAttributes = [];
    let mentalAttributes = [];

    for (let i = 0; i < 3; i++) {
        const attributeNamesSlice = attributeNamesList.slice(i * 10, (i + 1) * 10);

        const attributeDataList = attributeNamesSlice.map(attributeName => {
            const currentAttributeValue = getRandomNumberInRange(MAX_ATTR_VALUE);
            const attributeValueHistory = [];

            let attributeData = {
                name: attributeName,
                value: currentAttributeValue
            };

            if (hasHistory) {
                for (let j = 0; j < NUM_MONTHS - 1; j++) {
                    attributeValueHistory.push(getRandomNumberInRange(MAX_ATTR_VALUE));
                }
                attributeData = {
                    ...attributeData,
                    history: attributeValueHistory
                };
            }

            return attributeData;
        });

        if (i == 1) {
            technicalAttributes = attributeDataList;
        } else if (i == 2) {
            physicalAttributes = attributeDataList;
        } else {
            mentalAttributes = attributeDataList;
        }
    }

    attributeCategoryData.push({
        categoryName: 'Technical',
        attributesInCategory: technicalAttributes
    }, {
        categoryName: 'Physical',
        attributesInCategory: physicalAttributes,
    }, {
        categoryName: 'Mental',
        attributesInCategory: mentalAttributes
    });

    return attributeCategoryData;
}

function getPlayerRoles(numRoles, attributeNamesList) {
    const roles = faker.lorem.words(numRoles).split(' ');
    let roleToAttributeMapping = {};

    roles.forEach(role => {
        roleToAttributeMapping[role] = _.sampleSize(attributeNamesList, 6);
    });

    return roleToAttributeMapping;
}

function getPlayerData(numPlayers, numAttributes, nationsList) {
    const playerData = [];

    const attributeNamesList = [];
    for (let i = 0; i < numAttributes; i++) {
        attributeNamesList.push(faker.hacker.noun());
    }

    for (let i = 0; i < numPlayers; i++) {
        const currentAbility = getRandomNumberInRange(MAX_OVERALL_VALUE, 1);
        const abilityHistory = [];
        for (let j = 0; j < NUM_MONTHS - 1; j++) {
            abilityHistory.push(getRandomNumberInRange(MAX_OVERALL_VALUE));
        }
        abilityHistory.push(currentAbility);

        const player = {
            id: i,
            metadata: {
                name: faker.name.findName(),
                dob: faker.date.past().toJSON(),
                club: faker.company.companyName(),
                country: _.sample(nationsList),
                photo: `${faker.image.people()}?random=${getRandomNumberInRange(20)}`,
                clubLogo: `${faker.image.abstract()}?random=${getRandomNumberInRange(20)}`,
                countryLogo: `${faker.image.avatar()}?random=${getRandomNumberInRange(20)}`,
                age:  ' (' + faker.random.number({ 'min': 16, 'max': 35 }) + ' years old)'
            },
            roles: getPlayerRoles(3, attributeNamesList),
            attributes: {
                attributeGroups: getPlayerAttributeGroupData(10),
                attributeCategories: getPlayerAttributeCategoryData(attributeNamesList, true)
            },
            ability: {
                current: currentAbility,
                history: abilityHistory
            }

        }

        playerData.push(player);
    }
    return playerData;
}

function getPlayerPerformanceData(playerData, numCompetitions, numMatches) {
    // set the competition names
    const competitions = [];
    for (let i = 0; i < numCompetitions; i++) {
        competitions.push(faker.hacker.noun());
    }

    const performanceData = [];
    for (let i = 0; i < playerData.length; i++) {
        for (let j = 0; j < numCompetitions; j++) {

            const matchRatingHistory = [];
            for (let k = 0; k < numMatches; k++) {
                matchRatingHistory.push(getRandomNumberInRange(10));
            }

            performanceData.push({
                id: (i+j),
                competitionId: competitions[j],
                playerId: playerData[i].id,
                appearances: getRandomNumberInRange(30),
                goals: getRandomNumberInRange(30),
                penalties: getRandomNumberInRange(25),
                assists: getRandomNumberInRange(25),
                playerOfTheMatch: getRandomNumberInRange(10),
                yellowCards: getRandomNumberInRange(25),
                redCards: getRandomNumberInRange(25),
                tackles: getRandomNumberInRange(25),
                passCompletionRate: getRandomNumberInRange(25),
                dribbles: getRandomNumberInRange(25),
                fouls: getRandomNumberInRange(25),
                matchRatingHistory: matchRatingHistory
            });
        }
    }

    return performanceData;
}

function getTransferActivityData(playerData, numTransfers) {
    const transferredPlayers = _.sampleSize(playerData, numTransfers);
    const transferredPlayerData = [];
    for(let i=0; i<numTransfers; i++) {
        transferredPlayerData.push({
            id: i,
            name: transferredPlayers[i].metadata.name,
            role: _.sample(Object.keys(transferredPlayers[i].roles)),
            currentAbility: transferredPlayers[i].ability.current,
            incomingClub: transferredPlayers[i].metadata.club,
            outgoingClub: faker.company.companyName(),
            transferType: _.sample(['Player Swap', 'Loan To Buy', 'Basic']),
            swapPlayer: _.sample(['', faker.name.findName()]),
            fee: getRandomNumberInRange(100000000, 1000000),
            date: faker.date.past().toJSON()
        })
    }

    return transferredPlayerData;
};

module.exports = () => {
    const playerData = getPlayerData(100, 3 * 10, nations);
    const squadPlayerData = getSquadHubPlayerData(playerData, 10, moraleList);
    const playerPerformanceData = getPlayerPerformanceData(playerData, 5, 10);
    const transferActivityData = getTransferActivityData(playerData, 10);
    return {
        players: playerData,
        squadPlayers: squadPlayerData,
        performance: playerPerformanceData,
        transfers: transferActivityData
    };
}