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

        stage('Delete Sim') {
            steps {
                sh '''#!/bin/bash
###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/cli_util.sh

setup_gce_var


###
### run cli
###

if [ ! -f $VAR_DIR/input/sim/sim.inp ]; then
  echo "no found $VAR_DIR/input/sim/sim.inp"
  exit 1
fi


for sim_pc in `awk '{print \$1}' $VAR_DIR/input/sim/sim.inp`; do
  echo "start: `date`"
  delete_vm GCE $sim_pc
  echo "end: `date`"
done
'''
            }
        }
    }
}