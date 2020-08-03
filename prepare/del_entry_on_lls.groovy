
pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')

        string(name: 'SZ_IP', defaultValue: '', description: '')

    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${params.SCENARIO} - #${currentBuild.number}"
                }

            }
        }

        stage('Delete Entry On Local License Server') {
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

del_entry_on_lls $sn
'''
            }
        }
    }
}
