import React from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';

import AttributeComparisonTable from '../widgets/AttributeComparisonTable';

const createAttributeComparisonData = (attr1, attr2, playerNames) => {

    const maxRows = Math.max(
        ...Object.values(attr1).map(attr => attr.length)
    );

    const attrData1 = Object.values(attr1);
    const attrData2 = Object.values(attr2);

    let matr = [ ...Array(maxRows) ].map((_, i) => (
        [ ...Array(Object.keys(attr1).length) ].map((_, j) => {
            return i <= attrData1[j].length ? {
                attrComparisonItem: {
                    attrValues: [{
                        name: playerNames[0],
                        data: attrData1[j][i]['attr']
                    }, {
                        name: playerNames[1],
                        data: attrData2[j][i]['attr']
                    }],
                    label: attrData1[j][i]['attr']
                },
                isHighlighted: false
            } : null;
        })
    ));

    return {
        headers: Object.keys(attr1),
        rows: matr
    };
};

export default function PlayerComparisonView({ players }) {
    const playerA = players.find(player => player.isSelected && player.orientation == 'LEFT');
    
    const playerB = players.find(player => player.isSelected && player.orientation == 'RIGHT');

    const attributeComparisonData = createAttributeComparisonData(playerA.playerAttributes, playerB.playerAttributes,
        [ playerA.playerMetadata.name, playerB.playerMetadata.name ]);

    return (
        <div>
            <Grid container spacing={2}>
                <Grid item xs={6}>
                    <Paper>{ playerA.playerMetadata.name }</Paper>
                </Grid>
                <Grid item xs={6}>
                    <Paper>{ playerB.playerMetadata.name }</Paper>
                </Grid>
            </Grid>
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <AttributeComparisonTable { ...attributeComparisonData } />
                </Grid>
            </Grid>
        </div>
    );
}

PlayerComparisonView.propTypes = {
    players: PropTypes.arrayOf(
        PropTypes.shape({
            isSelected: PropTypes.bool,
            orientation: PropTypes.string,
            playerMetadata: PropTypes.shape({
                name: PropTypes.string,
                image: PropTypes.string,
            }),
            playerAttributes: PropTypes.shape({
                physical: PropTypes.arrayOf(
                    PropTypes.shape({
                        name: PropTypes.string,
                        data: PropTypes.number
                    })
                ),
                technical: PropTypes.arrayOf(
                    PropTypes.shape({
                        name: PropTypes.string,
                        data: PropTypes.number
                    })
                ),
                mental: PropTypes.arrayOf(
                    PropTypes.shape({
                        name: PropTypes.string,
                        attr: PropTypes.number
                    })
                )
            })
        })
    )
};