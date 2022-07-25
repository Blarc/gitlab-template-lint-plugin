# Changelog

## [Unreleased]

## [0.0.12]
### Added
- Throw exception when project id can not be retrieved.
- Run linting in the background.

## [0.0.11]
### Fixed
- Changed instance of AppSettingsState to nullable and added missing null checks.
- Linting error notification is now properly shown.

### Removed
- Removed linting in the background.

## [0.0.10]
### Added
- Linting now runs in the background.
- Linting status widget is now shown only for gitlab-ci.yml files.

## [0.0.9]
### Fixed
- Added null check to localizedMessage in GitlabLintRunner to prevent NPE.

### Added
- Added basic lint status widget to status bar.

## [0.0.8]
### Changed
- Updated readme.
- Updated plugin description.

## [0.0.7]
### Fixed
- Changed icon size to 40x40 pixels.

## [0.0.6]
### Added
- Added basic plugin icon.

### Changed
- Updated plugin description.

## [0.0.5]
### Added
- Add [chore] tag to changelog commit.

## [0.0.4]
### Added
- Added a basic table with remotes and project ids
- Added GitHub actions workflows

## [0.0.3]
### Added
- Added basic documentation for installation with zip
- Added OkHttp dependency
- Added Kotlin plugin dependency

## [0.0.2]
### Added
- Use the current branch for linting
- Search for project by project name and project url

### Changed
- Removed checking for valid yaml, because Gitlab CI template is not necessarily a valid yaml

## [0.0.1]
### Added
- Setting private Gitlab token
- Error message on linting error