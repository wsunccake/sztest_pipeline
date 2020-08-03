pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')
        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: '')
        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'API_VERSION', defaultValue: '', description: '')

        string(name: 'AP_NUM', defaultValue: '1', description: '')
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

        stage('Monitor AP On Line') {
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

init_time=`date +%s`
waiting_time=$WAITING_TIME
interval=10

echo "start job:`date`"
  while true; do
    pubapi_login $SZ_USERNAME $SZ_PASSWORD
    echo "start time:`date`"

    monitor_ap
    count_ap=`monitor_ap | sed -n 's/Response body: //p' | jq '.onlineCount'`
    count_ap=${count_ap:=0}
    
    end_time=`date +%s`
    echo "end time:`date`"
    echo "ap num: $AP_NUM, $count_ap"    
    [ "$count_ap" -ge "$AP_NUM" ] && break
    [ "`expr $end_time - $init_time`" -gt "$waiting_time" ] && exit 1
    
    sleep $interval
  done
echo "end job:`date`"
'''
            }
        }

        stage('Monitor AP Up-To-Date') {
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

init_time=`date +%s`
waiting_time=$WAITING_TIME
interval=10

echo "start job:`date`"
  while true; do
    pubapi_login $SZ_USERNAME $SZ_PASSWORD
    echo "start time:`date`"

    query_ap_up-to-date
    count_ap=`query_ap_up-to-date | sed -n 's/Response body: //p' | jq '.totalCount'`
    count_ap=${count_ap:=0}

    end_time=`date +%s`
    echo "end time:`date`"
    echo "ap num: $AP_NUM, $count_ap"    
    [ "$count_ap" -ge "$AP_NUM" ] && break
    [ "`expr $end_time - $init_time`" -gt "$waiting_time" ] && exit 1
    
    sleep $interval
  done
echo "end job:`date`"
'''
            }
        }

    }
}
