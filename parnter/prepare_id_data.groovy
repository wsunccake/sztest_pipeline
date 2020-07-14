library identifier: 'dynamic-libary@master', retriever: modernSCM(
        [$class: 'GitSCMSource',
         remote: 'https://github.com/wsunccake/sztest_pipeline.git'])

pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: 'phase1', description: '')
        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'API_VERSION', defaultValue: '', description: '')
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
source $SZTEST_HOME/util/common.sh
source $SZTEST_HOME/util/test_api/parnter.sh

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

domain_id=`head -1 $VAR_DIR/output/id/domain_ids.log | awk -F'|' '{print $1}'`

echo "query_all_l2acl_by_domain_id $domain_id > $VAR_DIR/output/id/l2acl_ids.log"
time query_all_l2acl_by_domain_id $domain_id > $VAR_DIR/output/id/l2acl_ids.log

echo "query_all_l3acp_by_domain_id $domain_id > $VAR_DIR/output/id/l3acp_ids.log"
query_all_l3acp_by_domain_id $domain_id > $VAR_DIR/output/id/l3acp_ids.log

# logout
pubapi_logout
echo "end job:`date`"
'''
            }
        }

    }
}
