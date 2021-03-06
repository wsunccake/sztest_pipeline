pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')
        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${params.SCENARIO}', description: '')
        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'CLUSTER_NAME', defaultValue: '', description: '')
        string(name: 'WAITING_TIME', defaultValue: '900', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${params.SCENARIO} - #${currentBuild.number}"
                }

            }
        }

        stage('Setup PinPoint') {
            steps {
                sh '''#!/bin/bash
###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/api_util.sh
source $SZTEST_HOME/util/test_api/common.sh
source $SZTEST_HOME/util/cli_util.sh


###
### get sn
###

setup_api_var

pubapi_login $SZ_USERNAME $SZ_PASSWORD

sn=`get_controller |
sed -n 's/Response body: //p' |
jq ".list | map(select(.managementIp == \\"$SZ_IP\\"))" |
jq '.[].serialNumber' |
tr -d \\"`

pubapi_logout

echo "serial number: $sn"


###
### run cmd
###

setup_cli_var
setup_cli_pinpoint_var

echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, SZ_VERSION: $SZ_VERSION"

export PASSPHRASE=`curl ${SESAME2_URL}${sn} | awk '/Access Key/{print \$3}' | tr -d \\'`
echo "PASSPHRASE: $PASSPHRASE"

echo "setup pippoint"
$SZTEST_HOME/util/test_cli/setup-pinpoint.exp
'''
            }
        }

        stage('Check In-Service') {
                steps {
                    sh '''#!/bin/bash
###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/api_util.sh

setup_api_var


###
### check in-service
###

init_time=`date +%s`
waiting_time=$WAITING_TIME
interval=10

echo "start job:`date`"
  while true; do
    echo "start time:`date`"

    pubapi_login $SZ_USERNAME $SZ_PASSWORD
    is_in_service=`pubapi_login $SZ_USERNAME "$SZ_PASSWORD" | grep 'Response code: 200' >& /dev/null && echo "true" || echo "false"`

    end_time=`date +%s`
    echo "end time:`date`"
    [ "x$is_in_service" == "xtrue" ] && break
    [ "`expr $end_time - $init_time`" -gt "$waiting_time" ] && exit 1

    sleep $interval
  done
  
  pubapi_login $SZ_USERNAME $SZ_PASSWORD
echo "end job:`date`"
'''
            }
        }
    }
}
