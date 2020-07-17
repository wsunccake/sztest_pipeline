pipeline {
    agent any
    parameters {
        string(name: 'version', defaultValue: '1.0.0.0', description: '')
        string(name: 'scenario', defaultValue: 'group0', description: '')

        string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: '')
        string(name: 'GCE_IMAGE', defaultValue: 'vscg-${version}', description: '')
        string(name: 'SZ_FILE', defaultValue: 'sz.inp', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
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
vm_name=${GCE_IMAGE}-${ACCOUNT%%.*}-${RANDOM}

###
### run cli
###

launch_sz GCE vm_name
sz_ip=`gcloud compute instances describe $vm_name | awk '/networkIP/ {print \$2}'`

mkdir -p $VAR_DIR/input/sz
echo -e "${vm_name}\\t${sz_ip}" > $VAR_DIR/input/sz/$SZ_FILE
echo -e "${vm_name}\\t${sz_ip}" >> $VAR_DIR/input/sz/cluster.inp

wait_until_pingable 20 10s $sz_ip
echo "is ping: ${is_ping}"
[ "x${is_ping}" == "xfalse" ] && exit 1 || exit 0
'''
            }
        }
    }
}
