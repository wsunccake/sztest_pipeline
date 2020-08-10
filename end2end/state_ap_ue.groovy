pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')
        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: '')

    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${SCENARIO} - #${currentBuild.number}"
                }

            }
        }

        stage('State AP and UE') {
            steps {
                sh '''#!/bin/bash
#set -e

###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/test_cli/sim_util.sh

setup_cli_sim_var
export -f state_ap_ue

###
### gen input
###

SIM_INPUT=$VAR_DIR/input/sim/sim.inp


###
### run cli
###

cd $VAR_DIR/input/sim

if [ ! -f $SIM_INPUT ]; then
  echo "no found $SIM_INPUT"
  exit 1
fi

awk '{print \$2}' $VAR_DIR/input/sim/sim.inp | xargs -P ${NPROC} -i sh -c 'state_ap_ue {}'
'''
            }
        }
    }
}
