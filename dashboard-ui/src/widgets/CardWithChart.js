import React from 'react';
import PropTypes from 'prop-types';

import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import CardContent from '@material-ui/core/CardContent';

import { useGlobalChartOptions } from '../context/chartOptionsProvider';
export default function CardWithChart({ cardTitle, chartData, dataTransformer, chartOptions, chartType, children }) {
    const globalChartOptions = useGlobalChartOptions();

    const transformedChartData = dataTransformer(chartData);

    const chartProps = {
        options: {
            ...globalChartOptions,
            ...chartOptions
        },
        series: transformedChartData,
        type: chartType
    };

    return (
        <Card>
            <CardHeader title={ cardTitle }/>
            <CardContent style={{ paddingTop: 0, paddingBottom: 0}}>
                {
                    React.Children.map(children, child => {
                        if (React.isValidElement(child)) {
                            return React.cloneElement(child, { ...chartProps });
                        }
                        return child;
                    })
                }
            </CardContent>
        </Card>
    );
}

CardWithChart.propTypes = {
    cardTitle: PropTypes.string,
    chartData: PropTypes.array,
    dataTransformer: PropTypes.func,
    chartOptions: PropTypes.object,
    chartType: PropTypes.string,
    children: PropTypes.node
};