import AddClub from '../widgets/AddClub';

export default {
    component: AddClub,
    title: 'Widgets/ClubPageView/AddClub',
    argTypes: { addClubAction: { control: { disable: true } } },
    decorators: [
        Story => (
            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <Story />
            </div>
        )
    ]
};

const Template = args => <AddClub {...args} />;

export const Success = Template.bind({});
Success.args = {
    addClubAction: _ => null
};

export const FormFailure = Template.bind({});
FormFailure.args = {
    addClubAction: _ => 'Failed to add new club!'
};