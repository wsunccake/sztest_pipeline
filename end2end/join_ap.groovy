pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')
        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: '')
        string(name: 'SZ_IP', defaultValue: '', description: '')
        string(name: 'API_VERSION', defaultValue: '', description: '')

        string(name: 'AP_VERSION', defaultValue: '', description: '')
        string(name: 'AP_MODEL', defaultValue: 'R710', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${SZ_VERSION} - ${SCENARIO} - #${currentBuild.number}"
                }

            }
        }

        stage('Join AP') {
            steps {
                sh '''#!/bin/bash
# set -e

###
### setup var
###

SZTEST_HOME=/var/lib/jenkins/sztest
source $SZTEST_HOME/conf/default/setup_var.sh
source $SZTEST_HOME/util/test_cli/sim_util.sh

setup_cli_sim_var


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

sim_number=`cat $SIM_INPUT | wc -l`
echo "start job:`date`"

for sim_config_dir in `seq $sim_number`; do
  sim_pc=`sed -n ${sim_config_dir}p $SIM_INPUT | awk '{print \$2}'`
  if [ -d $sim_config_dir ]; then
    echo "$sim_pc config"
    create_ap_cfg $VAR_DIR/input/sim/$sim_config_dir/apsim.cfg
    
    echo "start time:`date`"
    join_sim_ap $VAR_DIR/input/sim/$sim_config_dir/apsim.cfg $sim_pc
    
    echo "end time:`date`"
  else
    echo "$sim_pc no found config $sim_config_dir"
    exit 1
  fi
done

echo "end job:`date`"
'''
            }
        }
    }
}