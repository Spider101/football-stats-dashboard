import PropTypes from 'prop-types';
import ReactApexChart from 'react-apexcharts';

import Grid from '@material-ui/core/Grid';

import LeagueTable from '../widgets/LeagueTable';
import CardWithChart from '../widgets/CardWithChart';
import BoardObjectives from '../widgets/BoardObjectives';

// TODO: remove this once the fake data generators are replaced
import { getLeagueTableData, getPlayerProgressionData, MAX_ATTR_VALUE } from '../stories/utils/storyDataGenerators';

export default function ClubPageView({ club }) {
    return (
        <>
            <Grid container spacing={2}>
                <Grid item xs={4}>
                    <LeagueTable metadata={getLeagueTableData(10)} />
                </Grid>
                <Grid item xs={8} container direction='column'>
                    <ClubFinancesChart income={club.income} expenditure={club.expenditure}/>
                    <ClubSuccessChart />
                </Grid>
            </Grid>
            <Grid container spacing={2}>
                <Grid item xs>
                    {/* TODO: move this into a container function where the useMutation hook and other business logic
                     can be housed. Then pass in actual objectives instead of the default empty list here. */}
                    <BoardObjectives objectives={[]} />
                </Grid>
                <Grid item xs>
                    <BudgetBreakdownChart transferBudget={club.transferBudget} wageBudget={club.wageBudget}/>
                </Grid>
            </Grid>
        </>
    );
}

const ClubSuccessChart = () => {
    // TODO: remove this fake data with the real thing
    const clubSuccessData = {
        cardTitle: 'League History',
        chartData: getPlayerProgressionData(1, 'League Finish', MAX_ATTR_VALUE),
        dataTransformer: x => x,
        chartOptions: {
            stroke: { width: 2, curve: 'straight' },
            legend: { show: false },
            xaxis: {
                title: { text: 'Year', style: { fontFamily: 'Roboto' } },
                categories: [1, 2, 3, 4, 5, 6]
            },
            yaxis: { reversed: true, min: 1 }
        }
    };

    return (
        <Grid item xs>
            <CardWithChart {...clubSuccessData}>
                <ReactApexChart height={260} />
            </CardWithChart>
        </Grid>
    );
};

const ClubFinancesChart = ({ income, expenditure }) => {
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

    // TODO: build full array once income and expenditure history (month-wise) tracking is implemented
    let profit = [ ...Array(months.length).fill(0) ];
    profit[0] = income - expenditure;

    const clubFinancesData = {
        cardTitle: 'Club Finances',
        chartData: [{ name: 'Profit', data: profit }],
        dataTransformer: x => x,
        chartOptions: {
            stroke: { width: 2, curve: 'straight' },
            plotOptions: { bar: { columnWidth: '15%' } },
            dataLabels: { enabled: false },
            legend: { show: false },
            xaxis: {
                title: { text: 'Months', style: { fontFamily: 'Roboto' } },
                categories: months
            }
        },
        chartType: 'bar'
    };

    return (
        <Grid item xs>
            <CardWithChart {...clubFinancesData}>
                <ReactApexChart height={260} />
            </CardWithChart>
        </Grid>
    );
};
ClubFinancesChart.propTypes = {
    income: PropTypes.number,
    expenditure: PropTypes.number
};

const BudgetBreakdownChart = ({ transferBudget, wageBudget }) => {
    const transferBudgetData = {
        cardTitle: 'Budget for EY 2021',
        chartData: [transferBudget, wageBudget],
        dataTransformer: x => x,
        chartOptions: {
            labels: ['Transfers', 'Wages']
        },
        chartType: 'donut'
    };

    return (
        <Grid item xs>
            <CardWithChart {...transferBudgetData}>
                <ReactApexChart height={500} />
            </CardWithChart>
        </Grid>
    );
};
BudgetBreakdownChart.propTypes = {
    transferBudget: PropTypes.number,
    wageBudget: PropTypes.number
};

ClubPageView.propTypes = {
    club: PropTypes.shape({
        id: PropTypes.string,
        name: PropTypes.string,
        transferBudget: PropTypes.number,
        wageBudget: PropTypes.number,
        income: PropTypes.number,
        expenditure: PropTypes.number
    })
};