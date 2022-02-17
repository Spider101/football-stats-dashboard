import { render, screen } from '@testing-library/react';
import { LineChart, BarChart, AreaChart } from '../stories/CardWithChart.stories';

it.each`
    CardWithChart | cardWithChartArgs
    ${LineChart}      | ${LineChart.args}
    ${BarChart}      | ${BarChart.args}
    ${AreaChart}      | ${AreaChart.args}
`('renders $CardWithChart successfully', ({ CardWithChart, cardWithChartArgs}) => {
    render(<CardWithChart {...cardWithChartArgs}/>);

    expect(screen.getByText(cardWithChartArgs.cardTitle)).toBeInTheDocument();
    expect(screen.getByLabelText(cardWithChartArgs.chartType)).toBeInTheDocument();
});