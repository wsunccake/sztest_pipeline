library identifier: 'dynamic-libary@master', retriever: modernSCM(
        [$class: 'GitSCMSource',
         remote: 'https://github.com/wsunccake/sztest_pipeline.git'])

pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '5.2.1.0.515', description: '')
        string(name: 'SCENARIO', defaultValue: 'fr4818', description: '')
        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: '')
        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'NPROC', defaultValue: '8', description: '')
        string(name: 'API_VERSION', defaultValue: 'v9_1', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${SCENARIO} - #${currentBuild.number}"
                }

            }
        }

        stage('Patch Zone With LBS') {
            steps {
                sh '''#!/bin/bash
###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/api_util.sh
source $SZTEST_HOME/util/test_api/fr4818.sh

setup_api_var

echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, SZ_VERSION: $SZ_VERSION"


###
### gen input
###

mkdir -p $VAR_DIR/output/patch_zone_with_lbs

NEW_INPUT=zone_lbs.inp
INPUT_NUMBER=1000
TMP_DIR=`mktemp -d`
echo "TMP DIR: $TMP_DIR"

line=0
for zone_name in `cat $VAR_DIR/input/zones/zones.inp`; do
  # get zone_id
  line=`expr $line + 1`
  zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/${zone_name}.out`

  lbs_name=`sed -n ${line}p $VAR_DIR/input/lbs/lbs.inp`
  lbs_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/lbs/${lbs_name}.out`
  echo "zone: $zone_name $zone_id lsb: $lbs_name $lbs_id" >> $TMP_DIR/$NEW_INPUT
done

split -l $INPUT_NUMBER $TMP_DIR/$NEW_INPUT $TMP_DIR/in_
cp -fv $TMP_DIR/$NEW_INPUT $VAR_DIR/input/zones/.


###
### run api
###

echo "start job:`date`"
for f in `ls $TMP_DIR/in_*`; do
  # login
  pubapi_login $SZ_USERNAME $SZ_PASSWORD
  
  # create ap per zone
  cat $f | xargs -n6 -P $NPROC sh -c 'patch_zone_with_lbs $2 $5 | tee $VAR_DIR/output/patch_zone_with_lbs/$1_$3.out'
    
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
                    def result = util.checkResponseStatus "${VAR_DIR}/output/patch_zone_with_lbs"
                    println result
                    currentBuild.result = result
                }
            }
        }

        stage('Statistic Response') {
            steps {
                script {
                    util.statisticizeResponse "${VAR_DIR}/output/patch_zone_with_lbs"
                }
            }
        }

    }
}
