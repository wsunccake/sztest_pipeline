pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')
        string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: '')

        string(name: 'MADSZ_IMAGE', defaultValue: 'ubuntu-minimal-1804-bionic-v20191024', description: '')
        string(name: 'MADSZ_IMAGE_PROJECT', defaultValue: 'ubuntu-os-cloud', description: '')
        string(name: 'MADSZ_TGZ', defaultValue: 'madSZ-v5.2-38-u1804.tar.xz', description: '')
    }

    stages {
        stage('Update Build Name') {
            steps {
                script {
                    currentBuild.displayName = "${version} - ${scenario} - #${currentBuild.number}"
                }

            }
        }

        stage('Launch Sim') {
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
### env var
###

mkdir -p $VAR_DIR/input/sim
[ -f $VAR_DIR/input/sim/sim.inp ] && rm $VAR_DIR/input/sim/sim.inp

TMP_DIR=`mktemp sim-${SZ_VERSION}-XXXXXXXXXX --tmpdir=/tmp)`
TMP_DATE=`date +%s`
TMP_INVENTORY=${TMP_DIR}/${TMP_DATA}


###
### run cli
###

# launch sim
sim_num=`find $VAR_DIR/input/sim -maxdepth 1 -type d | wc -l`
sim_num=`expr $sim_num - 1`
for i in `seq $sim_num`; do
  vm_name=simtool-${ACCOUNT%%.*}-${RANDOM}
  if [ -f $VAR_DIR/input/sz/sz.inp ]; then
    vm_name=simtool`awk '{print \$1}' $VAR_DIR/input/sz/sz.inp | sed s/vscg//`
  fi
  vm_name=${vm_name}-${i}
  launch_sz GCE $vm_name
  madsz_ip=`gcloud compute instances describe $vm_name | awk '/networkIP/ {print \$2}'`
  
  is_ping=`wait_until_pingable 30 10s $madsz_ip`
  echo "is ping: ${is_ping}"
  
  echo -e "${vm_name}\\t${madsz_ip}" >> $VAR_DIR/input/sim/sim.inp
  echo -e "${vm_name}\\tansible_connection=ssh\\tansible_ssh_host=${madsz_ip}\\tansible_ssh_port=22\\tansible_ssh_user=${SIM_USER}\n" >> ${TMP_INVENTORY}
done

# run playbook
echo "[madsz]" >> ${TMP_INVENTORY}
awk '{print $1}' $VAR_DIR/input/sim/sim.inp >> ${TMP_INVENTORY}
cd $SZTEST_HOME/playbook

echo "ansible-playbook -i ${TMP_INVENTORY} deploy.yaml -t madsz -e madsz_package=$MADSZ_TGZ -v"
ansible-playbook -i ${TMP_INVENTORY} deploy.yaml -t madsz -e "madsz_package=$MADSZ_TGZ" -v

ansible_status=$?
echo "after ansible-playbook status: $ansible_status"

if [ "x$ansible_status" != "x0" ]; then
  echo "ansible-playbook again"
  ansible-playbook -i ${TMP_INVENTORY} deploy.yaml -t madsz -e "madsz_package=$MADSZ_TGZ" -v
  echo "after ansible-playbook status: $?"
fi

# reboot sim
echo "ansible madsz -i ${TMP_INVENTORY} -m command -a \"sudo reboot\" -v"
ansible madsz -i ${TMP_INVENTORY} -m command -a "sudo reboot" -v

sleep 300

echo "ansible madsz -i ${TMP_INVENTORY} -m command -a \"uptime\" -v"
ansible madsz -i ${TMP_INVENTORY} -m command -a "uptime" -v

for madsz_ip in `awk '{print \\$2}' $VAR_DIR/input/sim/sim.inp`; do
  wait_until_pingable 30 10s $madsz_ip
done

#rm ${TMP_INVENTORY}
'''
            }
        }
    }
}
