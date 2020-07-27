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

        string(name: 'RADIUS_PORT', defaultValue: '1812', description: '')
        string(name: 'RADIUS_SECRET', defaultValue: '1234', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${SCENARIO} - #${currentBuild.number}"
                }

            }
        }

        stage('Create Authentication Per Partner Domain') {
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

mkdir -p $VAR_DIR/output/proxy_auth

export radius_port=$RADIUS_PORT
export radius_secret=$RADIUS_SECRET

NEW_INPUT=partner_domain_proxy_auth.inp
INPUT_NUMBER=1000
TMP_DIR=`mktemp -d`
echo "TMP DIR: $TMP_DIR"

for domain_name in `cat $VAR_DIR/input/partner_domains/domains.inp`; do
  # get domain_id
  domain_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/partner_domains/$domain_name.out`
  
  # create proxy auth
  i=1
  for radius_ip in `cat $VAR_DIR/input/proxy_auth/$domain_name.inp`; do
    if [ ! -z $domain_id ]; then
      echo "domain: $domain_name $domain_id proxy_auth: $radius_ip $i" >> $TMP_DIR/$NEW_INPUT
      i=`expr $i + 1`
    fi
  done
done

split -l $INPUT_NUMBER $TMP_DIR/$NEW_INPUT $TMP_DIR/in_
cp -fv $TMP_DIR/$NEW_INPUT $VAR_DIR/input/proxy_auth/.


###
### run api
###

echo "start job:`date`"
for f in `ls $TMP_DIR/in_*`; do
  # login
  pubapi_login $SZ_USERNAME $SZ_PASSWORD
  
  # create auth
  cat $f | xargs -n6 -P $NPROC sh -c 'create_auth_service ${4}.${5} ${4} ${radius_port} ${radius_secret} ${2} | tee ${VAR_DIR}/output/proxy_auth/${1}_${4}.${5}.out'
    
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
                    def result = util.checkResponseStatus "${VAR_DIR}/output/proxy_auth"
                    println result
                    currentBuild.result = result
                }
            }
        }

        stage('Statistic Response') {
            steps {
                script {
                    util.statisticizeResponse "${VAR_DIR}/output/proxy_auth"
                }
            }
        }

    }
}
