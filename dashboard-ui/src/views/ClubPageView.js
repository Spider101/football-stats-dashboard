import PropTypes from 'prop-types';

import ReactApexChart from 'react-apexcharts';

// TODO: remove this once the fake data generators are replaced
import faker from 'faker';

import Grid from '@material-ui/core/Grid';
import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import CardContent from '@material-ui/core/CardContent';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Typography from '@material-ui/core/Typography';

import LeagueTable from '../widgets/LeagueTable';
import CardWithChart from '../widgets/CardWithChart';

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
                    <ClubFinancesChart />
                    <ClubSuccessChart />
                </Grid>
            </Grid>
            <Grid container spacing={2}>
                <Grid item xs>
                    <BoardObjectives />
                </Grid>
                <Grid item xs>
                    <TransferBudgetChart />
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

const ClubFinancesChart = () => {
    // TODO: remove this fake data with the real thing
    const clubFinancesData = {
        cardTitle: 'Club Finances',
        chartData: getPlayerProgressionData(1, 'Income', MAX_ATTR_VALUE),
        dataTransformer: x => x,
        chartOptions: {
            stroke: { width: 2, curve: 'straight' },
            plotOptions: { bar: { columnWidth: '15%' } },
            dataLabels: { enabled: false },
            legend: { show: false },
            xaxis: {
                title: { text: 'Months', style: { fontFamily: 'Roboto' } },
                categories: [1, 2, 3, 4, 5, 6]
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

const TransferBudgetChart = () => {
    // TODO: remove this fake data with the real thing
    const transferBudgetData = {
        cardTitle: 'Budget for EY 2021',
        chartData: [25, 60, 15],
        dataTransformer: x => x,
        chartOptions: {
            labels: ['Scouting', 'Transfers', 'Youth Academy']
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

const BoardObjectives = () => {
    // TODO: remove this fake data with the real thing
    const boardObjectives = [...Array(5)].map(() => ({
        title: faker.lorem.sentence(),
        description: faker.lorem.paragraph()
    }));
    return (
        <Card>
            <CardHeader title='Board Objectives' style={{ paddingBottom: 0 }} />
            <CardContent style={{ paddingTop: 0, paddingBottom: 0 }}>
                <List>
                    {boardObjectives.map((objective, idx) => {
                        return (
                            <ListItem key={idx} disableGutters divider={idx < boardObjectives.length - 1}>
                                <ListItemText
                                    primary={<Typography variant='h6'>{objective.title}</Typography>}
                                    secondary={objective.description}
                                />
                            </ListItem>
                        );
                    })}
                </List>
            </CardContent>
        </Card>
    );
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