library identifier: 'dynamic-libary@master', retriever: modernSCM(
        [$class: 'GitSCMSource',
         remote: 'https://github.com/wsunccake/sztest_pipeline.git'])

pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: 'phase1', description: '')
        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/${SCENARIO}/${SZ_VERSION}', description: '')
        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'NPROC', defaultValue: '2', description: '')
        string(name: 'API_VERSION', defaultValue: '', description: '')

        string(name: 'DPSK_AMOUNT', defaultValue: '2', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${SCENARIO} - #${currentBuild.number}"
                }
            }
        }

        stage('Create DPSK') {
            steps {
                sh '''#!/bin/bash
###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/api_util.sh
source $SZTEST_HOME/util/test_api/phase1.sh

setup_api_var

echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, SZ_VERSION: $SZ_VERSION"


###
### gen input
###

cd $API_PERF_DIR/public_api/$API_PERF_VER
mkdir -p $VAR_DIR/output/wlans/dpsk

NEW_INPUT=zone_wlan_dpsk_batch.inp
INPUT_NUMBER=1000
TMP_DIR=`mktemp -d`
echo "TMP DIR: $TMP_DIR"

for zone_name in `cat $VAR_DIR/input/zones/zones.inp`; do
  # get zone_id
  zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/$zone_name.out`

  for wlan_name in `grep dpsk $VAR_DIR/input/wlans/$zone_name.inp`; do
    if [ ! -z $zone_id ]; then
      wlan_id=`awk -F\\" '/"id":/{print \$4}' $VAR_DIR/output/wlans/${zone_name}_${wlan_name}.out`
      echo "zone: $zone_name $zone_id wlan_name: $wlan_name $wlan_id" >> $TMP_DIR/$NEW_INPUT
    fi
  done    

done

split -l $INPUT_NUMBER $TMP_DIR/$NEW_INPUT $TMP_DIR/in_
cp -fv $TMP_DIR/$NEW_INPUT $VAR_DIR/input/wlans/.


###
### run api
###

echo "start job:`date`"
for f in `ls $TMP_DIR/in_*`; do
  # login
  pubapi_login $SZ_USERNAME $SZ_PASSWORD
  
  # create ap per zone
  cat $f | xargs -n6 -P $NPROC sh -c 'create_dpsk_batch $DPSK_AMOUNT $2 $5 | tee $VAR_DIR/output/wlans/dpsk/$1_$4.out'
    
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
                    def result = util.checkResponseStatus "${VAR_DIR}/output/wlans/dpsk"
                    println result
                    currentBuild.result = result
                }
            }
        }

        stage('Statistic Response') {
            steps {
                script {
                    util.statisticizeResponse "${VAR_DIR}/output/wlans/dpsk"
                }
            }
        }

    }
}
