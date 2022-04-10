module.exports = {
  stories: ['../src/**/*.stories.js'],
  addons: [
    '@storybook/preset-create-react-app',
    '@storybook/addon-essentials'
  ],
  core: {
    builder: 'webpack5',
  },
};
