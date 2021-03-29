import React from 'react';
import { useMutation } from 'react-query';
import { addTransferData } from '../clients/DashboardClient';
import AddTransferForm from '../components/AddTransferForm';
import { transferTypes } from '../utils';

const useAddTransferData = () => {
    return useMutation(newTransferData => addTransferData(newTransferData));
};

export default function useAddTransferForm(transfers) {
    const keys =Object.keys(transfers[0]);
    const formFields = keys.map(field => {
        switch (field){
        case 'fee':
        case 'currentAbility':
            return { name: field, type: 'number', defaultValue: 0 };
        case 'date':
            return { name: field, type: 'date', defaultValue: new Date() };
        case 'transferType':
            return {
                name: field,
                type: 'select',
                defaultValue: transferTypes.BASIC,
                availableValues: Object.values(transferTypes)
            };
        default:
            return { name: field, type: 'text', defaultValue: '' };
        }
    });

    const defaultTransfer = formFields.reduce((transfer, field) => ({
        ...transfer,
        [field.name]: field.defaultValue
    }), {});
    const [transfer, setTransfer] = React.useState(defaultTransfer);

    const handleChange = e => {
        const { name, value } = e.target;
        setTransfer({
            ...transfer,
            [name]: value
        });
    };

    const { mutate: addTransferData, status } = useAddTransferData();

    const handleSubmit = async () => {
        console.log(transfer);
        addTransferData(transfer);
    }

    const addTransferForm =
        <AddTransferForm fields={ formFields } transfer={ transfer } handleChangeFn={ handleChange } />;

    return {
        form: addTransferForm,
        handleSubmit,
        formSubmitStatus: status
    }
}