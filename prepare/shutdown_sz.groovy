pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')

        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${params.SCENARIO}', description: '')
        string(name: 'SZ_CLUSTER_FILE', defaultValue: 'cluster.inp', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${params.SCENARIO} - #${currentBuild.number}"
                }
            }
        }

        stage('Shutdown SZ') {
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

if [ ! -f ${VAR_DIR}/input/sz/${SZ_CLUSTER_FILE} ]; then
  echo "no found ${VAR_DIR}/input/sz/${SZ_CLUSTER_FILE}"
  exit 1
fi

for vm_name in `awk '{print \$1}' $VAR_DIR/input/sz/${SZ_CLUSTER_FILE}`; do
  echo "start: `date`"
  gcloud compute instances stop $vm_name
  echo "end: `date`"
done
'''
            }
        }
    }
}
