import React from 'react';
import Fab from '@material-ui/core/Fab';
import EditIcon from '@material-ui/icons/Edit';
import AddIcon from '@material-ui/icons/Add';
import FormDialog from '../widgets/FormDialog';
import { makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles({
    actionButton: {
        position: 'fixed',
        bottom: 0,
        right: 0,
        margin: '5%'
    }
});

export default function PageAction({ dialogTitle, pageData, formBuilderHook, actionType }) {
    const [isFormOpen, setIsFormOpen] = React.useState(false);
    const classes = useStyles();

    const handleOpenForm = () => {
        setIsFormOpen(true);
    };

    const handleCloseForm = () => {
        setIsFormOpen(false);
    };

    return (
        <>
            <Fab
                color="secondary"
                aria-label={ actionType }
                onClick={ handleOpenForm }
                className={ classes.actionButton }
            >
                { actionType === 'edit' ? <EditIcon /> : <AddIcon /> }
            </Fab>
            <FormDialog open={ isFormOpen }
                handleClose={ handleCloseForm }
                dialogTitle={ dialogTitle }
                formData={ pageData }
                useFormBuilder={ formBuilderHook }
            />
        </>
    );
}