export default {
    preset: 'ts-jest',
    testEnvironment: 'node',
    rootDir: '.',
    moduleFileExtensions: ['ts', 'js', 'json'],
    transform: {
      '^.+\\.ts$': 'ts-jest',
    },
    testRegex: '.*\\.spec\\.ts$',
    collectCoverageFrom: ['**/*.ts'],
    coverageDirectory: '../coverage',
    moduleNameMapper: {
      '^src/(.*)$': '<rootDir>/src/$1'
    }
  };