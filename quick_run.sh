#!/bin/bash


ACTION=$1
STAGE=$2

###
### function
###

usage() {
echo """
  $0 <arg>

argument
  help
  create <stage_dir>   create all jobs in stage dir
  delete <stage_dir>   delete all jobs in stage dir
"""
}

create_view() {
  ../bin/jenkins_cli.sh create-view $STAGE
}


create_job_in_stage() {
  for f in `ls $STAGE/*.groovy`; do
    j=`echo ${f%.groovy} | tr '/' '_'`
    ../bin/jenkins_cli.sh create-job $j $f
    ../bin/jenkins_cli.sh build $j
    ../bin/jenkins_cli.sh add-job-to-view $STAGE $j
  done
}


stop_job_in_stage() {
  for f in `ls $STAGE/*.groovy`; do
    j=`echo ${f%.groovy} | tr '/' '_'`
    ../bin/jenkins_cli.sh stop-builds $j
  done
}


delete_job_in_stage() {
  for f in `ls $STAGE/*.groovy`; do
    j=`echo ${f%.groovy} | tr '/' '_'`
    ../bin/jenkins_cli.sh delete-job $j
  done
}


###
### main
###


case $ACTION in
  "create")
    create_view
    create_job_in_stage
#    stop_job_in_stage
  ;;

  "delete")
    delete_job_in_stage
  ;;

  *|"help")
    usage
  ;;
esac

