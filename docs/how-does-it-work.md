# How does it work

The plugin makes a request with your Gitlab Yaml file to the Gitlab Lint API, which returns a response whether the file
is valid or not. The plugin then parses the response and displays the errors in the editor.

To make this happen, it does the following:

1. Checks if the current file is a Gitlab Yaml file.
2. Tries to resolve the repository URL and Git branch for current file.
3. Parses Gitlab URL from the repository URL.
4. Checks if Gitlab API token exists for the Gitlab URL.
    1. If it doesn't exist:
        1. Saves the Gitlab URL to the Gitlab URL Token table in the plugin settings.
        2. Shows a notification to the user to set the Gitlab API token for the Gitlab URL.
5. Sends a request to the Gitlab Lint API with the current file content and the current branch as reference.
    1. If the reference branch does not exist on the remote and fallback branch is set, tries again with the fallback
       branch.
6. Parses the response:
   1. displays the errors in the editor, if the file is not valid
   2. shows merged preview, if the file is valid and the preview is enabled



