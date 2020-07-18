pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')

        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${params.SCENARIO}', description: '')
        string(name: 'GCE_IMAGE', defaultValue: 'vscg-${SZ_VERSION}', description: '')
        string(name: 'SZ_FILE', defaultValue: 'sz.inp', description: '')
        string(name: 'SZ_CLUSTER_FILE', defaultValue: 'cluster.inp', description: '')
        string(name: 'SZ_NUM', defaultValue: '1', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${params.SCENARIO} - #${currentBuild.number}"
                }

            }
        }

        stage('Startup SZ') {
            steps {
                sh '''#!/bin/bash
###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/cli_util.sh

setup_gce_var

export GCE_IMAGE=`echo "$GCE_IMAGE" | sed s'/\\./-/'g`

###
### run cli
###

mkdir -p $VAR_DIR/input/sz

for i in `seq 1 $SZ_NUM`; do
  vm_name=${GCE_IMAGE}-${ACCOUNT%%.*}-${RANDOM}
  launch_sz GCE $vm_name
  sz_ip=`gcloud compute instances describe $vm_name | awk '/networkIP/ {print \\$2}'`
  [ $i == "1" ] && echo -e "${vm_name}\\t${sz_ip}" > $VAR_DIR/input/sz/$SZ_FILE
  echo -e "${vm_name}\\t${sz_ip}" >> $VAR_DIR/input/sz/$SZ_CLUSTER_FILE
  is_ping=`wait_until_pingable 20 10s $sz_ip`
  echo "is ping: ${is_ping}"
  [ "x${is_ping}" == "xfalse" ] && exit 1 || exit 0
done
'''
            }
        }
    }
}
