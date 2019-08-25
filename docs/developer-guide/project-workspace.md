---
id: project-workspace
title: workspace.yml
sidebar_label: workspace/workspace.yml
---

`workspace.yml` stores the project metadata, and global settings.

```yaml
projects:
  - id: fifaplayer-2019
    path: /Users/ananas/ananas-examples/FifaPlayer2019
  - id: 5ce6789f9b0876a64f706c71
    path: /Users/ananas/Library/Application
      Support/AnanasAnalytics/5ce6789f9b0876a64f706c71
settings:
  env:
    GOOGLE_APPLICATION_CREDENTIALS: /Users/ananas/dev_credentials_file.json
```

## Projects

`projects` section contains a list of projects imported in the workspace.  

## Settings

### Supported Settings
- env

	`env` settings keep a set of environment variables. Only these environment variables are accessible to Ananas Analytics Desktop.

	> A useful scenario of `env` is to setup Google Cloud credential: when running analysis job on Google Dataflow, you need to setup an environment variable GOOGLE_APPLICATION_CREDENTIALS, with the google cloud credential file.

	Example:

	```yml
	settings:
		env:
			GOOGLE_APPLICATION_CREDENTIALS: /Users/ananas/dev_credentials_file.json
			ANOTHER_ENV_VAR: VAR_VALUE 
	```

- disableCheckUpdateOnStart (From 0.9.0+)

	boolean type, default false. Whether disable the update check when start Ananas Desktop.

	```yml
	settings:
		disableCheckUpdateOnStart: true
	```
