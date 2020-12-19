import React from 'react';
import PropTypes from 'prop-types';
import { Card, CardContent, CardHeader } from '@material-ui/core';

export default function CardWithChart({ cardTitle, chartData, dataTransformer, chartOptions, chartType, children }) {

    const transformedChartData = dataTransformer(chartData);

    const chartProps = {
        options: chartOptions,
        series: transformedChartData,
        type: chartType
    };

    return (
        <Card>
            <CardHeader
                title={ cardTitle }
            />
            <CardContent>
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
    chartData: PropTypes.object,
    dataTransformer: PropTypes.func,
    chartOptions: PropTypes.object,
    chartType: PropTypes.string,
    children: PropTypes.node
};