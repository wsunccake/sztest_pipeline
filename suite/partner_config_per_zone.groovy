node {
    properties([
            parameters([
                    string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: ''),
                    string(name: 'SCENARIO', defaultValue: 'partner', description: ''),
                    string(name: 'VAR_DIR', defaultValue: '/var/lib/jenkins/api_perf/var/${SCENARIO}', description: ''),
                    string(name: 'SZ_IP', defaultValue: '', description: ''),
                    string(name: 'NPROC', defaultValue: '8', description: ''),
                    string(name: 'API_VERSION', defaultValue: '', description: ''),
            ])
    ])

    currentBuild.displayName = "${params.SZ_VERSION} - ${params.SCENARIO} - #${currentBuild.number}"

    stage('Create Non Proxy Auth Per Zone') {
        build job: 'create_non_proxy_auth_service_per_zone',
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

    stage('Create Non Proxy Acct Per Zone') {
        build job: 'create_non_proxy_acct_service_per_zone',
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

    stage('Create PSK WLAN Per Zone') {
        build job: 'create_psk_wlan_per_zone',
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

    stage('Create 802.1x WLAN With Non Proxy Per Zone') {
        build job: 'create_8021x_wlan_with_non_proxy_per_zone',
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

    stage('Create Hotspot Per Zone') {
        build job: 'create_hotspot_per_zone',
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

    stage('Create WISPr MAC WLAN With Proxy Per Zone') {
        build job: 'create_wispr_mac_wlan_with_proxy_per_zone',
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

    stage('Create WISPr WLAN With Proxy Per Zone') {
        build job: 'create_wispr_wlan_with_proxy_per_zone',
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

    stage('AP Pre-Provision Per Zone') {
        build job: 'create_ap_per_zone',
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

}
