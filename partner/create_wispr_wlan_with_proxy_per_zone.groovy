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
                    currentBuild.displayName = "${SZ_VERSION} - ${SCENARIO} - #${currentBuild.number}"                }

            }
        }

        stage('Create WISPr WLAN With Proxy Per Zone') {
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

mkdir -p $VAR_DIR/output/wispr_wlans

NEW_INPUT=zone_wispr_wlan.inp
INPUT_NUMBER=1000
TMP_DIR=`mktemp -d`
echo "TMP DIR: $TMP_DIR"

for domain_name in `cat $VAR_DIR/input/partner_domains/domains.inp`; do
  # get domain_id
  domain_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/partner_domains/$domain_name.out`
  
  if [ ! -z $domain_id ]; then
    for zone_name in `cat $VAR_DIR/input/zones/$domain_name.inp`; do
      # get zone_id
      zone_id=`awk -F\\" '/id/{print \$4}' $VAR_DIR/output/zones/$zone_name.out`
      
      if [ ! -z $zone_id ]; then      
        for name in `grep wispropen $VAR_DIR/input/wlans/$zone_name.inp`; do
          n=1
          hotspot_name=`sed -n 1p $VAR_DIR/input/hotspot/$zone_name.inp`
          auth_ip=`sed -n ${n}p $VAR_DIR/input/proxy_auth/$domain_name.inp`
          auth_id=`awk -F\\" '/id/ {print \$4}' $VAR_DIR/output/proxy_auth/${domain_name}_${auth_ip}.${n}.out`
          acct_ip=`sed -n ${n}p $VAR_DIR/input/proxy_acct/$domain_name.inp`
          acct_id=`awk -F\\" '/id/ {print \$4}' $VAR_DIR/output/proxy_acct/${domain_name}_${acct_ip}.${n}.out`
          
          echo "zone: $zone_name $zone_id wlan: $name hotspot: $hotspot_name proxy_auth: $auth_id proxy_acct: $acct_id" >> $TMP_DIR/$NEW_INPUT
        done
      fi

    done
  fi

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
  
  # create wlan
  cat $f | xargs -n11 -P $NPROC sh -c 'create_wispr_wlan_with_proxy ${4} ${2} ${6} ${8} ${10} | tee ${VAR_DIR}/output/wispr_wlans/${1}_${4}.out'
    
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
                    def result = util.checkResponseStatus "${VAR_DIR}/output/wispr_wlans"
                    println result
                    currentBuild.result = result
                }
            }
        }

        stage('Statistic Response') {
            steps {
                script {
                    util.statisticizeResponse "${VAR_DIR}/output/wispr_wlans"
                }
            }
        }

    }
}
