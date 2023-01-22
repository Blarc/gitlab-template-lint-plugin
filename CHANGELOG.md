# Changelog

## [Unreleased]

### Added
- Option for setting file path glob patterns for linting.
- Move remotes table to separate tab.

### Changed
- Refactor settings ui.

### Fixed
- Refresh actions now properly runs on currently opened file.
- File listener waits for project to be initialized.

## 1.4.0 - 2023-01-15

### Added
- Basic implementation of editor with merged preview.
- Setting for enabling/disabling merged preview.
- Option for running lint on file selection change.

### Changed
- Updated plugin icon and description.

### Fixed
- Force HTTPS setting is now properly validated and saved.

## 1.3.0 - 2022-12-06

### Added
- Lint frequency setting.
- Support for 223.* IDE builds.

### Changed
- Update github-actions set-output command
- Update github-actions dependencies versions
- Update dependencies in build.gradle.kts
- Rename Gitlab URL to Gitlab API url
- Rename Remote ID to Project ID

## 1.2.2

### Fixed
- Notification section is now openable and shows notification settings correctly.

## 1.2.1

### Added
- More context to error notifications.
- - Enables edit of gitlab urls and token used for authentication.

### Fixed
- Do not duplicate notifications on start up.
- NumberFormatException when remote id is empty string.

## 1.1.1

### Fixed
- Field titles in remote editor.
- Change Gitlab Lint to Gitlab Template Lint where applicable.

## 1.1.0

### Added
- Button in settings for creating Gitlab private token.
- Gitlab API URL is now cached for each repository.
- Gitlab API URL can be edited in remotes table.

## 1.0.1

### Added
- Force HTTPS setting that is set by default.

## 1.0.0

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

## 0.0.12

### Added
- Throw exception when project id can not be retrieved.
- Run linting in the background.

## 0.0.11

### Fixed
- Changed instance of AppSettingsState to nullable and added missing null checks.
- Linting error notification is now properly shown.

### Removed
- Removed linting in the background.

## 0.0.10

### Added
- Linting now runs in the background.
- Linting status widget is now shown only for gitlab-ci.yml files.

## 0.0.9

### Fixed
- Added null check to localizedMessage in GitlabLintRunner to prevent NPE.

### Added
- Added basic lint status widget to status bar.

## 0.0.8

### Changed
- Updated readme.
- Updated plugin description.

## 0.0.7

### Fixed
- Changed icon size to 40x40 pixels.

## 0.0.6

### Added
- Added basic plugin icon.

### Changed
- Updated plugin description.

## 0.0.5

### Added
- Add [chore] tag to changelog commit.

## 0.0.4

### Added
- Added a basic table with remotes and project ids
- Added GitHub actions workflows

## 0.0.3

### Added
- Added basic documentation for installation with zip
- Added OkHttp dependency
- Added Kotlin plugin dependency

## 0.0.2

### Added
- Use the current branch for linting
- Search for project by project name and project url

### Changed
- Removed checking for valid yaml, because Gitlab CI template is not necessarily a valid yaml

## 0.0.1

### Added
- Setting private Gitlab token
- Error message on linting error
