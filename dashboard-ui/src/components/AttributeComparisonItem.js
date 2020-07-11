import React from 'react';
import ReactApexChart from 'react-apexcharts';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import PropTypes from 'prop-types';
import { fade, makeStyles } from '@material-ui/core/styles';
import clsx from 'clsx';

const options = {
    chart: { stacked: true, toolbar: { show: false } },
    xaxis: {
        labels: { show: false },
        axisTicks: { show: false },
        axisBorder: { show: false }
    },
    plotOptions: {
        bar: { horizontal: true }
    },
    dataLabels: { enabled: false },
    grid: { show: false },
    legend: { show: false },
    yaxis: {
        min: -20,
        max: 20,
        labels: { show: false }
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
    attrLabel: {
        maxWidth: '10%',
    },
    highlighted: {
        borderLeft: '2px solid',
        borderLeftColor: theme.palette.primary.main,
        backgroundColor: fade(theme.palette.primary.main, 0.15)
    },
}));

export default function AttributeComparisonItem({ attrComparisonItem: { attrValues, label }, isHighlighted }) {
    const classes = useStyles();
    return (
        <ListItem className={ clsx(classes.attr, {
            [classes.highlighted]: isHighlighted
        })}>
            <ListItemText className={ classes.attrLabel } primary={ label } />
            <ReactApexChart
                options={{
                    ...options,
                    xaxis: {
                        ...options.xaxis,
                        categories: [ label ]
                    }
                }}
                series={ attrValues }
                type='bar'
                width='500'
                height='100'
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
    isHighlighted: PropTypes.bool
};

