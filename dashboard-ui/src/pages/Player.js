import React from 'react';

export default function Player({ match: { params: { id } } }) {
    return (
        <>
            <h2 style={{textAlign: 'center', width: '100%'}}>{ `Player #${id} Details Page` }</h2>
        </>
    )
}