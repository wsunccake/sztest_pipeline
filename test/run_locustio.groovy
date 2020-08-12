library identifier: 'dynamic-libary@master', retriever: modernSCM(
        [$class: 'GitSCMSource',
         remote: 'https://github.com/wsunccake/sztest_pipeline.git'])

pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '5.2.1.1', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')
        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: '')
        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'API_VERSION', defaultValue: '', description: '')

        string(name: 'TASK_DIR', defaultValue: '', description: '')
        string(name: 'NUM_CLIENT', defaultValue: '10', description: '')
        string(name: 'HATCH_RATE', defaultValue: '2', description: '')
        string(name: 'RUN_TIME', defaultValue: '20m1s', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${SCENARIO} - #${currentBuild.number}"
                }

            }
        }

        stage('Run Locustio') {
            steps {
                sh '''#!/bin/bash
###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/api_util.sh
source $SZTEST_HOME/locustio/venv/bin/activate

setup_api_var

echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, SZ_VERSION: $SZ_VERSION"


###
### run locustio
###

mkdir -p $VAR_DIR/output/locustio


if [ "x${TASK_DIR}" == "xpartner-query" ] || [ "x${TASK_DIR}" == "xpartner-updaet" ] ; then
  export DOMAIN_ID_FILE=${VAR_DIR}/output/id/domain_ids.log
  echo "export DOMAIN_ID_FILE=$DOMAIN_ID_FILE"

  export AP_ZONE_DOMAIN_ID_FILE=${VAR_DIR}/output/id/ap_zone_domain_id.log
  echo "export AP_ZONE_DOMAIN_ID_FILE=$AP_ZONE_DOMAIN_ID_FILE"

  partner_test_functions=(l2acl l3acp wifi_calling device_policy lbs application_policy_v2 user_defined proxy_auth proxy_acct vlan_pooling)  
  for f in ${partner_test_functions[@]}; do
    echo "export ${f^^}_FILE=$VAR_DIR/output/id/${f}_ids.log"
    export ${f^^}_FILE=$VAR_DIR/output/id/${f}_ids.log
  done
fi

cd $SZTEST_HOME/locustio

for py_file in `ls task/${TASK_DIR}/*.py`; do
  task=`basename ${py_file%.*}`
  echo ${task}
  echo "./bin/run.sh -H https://${SZ_IP}:${SZ_PORT} -f ${py_file} --no-web -c${NUM_CLIENT} -r${HATCH_RATE} -t${RUN_TIME}"
  ./bin/run.sh -H https://${SZ_IP}:${SZ_PORT} -f ${py_file} --no-web -c${NUM_CLIENT} -r${HATCH_RATE} -t${RUN_TIME} |& tee ${VAR_DIR}/output/locustio/${task}.log
done
'''
            }
        }

        stage('Statisticize Result') {
            steps {
                sh '''#!/bin/bash
SZTEST_HOME=/var/lib/jenkins/sztest

cd $SZTEST_HOME/locustio

for py_file in `ls task/${TASK_DIR}/*.py`; do
  task=`basename ${py_file%.*}`
  echo ${task}
  echo "awk -F, '/status: 20/{print \\$2}' ${VAR_DIR}/output/locustio/${task}.log | sed 's/.*response time://' | statistics.awk"
  awk -F, '/status: 200/{print \$2}' ${VAR_DIR}/output/locustio/${task}.log | sed 's/.*response time://' | statistics.awk
  echo
done
'''
            }
        }

    }
}
