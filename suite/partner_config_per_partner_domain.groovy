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

    stage('Create Partner Domain') {
        build job: 'partner_create_partner_domain',
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

    stage('Create Zone Per Partner Domain') {
        build job: 'partner_create_zone_per_partner_domain',
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

    stage('Create Authentication Per Partner Domain') {
        build job: 'partner_create_auth_service_per_partner_domain',
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

    stage('Create Accounting Per Partner Domain') {
        build job: 'partner_create_acct_service_per_partner_domain',
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

    stage('Create VLAN Pooling Per Partner Domain') {
        build job: 'partner_create_vlan_pooling_per_partner_domain',
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

    stage('Create Application Policy Per Partner Domain') {
        build job: 'partner_create_application_policy_per_partner_domain',
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

    stage('Create User Defined Per Partner Domain') {
        build job: 'partner_create_user_defined_per_partner_domain',
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

    stage('Create L2ACL Per Partner Domain') {
        build job: 'partner_create_l2acl_per_partner_domain',
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

    stage('Create L3ACP Per Partner Domain') {
        build job: 'partner_create_l3acp_per_partner_domain',
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

    stage('Create LBS Per Partner Domain') {
        build job: 'partner_create_lbs_per_partner_domain',
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

    stage('Create Wifi Calling Policy Per Partner Domain') {
        build job: 'partner_create_wifi_calling_policy_per_partner_domain',
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

    stage('Create Device Policy Per Partner Domain') {
        build job: 'partner_create_device_policy_per_partner_domain',
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
