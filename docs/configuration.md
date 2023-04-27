# Configuration

The configuration tries to satisfy different users' needs and that's why it might be a bit more complicated. However,
for the basic usage, most configuration should be automatically resolved once you open a Gitlab Yaml file.

## Gitlab API token

The plugin needs a Gitlab API token to be able to make requests to the Gitlab Lint API. You can set the token in the
plugin settings for each Gitlab URL. Gitlab URLs are automatically resolved from the repositories URLs.

**This should (in theory) be the only mandatory setting that you need to configure manually.**

## Gitlab URL

The plugin needs to know the Gitlab URL to be able to make requests to the Gitlab Lint API. The url is computed from
the repository URL.

## Linting frequency

The plugin needs to know when to lint the file. The default is to lint the file on every file save.

## Self-signed certificate

By default, the plugin does not allow self-signed certificates. You can enable it via this setting.

## Force HTTPS

By default, the plugin forces HTTPS. This means that if you have a Gitlab URL that starts with `http://`, the plugin
will try to use `https://` instead. You can disable this behavior via this setting.

## Merged preview

By default, the plugin shows a merged preview of the Gitlab Yaml file. You can disable this behavior via this setting.

## Lint on selected file

By default, the plugin lints the file whenever you switch tab in the editor. You can disable this behavior via this
setting to reduce the number of requests to the Gitlab Lint API.

## Remote

A repository can have multiple remotes. The plugin needs to know which remote to use when making requests to the Gitlab
Lint API. By default, this it is set to `origin`.

## Fallback branch

The plugin needs to know which branch to use as reference when making requests to the Gitlab Lint API. The current Git
branch is used by default. If the branch is not resolved correctly, the fallback branch is used.

## Path globs

The plugin needs to know which files to lint. By default, it lints all files that are recognized via the file type
mechanism. However, that might not always be specific enough. You can use path globs to specify which files to lint by
their path.

## Remotes

The plugin needs to know which Gitlab URL to use when making requests to the Gitlab Lint API. The plugin tries to
resolve the Gitlab URL from the remote URL. If it is not resolved correctly, you can set it manually.

Because Gitlab URL is connected with the corresponding Gitlab API token, you must first create an entry in the Gitlab
URL Token table and then choose that Gitlab URL in the remotes table. This allows using the same Gitlab URL Token entry
for multiple remotes.

### Gitlab project ID

The plugin needs to know the Gitlab project ID to be able to make requests to the Gitlab Lint API. The plugin tries to
resolve the project ID, by searching for the project ID by its remote URL using the Gitlab project API, but it might
not always be resolved correctly. You can set the project ID manually for each repository in the remotes table.
