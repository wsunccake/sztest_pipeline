pipeline {
    agent any
    parameters {
        string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: '')
        string(name: 'SCENARIO', defaultValue: '', description: '')

        string(name: 'SRC_DIR', defaultValue: '/var/lib/jenkins/sztest/conf/${SCENARIO}', description: '')
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

        stage('Prepare Var Dir') {
            steps {
                sh '''#!/bin/bash
[ -d ${VAR_DIR} ] && mv ${VAR_DIR} ${VAR_DIR}-`date +%s`
mkdir -p $VAR_DIR/..
cp -rfv ${SRC_DIR} ${VAR_DIR}/..

mkdir -p $VAR_DIR/output
'''
                echo "var dir: ${VAR_DIR}"
                echo "input dir: ${VAR_DIR}/input"
                echo "output dir: ${VAR_DIR}/output"
            }
        }
    }
}