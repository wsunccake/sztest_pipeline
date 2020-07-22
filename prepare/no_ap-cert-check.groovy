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
                    currentBuild.displayName = "${SZ_VERSION} - ${SCENARIO} - #${currentBuild.number}"
                }

            }
        }

        stage('Disable AP Cert Check') {
            steps {
                sh '''#!/bin/bash
###
### setup var
###
SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/cli_util.sh


###
### run cli
###

for i in `wc -l $VAR_DIR/input/sz/$SZ_CLUSTER_FILE | awk '{print \$1}' | xargs -i seq {}`; do
  sz_ip=`sed -n ${i}p $VAR_DIR/input/sz/$SZ_CLUSTER_FILE | awk '{print \$2}'`
  export SZ_IP=$sz_ip
  setup_cli_var
  echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME"

  echo "no ap-cert-check"
  $SZTEST_HOME/util/test_cli/no-ap-cert-check.exp
done
'''
            }
        }
    }
}
