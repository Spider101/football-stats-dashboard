import React from 'react';
import SimpleFixedTabs from '../components/SimpleFixedTabs';

export default {
    component: SimpleFixedTabs,
    title: 'SimpleFixedTabs',
    excludeStories: /.*Data$/
};
const handleTabChange = () => console.log('hola');

export const Default = () => (
    <SimpleFixedTabs tabValue={0} onTabChange={ handleTabChange }>
        <div>Nothing to see here. Move alongg!</div>
    </SimpleFixedTabs>
)