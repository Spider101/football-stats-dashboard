import React from 'react'
import Fab from '@material-ui/core/Fab';
import EditIcon from '@material-ui/icons/Edit';
import FormDialog from '../widgets/FormDialog';

export default function PageEditAction({ dialogTitle, pageData, formBuilderHook }) {
    const [isFormOpen, setIsFormOpen] = React.useState(false);

    const handleOpenForm = () => {
        setIsFormOpen(true);
    };

    const handleCloseForm = () => {
        setIsFormOpen(false);
    };

    return (
        <>
            <Fab color="secondary" aria-label="edit" onClick={ handleOpenForm }>
                <EditIcon />
            </Fab>
            <FormDialog open={ isFormOpen }
                handleClose={ handleCloseForm }
                dialogTitle={ dialogTitle }
                formData={ pageData }
                useFormBuilder={ formBuilderHook }
            />
        </>
    )
}