import AddPlayer from '../widgets/AddPlayer';

export default {
    component: AddPlayer,
    title: 'Widgets/SquadHubView/AddPlayer',
    argTypes: { addPlayerAction: { control: { disable: true } } },
    decorators: [
        Story => (
            <div style={{ display: 'flex', justifyContent: 'center' }}>
                <Story />
            </div>
        )
    ]
};

const Template = args => <AddPlayer {...args} />;

export const Success = Template.bind({});
Success.args = {
    addPlayerAction: _ => null
};

export const FormFailure = Template.bind({});
FormFailure.args = {
    addPlayerAction: _ => 'Failed to add new player!'
};