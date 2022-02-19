import PropTypes from 'prop-types';

import Grid from '@material-ui/core/Grid';

import LeagueTable from '../widgets/LeagueTable';
import CardWithChart from '../widgets/CardWithChart';
import BoardObjectives from '../widgets/BoardObjectives';

// TODO: remove this once the fake data generators are replaced
import { getCardWithChartData, getLeagueTableData, MAX_ATTR_VALUE } from '../stories/utils/storyDataGenerators';
import { MONTHS } from '../constants';

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
    const clubSuccessDataKeys = ['leagueFinish'];
    const chartOptions = {
        height: 260,
        dataKeys: clubSuccessDataKeys,
        barSize: 80
    };

    return (
        <Grid item xs>
            <CardWithChart
                cardTitle='League History'
                chartType='bar'
                chartData={getCardWithChartData(clubSuccessDataKeys, MONTHS.length, MAX_ATTR_VALUE)}
                chartOptions={chartOptions}
            />
        </Grid>
    );
};

const ClubFinancesChart = ({ income, expenditure }) => {
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

    // TODO: build full array once income and expenditure history (month-wise) tracking is implemented
    const profit = [ income - expenditure, ...Array(months.length).fill(0) ];

    const clubFinancesDataKeys = ['profit'];
    const chartOptions = {
        height: 260,
        fillOpacity: 50,
        dataKeys: clubFinancesDataKeys
    };
    const chartData = [...Array(months.length)].map((_, idx) =>
        Object.fromEntries(clubFinancesDataKeys.map(dataKey => [dataKey, profit[idx]]))
    );

    return (
        <Grid item xs>
            <CardWithChart
                cardTitle='Club Finances'
                chartType='area'
                chartData={chartData}
                chartOptions={chartOptions}
            />
        </Grid>
    );
};
ClubFinancesChart.propTypes = {
    income: PropTypes.number,
    expenditure: PropTypes.number
};

const BudgetBreakdownChart = ({ transferBudget, wageBudget }) => {
    const budgetBreakdownDataKeys = ['value'];
    const chartData = [{
        name: 'Transfers',
        value: transferBudget
    }, {
        name: 'Wages',
        value: wageBudget
    }];
    const chartOptions = {
        height: 500,
        dataKeys: budgetBreakdownDataKeys
    };

    return (
        <Grid item xs>
            <CardWithChart
                cardTitle='Budget for EY 2021'
                chartType='donut'
                chartData={chartData}
                chartOptions={chartOptions}
            />
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