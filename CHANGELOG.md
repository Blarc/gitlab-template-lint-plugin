# Changelog

## [Unreleased]
### Added
- More context to error notifications.

### Fixed
- Do not duplicate notifications on start up.

## [1.1.1]
### Fixed
- Field titles in remote editor.
- Change Gitlab Lint to Gitlab Template Lint where applicable.

## [1.1.0]
### Added
- Button in settings for creating Gitlab private token.
- Gitlab API URL is now cached for each repository.
- Gitlab API URL can be edited in remotes table.

## [1.0.1]
### Added
- Force HTTPS setting that is set by default.

## [1.0.0]
### Added
- Notifications on start up.
- Error notifications.
- Open Settings and Refresh actions to actions group popup in widget.
- Remote field to settings.
- VcsMappingChangedListener for detecting repositories.
- ComboBox with Gitlab urls in settings.
- Support for multiple Gitlab instances in one project.

### Changed
- Refactor linting to pipeline with middlewares.

### Fixed
- Gitlab token is now properly saved when changing remotes.

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