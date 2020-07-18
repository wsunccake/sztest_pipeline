pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')
        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${params.SCENARIO}', description: '')

        string(name: 'CLUSTER_NAME', defaultValue: '', description: '')
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

        stage('Join SZ Cluster') {
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

for i in `cat $VAR_DIR/input/sz/$SZ_CLUSTER_FILE`; do
  tmp_ip=`sed -n ${i}p $VAR_DIR/input/sz/$SZ_CLUSTER_FILE | awk '{print \$2}'
  if [ $i == "1" ]; then
    export CLUSTER_IP=$tmp_ip
  else
    unset SZ_NAME
    export SZ_IP=$tmp_ip
    setup_cli_var
    echo "SZ_IP: $SZ_IP, SZ_NAME: $SZ_NAME, CLUSTER_NAME: $CLUSTER_NAME, CLUSTER_IP: $CLUSTER_IP"

    echo "run setup network"
    $SZTEST_HOME/util/test_cli/setup-network.exp

    echo "run join cluster"
    $SZTEST_HOME/util/test_cli/setup-as-cluster.exp
  fi
done
'''
            }
        }
    }
}