
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
        string(name: 'NPROC', defaultValue: '2', description: '')
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

        stage('Update L2ACL') {
            steps {
                sh '''#!/bin/bash
###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/api_util.sh
source $SZTEST_HOME/util/test_api/parnter.sh

setup_api_var

echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, SZ_VERSION: $SZ_VERSION"


###
### get id
###

domain_id=`head -1 $VAR_DIR/output/id/domain_ids.log | awk -F'|' '{print $1}'`


###
### run api
###

echo "start job:`date`"
# login
pubapi_login $SZ_USERNAME $SZ_PASSWORD

query_all_l2_by_domain_id $domain_id > $VAR_DIR/output/id/l2acl_ids.log
l2acl_id=`head -1 $VAR_DIR/output/id/l2acl_ids.log | awk -F'|' '{print \$1}'`

get_l2acl_by_id $l2acl_id | sed -n 's/Response body: //p' | jq '.' > tmp.json

lines=(creatorId creatorUsername createDateTime modifierId modifierUsername modifiedDateTime domainId id)
for l in ${lines[@]}; do
  jq "del(.${l})" < tmp.json > result.json
  cp result.json tmp.json
done
letter=`head /dev/urandom | tr -dc A-Za-z | head -c 8`
jq ".description='"$letter"' tmp.json > result.json

put_l2acl $l2acl_id result.json

# logout
pubapi_logout
echo "end job:`date`"
'''
            }
        }

    }
}
