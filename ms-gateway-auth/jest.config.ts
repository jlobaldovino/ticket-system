export default {
    preset: 'ts-jest',
    testEnvironment: 'node',
    rootDir: 'src',
    moduleFileExtensions: ['ts', 'js', 'json'],
    transform: {
      '^.+\\.ts$': 'ts-jest',
    },
    testRegex: '.*\\.spec\\.ts$',
    collectCoverageFrom: ['**/*.ts'],
    coverageDirectory: '../coverage',
  };