library identifier: 'dynamic-libary@master', retriever: modernSCM(
        [$class: 'GitSCMSource',
         remote: 'https://github.com/wsunccake/sztest_pipeline.git'])

pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: 'phase1', description: '')
        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: '')
        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'API_VERSION', defaultValue: '', description: '')

        string(name: 'COUNT_NUMBER', defaultValue: '100', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${SCENARIO} - #${currentBuild.number}"
                }

            }
        }

        stage('Prepare Locust Data') {
            steps {
                sh '''#!/bin/bash
###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/api_util.sh
source $SZTEST_HOME/util/test_api/common.sh
source $SZTEST_HOME/util/test_api/partner.sh

setup_api_var

echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, SZ_VERSION: $SZ_VERSION"


###
### run api
###

mkdir -p $VAR_DIR/output/id

echo "start job:`date`"
# login
pubapi_login $SZ_USERNAME $SZ_PASSWORD

echo "get_all_domain > $VAR_DIR/output/id/domain_ids.log"
time get_all_domain > $VAR_DIR/output/id/domain_ids.log

echo "query_all_ap > $VAR_DIR/output/id/ap_zone_domain_id.log"
time query_all_ap > $VAR_DIR/output/id/ap_zone_domain_id.log

echo "query_all_wlan > $VAR_DIR/output/id/wlan_zone_domain.log"
time query_all_wlan > $VAR_DIR/output/id/wlan_zone_domain.log

domain_id=`head -1 $VAR_DIR/output/id/domain_ids.log | awk -F'|' '{print $1}'`

test_functions=(l2acl l3acp wifi_calling device_policy lbs application_policy_v2 user_defined proxy_auth proxy_acct vlan_pooling)

for f in ${test_functions[@]}; do
  l=$VAR_DIR/output/id/${f}_ids.log
  [ -f $l ] && rm -fv $l
done

for domain_id in `sed -n 1,${COUNT_NUMBER}p $VAR_DIR/output/id/domain_ids.log | sed 's/|.*//'`; do
  for f in ${test_functions[@]}; do
    l=$VAR_DIR/output/id/${f}_ids.log
    echo "query_all_${f}_by_domain_id $domain_id >> $l"
    query_all_${f}_by_domain_id $domain_id >> $l
  done
done

# logout
pubapi_logout
echo "end job:`date`"
'''
            }
        }

    }
}
