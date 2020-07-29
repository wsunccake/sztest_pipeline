node {
    properties([
            parameters([
                    string(name: 'SZ_VERSION', defaultValue: '1.0.0.0', description: ''),
                    string(name: 'SCENARIO', defaultValue: 'partner', description: ''),
                    string(name: 'VAR_DIR', defaultValue: '/usr/share/nginx/html/api_perf/${SZ_VERSION}/${SCENARIO}', description: ''),
                    string(name: 'SZ_IP', defaultValue: '', description: ''),
                    string(name: 'NPROC', defaultValue: '8', description: ''),
                    string(name: 'API_VERSION', defaultValue: '', description: ''),
            ])
    ])

    currentBuild.displayName = "${params.SZ_VERSION} - ${params.SCENARIO} - #${currentBuild.number}"


    stage('Delete Authentication Per Partner Domain') {
        build job: 'partner_delete_auth_service_per_partner_domain',
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

    stage('Delete Accounting Per Partner Domain') {
        build job: 'partner_delete_acct_service_per_partner_domain',
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

    stage('Delete VLAN Pooling Per Partner Domain') {
        build job: 'partner_delete_vlan_pooling_per_partner_domain',
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

    stage('Delete Application Policy Per Partner Domain') {
        build job: 'partner_delete_application_policy_per_partner_domain',
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

    stage('Delete User Defined Per Partner Domain') {
        build job: 'partner_delete_user_defined_per_partner_domain',
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

    stage('Delete L2ACL Per Partner Domain') {
        build job: 'partner_delete_l2acl_per_partner_domain',
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

    stage('Delete L3ACP Per Partner Domain') {
        build job: 'partner_delete_l3acp_per_partner_domain',
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

    stage('Delete LBS Per Partner Domain') {
        build job: 'partner_delete_lbs_per_partner_domain',
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

    stage('Delete Wifi Calling Policy Per Partner Domain') {
        build job: 'partner_delete_wifi_calling_policy_per_partner_domain',
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

    stage('Delete Device Policy Per Partner Domain') {
        build job: 'partner_delete_device_policy_per_partner_domain',
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
