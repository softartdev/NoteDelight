# `gh` CLI Cheat Sheet

## Check Workflow Status

To check the status of the last run for a specific workflow, use the following command:

```bash
gh run list --workflow=<workflow_file_name> --limit=1
```

**Example for `web.yml`:**

```bash
gh run list --workflow=web.yml --limit=1
```

## Cancel a Workflow Run

To cancel a running workflow, you first need to get the `RUN_ID` of the job.

1.  **List running jobs to find the `RUN_ID`:**

    ```bash
    gh run list --workflow=<workflow_file_name>
    ```

    **Example for `kmp.yml`:**

    ```bash
    gh run list --workflow=kmp.yml
    ```

2.  **Cancel the run using the `RUN_ID`:**

    ```bash
    gh run cancel <RUN_ID>
    ```

    **Example:**

    ```bash
    gh run cancel 123456789
    ```
