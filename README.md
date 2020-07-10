# sztest_pipeline


## download pipeline

```bash
~ $ cd $SZTEST_HOME

# use SSH 
sztest $ git clone git@github.com:wsunccake/sztest_pipeline.git pipeline

# use HTTPS
sztest $ git clone https://github.com/wsunccake/sztest_pipeline.git pipeline
```


## create pipeline job

```bash
# setup env
sztest $ export JENKINS_IP=<jenkins_ip>
sztest $ export JENKINS_USER_ID=<username>
sztest $ export JENKINS_API_TOKEN=<password>

sztest $ export GIT_REPO=https://github.com/wsunccake/sztest_pipeline
sztest $ export GIT_BRANCH=master

# run jenkins util
sztest/pipeline $ ../bin/jenkins_cli.sh help
sztest/pipeline $ ../bin/jenkins_cli.sh list-job
sztest/pipeline $ ../bin/jenkins_cli.sh create-job <job> <file>
sztest/pipeline $ ../bin/jenkins_cli.sh delete-job <job>

# run quick tool
sztest/pipeline $ ./quick_run.sh create <stage>
sztest/pipeline $ ./quick_run.sh delete <stage>
```

