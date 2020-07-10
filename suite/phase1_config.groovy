node {
    properties([
            parameters([
                    string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: ''),
                    string(name: 'SCENARIO', defaultValue: 'phase1', description: ''),
                    string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${scenario}', description: ''),
                    string(name: 'SZ_IP', defaultValue: '', description: ''),
                    string(name: 'NPROC', defaultValue: '8', description: ''),
                    string(name: 'DPSK_AMOUNT', defaultValue: '2', description: ''),
                    string(name: 'API_VERSION', defaultValue: '', description: ''),
            ])
    ])

    currentBuild.displayName = "${params.SZ_VERSION} - ${params.SCENARIO} - #${currentBuild.number}"

    stage('Create Domain') {
        build job: 'phase1_create_domain',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'NPROC', value: "${NPROC}"),
                        string(name: 'API_VERSION', value: "${API_VERSION}"),
                ],
                propagate: false
    }

    stage('Create Zone') {
        build job: 'phase1_create_zone',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'NPROC', value: "${NPROC}"),
                        string(name: 'API_VERSION', value: "${API_VERSION}"),
                ],
                propagate: false
    }

    stage('Create Open WLAN') {
        build job: 'phase1_create_open_wlan',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'NPROC', value: "${NPROC}"),
                        string(name: 'API_VERSION', value: "${API_VERSION}"),
                ],
                propagate: false
    }

    stage('Create DPSK WLAN') {
        build job: 'phase1_create_dpsk_wlan',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'API_VERSION', value: "${API_VERSION}"),
                ],
                propagate: false
    }

    stage('Create DPSK ') {
        build job: 'phase1_create_dpsk_batch',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'DPSK_AMOUNT', value: "${DPSK_AMOUNT}"),
                        string(name: 'API_VERSION', value: "${API_VERSION}"),
                ],
                propagate: false
    }

//    stage('Analyze DPSK') {
//        build job: 'phase1_statistics_performance',
//                parameters: [
//                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
//                        string(name: 'scenario', value: "${scenario}"),
//                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
//                        string(name: 'VAR_DATA', value: "wlans/dpsk"),
//                ],
//                propagate: false
//    }

    stage('Create WLAN Group') {
        build job: 'phase1_create_wlan_group',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'NPROC', value: "${NPROC}"),
                        string(name: 'API_VERSION', value: "${API_VERSION}"),
                ],
                propagate: false
    }

    stage('Pre-Provision AP') {
        build job: 'phase1_create_ap',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'NPROC', value: "${NPROC}"),
                        string(name: 'API_VERSION', value: "${API_VERSION}"),
                ],
                propagate: false
    }

    stage('Create AP Group') {
        build job: 'phase1_create_ap_group',
                parameters: [
                        string(name: 'SZ_VERSION', value: "${SZ_VERSION}"),
                        string(name: 'SCENARIO', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                        string(name: 'SZ_IP', value: "${SZ_IP}"),
                        string(name: 'NPROC', value: "${NPROC}"),
                        string(name: 'API_VERSION', value: "${API_VERSION}"),
                ],
                propagate: false
    }

    stage('Arrange Data') {
        build job: 'pickup_data',
                parameters: [
                        string(name: 'version', value: "${SZ_VERSION}"),
                        string(name: 'scenario', value: "${SCENARIO}"),
                        string(name: 'VAR_DIR', value: "${VAR_DIR}"),
                ],
                propagate: false
    }

}
