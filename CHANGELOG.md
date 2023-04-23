# Changelog

## [Unreleased]
### Added
- Hints for better understanding of settings.
- Rename settings labels to be more inline with Gitlab terminology.

## [1.7.0] - 2023-04-20

### Added
- Exclusion globs for excluding files from linting.
- Gitlab yaml file type and icon.
- Update file icons after updating file globs.

## [1.6.0] - 2023-03-31

### Added
- Run linting on startup if the opened file matches the glob patterns.
- Update lint status widget whenever the file changes, so it properly shows whether the file is linted or not.

### Changed
- Replace StatusBarWidgetSettings by controlling the widget visibility in LintStatusPresentation.

### Fixed
- Null value for gitlab token preventing saving settings.

## [1.5.3] - 2023-03-29

### Added
- Refactor null checking in FileListener.

### Fixed
- Plugin should not have until build number.

## [1.5.2] - 2023-01-31

### Fixed
- Remotes are now property saved after updating Gitlab url or remote id.

## [1.5.1] - 2023-01-22

### Added
- Option for setting file path glob patterns for linting.

### Changed
- Refactor settings ui.
- Move remotes table to separate tab.

### Fixed
- Refresh actions now properly runs on currently opened file.
- File listener waits for project to be initialized.

## [1.4.0] - 2023-01-15

### Added
- Basic implementation of editor with merged preview.
- Setting for enabling/disabling merged preview.
- Option for running lint on file selection change.

### Changed
- Updated plugin icon and description.

### Fixed
- Force HTTPS setting is now properly validated and saved.

## [1.3.0] - 2022-12-06

### Added
- Lint frequency setting.
- Support for 223.* IDE builds.

### Changed
- Update github-actions set-output command
- Update github-actions dependencies versions
- Update dependencies in build.gradle.kts
- Rename Gitlab URL to Gitlab API url
- Rename Remote ID to Project ID

## [1.2.2]

### Fixed
- Notification section is now openable and shows notification settings correctly.

## [1.2.1]

### Added
- More context to error notifications.
- - Enables edit of gitlab urls and token used for authentication.

### Fixed
- Do not duplicate notifications on start up.
- NumberFormatException when remote id is empty string.

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

[Unreleased]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v1.7.0...HEAD
[1.7.0]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v1.6.0...v1.7.0
[1.6.0]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v1.5.3...v1.6.0
[1.5.3]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v1.5.2...v1.5.3
[1.5.2]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v1.5.1...v1.5.2
[1.5.1]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v1.4.0...v1.5.1
[1.4.0]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v1.3.0...v1.4.0
[1.3.0]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v1.2.2...v1.3.0
[1.2.2]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v1.2.1...v1.2.2
[1.2.1]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v1.1.1...v1.2.1
[1.1.1]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v1.0.1...v1.1.0
[1.0.1]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v0.0.12...v1.0.0
[0.0.12]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v0.0.11...v0.0.12
[0.0.11]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v0.0.10...v0.0.11
[0.0.10]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v0.0.9...v0.0.10
[0.0.9]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v0.0.8...v0.0.9
[0.0.8]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v0.0.7...v0.0.8
[0.0.7]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v0.0.6...v0.0.7
[0.0.6]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v0.0.5...v0.0.6
[0.0.5]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v0.0.4...v0.0.5
[0.0.4]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v0.0.3...v0.0.4
[0.0.3]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v0.0.2...v0.0.3
[0.0.2]: https://github.com/Blarc/gitlab-template-lint-plugin/compare/v0.0.1...v0.0.2
[0.0.1]: https://github.com/Blarc/gitlab-template-lint-plugin/commits/v0.0.1
