import React from 'react';
import { useMutation, useQueryClient } from 'react-query';
import { useParams } from 'react-router';
import { savePlayerProgressData } from '../clients/DashboardClient';
import { queryKeys } from '../utils';

import EditPlayerForm from '../components/EditPlayerForm';

const useSavePlayerData = () => {
    const queryClient = useQueryClient();
    return useMutation(updatedPlayerData => savePlayerProgressData(updatedPlayerData), {
        onSuccess: (savedPlayerData) => {
            console.log(queryClient.getQueryCache().getAll());
            queryClient.invalidateQueries(['playerData', savedPlayerData.id]);
        }
    });
};

export default function useEditPlayerForm(playerData) {
    const { playerAttributes: { attributeCategories: initialAttributeCategories } } = playerData;
    const { playerId } = useParams();
    const { mutate: savePlayerData, status } = useSavePlayerData();

    const [attributeCategories, setAttributeCategories] = React.useState(initialAttributeCategories);

    const handleChange = (e, categoryIdx, attributeIdx) => {
        const { name, value } = e.target;

        let copyOfAttributesInCategory = [ ...attributeCategories[categoryIdx].attributesInCategory ];
        copyOfAttributesInCategory[attributeIdx] = {
            ...copyOfAttributesInCategory[attributeIdx],
            name,
            value: parseInt(value)
        };

        let copyOfAttributeCategories = [ ...attributeCategories ];
        copyOfAttributeCategories[categoryIdx] = {
            ...copyOfAttributeCategories[categoryIdx],
            attributesInCategory: copyOfAttributesInCategory
        };

        setAttributeCategories(copyOfAttributeCategories);
    };

    const handleSubmit = async () => {
        const updatedPlayerData = {
            id: playerId,
            metadata: playerData.playerMetadata,
            roles: playerData.playerRoles,
            ability: {
                current: playerData.playerOverall.currentValue,
                history: playerData.playerOverall.history
            },
            attributes: {
                attributeCategories,
                attributeGroups: playerData.playerAttributes.attributeGroups
            }
        };
        savePlayerData(updatedPlayerData);
    };

    const editPlayerForm =
        <EditPlayerForm attributeCategories={ attributeCategories } handleChangeFn={ handleChange } />;

    return {
        form: editPlayerForm,
        handleSubmit,
        formSubmitStatus: status
    };
}