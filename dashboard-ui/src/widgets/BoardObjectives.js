import { useState } from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';

import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';
import List from '@material-ui/core/List';
import Typography from '@material-ui/core/Typography';
import Divider from '@material-ui/core/Divider';
import Button from '@material-ui/core/Button';

import BoardObjective from '../components/BoardObjective';

export default function BoardObjectives({ objectives: initialObjectives }) {
    const [isDirty, setIsDirty] = useState(false);
    const [objectives, setObjectives] = useState(initialObjectives);

    const completeObjective = objectiveId => {
        const updatedObjectives = objectives.map(objective => {
            if (objective.id === objectiveId) {
                return {
                    ...objective,
                    isCompleted: !objective.isCompleted
                };
            }
            return objective;
        });

        setIsDirty(!_.isEqual(updatedObjectives, initialObjectives));
        setObjectives(updatedObjectives);
    };

    const noObjective = (
        <Typography component='div' variant='h6' align='center' color='textSecondary'>
            No Board Objectives have been added yet!
        </Typography>
    );

    return (
        <Card>
            {/* TODO: add icon to represent manager rating */}
            <CardHeader title='Board Objectives' style={{ paddingBottom: 0 }} />
            <CardContent style={{ paddingTop: 0, paddingBottom: 0 }}>
                {objectives.length === 0 && noObjective}
                <List>
                    {objectives.map((objective, idx) => {
                        return (
                            <BoardObjective
                                key={objective.id}
                                objective={objective}
                                hasDivider={idx < objectives.length - 1}
                                handleClickFn={() => completeObjective(objective.id)}
                            />
                        );
                    })}
                </List>
            </CardContent>
            {/* TODO: add the ObjectiveForm widget for adding new objectives when the rest of the functionality
            is ready */}
            <Divider variant='middle' />
            <CardActions>
                <Button color='primary' disabled={objectives.length === 5}>
                    Add Objective
                </Button>
                {/* TODO: update the widget to consume a mutute function which can be used to save changes to
                the objectives  */}
                <Button color='primary' disabled={!isDirty}>
                    Save Changes
                </Button>
            </CardActions>
        </Card>
    );
}

BoardObjectives.propTypes = {
    objectives: PropTypes.arrayOf(BoardObjective.propTypes.objective)
};