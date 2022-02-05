import { LookupDataContextProvider } from '../context/LookupDataProvider';
import { getLookupDataHandlers } from '../mocks/handlers';
import AddPlayer from '../widgets/AddPlayer';

export default {
    component: AddPlayer,
    title: 'Widgets/SquadHubView/AddPlayer',
    argTypes: { addPlayerAction: { control: { disable: true } } },
    decorators: [
        Story => (
            <LookupDataContextProvider>
                <div style={{ display: 'flex', justifyContent: 'center' }}>
                    <Story />
                </div>
            </LookupDataContextProvider>
        )
    ],
    parameters: {
        msw: {
            handlers: getLookupDataHandlers()
        }
    }
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