import React from 'react';
import ReactApexChart from 'react-apexcharts';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import PropTypes from 'prop-types';
import { fade, makeStyles } from '@material-ui/core/styles';
import clsx from 'clsx';

const options = {
    chart: { stacked: true, sparkline: { enabled: true } },
    plotOptions: {
        bar: { horizontal: true }
    },
    yaxis: {
        min: -20,
        max: 20
    },
    tooltip: {
        shared: false,
        x: { formatter: val => val },
        y: { formatter: val => Math.abs(val) }
    }
};

const useStyles = makeStyles((theme) => ({
    attr: {
        height: '60px'
    },
    highlighted: {
        borderLeft: '2px solid',
        borderLeftColor: theme.palette.primary.main,
        backgroundColor: fade(theme.palette.primary.main, 0.15)
    },
}));

export default function AttributeComparisonItem({ attrComparisonItem: { attrValues, label }, highlightedAttributes }) {
    const classes = useStyles();
    const isHighlighted = highlightedAttributes.includes(label);
    return (
        <ListItem className={ clsx(classes.attr, {
            [classes.highlighted]: isHighlighted
        })}>
            <ListItemText primary={ label } />
            <ReactApexChart
                options={{
                    ...options,
                    xaxis: {
                        categories: [ label ]
                    }
                }}
                series={ attrValues }
                type='bar'
                width='350'
                height='50'
            />
        </ListItem>
    );
}

AttributeComparisonItem.propTypes = {
    attrComparisonItem: PropTypes.shape({
        attrValues: PropTypes.arrayOf(PropTypes.shape({
            name: PropTypes.string,
            data: PropTypes.array
        })),
        label: PropTypes.string
    }),
    highlightedAttributes: PropTypes.arrayOf(PropTypes.string)
};

