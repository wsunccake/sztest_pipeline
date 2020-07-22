pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')
        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${params.SCENARIO}', description: '')
        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'CLUSTER_NAME', defaultValue: '', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${params.SCENARIO} - #${currentBuild.number}"                }

            }
        }

        stage('Setup Collectd') {
            steps {
                sh '''#!/bin/bash
###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/api_util.sh
source $SZTEST_HOME/util/test_api/common.sh
source $SZTEST_HOME/util/cli_util.sh


###
### get sn
###

setup_api_var

pubapi_login $SZ_USERNAME $SZ_PASSWORD

sn=`get_controller |
sed -n 's/Response body: //p' |
jq ".list | map(select(.managementIp == \\"$SZ_IP\\"))" |
jq '.[].serialNumber' |
tr -d \\"`

pubapi_logout

echo "serial number: $sn"


###
### run cmd
###

setup_cli_var
setup_cli_collectd_var

echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, SZ_VERSION: $SZ_VERSION"

export PASSPHRASE=`curl ${SESAME2_URL}${sn} | awk '/Access Key/{print \$3}' | tr -d \\'`
echo "PASSPHRASE: $PASSPHRASE"

echo "setup collectd"
$SZTEST_HOME/util/test_cli/setup-collectd.exp
'''
            }
        }
    }
}
