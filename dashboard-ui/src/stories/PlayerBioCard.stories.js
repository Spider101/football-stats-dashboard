import React from 'react';
import PlayerBioCard from '../components/PlayerBioCard';
import faker from 'faker';

export default {
    component: PlayerBioCard,
    title: 'PlayerBioCard',
    excludeStories: /.*data$/
};

// TODO: move the faker utility functions to a separate file
export const getPlayerMetadata = () => ({
    name: faker.name.findName(),
    dob: faker.date.past(),
    club: faker.company.companyName(),
    country: faker.address.country(),
    photo: `${faker.image.people()}?random=${Math.round(Math.random() * 20)}`,
    clubLogo: `${faker.image.abstract()}?random=${Math.round(Math.random() * 20)}`,
    countryLogo: `${faker.image.avatar()}?random=${Math.round(Math.random() * 20)}`,
    age:  ' (' + faker.random.number({ 'min': 16, 'max': 35 }) + ' years old)'
});

export const Default = () => <PlayerBioCard { ...getPlayerMetadata() } />;