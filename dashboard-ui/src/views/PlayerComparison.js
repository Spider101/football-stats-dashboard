import React from 'react';
import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';

import AttributeComparisonTable from '../widgets/AttributeComparisonTable';

const createAttributeComparisonData = (attributeGroupList1, attributeGroupList2, playerNames) => {

    const maxRows = Math.max( ...attributeGroupList1.map(attrGroup => attrGroup.attributesInGroup.length));

    let tabularAttributeData = [ ...Array(maxRows) ].map((_, i) => (
        [ ...Array(attributeGroupList1.length) ].map((_, j) => {
            const currentAttributeGroup1 = attributeGroupList1[j].attributesInGroup;
            const currentAttributeGroup2 = attributeGroupList2[j].attributesInGroup;

            return i <= currentAttributeGroup1.length ? {
                attrComparisonItem: {
                    attrValues: [{
                        name: playerNames[0].split(' ')[1],
                        data: [ -1 * currentAttributeGroup1[i]['value'] ]
                    }, {
                        name: playerNames[1].split(' ')[1],
                        data: [ currentAttributeGroup2[i]['value'] ]
                    }],
                    label: currentAttributeGroup1[i]['name']
                },
                isHighlighted: false
            } : null;
        })
    ));

    return {
        headers: attributeGroupList1.map(attrGroup => attrGroup.groupName),
        rows: tabularAttributeData
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
            playerAttributes: PropTypes.arrayOf(
                PropTypes.shape({
                    groupName: PropTypes.string,
                    attributesInGroup: PropTypes.arrayOf(
                        PropTypes.shape({
                            name: PropTypes.string,
                            value: PropTypes.number
                        })
                    )
                })
            )
        })
    )
};