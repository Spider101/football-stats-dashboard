import { useCallback, useEffect, useState } from 'react';
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
import TextField from '@material-ui/core/TextField';

import BoardObjective from '../components/BoardObjective';
import useForm from '../hooks/useForm';
import { formSubmission } from '../constants';
export default function BoardObjectives({ objectives, addBoardObjectiveAction }) {
    const [isAddFormOpen, setIsAddFormOpen] = useState(false);

    const handleAddFormOpen = () => {
        setIsAddFormOpen(true);
    };

    const handleAddFormClose = () => {
        setIsAddFormOpen(false);
    };

    const {
        handleChangeFn,
        handleSubmitFn,
        formData: addNewObjectiveData,
        formValidations: addNewObjectiveValidations,
        submitStatus
    } = useForm(
        {
            title: '',
            description: ''
        },
        useCallback(newBoardObjectiveData => addBoardObjectiveAction(newBoardObjectiveData), [])
    );

    useEffect(() => {
        if (submitStatus === formSubmission.COMPLETE) {
            handleAddFormClose();
        }
    }, [submitStatus]);

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
                {objectives.length === 0 && !isAddFormOpen && noObjective}
                <List>
                    {objectives.map((objective, idx) => {
                        return (
                            <BoardObjective
                                key={objective.id}
                                objective={objective}
                                hasDivider={idx < objectives.length - 1}
                                handleClickFn={x => x}
                            />
                        );
                    })}
                </List>
            </CardContent>
            <Divider variant='middle' />
            {isAddFormOpen && (
                <AddBoardObjectiveForm
                    newObjectiveData={addNewObjectiveData}
                    newObjectiveValidations={addNewObjectiveValidations}
                    handleChangeFn={handleChangeFn}
                    submitStatus={submitStatus}
                />
            )}
            <CardActions>
                <Button color='primary' disabled={objectives.length === 5 || isAddFormOpen} onClick={handleAddFormOpen}>
                    Add Objective
                </Button>
                <Button color='primary' disabled={!isAddFormOpen} onClick={handleSubmitFn}>
                    Save Changes
                </Button>
                {isAddFormOpen && (
                    <Button color='secondary' onClick={handleAddFormClose}>
                        Cancel
                    </Button>
                )}
            </CardActions>
        </Card>
    );
}

BoardObjectives.propTypes = {
    objectives: PropTypes.arrayOf(BoardObjective.propTypes.objective),
    addBoardObjectiveAction: PropTypes.func
};

const AddBoardObjectiveForm = ({ newObjectiveData, newObjectiveValidations, submitStatus, handleChangeFn }) => {
    return (
        <div style={{ width: '80%', margin: 'auto' }}>
            <TextField
                autoFocus
                name='title'
                label='Title'
                id='title'
                required
                margin='normal'
                fullWidth
                disabled={submitStatus === formSubmission.INPROGRESS}
                onChange={e => handleChangeFn(e)}
                value={newObjectiveData.title}
                error={!!newObjectiveValidations.title}
                helperText={newObjectiveValidations.title}
            />
            <TextField
                name='description'
                label='Description'
                id='description'
                required
                margin='normal'
                fullWidth
                disabled={submitStatus === formSubmission.INPROGRESS}
                onChange={e => handleChangeFn(e)}
                value={newObjectiveData.description}
                error={!!newObjectiveValidations.description}
                helperText={newObjectiveValidations.description}
            />
        </div>
    );
};

AddBoardObjectiveForm.propTypes = {
    newObjectiveData: PropTypes.object,
    newObjectiveValidations: PropTypes.object,
    submitStatus: PropTypes.string,
    handleChangeFn: PropTypes.func
};