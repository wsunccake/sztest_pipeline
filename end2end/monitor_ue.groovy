pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')
        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: '')
        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'API_VERSION', defaultValue: '', description: '')

        string(name: 'UE_NUM', defaultValue: '1', description: '')
        string(name: 'WAITING_TIME', defaultValue: '600', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${SCENARIO} - #${currentBuild.number}"
                }

            }
        }

        stage('Monitor UE') {
            steps {
                sh '''#!/bin/bash
###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/api_util.sh
source $SZTEST_HOME/util/test_api/common.sh

setup_api_var

echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, SZ_VERSION: $SZ_VERSION"


###
### run api
###

# run
init_time=`date +%s`
waiting_time=$WAITING_TIME
interval=10

echo "start job:`date`"
  while true; do
    pubapi_login $SZ_USERNAME $SZ_PASSWORD
    echo "start time:`date`"

    monitor_ue
    count_ue=`monitor_ue | sed -n 's/Response body: //p' | jq '.onlineCount'`
    echo "end time:`date`"
    count_ue=${count_ue:=0}

    end_time=`date +%s`
    echo "end time:`date`"
    echo "ue num: $UE_NUM, $count_ue"    
    [ "$count_ue" -ge "$UE_NUM" ] && break
    [ "`expr $end_time - $init_time`" -gt "$waiting_time" ] && exit 1
    
    sleep $interval
  done
echo "end job:`date`"
'''
            }
        }
    }
}
