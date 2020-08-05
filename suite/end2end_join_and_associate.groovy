node {
    properties([
            parameters([
                    // for common
                    string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: ''),
                    string(name: 'SCENARIO', defaultValue: 'partner', description: ''),
                    string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: ''),
                    string(name: 'SZ_IP', defaultValue: '', description: ''),
                    string(name: 'NPROC', defaultValue: '8', description: ''),
                    string(name: 'API_VERSION', defaultValue: '', description: ''),

                    // for end2end_launch_sim
                    string(name: 'MADSZ_IMAGE', defaultValue: 'ubuntu-minimal-1804-bionic-v20200729', description: ''),
                    string(name: 'MADSZ_IMAGE_PROJECT', defaultValue: 'ubuntu-os-cloud', description: ''),
                    string(name: 'MADSZ_TGZ', defaultValue: 'madSZ-v5.2.1-14-u1804.tar.xz', description: ''),

                    // for end2end_join_ap
                    string(name: 'AP_VERSION', defaultValue: '', description: ''),
                    string(name: 'AP_MODEL', defaultValue: 'R710', description: ''),

                    // for end2end_monitor_ap, end2end_monitor_ue
                    string(name: 'AP_NUM', defaultValue: '1', description: ''),
                    string(name: 'UE_NUM', defaultValue: '1', description: ''),
                    string(name: 'WAITING_TIME', defaultValue: '600', description: ''),
            ])
    ])

    currentBuild.displayName = "${params.SZ_VERSION} - ${params.SCENARIO} - #${currentBuild.number}"

    stage('Launch Sim') {
        build job: 'end2end_launch_sim',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'MADSZ_IMAGE', value: "${MADSZ_IMAGE}"),
                        string(name: 'MADSZ_IMAGE_PROJECT', value: "${MADSZ_IMAGE_PROJECT}"),
                        string(name: 'MADSZ_TGZ', value: "${MADSZ_TGZ}"),
                ],
                propagate: false
    }

    stage('Join AP') {
        build job: 'end2end_join_ap',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'API_VERSION', value: "${API_VERSION}"),
                        string(name: 'AP_VERSION', value: "${AP_VERSION}"),
                        string(name: 'AP_MODEL', value: "${AP_MODEL}"),
                ],
                propagate: false
    }

    stage('Count AP') {
        build job: 'end2end_monitor_ap',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'API_VERSION', value: "${API_VERSION}"),
                        string(name: 'AP_NUM', value: "${AP_NUM}"),
                        string(name: 'WAITING_TIME', value: "${WAITING_TIME}"),
                ],
                propagate: false
    }

    stage('Associate UE') {
        build job: 'end2end_associate_ue',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                ],
                propagate: false
    }

    stage('Count UE') {
        build job: 'end2end_monitor_ue',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'API_VERSION', value: "${API_VERSION}"),
                        string(name: 'UE_NUM', value: "${UE_NUM}"),
                        string(name: 'WAITING_TIME', value: "${WAITING_TIME}"),
                ],
                propagate: false
    }

}
