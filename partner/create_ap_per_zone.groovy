library identifier: 'dynamic-libary@master', retriever: modernSCM(
        [$class: 'GitSCMSource',
         remote: 'https://github.com/wsunccake/sztest_pipeline.git'])

pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')
        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: '')
        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'NPROC', defaultValue: '2', description: '')
        string(name: 'API_VERSION', defaultValue: '', description: '')

        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'NPROC', defaultValue: '2', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${SCENARIO} - #${currentBuild.number}"
                }

            }
        }

        stage('Pre-Provision AP Per Zone') {
            steps {
                sh '''#!/bin/bash
###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/api_util.sh
source $SZTEST_HOME/util/test_api/partner.sh

setup_api_var

echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, SZ_VERSION: $SZ_VERSION"


###
### gen input
###

mkdir -p $VAR_DIR/output/aps

NEW_INPUT=zone_ap_mac.inp
INPUT_NUMBER=1000
TMP_DIR=`mktemp -d`
echo "TMP DIR: $TMP_DIR"

for zone_name in `cat $VAR_DIR/input/zones/zones.inp`; do
  # get zone_id
  zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/$zone_name.out`

  for ap_mac in `cat $VAR_DIR/input/aps/$zone_name.inp`; do
    if [ ! -z $zone_id ]; then
      echo "zone: $zone_name $zone_id ap_mac: $ap_mac" >> $TMP_DIR/$NEW_INPUT
    fi
  done

done

split -l $INPUT_NUMBER $TMP_DIR/$NEW_INPUT $TMP_DIR/in_
cp -fv $TMP_DIR/$NEW_INPUT $VAR_DIR/input/aps/.


###
### run api
###

echo "start job:`date`"
for f in `ls $TMP_DIR/in_*`; do
  # login
  pubapi_login $SZ_USERNAME $SZ_PASSWORD
  
  # create ap per zone
  cat $f | xargs -n5 -P $NPROC sh -c 'create_ap ${4} "" "" ${2} | tee ${VAR_DIR}/output/aps/${4}.out'
    
  # logout
  pubapi_logout
done
echo "end job:`date`"

rm -rfv $TMP_DIR
'''
            }
        }

        stage('Check Response') {
            steps {
                script {
                    def result = util.checkResponseStatus "${VAR_DIR}/output/aps"
                    println result
                    currentBuild.result = result
                }
            }
        }

        stage('Statistic Response') {
            steps {
                script {
                    util.statisticizeResponse "${VAR_DIR}/output/aps"
                }
            }
        }

    }
}
