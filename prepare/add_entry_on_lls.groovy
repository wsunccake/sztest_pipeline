pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')
        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${params.SCENARIO}', description: '')
        string(name: 'SZ_IP', defaultValue: '', description: '')

        string(name: 'LOCAL_LICENSE_SERVER_DEVICE_TYPE', defaultValue: '${params.SCENARIO}-${SZ_VERSION}', description: '')
        string(name: 'LOCAL_LICENSE_SERVER_IP_DEVICE_ALIAS', defaultValue: '${params.SCENARIO}-${SZ_VERSION}', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${params.SCENARIO} - #${currentBuild.number}"
                }

            }
        }

        stage('Add Entry On Local License Server') {
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
source $SZTEST_HOME/util/test_cli/lls_util.sh


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
### add entry on lls
###

setup_cli_var
setup_cli_lls_var

add_entry_on_lls $sn CAPACITY-AP 1.0 10000
'''
            }
        }

        stage('Update Local License Server') {
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

setup_api_var
setup_cli_lls_var


###
### run api
###

pubapi_login $SZ_USERNAME $SZ_PASSWORD

update_local_license_server $LOCAL_LICENSE_SERVER_IP
sleep 5
sync_local_license_server

pubapi_logout
'''
                }
        }

        stage('Show License') {
            steps {
                sh '''#!/bin/bash
###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/cli_util.sh

setup_cli_var
setup_cli_lls_var

echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, SZ_VERSION: $SZ_VERSION"


###
### run cli
###

echo "run show license"
$SZTEST_HOME/util/test_cli/show-license.exp
'''
            }
        }
    }
}
